import java.net.UnknownHostException;

public class Main {
	
	public static void main(String[] args) {
		try {
			new SensorEventSubcriber().run();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] subcriberTopics = new String[2];
		subcriberTopics[0] = Consts.TOPIC_ACTIVITY;
		subcriberTopics[1] = Consts.TOPIC_CONTEXT;
		MQTTSubcriber subcriber = new MQTTSubcriber(Consts.LOCALHOST, "ID_GIANG", subcriberTopics);
		CAR mAlgorithm = new CAR();
			
		mAlgorithm	.setRunning(true);
		subcriber	.setRunning(true);

		mAlgorithm.setmInQ(subcriber.getmOutQueue());
			
		mAlgorithm				.start();
		subcriber				.start();
	}
}