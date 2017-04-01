/**
 * This class represent an atomic activity. Each atomic activity has a name and a
 * weight. The weight represent importance of the atomic activity when included in 
 * a complex activity.
 * <p> To instantiate :
 * <p><b><code>AtomicActivity mActivity = new AtomicActivity(String mName, double mWeight);</code></b>
 * */
public class AtomicActivity {
	
	private String mName;
	private double mWeight;
	/**
	 * Creates an Atomic Activity object with the given name
	 * @param mName the Atomic Activity name.
	 * */
	public AtomicActivity(String mName){
		this.mName = mName;
	}
	/**
	 * Creates an Atomic Activity object with the given name/weight
	 * @param mName the Atomic Activity name.
	 * @param mWeight the Atomic Activity weight. 
	 * */
	public AtomicActivity(String mName , double mWeight){
		this.mName		= mName;
		this.mWeight	= mWeight;
	}
	/**
	 * @return the Atomic Activity name
	 * */
	public String getmName() {
		return mName;
	}
	/**
	 * Sets the Atomic Activity name
	 * */
	public void setmName(String mName) {
		this.mName = mName;
	}
	/**
	 * Returns the Atomic Activity weight.
	 * @return a double value between 0 and 1
	 * */
	public double getmWeight() {
		return mWeight;
	}
	/**
	 * Sets the Atomic Activity weight.
	 * @param mWeight a double value between 0 and 1
	 * */
	public void setmWeight(double mWeight) {
		this.mWeight = mWeight;
	}
}