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

	private JSONObject event;
	private Situation  situation;
	private BlockingQueue<JSONObject> inQueue;
	private Hashtable<String, OnGoingCA> onGoingCAList = new Hashtable<>();
	private Hashtable<String, OnGoingCA> recognizedCAList = new Hashtable<>();
	private boolean situationChanged = false;
	private boolean isRunning = false;
	@Override
	public void run() {
		System.out.println("Complex Activity Recognition algorithm is running and waiting for new events");
		while(isRunning){
			try{
				event = inQueue.take();
				checkCurrentSituation();
				deleteDeadCA();
				checkForNewCA(event);
				passNewEvent(event);
				checkIfAnyCAFulfilled();
			}catch (InterruptedException e) {
				System.out.println("-----------------INTERRUP-------------------");}	
		}
	}
	/**
	 * Passes the new event to the current tracked complex activities list
	 * @param event a JSON object contain an Event
	 * */
	private void passNewEvent(JSONObject event){
		if(event.has(Consts.ACTIVITY)){
			newAtomicActivity(new AtomicActivity((event.getString(Consts.ACTIVITY))));
		}else if(event.has(Consts.CONTEXT)){
			newContextAttribute(new ContextAttribute(event.getString(Consts.CONTEXT)));
		}
	}
	/**
	 * Checks if this event is the start of any complex activity in this situation.
	 * If it is, adds the new complex activity to the current tracked complex activities
	 * @param event a JSON object contain an Event
	 * */
	private void checkForNewCA(JSONObject event){
		if(event.has(Consts.ACTIVITY)){
			checkForNewCAA(new AtomicActivity((event.getString(Consts.ACTIVITY))));
		}else if(event.has(Consts.CONTEXT)){
			checkForNewCAC(new ContextAttribute(event.getString(Consts.CONTEXT)));
		}
	}
	/**
	 * Looks at tracked complex activities and checks if any has exceeded its life span.
	 * If it finds any, delete it. 
	 * */
	private void deleteDeadCA(){
		Iterator<Entry<String, OnGoingCA>> iterator = onGoingCAList.entrySet().iterator();
		while(iterator.hasNext()){
			 OnGoingCA mOnGoingCA = iterator.next().getValue();
			 if(!mOnGoingCA.isAlive()){
				 System.out.println("Comlex Activity deleted (Exceded Time Range): "+ mOnGoingCA.getName());
				 iterator.remove(); 
			 }
		}
	}
	/**
	 * Passes the new atomic activity to the current tracked complex activities list
	 * @param mActicity the atomic activity to be passed
	 * */
	private void newAtomicActivity(AtomicActivity mActivity){
		Iterator<Entry<String, OnGoingCA>> mIterator = onGoingCAList.entrySet().iterator();
		while(mIterator.hasNext()){ 
			mIterator.next().getValue().newAtomicActivity(mActivity);
			}
	}
	/**
	 * Passes the new context attribute to the current tracked complex activities list
	 * @param context the context attribute to be passed
	 * */
	private void newContextAttribute(ContextAttribute context){
		Iterator<Entry<String, OnGoingCA>> mIterator = onGoingCAList.entrySet().iterator();
		while(mIterator.hasNext()){	
			mIterator.next().getValue().newContextAttribute(context);
			}
	}
	/**
	 * Checks for the current situation and sets it.
	 * */
	private void checkCurrentSituation(){
		situation = CAFactory.atMeetingRoom();
		if(situationChanged){deleteNonSituationCA();}
	}
	/**
	 * Checks if this atomic activity is the start of any complex activity in this situation.
	 * If it is, adds the new complex activity to the current tracked complex activities
	 * @param activity an atomic activity object
	 * */
	private void checkForNewCAA(AtomicActivity activity){
		Iterator<Entry<String, ComplexActivity>> mSituationCAListIterator = situation.getmCAList().entrySet().iterator();
		while(mSituationCAListIterator.hasNext()){
			ComplexActivity mSituationCA = mSituationCAListIterator.next().getValue();
			if(mSituationCA.isStartAtomicActivity(activity) && !onGoingCAList.containsKey(mSituationCA.getName())){
				addComplexActivitytoOngoingCALisht(mSituationCA);
			}
		}
	}
	/**
	 * Checks if this context attribute is the start of any complex activity in this situation.
	 * If it is, adds the new complex activity to the current tracked complex activities
	 * @param context a context attribute object
	 * */
	private void checkForNewCAC(ContextAttribute context){
		Iterator<Entry<String, ComplexActivity>> situationCAListIterator = situation.getmCAList().entrySet().iterator();
		while(situationCAListIterator.hasNext()){
			ComplexActivity situationCA = situationCAListIterator.next().getValue();
			if(situationCA.isStartContextAttribute(context) && !onGoingCAList.containsKey(situationCA.getName())){
				addComplexActivitytoOngoingCALisht(situationCA);
			}
		}
	}
	/**
	 * Adds a complex activity to the tracked complex activities list
	 * @param a complex activity object
	 * */
	private void addComplexActivitytoOngoingCALisht(ComplexActivity complexActivity){
		onGoingCAList.put(complexActivity.getName(),new OnGoingCA(complexActivity));
		System.out.println("Detected beginning of a Complex Activity :" + complexActivity.getName());
		
		/* 
		 * Track start time of the activity!!!
		 */
	}
	/**
	 * Check if any complex activity in the tracked complex activities is fulfilled.
	 * If so, print its name and remove it from the list
	 * */
	private void checkIfAnyCAFulfilled(){
		Iterator<Entry<String, OnGoingCA>> mIterator = onGoingCAList.entrySet().iterator();
		while(mIterator.hasNext()){
			OnGoingCA mOnGoingCA = mIterator.next().getValue();
			if(mOnGoingCA.fulfilled()){
				// Start time and End Time
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
				Date resultdate = new Date(mOnGoingCA.getStartTime());
				
				System.out.println("--------- Complex Activity fulfilled and removed --------- ");
				System.out.println("Name       :" + mOnGoingCA.getComplexActivity().getName());
				System.out.println("Wieght     :" + mOnGoingCA.getWieght());
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
		Iterator<Entry<String, OnGoingCA>> mIterator = onGoingCAList.entrySet().iterator();
		while(mIterator.hasNext()){
			if(!situation.getmCAList().containsKey(mIterator.next().getValue().getName())){
				mIterator.remove();
			}
		}
	}
	public void setRunning(boolean isRunning){
		this.isRunning = isRunning;
		if(!isRunning){this.interrupt();}
	}
	/**
	 * Sets the input queue to the algorithm
	 * */
	public void setmInQ(BlockingQueue<JSONObject> inQueue) {
		this.inQueue = inQueue;
	}
}
