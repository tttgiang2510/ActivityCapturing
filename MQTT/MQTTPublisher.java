import java.io.UnsupportedEncodingException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTPublisher extends Thread implements MqttCallback {
	MqttClient client = null;
	MqttMessage message = null;
	String topic = null;
	
	public MQTTPublisher() {
	}
	
	public static void main(String[] args) {
		new MQTTPublisher().doDemo();
	}
	
	public void doDemo() {
		try {
			client =  new MqttClient("tcp://localhost:1883", "demo");
			client.connect();
			MqttMessage message = new MqttMessage();
			String payload = "Helloooooooooooooooo";
			message.setPayload(payload.getBytes("UTF-8"));
			client.publish("/test/hm", message);
			client.disconnect();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		// TODO Auto-generated method stub
		
	}
	

}
