import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDB {
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

		subcriber	.setRunning(true);
		//subcriber				.start();
	}

}
