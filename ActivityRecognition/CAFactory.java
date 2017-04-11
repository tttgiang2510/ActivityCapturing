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
	
	private ComplexActivity complexActivity;

	public static ComplexActivity workingInARoom(){
		if(workingInARoom == null){
			CAFactory factory = new CAFactory(Consts.CA_WORKING_IN_ROOM, 14400, 0.6);
			
/*			factory.addAtomicActivity(Consts.A_OPEN_DOOR,      0.1, true  , false , true  );
			factory.addAtomicActivity(Consts.A_CLOSE_DOOR,    	0.1, false , false , false );
			factory.addAtomicActivity(Consts.A_WALKING,        0.3, false , false , true );
			factory.addAtomicActivity(Consts.A_SITTING,		0.3, false , false , true );
			factory.addAtomicActivity(Consts.A_OPEN_DOOR, 		0.1, false, false, false);
			factory.addAtomicActivity(Consts.A_CLOSE_DOOR, 	0.1, false, true, false);*/
			
			factory.addAtomicActivity(Consts.A_ENTERING,      0.5, true  , false , true  );
			//factory.addAtomicActivity(Consts.A_SITTING,		0.3, false , false , true );
			factory.addAtomicActivity(Consts.A_LEAVING, 	0.5, false, true, true);
			
			factory.addContextAttribute(Consts.C_AT_WORKINGROOM       , 0.6, false, false , true);
			factory.addContextAttribute(Consts.C_WORKINGROOM_LIGHT_ON , 0.4, false, false , false);
			
			workingInARoom = factory.getmCA();
			return workingInARoom;
		}
		return workingInARoom;
	}
	
	public static ComplexActivity presenting(){
		if(presenting == null){

			CAFactory factory = new CAFactory(Consts.CA_PRESENTING, 7200, 0.7);
			
			factory.addAtomicActivity(Consts.A_OPEN_DOOR,  		0.1, true,	false, 	false);
			factory.addAtomicActivity(Consts.A_CLOSE_DOOR,			0.1, false, false, 	false);
			factory.addAtomicActivity(Consts.A_USING_PROJECTOR, 	0.3, false, false, 	true);
			factory.addAtomicActivity(Consts.A_SPEAKING, 			0.3, false, false, 	true);
			factory.addAtomicActivity(Consts.A_WALKING,         	0.05, false, false, 	false);
			factory.addAtomicActivity(Consts.A_OPEN_DOOR, 			0.05, false, false, 	false);
			factory.addAtomicActivity(Consts.A_CLOSE_DOOR, 		0.1, false, true,	false);

			
			factory.addContextAttribute(Consts.C_AT_MEETINGROOM       	, 0.4, true, 	false , true);
			factory.addContextAttribute(Consts.C_MEETINGROOM_LIGHT_ON 	, 0.2, true , 	false , false);
			factory.addContextAttribute(Consts.C_ABSENCE_MEETINGROOM	, 0.4, false, 	true, 	false);
			
			presenting = factory.getmCA();
			return presenting;
		}
		return presenting;
	}
	
	public static ComplexActivity meeting() {
		if(meeting == null) {
			CAFactory factory = new CAFactory(Consts.CA_MEETING, 7200, 0.6);
			
			factory.addAtomicActivity(Consts.A_OPEN_DOOR, 	0.1, true, 	false, 	false);
			factory.addAtomicActivity(Consts.A_CLOSE_DOOR,	0.1, false, false, 	false);
			factory.addAtomicActivity(Consts.A_SPEAKING, 	0.5, false, false, 	true);
			//mFactory.addAtomicActivity(Consts.A_WALKING, 	0.3, false, false, 	true);
			factory.addAtomicActivity(Consts.A_OPEN_DOOR, 	0.1, false, false,	false);
			factory.addAtomicActivity(Consts.A_CLOSE_DOOR, 0.1, false, true,	false);
			
			factory.addContextAttribute(Consts.C_AT_MEETINGROOM       , 0.4, false, false , true);
			factory.addContextAttribute(Consts.C_MEETINGROOM_LIGHT_ON , 0.2, true , false , false);
			factory.addContextAttribute(Consts.C_ABSENCE_MEETINGROOM	, 0.4, false, 	true, 	false);
			
			meeting = factory.getmCA();
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
	 * @param name The complex activity name
	 * @param lifespan the complex activity life span in seconds
	 * @param threshold a double value between 0 and 1 
	 * */
	private CAFactory(String name, int lifespan , double threshold){
		complexActivity = new ComplexActivity(name);
		complexActivity.setLifespan(lifespan);
		complexActivity.setThreshold(threshold);
	}
	/**
	 * Adds an atomic activity to the complex activity
	 * @param name Atomic activity name
	 * @param weight Atomic activity weight
	 * @param isStartAtomicActivity true if this atomic activity is a start activity
	 * @param isEndAtomicActivity true if this atomic activity is an end activity
	 * @param isCoreAtomicActivity true if this atomic activity is a core atomic activity 
	 * */
	private void addAtomicActivity(String name, double weight,
			boolean isStartAtomicActivity, boolean isEndAtomicActivity, boolean isCoreAtomicActivity){
		
		AtomicActivity activity = new AtomicActivity(name, weight);
		complexActivity.addActivity(activity);
		
		if(isStartAtomicActivity){complexActivity.addStartActivity(activity);}
		if(isEndAtomicActivity  ){complexActivity.addEndActivity  (activity);}
		if(isCoreAtomicActivity ){complexActivity.addCoreActivity (activity);}
		if(isStartAtomicActivity&&isEndAtomicActivity){System.out.println("Can't be start and end at the same time");}
	}
	/**
	 * Adds an context attribute to the complex activity
	 * @param name Context attribute name
	 * @param weight Context attribute weight
	 * @param isStartContextAttribute true if this context attribute is a start context
	 * @param isEndContextAttribute true if this context attribute is an end context
	 * @param isCoreContextAttribute true if this context attribute is a core context attribute 
	 * */
	private void addContextAttribute(String name, double weight,
			boolean isStartContextAttribute, boolean isEndContextAttribute, boolean isCoreContextAttribute){
		ContextAttribute context = new ContextAttribute(name, weight);
		complexActivity.addContext(context);
		
		if(isStartContextAttribute){complexActivity.addStartContext(context);}
		if(isEndContextAttribute  ){complexActivity.addEndContext  (context);}
		if(isCoreContextAttribute ){complexActivity.addCoreContext (context);}
		if(isStartContextAttribute&&isEndContextAttribute){System.out.println("Can't be start and end at the same time");}
	}
	private ComplexActivity getmCA(){
		return complexActivity;
	}
}