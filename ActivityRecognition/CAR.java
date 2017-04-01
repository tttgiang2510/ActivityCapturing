import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import org.json.JSONObject;
	/**
	 * This class represents the Complex Activity Recognition (CAR) algorithm.
	 * The class takes an event queue as an input 
	 * <p><b><code>CAR mAlgorithm = new CAR;</br>
	 * BlockingQueue<JSONObject> mQ = new ArrayBlockingQueue<>();</br>
	 * mAlgorithm.setmInQ(mQ);</br>
	 * mAlgorithm.setRunning(true);</br>
	 * mAlgorithm.start();</br></code></b>
	 * <p>The input queue contain the atomic activities and contexts attribute in
	 *  form of a JSON object.
	 * <p><b><code> {"activity":<"activityName">}</br>
	 * {"context" :<"contextName">}
	 * </code></b>
	 * 
	 * */
public class CAR extends Thread{

	private JSONObject mEvent;
	private Situation  mSituation;
	private BlockingQueue<JSONObject> mInQ;
	private Hashtable<String, OnGoingCA> mOnGoingCAList = new Hashtable<>();
	private Hashtable<String, OnGoingCA> mRecognizedCAList = new Hashtable<>();
	private boolean situationChanged = false;
	private boolean running = false;
	@Override
	public void run() {
		System.out.println("Complex Activity Recognition algorithm is running and waiting for new events");
		while(running){
			try{
				mEvent = mInQ.take();
				checkCurrentSituation();
				deleteDeadCA();
				checkForNewCA(mEvent);
				passNewEvent(mEvent);
				checkIfAnyCAFulfilled();
			}catch (InterruptedException e) {
				System.out.println("-----------------INTERRUP-------------------");}	
		}
	}
	/**
	 * Passes the new event to the current tracked complex activities list
	 * @param mEvent a JSON object contain an Event
	 * */
	private void passNewEvent(JSONObject mEvent){
		if(mEvent.has(Consts.ACTIVITY)){
			newAtomicActivity(new AtomicActivity((mEvent.getString(Consts.ACTIVITY))));
		}else if(mEvent.has(Consts.CONTEXT)){
			newContextAttribute(new ContextAttribute(mEvent.getString(Consts.CONTEXT)));
		}
	}
	/**
	 * Checks if this event is the start of any complex activity in this situation.
	 * If it is, adds the new complex activity to the current tracked complex activities
	 * @param mEvent a JSON object contain an Event
	 * */
	private void checkForNewCA(JSONObject mEvent){
		if(mEvent.has(Consts.ACTIVITY)){
			checkForNewCAA(new AtomicActivity((mEvent.getString(Consts.ACTIVITY))));
		}else if(mEvent.has(Consts.CONTEXT)){
			checkForNewCAC(new ContextAttribute(mEvent.getString(Consts.CONTEXT)));
		}
	}
	/**
	 * Looks at tracked complex activities and checks if any has exceeded its life span.
	 * If it finds any, delete it. 
	 * */
	private void deleteDeadCA(){
		Iterator<Entry<String, OnGoingCA>> mIterator = mOnGoingCAList.entrySet().iterator();
		while(mIterator.hasNext()){
			 OnGoingCA mOnGoingCA = mIterator.next().getValue();
			 if(!mOnGoingCA.isAlive()){
				 System.out.println("Comlex Activity deleted (Exceded Time Range): "+ mOnGoingCA.getmName());
				 mIterator.remove(); 
			 }
		}
	}
	/**
	 * Passes the new atomic activity to the current tracked complex activities list
	 * @param mActicity the atomic activity to be passed
	 * */
	private void newAtomicActivity(AtomicActivity mActivity){
		Iterator<Entry<String, OnGoingCA>> mIterator = mOnGoingCAList.entrySet().iterator();
		while(mIterator.hasNext()){ 
			mIterator.next().getValue().newAtomicActivity(mActivity);
			}
	}
	/**
	 * Passes the new context attribute to the current tracked complex activities list
	 * @param mContext the context attribute to be passed
	 * */
	private void newContextAttribute(ContextAttribute mContext){
		Iterator<Entry<String, OnGoingCA>> mIterator = mOnGoingCAList.entrySet().iterator();
		while(mIterator.hasNext()){	
			mIterator.next().getValue().newContextAttribute(mContext);
			}
	}
	/**
	 * Checks for the current situation and sets it.
	 * */
	private void checkCurrentSituation(){
		mSituation = CAFactory.atMeetingRoom();
		if(situationChanged){deleteNonSituationCA();}
	}
	/**
	 * Checks if this atomic activity is the start of any complex activity in this situation.
	 * If it is, adds the new complex activity to the current tracked complex activities
	 * @param mActivity an atomic activity object
	 * */
	private void checkForNewCAA(AtomicActivity mActivity){
		Iterator<Entry<String, ComplexActivity>> mSituationCAListIterator = mSituation.getmCAList().entrySet().iterator();
		while(mSituationCAListIterator.hasNext()){
			ComplexActivity mSituationCA = mSituationCAListIterator.next().getValue();
			if(mSituationCA.isStartAtomicActivity(mActivity) && !mOnGoingCAList.containsKey(mSituationCA.getmName())){
				addComplexActivitytoOngoingCALisht(mSituationCA);
			}
		}
	}
	/**
	 * Checks if this context attribute is the start of any complex activity in this situation.
	 * If it is, adds the new complex activity to the current tracked complex activities
	 * @param mContext a context attribute object
	 * */
	private void checkForNewCAC(ContextAttribute mContext){
		Iterator<Entry<String, ComplexActivity>> mSituationCAListIterator = mSituation.getmCAList().entrySet().iterator();
		while(mSituationCAListIterator.hasNext()){
			ComplexActivity mSituationCA = mSituationCAListIterator.next().getValue();
			if(mSituationCA.isStartContextAttribute(mContext) && !mOnGoingCAList.containsKey(mSituationCA.getmName())){
				addComplexActivitytoOngoingCALisht(mSituationCA);
			}
		}
	}
	/**
	 * Adds a complex activity to the tracked complex activities list
	 * @param a complex activity object
	 * */
	private void addComplexActivitytoOngoingCALisht(ComplexActivity mCA){
		mOnGoingCAList.put(mCA.getmName(),new OnGoingCA(mCA));
		System.out.println("Detected a biggining of a Complex Activity :" + mCA.getmName());
		
		/* 
		 * Track start time of the activity!!!
		 */
	}
	/**
	 * Check if any complex activity in the tracked complex activities is fulfilled.
	 * If so, print its name and remove it from the list
	 * */
	private void checkIfAnyCAFulfilled(){
		Iterator<Entry<String, OnGoingCA>> mIterator = mOnGoingCAList.entrySet().iterator();
		while(mIterator.hasNext()){
			OnGoingCA mOnGoingCA = mIterator.next().getValue();
			if(mOnGoingCA.fulfilled()){
				// Start time and End Time
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
				Date resultdate = new Date(mOnGoingCA.getmStartTime());
				
				System.out.println("--------- Complex Activity fulfilled and removed --------- ");
				System.out.println("Name       :" + mOnGoingCA.getmCA().getmName());
				System.out.println("Wieght     :" + mOnGoingCA.getmWieght());
				System.out.println("Start time :" + sdf.format(resultdate));
				resultdate = new Date(System.currentTimeMillis());
				System.out.println("End time   :" + sdf.format(resultdate));
				
				mIterator.remove();
				
				/*
				 * If activity is recognized then put it in the recognized activity list, 
				 * and always check the finish time of the activity
				 */
				
			}
		}
	}
	/**
	 * Checks the tracked complex activities list and the situation to find if there is any
	 * complex activity that doesn't belong to the current situation and delete it.
	 * */
	private void deleteNonSituationCA(){
		Iterator<Entry<String, OnGoingCA>> mIterator = mOnGoingCAList.entrySet().iterator();
		while(mIterator.hasNext()){
			if(!mSituation.getmCAList().containsKey(mIterator.next().getValue().getmName())){
				mIterator.remove();
			}
		}
	}
	public void setRunning(boolean running){
		this.running = running;
		if(!running){this.interrupt();}
	}
	/**
	 * Sets the input queue to the algorithm
	 * */
	public void setmInQ(BlockingQueue<JSONObject> mInQ) {
		this.mInQ = mInQ;
	}
}
