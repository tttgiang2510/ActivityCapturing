import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class Main {
	public static GUI gui;
	
	// MongoDB
	private static MongoClient mongoClient 		= null;
	
	public static void main(String[] args) {
		
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	// Connect to database
            	try {
        			mongoClient = new MongoClient(new MongoClientURI(Consts.MONGO_CLIENT_URI));
        			new SensorEventSubcriber(mongoClient).run();
        		} catch (UnknownHostException e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
            	
        		String[] subcriberTopics = new String[2];
        		subcriberTopics[0] = Consts.TOPIC_ACTIVITY;
        		subcriberTopics[1] = Consts.TOPIC_CONTEXT;
        		MQTTSubcriber subcriber = new MQTTSubcriber(Consts.LOCALHOST, "ID_7615.1", subcriberTopics);
        		CAR mAlgorithm = new CAR(mongoClient);
        			
        		mAlgorithm	.setRunning(true);
        		subcriber	.setRunning(true);

        		mAlgorithm.setmInQ(subcriber.getmOutQueue());
        			
        		mAlgorithm				.start();
        		subcriber				.start();
        		
                gui = new GUI();
                gui.fillTable();
                gui.setVisible(true);
            }
        });
		
		
	}
}