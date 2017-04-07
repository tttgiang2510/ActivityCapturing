import java.util.Hashtable;
/**
 * This class represents a complex activity. A complex activity is a collection of 
 * atomic activities and context attributes, has a life span and a threshold.
 * <p> To instantiate :
 * <p><b><code>ComplexActivity complexActivity = new ComplexActivity(String name);</code></b>
 * <p> The activities and attributes are categorized in three categories. Start, End and Core.
 * Each of these categories has its own Hashtable to be stored in and all the atomic activities and
 * context attributes should be add to activities/contexts.
 * <p><b><code>AtomicActivity activity = new AtomicActivity(name, weight); <br>
 *             mCA.addCoreActivity(activity);<br>
 *             mCA.addActivity(activity);</code></b>
 *  <p> Complex activity threshold is a double value between 0 and 1
 *  <p><b><code>mCA.setThreshold(threshold);</code></b>
 *  <p> Complex activity life span is an int value represent the maximum time in seconds
 *  which this activity could be still going.
 *  <p><b><code>mCA.setLifespan(lifespan);</code></b>
 * */

public class ComplexActivity {

	private String name;
	private double threshold = 0.0d;
	private int lifespan;
	private Hashtable<String,AtomicActivity>   startActivities  = new Hashtable<>();
	private Hashtable<String,AtomicActivity>   coreActivities   = new Hashtable<>();
	private Hashtable<String,AtomicActivity>   endActivities    = new Hashtable<>();
	private Hashtable<String,AtomicActivity>   activities       = new Hashtable<>();
	private Hashtable<String,ContextAttribute> startContexts    = new Hashtable<>();
	private Hashtable<String,ContextAttribute> coreContexts     = new Hashtable<>();
	private Hashtable<String,ContextAttribute> endContexts      = new Hashtable<>();
	private Hashtable<String,ContextAttribute> contexts         = new Hashtable<>();
	/**
	 * Creates a Complex Activity object with the given name
	 * @param name the Atomic Activity name.
	 * */
	public ComplexActivity (String name){
		this.name = name;
	}
	/**
	 * Adds an Atomic Activity to activities' list.
	 * @param activity Atomic Activity to be added
	 * */
	public void addActivity(AtomicActivity activity){
		activities.put(activity.getName(), activity);
	}
	/**
	 * Adds an Atomic Activity to start activities' list.
	 * @param activity Atomic Activity to be added
	 * */
	public void addStartActivity(AtomicActivity activity){
		startActivities.put(activity.getName(), activity);
	}
	/**
	 * Adds an Atomic Activity to end activities' list.
	 * @param activity Atomic Activity to be added
	 * */
	public void addEndActivity(AtomicActivity activity){
		endActivities.put(activity.getName(), activity);
	}
	/**
	 * Adds an Atomic Activity to core activities' list.
	 * @param activity Atomic Activity to be added
	 * */
	public void addCoreActivity(AtomicActivity activity){
		coreActivities.put(activity.getName(), activity);
	}
	/**
	 * Adds a Context Attribute to contexts' list.
	 * @param context Context Attribute to be added
	 * */
	public void addContext(ContextAttribute context){
		contexts.put(context.getName(), context);
	}
	/**
	 * Adds a Context Attribute to start contexts' list.
	 * @param context Context Attribute to be added
	 * */
	public void addStartContext(ContextAttribute context){
		startContexts.put(context.getName(), context);
	}
	/**
	 * Adds a Context Attribute to end contexts' list.
	 * @param context Context Attribute to be added
	 * */
	public void addEndContext(ContextAttribute context){
		endContexts.put(context.getName(), context);
	}
	/**
	 * Adds a Context Attribute to core contexts' list.
	 * @param context Context Attribute to be added
	 * */
	public void addCoreContext(ContextAttribute context){
		coreContexts.put(context.getName(), context);
	}
	/**
	 * Returns the Atomic Activity weight or 0 if this Complex
	 * Activity contains no such an activity.
	 * @param activityName the atomic activity name
	 * @return the Atomic Activity's weight or 0 if the activity doesn't exist
	 * */
	public double getActivityWeight(String activityName){
		return activities.get(activityName).getWeight();
	}
	/**
	 * Returns the Context Attribute weight or 0 if this Complex
	 * Activity contains no such a context.
	 * @param contextName the Context Attribute name.
	 * @return the attribute's weight or 0 if the attribute doesn't exist
	 * */
	public double getContextWeight(String contextName){
		return contexts.get(contextName).getWeight();
	}
	/**
	 * Checks if a given Atomic Activity is contained in this Complex Activity
	 * activities' list
	 * @param activity the Atomic Activity.
	 * @return true if exist and false if it doesn't doesn't
	 * */
	public boolean isAtomicActivity(AtomicActivity activity){
		return activities.containsKey(activity.getName());
	}
	/**
	 * Checks if a given Context Attribute is contained in this Complex Activity
	 * contexts' list
	 * @param context the Atomic Activity.
	 * @return true if exist and false if it doesn't doesn't
	 * */
	public boolean isContextAttribute(ContextAttribute context){
		return contexts.containsKey(context.getName());
	}
	/**
	 * Checks if a given Atomic Activity is contained in this Complex Activity
	 * start activities list
	 * @param activity the Atomic Activity.
	 * @return true if exist and false if it doesn't doesn't
	 * */
	public boolean isStartAtomicActivity(AtomicActivity activity){
			return startActivities.containsKey(activity.getName());
	}
	/**
	 * Checks if a given Context Attribute is contained in this Complex Activity
	 * start contexts' list
	 * @param context the Atomic Activity.
	 * @return true if exist and false if it doesn't doesn't
	 * */
	public boolean isStartContextAttribute(ContextAttribute context){ 
			return startContexts.containsKey(context.getName());
	}
	/**
	 * Returns the core Atomic Activity's list
	 * @return a Hashtable containing the core Atomic Activity list
	 * */
	public Hashtable<String,AtomicActivity> getCoreActivities() {
		return coreActivities;
	}
	/**
	 * Returns the end Atomic Activity's list
	 * @return a Hashtable containing the end Atomic Activity list
	 * */
	public Hashtable<String,AtomicActivity> getEndActivities() {
		return endActivities;
	}
	/**
	 * Returns the core Context Attribute's list
	 * @return a Hashtable containing the core Context Attribute list
	 * */
	public Hashtable<String,ContextAttribute> getCoreContext() {
		return coreContexts;
	}
	/**
	 * Returns the end Context Attribute's list
	 * @return a Hashtable containing the end Context Attribute list
	 * */
	public Hashtable<String,ContextAttribute> getEndContext() {
		return endContexts;
	}
	/**
	 * Sets the complex activity Threshold.
	 * @param threshold a double value between 0 and 1
	 * */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	/**
	 * Returns the complex activity threshold.
	 * @return a double value between 0 and 1
	 * */
	public double getThreshold() {
		return threshold;
	}
	/**
	 * @return the Complex Activity name
	 * */
	public String getName() {
		return name;
	}
	/**
	 * Returns the Complex Activity life span in seconds.
	 * @return life span
	 * */
	public int getLifespan() {
		return lifespan;
	}
	/**
	 * Sets the Complex Activity life span.
	 * @param lifespan Complex Activity life span in seconds
	 * */
	public void setLifespan(int lifespan) {
		this.lifespan = lifespan;
	}
}