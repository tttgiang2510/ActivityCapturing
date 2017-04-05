/**
 * This class represents a SensorEventSubcriber
 * SensorEventSubcriber gets sensor event from a Mosquitto broker, 
 * then translate the event into ACTIVITY and CONTEXT and send to 
 * Complex ActivityCapturing component 
 * Also, the received events will be stored into the database
 * */

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class SensorEventSubcriber implements MqttCallback {
	public static final String MONGO_CLIENT_URI		= "mongodb://localhost:27017";
	public static final String DATE_FORMAT 			= "MMM dd,yyyy HH:mm";
	private String hostname 		= null;
	private String clientId 		= null;
	private String[] subcribeTopics = null;
	private boolean isRunning 		= true;
	
/*	private BlockingQueue<JSONObject> outQueue      = new ArrayBlockingQueue<>(100);  
	private BlockingQueue<JSONObject> internalQueue = new ArrayBlockingQueue<>(100);*/
	
	MqttClient client;
	
	// MongoDB
	MongoClient mongoClient 		= null;
	DB database 					= null; 
	DBCollection collection 		= null;
	String databaseName 			= null;
	String collectionName 			= null;
	
	
	public SensorEventSubcriber () {
	}
	
	public SensorEventSubcriber (String hostname, String clientId, String[] subcribeTopic) {
		this.hostname 		= hostname;
		this.clientId 		= clientId; 
		this.subcribeTopics = subcribeTopic;
	}
	
	public SensorEventSubcriber (String hostname, String clientId, String[] subcribeTopic, 
			String databaseName, String collectionName) {
		this.hostname 		= hostname;
		this.clientId 		= clientId; 
		this.subcribeTopics = subcribeTopic;
		this.databaseName 	= databaseName;
		this.collectionName	= collectionName;
	}
	
	public void setRunning(boolean isRunning){
		this.isRunning  = isRunning;
		if(!isRunning){
			disconnect();
		}
	}
	
	public void run() {
		// TODO Auto-generated method stub
		String connect = "tcp://" + this.hostname + ":1883";
		try {
			// Connect to database
			mongoClient = new MongoClient(new MongoClientURI(MONGO_CLIENT_URI));
			database = mongoClient.getDB(this.databaseName);
			collection = database.getCollection(this.collectionName);
			
			// Connect to MQTT broker
			client = new MqttClient(connect, clientId);
			client.connect();
			client.setCallback(this);
			client.subscribe(subcribeTopics);
/*			while(isRunning) {
				
				JSONObject message = new JSONObject();
				try {
					message = internalQueue.take();
					outQueue.add(message);
					System.out.println("---> Put object to outQueue!");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}*/
			if(!isRunning) {
				disconnect();
			}
		} catch (MqttException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
	
	public void disconnect() {
		try {
			client.disconnect();
			client.close();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
/*	public BlockingQueue<JSONObject> getmOutQueue() {
		return outQueue;
	}*/

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("Connection lost!!! ::: " + arg0.getMessage());
		// reconnect
			try {
				client.connect();
				client.setCallback(this);
				client.subscribe(subcribeTopics);
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
		// Date format
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Date timestamp = new Date();
		
		System.out.println("----Topic: " + topic);
		byte[] payload = message.getPayload();
		String receivedMessage = new String(payload, "UTF-8");
		System.out.println("---Received: " + receivedMessage);
		
/*		JSONObject JSONmessage = new JSONObject();
		JSONmessage.put(topic, receivedMessage);
		
		internalQueue.add(JSONmessage);*/
		
		// Save to database 
		DBObject object = new BasicDBObject("timestamp", sdf.format(timestamp))
									.append("sensor", topic)
									.append("status", message);

		collection.insert(object);
	}
	
	public static void main (String[] args) {
		String[] subcriberTopics = new String[5];
		subcriberTopics[0] = Consts.TOPIC_DOOR;
		subcriberTopics[1] = Consts.TOPIC_WINDOW;
		subcriberTopics[2] = Consts.TOPIC_MOTION;
		subcriberTopics[3] = Consts.TOPIC_SWITCH;
		subcriberTopics[4] = Consts.TOPIC_TWILIGHT;
		
		String databaseName = "Events";
		String collectionName = "SensorEvents";
		SensorEventSubcriber subcriber = new SensorEventSubcriber(Consts.LOCALHOST,
				"ID_GIANG", subcriberTopics, databaseName, collectionName);

		//subcriber.setRunning(true);
		subcriber.run();
	}

}
