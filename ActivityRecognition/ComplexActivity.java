import java.util.Hashtable;
/**
 * This class represents a complex activity. A complex activity is a collection of 
 * atomic activities and context attributes, has a life span and a threshold.
 * <p> To instantiate :
 * <p><b><code>ComplexActivity mCA = new ComplexActivity(String mName);</code></b>
 * <p> The activities and attributes are categorized in three categories. Start, End and Core.
 * Each of these categories has its own Hashtabel to be stored in and all the atomic activities and
 * context attributes should be add to mActivities/mContexts.
 * <p><b><code>AtomicActivity mActivity = new AtomicActivity(mName, mWeight); <br>
 *             mCA.addCoreActivity(mActivity);<br>
 *             mCA.addActivity(mActivity);</code></b>
 *  <p> Complex activity threshold is a double value between 0 and 1
 *  <p><b><code>mCA.setmThreshold(mThreshold);</code></b>
 *  <p> Complex activity life span is an int value represent the maximum time in seconds
 *  which this activity could be still going.
 *  <p><b><code>mCA.setmLifespan(mLifespan);</code></b>
 * */

public class ComplexActivity {

	private String mName;
	private double mThreshold = 0.0d;
	private int mLifespan;
	private Hashtable<String,AtomicActivity>   mStartActivities  = new Hashtable<>();
	private Hashtable<String,AtomicActivity>   mCoreActivities   = new Hashtable<>();
	private Hashtable<String,AtomicActivity>   mEndActivities    = new Hashtable<>();
	private Hashtable<String,AtomicActivity>   mActivities       = new Hashtable<>();
	private Hashtable<String,ContextAttribute> mStartContexts    = new Hashtable<>();
	private Hashtable<String,ContextAttribute> mCoreContexts     = new Hashtable<>();
	private Hashtable<String,ContextAttribute> mEndContexts      = new Hashtable<>();
	private Hashtable<String,ContextAttribute> mContexts         = new Hashtable<>();
	/**
	 * Creates a Complex Activity object with the given name
	 * @param mName the Atomic Activity name.
	 * */
	public ComplexActivity (String mName){
		this.mName = mName;
	}
	/**
	 * Adds an Atomic Activity to activities' list.
	 * @param mActivity Atomic Activity to be added
	 * */
	public void addActivity(AtomicActivity mActivity){
		mActivities.put(mActivity.getmName(), mActivity);
	}
	/**
	 * Adds an Atomic Activity to start activities' list.
	 * @param mActivity Atomic Activity to be added
	 * */
	public void addStartActivity(AtomicActivity mActivity){
		mStartActivities.put(mActivity.getmName(), mActivity);
	}
	/**
	 * Adds an Atomic Activity to end activities' list.
	 * @param mActivity Atomic Activity to be added
	 * */
	public void addEndActivity(AtomicActivity mActivity){
		mEndActivities.put(mActivity.getmName(), mActivity);
	}
	/**
	 * Adds an Atomic Activity to core activities' list.
	 * @param mActivity Atomic Activity to be added
	 * */
	public void addCoreActivity(AtomicActivity mActivity){
		mCoreActivities.put(mActivity.getmName(), mActivity);
	}
	/**
	 * Adds a Context Attribute to contexts' list.
	 * @param mContext Context Attribute to be added
	 * */
	public void addContext(ContextAttribute mContext){
		mContexts.put(mContext.getmName(), mContext);
	}
	/**
	 * Adds a Context Attribute to start contexts' list.
	 * @param mContext Context Attribute to be added
	 * */
	public void addStartContext(ContextAttribute mContext){
		mStartContexts.put(mContext.getmName(), mContext);
	}
	/**
	 * Adds a Context Attribute to end contexts' list.
	 * @param mContext Context Attribute to be added
	 * */
	public void addEndContext(ContextAttribute mContext){
		mEndContexts.put(mContext.getmName(), mContext);
	}
	/**
	 * Adds a Context Attribute to core contexts' list.
	 * @param mContext Context Attribute to be added
	 * */
	public void addCoreContext(ContextAttribute mContext){
		mCoreContexts.put(mContext.getmName(), mContext);
	}
	/**
	 * Returns the Atomic Activity weight or 0 if this Complex
	 * Activity contains no such an activity.
	 * @param mActivityName the atomic activity name
	 * @return the Atomic Activity's weight or 0 if the activity doesn't exist
	 * */
	public double getActivityWeight(String mActivityName){
		return mActivities.get(mActivityName).getmWeight();
	}
	/**
	 * Returns the Context Attribute weight or 0 if this Complex
	 * Activity contains no such a context.
	 * @param mContextName the Context Attribute name.
	 * @return the attribute's weight or 0 if the attribute doesn't exist
	 * */
	public double getContextWeight(String mContextName){
		return mContexts.get(mContextName).getmWeight();
	}
	/**
	 * Checks if a given Atomic Activity is contained in this Complex Activity
	 * activities' list
	 * @param mActivity the Atomic Activity.
	 * @return true if exist and false if it doesn't doesn't
	 * */
	public boolean isAtomicActivity(AtomicActivity mActivity){
		return mActivities.containsKey(mActivity.getmName());
	}
	/**
	 * Checks if a given Context Attribute is contained in this Complex Activity
	 * contexts' list
	 * @param mContext the Atomic Activity.
	 * @return true if exist and false if it doesn't doesn't
	 * */
	public boolean isContextAttribute(ContextAttribute mContext){
		return mContexts.containsKey(mContext.getmName());
	}
	/**
	 * Checks if a given Atomic Activity is contained in this Complex Activity
	 * start activities list
	 * @param mActivity the Atomic Activity.
	 * @return true if exist and false if it doesn't doesn't
	 * */
	public boolean isStartAtomicActivity(AtomicActivity mActivity){
			return mStartActivities.containsKey(mActivity.getmName());
	}
	/**
	 * Checks if a given Context Attribute is contained in this Complex Activity
	 * start contexts' list
	 * @param mContext the Atomic Activity.
	 * @return true if exist and false if it doesn't doesn't
	 * */
	public boolean isStartContextAttribute(ContextAttribute mContext){ 
			return mStartContexts.containsKey(mContext.getmName());
	}
	/**
	 * Returns the core Atomic Activity's list
	 * @return a Hashtable containing the core Atomic Activity list
	 * */
	public Hashtable<String,AtomicActivity> getmCoreActivities() {
		return mCoreActivities;
	}
	/**
	 * Returns the end Atomic Activity's list
	 * @return a Hashtable containing the end Atomic Activity list
	 * */
	public Hashtable<String,AtomicActivity> getmEndActivities() {
		return mEndActivities;
	}
	/**
	 * Returns the core Context Attribute's list
	 * @return a Hashtable containing the core Context Attribute list
	 * */
	public Hashtable<String,ContextAttribute> getmCoreContext() {
		return mCoreContexts;
	}
	/**
	 * Returns the end Context Attribute's list
	 * @return a Hashtable containing the end Context Attribute list
	 * */
	public Hashtable<String,ContextAttribute> getmEndContext() {
		return mEndContexts;
	}
	/**
	 * Sets the complex activity Threshold.
	 * @param mThreshold a double value between 0 and 1
	 * */
	public void setmThreshold(double mThreshold) {
		this.mThreshold = mThreshold;
	}
	/**
	 * Returns the complex activity threshold.
	 * @return a double value between 0 and 1
	 * */
	public double getmThreshold() {
		return mThreshold;
	}
	/**
	 * @return the Complex Activity name
	 * */
	public String getmName() {
		return mName;
	}
	/**
	 * Returns the Complex Activity life span in seconds.
	 * @return life span
	 * */
	public int getmLifespan() {
		return mLifespan;
	}
	/**
	 * Sets the Complex Activity life span.
	 * @param mLifespan Complex Activity life span in seconds
	 * */
	public void setmLifespan(int mLifespan) {
		this.mLifespan = mLifespan;
	}
}