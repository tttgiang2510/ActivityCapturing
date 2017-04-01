/**
 * This is a helper class, make it easier to create and
 * get use complex activities.
 * */
public class CAFactory {
	private static ComplexActivity workingInARoom  	= null; 
	private static ComplexActivity presenting 		= null;
	private static ComplexActivity meeting 			= null;
	
	private static Situation atWorkingRoom  		= null;
	private static Situation atMeetingRoom 			= null;
	
	private ComplexActivity mCA;

	public static ComplexActivity workingInARoom(){
		if(workingInARoom == null){
			CAFactory mFactory = new CAFactory(Consts.CA_WORKING_IN_ROOM, 10800, 0.6);
			
			mFactory.addAtomicActivity(Consts.A_OPEN_DOOR,      0.1, true  , false , true  );
			mFactory.addAtomicActivity(Consts.A_CLOSE_DOOR,    	0.1, false , false , false );
			mFactory.addAtomicActivity(Consts.A_WALKING,        0.3, false , false , true );
			mFactory.addAtomicActivity(Consts.A_SITTING,		0.3, false , false , true );
			mFactory.addAtomicActivity(Consts.A_OPEN_DOOR, 		0.1, false, false, false);
			mFactory.addAtomicActivity(Consts.A_CLOSE_DOOR, 	0.1, false, true, false);
			
			mFactory.addContextAttribute(Consts.C_AT_WORKINGROOM       , 0.6, false, false , false);
			mFactory.addContextAttribute(Consts.C_WORKINGROOM_LIGHT_ON , 0.4, false, false , true);
			
			workingInARoom = mFactory.getmCA();
			return workingInARoom;
		}
		return workingInARoom;
	}
	
	public static ComplexActivity presenting(){
		if(presenting == null){

			CAFactory mFactory = new CAFactory(Consts.CA_PRESENTING, 7200, 0.7);
			
			mFactory.addAtomicActivity(Consts.A_OPEN_DOOR,  		0.1, true,	false, 	false);
			mFactory.addAtomicActivity(Consts.A_CLOSE_DOOR,			0.1, false, false, 	false);
			mFactory.addAtomicActivity(Consts.A_USING_PROJECTOR, 	0.3, false, false, 	true);
			mFactory.addAtomicActivity(Consts.A_SPEAKING, 			0.3, false, false, 	true);
			mFactory.addAtomicActivity(Consts.A_WALKING,         	0.05, false, false, 	false);
			mFactory.addAtomicActivity(Consts.A_OPEN_DOOR, 			0.05, false, false, 	false);
			mFactory.addAtomicActivity(Consts.A_CLOSE_DOOR, 		0.1, false, true,	false);

			
			mFactory.addContextAttribute(Consts.C_AT_MEETINGROOM       	, 0.4, true, 	false , true);
			mFactory.addContextAttribute(Consts.C_MEETINGROOM_LIGHT_ON 	, 0.2, true , 	false , false);
			mFactory.addContextAttribute(Consts.C_ABSENCE_MEETINGROOM	, 0.4, false, 	true, 	false);
			
			presenting = mFactory.getmCA();
			return presenting;
		}
		return presenting;
	}
	
	public static ComplexActivity meeting() {
		if(meeting == null) {
			CAFactory mFactory = new CAFactory(Consts.CA_MEETING, 7200, 0.6);
			
			mFactory.addAtomicActivity(Consts.A_OPEN_DOOR, 	0.1, true, 	false, 	false);
			mFactory.addAtomicActivity(Consts.A_CLOSE_DOOR,	0.1, false, false, 	false);
			mFactory.addAtomicActivity(Consts.A_SPEAKING, 	0.5, false, false, 	true);
			//mFactory.addAtomicActivity(Consts.A_WALKING, 	0.3, false, false, 	true);
			mFactory.addAtomicActivity(Consts.A_OPEN_DOOR, 	0.1, false, false,	false);
			mFactory.addAtomicActivity(Consts.A_CLOSE_DOOR, 0.1, false, true,	false);
			
			mFactory.addContextAttribute(Consts.C_AT_MEETINGROOM       , 0.4, false, false , true);
			mFactory.addContextAttribute(Consts.C_MEETINGROOM_LIGHT_ON , 0.2, true , false , false);
			mFactory.addContextAttribute(Consts.C_ABSENCE_MEETINGROOM	, 0.4, false, 	true, 	false);
			
			meeting = mFactory.getmCA();
			return meeting;
		}
		return meeting;
	}
	
	public static Situation atMeetingRoom(){
		if(atMeetingRoom == null){
			atMeetingRoom = new Situation(Consts.S_AT_MEETING_ROOM);
			atMeetingRoom.addComplexActivity(CAFactory.presenting());
			atMeetingRoom.addComplexActivity(CAFactory.meeting());
			return atMeetingRoom;
		}
		return atMeetingRoom;
	}
	
	public static Situation atWorkingRoom() {
		if(atWorkingRoom == null){
			atWorkingRoom = new Situation(Consts.S_AT_WORKING_ROOM);
			atWorkingRoom.addComplexActivity(CAFactory.workingInARoom());
			return atWorkingRoom;
		}
		return atWorkingRoom;
	}
	/**
	 * Creates a new CAFactory object with the given name, life span and threshold
	 * @param mName The complex activity name
	 * @param mLifespan the complex activity life span in seconds
	 * @param mThreshold a double value between 0 and 1 
	 * */
	private CAFactory(String mName, int mLifespan , double mThreshold){
		mCA = new ComplexActivity(mName);
		mCA.setmLifespan(mLifespan);
		mCA.setmThreshold(mThreshold);
	}
	/**
	 * Adds an atomic activity to the complex activity
	 * @param mName Atomic activity name
	 * @param mWeight Atomic activity weight
	 * @param isStartAtomicActivity true if this atomic activity is a start activity
	 * @param isEndAtomicActivity true if this atomic activity is an end activity
	 * @param isCoreAtomicActivity true if this atomic activity is a core atomic activity 
	 * */
	private void addAtomicActivity(String mName, double mWeight,
			boolean isStartAtomicActivity, boolean isEndAtomicActivity, boolean isCoreAtomicActivity){
		
		AtomicActivity mActivity = new AtomicActivity(mName, mWeight);
		mCA.addActivity(mActivity);
		
		if(isStartAtomicActivity){mCA.addStartActivity(mActivity);}
		if(isEndAtomicActivity  ){mCA.addEndActivity  (mActivity);}
		if(isCoreAtomicActivity ){mCA.addCoreActivity (mActivity);}
		if(isStartAtomicActivity&&isEndAtomicActivity){System.out.println("Can't be start and end at the same time");}
	}
	/**
	 * Adds an context attribute to the complex activity
	 * @param mName Context attribute name
	 * @param mWeight Context attribute weight
	 * @param isStartContextAttribute true if this context attribute is a start context
	 * @param isEndContextAttribute true if this context attribute is an end context
	 * @param isCoreContextAttribute true if this context attribute is a core context attribute 
	 * */
	private void addContextAttribute(String mName, double mWeight,
			boolean isStartContextAttribute, boolean isEndContextAttribute, boolean isCoreContextAttribute){
		ContextAttribute mContext = new ContextAttribute(mName, mWeight);
		mCA.addContext(mContext);
		
		if(isStartContextAttribute){mCA.addStartContext(mContext);}
		if(isEndContextAttribute  ){mCA.addEndContext  (mContext);}
		if(isCoreContextAttribute ){mCA.addCoreContext (mContext);}
		if(isStartContextAttribute&&isEndContextAttribute){System.out.println("Can't be start and end at the same time");}
	}
	private ComplexActivity getmCA(){
		return mCA;
	}
}