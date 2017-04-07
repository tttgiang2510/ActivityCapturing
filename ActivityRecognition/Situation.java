import java.util.Hashtable;

public class Situation {
	
	@SuppressWarnings("unused")
	private String mName;
	private Hashtable<String,ComplexActivity> mCA = new Hashtable<>();
	/**
	 * Creates a Situation object with the given name
	 * @param mName the Situation's name.
	 * */
	public Situation(String mName){
		this.mName = mName;
	}
	/**
	 * Adds a Complex Activity to this Situation
	 * @param mComplexActivity the Complex Activity to be added
	 * */
	public void addComplexActivity(ComplexActivity mComplexActivity){
		mCA.put(mComplexActivity.getName(), mComplexActivity);
	}
	/**
	 * @return Hashtable contains this situation Complex Activity list
	 * */
	public Hashtable<String,ComplexActivity> getmCAList() {
		return mCA;
	}
}