package application;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class QueueAsynchConsumer implements MessageListener {
	@Override
	public void onMessage(Message message) {
		try {
			System.out.printf("Odebrano wiadomość:'%s'\n", message.getStringProperty("TEXT"));
		}
		catch (JMSException e) { e.printStackTrace(); }
	}
}
