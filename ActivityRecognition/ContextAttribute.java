/**
 * This class represent a context attribute. Each context attribute has a name and a
 * weight. The weight represent importance of the context attribute when included in 
 * a complex activity.
 * <p> To instantiate :
 * <p><b><code>ContextAttribute mContext = new ContextAttribute(String mName, double mWeight);</code></b>
 * 
 * */
public class ContextAttribute {
	
	private String mName;
	private double mWeight;

	/**
	 * Creates an Context Attribute object with the given name
	 * @param mName the Context Attribute name.
	 * */
	public ContextAttribute(String mName){
		this.mName = mName;
	}
	/**
	 * Creates an Context Attribute object with the given name/weight
	 * @param mName the Context Attribute name.
	 * @param mWeight the Context Attribute weight. 
	 * */
	public ContextAttribute (String mName, double mWeight){
		this.mName 		= mName;
		this.mWeight 	= mWeight;
	}
	/**
	 * @return the Context Attribute name
	 * */
	public String getName() {
		return mName;
	}
	/**
	 * Sets the Context Attribute name
	 * @param mName the contexts's name
	 * */
	public void setmName(String mName) {
		this.mName = mName;
	}
	/**
	 * Returns the Context Attribute weight.
	 * @return a double value between 0 and 1
	 * */
	public double getWeight() {
		return mWeight;
	}
	/**
	 * Sets the Context Attribute weight.
	 * @param mWeight a double value between 0 and 1
	 * */
	public void setmWeight(double mWeight) {
		this.mWeight = mWeight;
	}
}
