package application;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;

public class PTPConsumer {
	private JMSContext jmsContext;
	private JMSConsumer jmsConsumer;
	
	public PTPConsumer(Integer hash, application.TickTackToeController.QueueAsynchConsumer asynchConsumer) {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
		jmsContext = connectionFactory.createContext();
		try {
			Queue queue = new com.sun.messaging.Queue("ATJQueue");
			
			if(hash>0) jmsConsumer = jmsContext.createConsumer(queue, "HASH <> '" + hash + "'"); //if hash>0 filter messages, if not just consume everything
			else jmsConsumer = jmsContext.createConsumer(queue);
			
			jmsConsumer.setMessageListener(asynchConsumer);
		}
		catch(JMSException e) { e.printStackTrace(); }
	}
	
	public void finishReceiving() {
		jmsConsumer.close();
		jmsContext.close();
		
	}

}
