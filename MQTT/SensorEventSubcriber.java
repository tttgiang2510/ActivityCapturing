import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

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
	
	// doorActivated = true if door is recently CLOSED/OPEN
	private boolean doorActivated = false;	
	private boolean doorClosed = false;
	
	// flag motion_OFF to check user availability in case user is not moving
	private boolean motionOff = false;

	public SensorEventSubcriber() {
	}
	
	public SensorEventSubcriber(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	public void run() throws UnknownHostException {
	    try {
	    	
	    	// Connect to database
	    	if (this.mongoClient == null) {
	    		this.mongoClient = new MongoClient(new MongoClientURI(Consts.MONGO_CLIENT_URI));
	    	}
	    	this.database = mongoClient.getDB(Consts.DATABASE_NAME);
	    	this.collection = database.getCollection(Consts.COLLECTION_SENSOR_EVENTS);
	    	
			this.subcriberTopics = new String[5];
			subcriberTopics[0] = Consts.TOPIC_DOOR;
			subcriberTopics[1] = Consts.TOPIC_WINDOW;
			subcriberTopics[2] = Consts.TOPIC_MOTION;
			subcriberTopics[3] = Consts.TOPIC_SWITCH;
			subcriberTopics[4] = Consts.TOPIC_TWILIGHT;
			
			int[] qos = {2, 2, 2, 2, 2};

	        mqttClient = new MqttClient("tcp://127.0.0.1:1883", "SensorEventSubcriber");
	        mqttClient.connect();
	        mqttClient.setCallback(this);
	        mqttClient.subscribe(subcriberTopics, qos);
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
		// Date format
		SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_FORMAT);
		Date timestamp = new Date();
				
		// time out for doorActivated
		System.out.println("---" + sdf.format(System.currentTimeMillis()) 
				+ " - doorActivated = " + this.doorActivated 
				+ " till : " + sdf.format(this.doorTimeCounter));
		System.out.println("---:::" + sdf.format(System.currentTimeMillis()) 
		+ " - doorClosed = " + this.doorClosed);
		if (System.currentTimeMillis() > this.doorTimeCounter) {
			this.doorActivated = false;
		}
		
		
		byte[] payload = message.getPayload();
		String receivedMessage = new String(payload, "UTF-8");
		
		// Save sensor event to database 
		DBObject object = new BasicDBObject("timestamp", sdf.format(timestamp))
									.append("sensor", topic)
									.append("status", receivedMessage);
		collection.insert(object);
		
		//System.out.println("--Inserted: " + object); 
		Main.gui.fillEventLog("--" + object + "\n");
		
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
			this.motionOff = true;
			if (this.doorActivated && this.doorClosed) {
				/*
				 * wait for 1.5 min
				 * motionOff still = true means no one in the room
				 * publish activity = leaving
				 * */
				Thread publish = new Thread() {
					public void run() {
						// Sleep 70000ms (~1.2min)
						try {
							Thread.sleep(70000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (isMotionOff()) {
							publishAMessage(Consts.TOPIC_ACTIVITY, Consts.A_LEAVING);
						}
					}
				};
				publish.start();
				
				/*if (this.motionOffCounter >= Consts.MOTION_SENSOR_THRESHOLD) {
					// leaving the room 
					publishAMessage(Consts.TOPIC_ACTIVITY, Consts.A_LEAVING);
					this.motionOffCounter = 0;
				}*/
			}
		} else {	// motion ON
			this.motionOff = false;
			if (this.doorActivated) {
				// entering the room
				publishAMessage(Consts.TOPIC_ACTIVITY, Consts.A_ENTERING);
			}
			
			// Context = At working room
			publishAMessage(Consts.TOPIC_CONTEXT, Consts.C_AT_WORKINGROOM);
		}
		
	}
	
	public void translateDoorData(String sensorStatus) {
		this.doorActivated = true;
		// set time out for doorActivated = 3 mins
		this.doorTimeCounter = System.currentTimeMillis() + 180000;
		
		if (sensorStatus.equalsIgnoreCase("Door_CLOSED")) {
			this.doorClosed = true;
		} else {
			this.doorClosed = false;
		}
	}
	
	public void translateSwitchData(String sensorStatus) 
			throws UnsupportedEncodingException, MqttPersistenceException, MqttException{
		if (sensorStatus.equalsIgnoreCase("Switch_ON")) {
			// track lightON start time
			this.startLightTime = new Date();
			
			// Context = workingroom_light_on
			publishAMessage(Consts.TOPIC_CONTEXT, Consts.C_WORKINGROOM_LIGHT_ON);
		} else {
			// light OFF, store to database
			if (this.startLightTime != null) {
				Date endLightTime = new Date();
				
				// Date format
				SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_FORMAT);
				
				// Save sensor event to database 
				DBObject object = new BasicDBObject("room", "7615.1")
											.append("activity", "leavingLightON")
											.append("startTime", sdf.format(this.startLightTime))
											.append("endTime", sdf.format(endLightTime));
				DBCollection activityCollection = database.getCollection(Consts.COLLECTION_ACTIVITIES);
				activityCollection.insert(object);
				
				// gui & log
				Vector<Object> row = new Vector<Object>();
				row.add("7615.1");
				row.add("leavingLightON");
				row.add(sdf.format(this.startLightTime));
				row.add(sdf.format(endLightTime));
				Main.gui.addTableRow(row);
				
				System.out.println("--Inserted into " + Consts.COLLECTION_ACTIVITIES + " : " + object); 
				
				// release time tracking for light
				this.startLightTime = null;
			}
		}
	}
	
	public void translateWindowData(String sensorStatus) {
		if (sensorStatus.equalsIgnoreCase("Window_OPEN")) {
			if (this.startOpenedWindowTime == null) {
				this.startOpenedWindowTime = new Date();
			}
		} else {
			if (this.startOpenedWindowTime != null) {
				Date endOpenedWindowTime = new Date();
				
				// Date format
				SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_FORMAT);
				
				// Save activity event to database 
				DBObject object = new BasicDBObject("room", "7615.1")
											.append("activity", "leavingWindowOPEN")
											.append("startTime", sdf.format(this.startOpenedWindowTime))
											.append("endTime", sdf.format(endOpenedWindowTime));
				DBCollection activityCollection = database.getCollection(Consts.COLLECTION_ACTIVITIES);
				activityCollection.insert(object);
				
				// gui & log
				Vector<Object> row = new Vector<Object>();
				row.add("7615.1");
				row.add("leavingWindowOPEN");
				row.add(sdf.format(this.startOpenedWindowTime));
				row.add(sdf.format(endOpenedWindowTime));
				Main.gui.addTableRow(row);
				
				System.out.println("--Inserted into " + Consts.COLLECTION_ACTIVITIES + " : " + object); 
				
				// release time tracking for light
				this.startOpenedWindowTime = null;
			}
		}
	}
	
	public void publishAMessage(String topic, String message) {
		MqttDeliveryToken token;
		MqttTopic mqttTopic = mqttClient.getTopic(topic);
		try {
			token = mqttTopic.publish(message.getBytes(), 2, false);
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isMotionOff() {
		return this.motionOff;
	}

}