package application;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;

public class PTPConsumer {
	private JMSContext jmsContext;
	private JMSConsumer jmsConsumer;
	private Queue queue;
	private application.TickTackToeController.QueueAsynchConsumer asynchConsumer;
	private Integer hash;
	private String source = "0";
	private Boolean inGame = false;
	
	public PTPConsumer(Integer hash, application.TickTackToeController.QueueAsynchConsumer asynchConsumer) {
		ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();
		jmsContext = connectionFactory.createContext();
		try {
			queue = new com.sun.messaging.Queue("ATJQueue");
			this.asynchConsumer = asynchConsumer;
			this.hash = hash;
			createConsumer();
		}
		catch(JMSException e) { e.printStackTrace(); }
	}
	
	public void setSource(String source) {
		this.source = source;
		jmsConsumer.close();
		createConsumer();
	}
	
	public void setInGame(Boolean inGame) {
		this.inGame = inGame;
		jmsConsumer.close();
		createConsumer();
	}
	
	public void finishReceiving() {
		jmsConsumer.close();
		jmsContext.close();
		
	}

	private void createConsumer() {
		if(hash>0) { //if hash>0 filter messages, if not just consume everything
			if(source.equals("0"))
				jmsConsumer = jmsContext.createConsumer(queue, "HASH <> '" + hash + "' AND INGAME = "+inGame); //if source==0 consume all messages apart from my own
			else
				jmsConsumer = jmsContext.createConsumer(queue, "HASH = '" + source + "' AND INGAME = "+inGame); //consume only messages from specified player
		}
		else jmsConsumer = jmsContext.createConsumer(queue);
		
		jmsConsumer.setMessageListener(asynchConsumer);
	}
}
