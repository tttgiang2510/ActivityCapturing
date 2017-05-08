import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDB {
	
	public static void main (String[] args) throws UnknownHostException, FileNotFoundException, UnsupportedEncodingException {
		
		PrintWriter writer = new PrintWriter("C:\\data\\ActivityData_May5.txt", "UTF-8");
		
		
		MongoClient client = new MongoClient(new MongoClientURI(Consts.MONGO_CLIENT_URI));
		DB db = client.getDB(Consts.DATABASE_NAME);
		DBCollection collection = db.getCollection(Consts.COLLECTION_ACTIVITIES);
		
		BasicDBObject query = new BasicDBObject();
		query.put("activity", "working_in_room");
		
		DBCursor cursor = collection.find(query);
		
		while (cursor.hasNext()) {
			//System.out.println(cursor.next());
			writer.println(cursor.next());
		}
		
		writer.close();
		
		System.out.println("Done!!!!");
		
	}

}
