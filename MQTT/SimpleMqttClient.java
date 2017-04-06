import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SimpleMqttClient implements MqttCallback {

MqttClient client;

public SimpleMqttClient() {
}

public static void main(String[] args) {
    new SimpleMqttClient().run();
}

public void run() {
    try {
		String[] subcriberTopics = new String[5];
		subcriberTopics[0] = Consts.TOPIC_DOOR;
		subcriberTopics[1] = Consts.TOPIC_WINDOW;
		subcriberTopics[2] = Consts.TOPIC_MOTION;
		subcriberTopics[3] = Consts.TOPIC_SWITCH;
		subcriberTopics[4] = Consts.TOPIC_TWILIGHT;
		
        client = new MqttClient("tcp://127.0.0.1:1883", "GIANG");
        client.connect();
        client.setCallback(this);
        client.subscribe(subcriberTopics);
    } catch (MqttException e) {
        e.printStackTrace();
    }
}

@Override
public void connectionLost(Throwable cause) {
    // TODO Auto-generated method stub

}

@Override
public void messageArrived(String topic, MqttMessage message) throws Exception {
	System.out.println("Received ");   
	System.out.println(topic + " : " + message);   
}

@Override
public void deliveryComplete(IMqttDeliveryToken token) {
    // TODO Auto-generated method stub

}

}