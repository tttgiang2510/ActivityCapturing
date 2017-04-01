import java.text.SimpleDateFormat;
import java.util.Hashtable;
/**
 * This class represent a tracked complex activity. When a complex activity is detected from 
 * a start atomic activity or context attribute, the algorithm initiate a new object from this class 
 * and passes the static complex activity as a reference.
 * <p><b><code>OnGoingCA mOnGoingCA = new OnGoingCA(ComplexActivity mCA);</code></b>
 * <p>The algorithm can pass new activities 
 * <p><b><code>mOnGoingCA.newAtomicActivity(mActivity);<br></code></b>
 * <p>The algorithm can check if the activity is alive and fulfilled
 * <p><b><code>	boolean fulfilled  = mOnGoingCA.fulfilled();<br>
	boolean isAlive = mOnGoingCA.isAlive();</code></b>
 * */

public class OnGoingCA {
	
	private ComplexActivity mCA;
	private String mName;
	/*
	 * Start time of the activity
	 * */
	private long mStartTime;
	/*
	 * End time of the activity
	 * */
	private long mEndTime;
	private long mTimeCounter;
	private double mWieght;
	private Hashtable<String,AtomicActivity>  mActivityList = new Hashtable<>();
	private Hashtable<String,ContextAttribute> mContextList = new Hashtable<>();
	
	/**
	 * Creates an Ongoing complex activity object with the given complex activity
	 * as a reference 
	 * @param mCA the reference Complex Activity.
	 * */
	public OnGoingCA(ComplexActivity mCA){
		this.mCA = mCA;
		this.setTime(mCA.getmLifespan());
		this.mName = mCA.getmName();
	}
	/**
	 * Passes an atomic activity to current activities List. If the atomic activity doesn't
	 * belong to this complex activity, it will just ignore it.
	 * @param mActivity the atomic activity to be added
	 * */
	public void newAtomicActivity(AtomicActivity mActivity){
		if(mCA.isAtomicActivity(mActivity)&&!mActivityList.containsKey(mActivity.getmName())){
			mActivity.setmWeight(mCA.getActivityWeight(mActivity.getmName()));
			mActivityList.put(mActivity.getmName(),mActivity);
			mWieght += mActivity.getmWeight()/2;
		}
	}
	/**
	 * Passes a context attribute to current contexts List. If the context attribute doesn't
	 * belong to this complex activity, it will just ignore it.
	 * @param mContext the context attribute to be added
	 * */
	public void newContextAttribute(ContextAttribute mContext){
		if(mCA.isContextAttribute(mContext)&&!mContextList.containsKey(mContext.getmName())){
			mContext.setmWeight(mCA.getContextWeight(mContext.getmName()));
			mContextList.put(mContext.getmName(),mContext);
			mWieght += mContext.getmWeight()/2;
		}
	}
	// It can handle CA without any end or core atomic attributes
	/**
	 * Checks if the complex activity is fulfilled 
	 * @return true if the complex activity fulfilled, false otherwise
	 * */
	public boolean fulfilled(){
		return(	coreAtomicActivityFullfilled() &&
				coreContextAttributeFulfilled()&&
				endAtomicActivityFulfilled()   &&
				endContextAttributeFulfilled() &&
				thresholdFulfilled());
	}
	/**
	 * Compares the current activities list with the complex activity core atomic activities list.
	 * @return true if all core activities fulfilled, false otherwise
	 * */
	private boolean coreAtomicActivityFullfilled(){
		return mActivityList.keySet().containsAll(mCA.getmCoreActivities().keySet());
	}
	/**
	 * Compares the current contexts list with the complex activity core context attributes list.
	 * @return true if all core contexts fulfilled, false otherwise
	 * */
	private boolean coreContextAttributeFulfilled(){
		return mContextList.keySet().containsAll(mCA.getmCoreContext().keySet());
	}
	/**
	 * Compares the current activities list with the complex activity end atomic activities list.
	 * @return true if all end activities fulfilled, false otherwise
	 * */
	private boolean endAtomicActivityFulfilled(){
		return mActivityList.keySet().containsAll(mCA.getmEndActivities().keySet());
	}
	/**
	 * Compares the current contexts list with the complex activity end context attributes list.
	 * @return true if all end contexts fulfilled, false otherwise
	 * */
	private boolean endContextAttributeFulfilled(){
		return mContextList.keySet().containsAll(mCA.getmEndContext().keySet());
	}
	/**
	 * Checks if the weight of the current atomic activities and context attributes fulfills 
	 * the complex activity threshold
	 * @return true if the threshold is been reached, false otherwise
	 * */
	private boolean thresholdFulfilled(){
		return mWieght >= mCA.getmThreshold();
	}
	/**
	 * Checks if the ongoing complex activity exceeded it's predefined time duration
	 * @return true if the complex activity is alive, false otherwise
	 * */
	public boolean isAlive() {
		return mTimeCounter > System.currentTimeMillis();
	}
	/**
	 * Sets the time duration in which this ongoing complex activity will 
	 * keep listing for new events
	 * @param mAliveTime complex activity life span in seconds
	 * 
	 * Also set the start time of activity
	 * */
	public void setTime(long mTimeCounter) {
		// Set start time of activity
		this.mStartTime = System.currentTimeMillis();
		
		/*
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		Date resultdate = new Date(yourmilliseconds);
		System.out.println(sdf.format(resultdate));
		*/
		
		mTimeCounter *= 1000L;
		mTimeCounter += this.mStartTime;
		this.mTimeCounter = mTimeCounter;
		
		
	}
	/**
	 * Returns the current atomic activities list
	 * @return a String with the current atomic activities names
	 * */
	public String getCurrentActivities(){
		return mActivityList.keySet().toString();
	}
	/**
	 * Returns the current context attributes list
	 * @return a String with the current context attributes names
	 * */
	public String getCurrentContext(){
		return mContextList.keySet().toString();
	}
	/**
	 * Returns the reference complex activity
	 * @return Reference complex activity object
	 * */
	public ComplexActivity getmCA() {
		return mCA;
	}
	/**
	 * Returns the current weight of the ongoing complex activity
	 * @return double value between 0 and 1
	 * */
	public double getmWieght(){
		return mWieght;
	}
	/**
	 * Returns name of the ongoing complex activity
	 * */
	public String getmName() {
		return mName;
	}
	
	public long getmStartTime() {
		return this.mStartTime;
	}
}