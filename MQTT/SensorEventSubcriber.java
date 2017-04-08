import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class SensorEventSubcriber implements MqttCallback {
	// MongoDB
	MongoClient mongoClient 		= null;
	DB database 					= null; 
	DBCollection collection 		= null;

	MqttClient mqttClient;
	String[] subcriberTopics = null;
	
	/*
	 * Flags used for translating sensor events into Atomic Activity  
	 * and Context events
	 * */
	private long doorTimeCounter;
	private long motionTimeCounter;
	private Date startLightTime = null;
	private Date startOpenedWindowTime = null;
	private boolean doorActivated = false;	// either CLOSED or OPEN;
	private boolean doorClosed = false;
	private boolean motionOn = false;

	public SensorEventSubcriber() {
	}

	public static void main(String[] args) {
	    try {
			new SensorEventSubcriber().run();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() throws UnknownHostException {
	    try {
	    	
	    	// Connect to database
	    	mongoClient = new MongoClient(new MongoClientURI(Consts.MONGO_CLIENT_URI));
	    	database = mongoClient.getDB(Consts.DATABASE_NAME);
	    	collection = database.getCollection(Consts.COLLECTION_SENSOR_EVENTS);
	    	
			this.subcriberTopics = new String[5];
			subcriberTopics[0] = Consts.TOPIC_DOOR;
			subcriberTopics[1] = Consts.TOPIC_WINDOW;
			subcriberTopics[2] = Consts.TOPIC_MOTION;
			subcriberTopics[3] = Consts.TOPIC_SWITCH;
			subcriberTopics[4] = Consts.TOPIC_TWILIGHT;
			
			MqttConnectOptions options = new MqttConnectOptions();

	        mqttClient = new MqttClient("tcp://127.0.0.1:1883", "SensorEventSubcriber");
	        mqttClient.connect();
	        mqttClient.setCallback(this);
	        mqttClient.subscribe(subcriberTopics);
	    } catch (MqttException e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public void connectionLost(Throwable cause) {
	    // TODO Auto-generated method stub
		System.out.println("Connection lost!!! ::: " + cause.getMessage());
		// reconnect
			try {
				mqttClient.connect();
				mqttClient.setCallback(this);
				mqttClient.subscribe(this.subcriberTopics);
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// time out for doorActivated
		if (this.doorTimeCounter > System.currentTimeMillis()) {
			this.doorActivated = false;
		}
		// Date format
		SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_FORMAT);
		Date timestamp = new Date();
		byte[] payload = message.getPayload();
		String receivedMessage = new String(payload, "UTF-8");
		
		// Save sensor event to database 
		DBObject object = new BasicDBObject("timestamp", sdf.format(timestamp))
									.append("sensor", topic)
									.append("status", receivedMessage);
		collection.insert(object);
		
		System.out.println("--Inserted: " + object); 
		
		/* *
		 * Translate sensor data into activity and context events
		 * GetIn = (Door Sensor = activated & Motion Sensor = ON)
		 * GetOut = (Door Sensor = CLOSED & Motion Sensor = OFF)
		 * Working = Motion Sensor = ON for long period of time 
		 * */
		switch (topic) {
			case "motion":
				translateMotionData(receivedMessage);
				break;
			case "door":
				translateDoorData(receivedMessage);
				break;
			case "switch":
				translateSwitchData(receivedMessage);
				break;
			default:
				translateWindowData(receivedMessage);
				break;
		}
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
	    // TODO Auto-generated method stub
	
	}

	public void translateMotionData(String sensorStatus) 
			throws UnsupportedEncodingException, MqttPersistenceException, 
			MqttException {
		if (sensorStatus.equalsIgnoreCase("Motion_OFF")) {
			if (this.doorActivated && this.doorClosed) {
				// leaving the room 
				// send topic = activity & message = leaving
				MqttMessage message = new MqttMessage();
				String payload = Consts.A_LEAVING;
				message.setPayload(payload.getBytes("UTF-8"));
				mqttClient.publish(Consts.TOPIC_ACTIVITY, message);
			}
		} else {	// motion ON
			this.motionOn = true;
			if (this.doorActivated) {
				// entering the room
				// send topic = activity & message = entering
				MqttMessage message = new MqttMessage();
				String payload = Consts.A_ENTERING;
				message.setPayload(payload.getBytes("UTF-8"));
				mqttClient.publish(Consts.TOPIC_ACTIVITY, message);
			}
		}
		
	}
	
	public void translateDoorData(String sensorStatus) {
		this.doorActivated = true;
		// set time out for doorActivated = 1 min
		this.doorTimeCounter = System.currentTimeMillis() + 1000;
		
		if (sensorStatus.equalsIgnoreCase("Door_CLOSED")) {
			this.doorClosed = true;
		} else {
			this.doorClosed = false;
		}
	}
	
	public void translateSwitchData(String sensorStatus){
		if (sensorStatus.equalsIgnoreCase("Switch_ON")) {
			// track lightON start time
			this.startLightTime = new Date();
		} else {
			// light OFF, store to database
			if (this.startLightTime != null) {
				
				// Date format
				SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_FORMAT);
				
				// Save sensor event to database 
				DBObject object = new BasicDBObject("room", "7615.1")
											.append("activity", "leavingLightON")
											.append("startTime", sdf.format(this.startLightTime))
											.append("endTime", sdf.format(new Date()));
				DBCollection activityCollection = database.getCollection(Consts.COLLECTION_ACTIVITIES);
				activityCollection.insert(object);
				
				// release time tracking for light
				this.startLightTime = null;
				System.out.println("--Inserted into " + Consts.COLLECTION_ACTIVITIES + " : " + object); 
			}
		}
	}
	
	public void translateWindowData(String sensorStatus) {
		if (sensorStatus.equalsIgnoreCase("Window_OPEN")) {
			this.startOpenedWindowTime = new Date();
		} else {
			if (this.startOpenedWindowTime != null) {
				// Date format
				SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_FORMAT);
				
				// Save sensor event to database 
				DBObject object = new BasicDBObject("room", "7615.1")
											.append("activity", "leavingWindowOPEN")
											.append("startTime", sdf.format(this.startOpenedWindowTime))
											.append("endTime", sdf.format(new Date()));
				DBCollection activityCollection = database.getCollection(Consts.COLLECTION_ACTIVITIES);
				activityCollection.insert(object);
				
				// release time tracking for light
				this.startOpenedWindowTime = null;
				System.out.println("--Inserted into " + Consts.COLLECTION_ACTIVITIES + " : " + object); 
			}
		}
	}

}