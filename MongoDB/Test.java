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

public class Test {
	public static void main (String[] args) {
		try {
			MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
			DB database = mongoClient.getDB("Events");
			DBCollection collection = database.getCollection("SensorEvents");
			
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
			Date timestamp = new Date();
			
//			DBObject activity = new BasicDBObject("id", sdf.format(timestamp))
//										.append("activity", "close_door");
//			
//			collection.insert(activity);
			
			// get data
			System.out.println("Data: ");
			DBCursor cursor = collection.find();
			while(cursor.hasNext()) {
				System.out.println("-: ");
				DBObject storeData = cursor.next();
				String time = storeData.get("timestamp").toString();
				System.out.println(storeData);
				System.out.println("- Time: " + time);
				try {
					timestamp = sdf.parse(time);
					System.out.println("- Time converted: " + sdf.format(timestamp));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
