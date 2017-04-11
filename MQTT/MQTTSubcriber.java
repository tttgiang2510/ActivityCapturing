import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.json.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttCallback;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

public class MQTTSubcriber extends Thread implements MqttCallback  {
	
	private String mHostname = null;
	private String mClientId = null;
	private String[] mSubcribeTopics = null;
	private boolean isRunning = false;
	
	private BlockingQueue<JSONObject> mOutQueue      = new ArrayBlockingQueue<>(100);  
	private BlockingQueue<JSONObject> mInternalQueue = new ArrayBlockingQueue<>(100);
	
	MqttClient client;
	
	public MQTTSubcriber () {
	}
	
	public MQTTSubcriber(String hostname, String clientId, String[] subcribeTopics) {
		this.mHostname = hostname;
		this.mClientId = clientId; 
		this.mSubcribeTopics = subcribeTopics;
	}
	
	public void setRunning(boolean isRunning){
		this.isRunning  = isRunning;
		if(!isRunning){
			disconnect();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String connect = "tcp://" + mHostname + ":1883";
		try {
			client = new MqttClient(connect, mClientId);
			client.connect();
			client.setCallback(this);
			client.subscribe(mSubcribeTopics);
			while(isRunning) {
				
				JSONObject message = new JSONObject();
				try {
					message = mInternalQueue.take();
					mOutQueue.add(message);
/*					System.out.println("---> Put object to outQueue!");*/
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			if(!isRunning) {
				disconnect();
			}
		} catch (MqttException e1) {
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
	
	public BlockingQueue<JSONObject> getmOutQueue() {
		return mOutQueue;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		System.out.println("Connection lost!!! ::: " + arg0.getMessage());
		// reconnect
			try {
				client.connect();
				client.setCallback(this);
				client.subscribe(mSubcribeTopics);
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
		System.out.println("----Topic: " + topic);
		byte[] payload = message.getPayload();
		String receivedMessage = new String(payload, "UTF-8");
		System.out.println("---Received: " + receivedMessage);
		
		JSONObject JSONmessage = new JSONObject();
		JSONmessage.put(topic, receivedMessage);
		
		mInternalQueue.add(JSONmessage);
	}

}
