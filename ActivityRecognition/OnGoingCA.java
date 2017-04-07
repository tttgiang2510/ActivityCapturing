import java.text.SimpleDateFormat;
import java.util.Hashtable;

/**
 * This class represents a tracked complex activity. When a complex activity is
 * detected from a start atomic activity or context attribute, the algorithm 
 * initiate a new object from this class and passes the static complex 
 * activity as a reference.
 * <p><b><code>OnGoingCA onGoingCA = new OnGoingCA(ComplexActivity complexActivity);</code></b>
 * <p>The algorithm can pass new activities 
 * <p><b><code>mOnGoingCA.newAtomicActivity(activity);<br></code></b>
 * <p>The algorithm can check if the activity is alive and fulfilled
 * <p><b><code>	boolean fulfilled  = onGoingCA.fulfilled();<br>
 * <p><b><code> boolean isAlive = onGoingCA.isAlive();</code></b>
 * */

public class OnGoingCA {
	
	private ComplexActivity complexActivity;
	private String name;
	/*
	 * Start time of the activity
	 * */
	private long startTime;
	/*
	 * End time of the activity
	 * */
	private long endTime;
	private long timeCounter;
	private double wieght;
	private Hashtable<String,AtomicActivity>  atomicActivityList = new Hashtable<>();
	private Hashtable<String,ContextAttribute> contextList = new Hashtable<>();
	
	/**
	 * Creates an Ongoing complex activity object with the given complex activity
	 * as a reference 
	 * @param mCA the reference Complex Activity.
	 * */
	public OnGoingCA(ComplexActivity complexActivity){
		this.complexActivity = complexActivity;
		this.setTime(complexActivity.getLifespan());
		this.name = complexActivity.getName();
	}
	/**
	 * Passes an atomic activity to current activities List. If the atomic activity doesn't
	 * belong to this complex activity, it will just ignore it.
	 * @param atomicActivity the atomic activity to be added
	 * */
	public void newAtomicActivity(AtomicActivity atomicActivity){
		if(complexActivity.isAtomicActivity(atomicActivity)&&!atomicActivityList.containsKey(atomicActivity.getName())){
			atomicActivity.setWeight(complexActivity.getActivityWeight(atomicActivity.getName()));
			atomicActivityList.put(atomicActivity.getName(),atomicActivity);
			wieght += atomicActivity.getWeight()/2;
		}
	}
	/**
	 * Passes a context attribute to current contexts List. If the context attribute doesn't
	 * belong to this complex activity, it will just ignore it.
	 * @param context the context attribute to be added
	 * */
	public void newContextAttribute(ContextAttribute context){
		if(complexActivity.isContextAttribute(context)&&!contextList.containsKey(context.getName())){
			context.setmWeight(complexActivity.getContextWeight(context.getName()));
			contextList.put(context.getName(),context);
			wieght += context.getWeight()/2;
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
		return atomicActivityList.keySet().containsAll(complexActivity.getCoreActivities().keySet());
	}
	/**
	 * Compares the current contexts list with the complex activity core context attributes list.
	 * @return true if all core contexts fulfilled, false otherwise
	 * */
	private boolean coreContextAttributeFulfilled(){
		return contextList.keySet().containsAll(complexActivity.getCoreContext().keySet());
	}
	/**
	 * Compares the current activities list with the complex activity end atomic activities list.
	 * @return true if all end activities fulfilled, false otherwise
	 * */
	private boolean endAtomicActivityFulfilled(){
		return atomicActivityList.keySet().containsAll(complexActivity.getEndActivities().keySet());
	}
	/**
	 * Compares the current contexts list with the complex activity end context attributes list.
	 * @return true if all end contexts fulfilled, false otherwise
	 * */
	private boolean endContextAttributeFulfilled(){
		return contextList.keySet().containsAll(complexActivity.getEndContext().keySet());
	}
	/**
	 * Checks if the weight of the current atomic activities and context attributes fulfills 
	 * the complex activity threshold
	 * @return true if the threshold is been reached, false otherwise
	 * */
	private boolean thresholdFulfilled(){
		return wieght >= complexActivity.getThreshold();
	}
	/**
	 * Checks if the ongoing complex activity exceeded it's predefined time duration
	 * @return true if the complex activity is alive, false otherwise
	 * */
	public boolean isAlive() {
		return timeCounter > System.currentTimeMillis();
	}
	/**
	 * Sets the time duration in which this ongoing complex activity will 
	 * keep listing for new events
	 * @param mAliveTime complex activity life span in seconds
	 * 
	 * Also set the start time of activity
	 * */
	public void setTime(long timeCounter) {
		// Set start time of activity
		this.startTime = System.currentTimeMillis();
		
		/*
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		Date resultdate = new Date(yourmilliseconds);
		System.out.println(sdf.format(resultdate));
		*/
		
		timeCounter *= 1000L;
		timeCounter += this.startTime;
		this.timeCounter = timeCounter;
		
		
	}
	/**
	 * Returns the current atomic activities list
	 * @return a String with the current atomic activities names
	 * */
	public String getCurrentActivities(){
		return atomicActivityList.keySet().toString();
	}
	/**
	 * Returns the current context attributes list
	 * @return a String with the current context attributes names
	 * */
	public String getCurrentContext(){
		return contextList.keySet().toString();
	}
	/**
	 * Returns the reference complex activity
	 * @return Reference complex activity object
	 * */
	public ComplexActivity getComplexActivity() {
		return complexActivity;
	}
	/**
	 * Returns the current weight of the ongoing complex activity
	 * @return double value between 0 and 1
	 * */
	public double getWieght(){
		return wieght;
	}
	/**
	 * Returns name of the ongoing complex activity
	 * */
	public String getName() {
		return name;
	}
	
	public long getStartTime() {
		return this.startTime;
	}
}