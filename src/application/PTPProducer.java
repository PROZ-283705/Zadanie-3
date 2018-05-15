package application;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;

public class PTPProducer {
	
	private Integer hash;
	
	public PTPProducer (Integer hash) {
		this.hash = hash;
	}
	
	public void sendQueueMessage(String text,Boolean inGame) {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
		
		try {
			((com.sun.messaging.ConnectionFactory) connectionFactory).setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList, "localhost:7676/jms");
			JMSContext jmsContext = connectionFactory.createContext() ;
			
			Message message = jmsContext.createMessage();
			message.setStringProperty("HASH", hash.toString());
			message.setLongProperty("TIME", new java.util.Date().getTime());
			message.setStringProperty("TEXT", text);
			message.setBooleanProperty("INGAME", inGame);
			
			JMSProducer jmsProducer = jmsContext.createProducer();
			Queue queue = new com.sun.messaging.Queue("ATJQueue");
			
			jmsProducer.send(queue, message);
			
			System.out.printf("Wiadomość '%s' została wysłana.\n", message.getStringProperty("TEXT"));
			
			jmsContext.close();
		}
		catch(JMSException e) { e.printStackTrace(); }
	}
	
}
