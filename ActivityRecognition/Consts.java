public class Consts {
	
	private Consts(){}
	
	// Atomic Activities A_
	public static final String A_OPEN_DOOR				= "open_door";
	public static final String A_CLOSE_DOOR				= "close_door";
	public static final String A_SITTING                = "sitting";
	public static final String A_WALKING                = "walking";
	public static final String A_SPEAKING  				= "speaking";
	public static final String A_OPEN_CUPBOARD    	    = "open_cupboard";
	public static final String A_OPEN_FRIDGE			= "open_fridge";
	public static final String A_USING_PROJECTOR		= "using_projector";
	public static final String A_USING_COMPUTER			= "using_computer";
	public static final String A_COFFEE_MACHINE         = "coffee_machine";
	
	// Context Attributes C_
	public static final String C_AT_WORKINGROOM         = "at_workingroom";
	public static final String C_AT_KITCHEN             = "at_kitchen";
	public static final String C_AT_MEETINGROOM			= "at_meetingroom";
	public static final String C_ABSENCE_MEETINGROOM	= "absence_meetingroom";
	public static final String C_WORKINGROOM_LIGHT_ON   = "workingroom_light_on";
	public static final String C_KITCHEN_LIGHT_ON       = "kitchen_light_on";
	public static final String C_MEETINGROOM_LIGHT_ON	= "meetingroom_light_on";
	
	// Complex Activity CA_
	public static final String CA_WORKING_IN_ROOM       = "working_in_room";
	public static final String CA_HAVING_COFFEEBREAK	= "having_coffee_break";
	public static final String CA_PRESENTING          	= "presenting";
	public static final String CA_MEETING               = "meeting";
	
	// Situation S_
	public static final String S_AT_MEETING_ROOM        = "at_meeting_room";
	public static final String S_AT_WORKING_ROOM        = "at_working_room";
	public static final String S_AT_KITCHEN				= "at_kitchen";
	
	// JSON Keys
	public static final String ACTIVITY                 = "activity";
	public static final String CONTEXT                  = "context";
	
	// MQTT
	public static final String LOCALHOST                = "localhost";
	public static final String TOPIC_ACTIVITY			= "activity";
	public static final String TOPIC_CONTEXT			= "context";
	public static final String TOPIC_SENSOR_DATA        = "sensor";
	
	public static final String TOPIC_DOOR        		= "door";
	public static final String TOPIC_WINDOW        		= "window";
	public static final String TOPIC_MOTION        		= "motion";
	public static final String TOPIC_SWITCH        		= "switch";
	public static final String TOPIC_TWILIGHT        	= "twilight";
}