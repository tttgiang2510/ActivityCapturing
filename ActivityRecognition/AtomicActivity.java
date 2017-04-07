/**
 * This class represents an atomic activity. 
 * Each atomic activity has a name and a weight. 
 * The weight represent importance of the atomic activity when included in a CA
 * <p> To instantiate :
 * <p><b><code>AtomicActivity mActivity = new AtomicActivity(String mName, double mWeight);</code></b>
 * */
public class AtomicActivity {
	
	private String name;
	private double weight;
	/**
	 * Creates an Atomic Activity object with the given name
	 * @param name the Atomic Activity name.
	 * */
	public AtomicActivity(String name){
		this.name = name;
	}
	/**
	 * Creates an Atomic Activity object with the given name/weight
	 * @param name the Atomic Activity name.
	 * @param weight the Atomic Activity weight. 
	 * */
	public AtomicActivity(String name , double weight){
		this.name		= name;
		this.weight	= weight;
	}
	/**
	 * @return the Atomic Activity name
	 * */
	public String getName() {
		return name;
	}
	/**
	 * Sets the Atomic Activity name
	 * */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Returns the Atomic Activity weight.
	 * @return a double value between 0 and 1
	 * */
	public double getWeight() {
		return weight;
	}
	/**
	 * Sets the Atomic Activity weight.
	 * @param weight a double value between 0 and 1
	 * */
	public void setWeight(double weight) {
		this.weight = weight;
	}
}