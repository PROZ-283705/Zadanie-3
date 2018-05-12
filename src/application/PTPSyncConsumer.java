package application;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;

public class PTPSyncConsumer {
	public void receiveQueueMessages() {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
		JMSContext jmsContext = connectionFactory.createContext();
		try {
			((com.sun.messaging.ConnectionFactory) connectionFactory).setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList,"localhost:7676/jms"); // [hostName][:portNumber][/serviceName]
			Queue queue = new com.sun.messaging.Queue("ATJQueue");
			JMSConsumer jmsConsumer = jmsContext.createConsumer(queue);
			Message msg;
			while ((msg = jmsConsumer.receive(10)) != null) {
				System.out.printf("Cleaned message '%s' from queue\n", msg.getStringProperty("TEXT"));
			}
			jmsConsumer.close();
		}
		catch (JMSException e) { e.printStackTrace(); }
		jmsContext.close();
	}

}
