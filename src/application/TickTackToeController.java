package application;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TickTackToeController {
	@FXML Label symbolLbl;
	@FXML Label stateLbl;
	@FXML Label outcomeLbl;
	@FXML Label timeForMoveLbl;
	@FXML Button restartBtn;
	
	@FXML Button btn0;
	@FXML Button btn1;
	@FXML Button btn2;
	@FXML Button btn3;
	@FXML Button btn4;
	@FXML Button btn5;
	@FXML Button btn6;
	@FXML Button btn7;
	@FXML Button btn8;
	
	private PTPProducer producer;
	private PTPConsumer consumer;
	
	private Integer hash = new java.util.Date().hashCode();
	private long sentTimestamp;
	private Boolean inGame = false;
	
	private String state = "waitForOtherStart"; //waitForOtherStart || myMove || hisMove || won || lost || draw
	private String symbol = "X";
	private String otherSymbol = "O";
	
	private int litButtonsCount = 0;
	private Button buttons[] = new Button[9];
	
	@FXML private void initialize(){	
		producer = new PTPProducer(hash);
		consumer = new PTPConsumer(hash, new QueueAsynchConsumer());
		buttons[0] = btn0;
		buttons[1] = btn1;
		buttons[2] = btn2;
		buttons[3] = btn3;
		buttons[4] = btn4;
		buttons[5] = btn5;
		buttons[6] = btn6;
		buttons[7] = btn7;
		buttons[8] = btn8;
		
		provisionGame();
	}
	
	@FXML private void restartBtn_Click() {
		producer.sendQueueMessage("restart",inGame);
		restartGame(false);
	}
	
	@FXML private void btn0_Click() {
		sendMove(0);
	}
	
	@FXML private void btn1_Click() {
		sendMove(1);
	}
	
	@FXML private void btn2_Click() {
		sendMove(2);
	}
	
	@FXML private void btn3_Click() {
		sendMove(3);
	}
	
	@FXML private void btn4_Click() {
		sendMove(4);
	}
	
	@FXML private void btn5_Click() {
		sendMove(5);
	}
	
	@FXML private void btn6_Click() {
		sendMove(6);
	}
	
	@FXML private void btn7_Click() {
		sendMove(7);
	}
	
	@FXML private void btn8_Click() {
		sendMove(8);
	}
	
	private void provisionGame() {
		state = "waitForOtherStart";
		stateLbl.setText("waiting for your opponent to show up");
		outcomeLbl.setVisible(false);
		prepareButtons();
		consumer.setSource("0");
		inGame = false;
		consumer.setInGame(inGame);
		
		//if(!rerun) {
			producer.sendQueueMessage("start",inGame);
			sentTimestamp = new java.util.Date().getTime();
		//}
	}
	
	private void prepareButtons() {		
		for(Button b : buttons) {
			b.setText("");
		}
		restartBtn.setVisible(false);
		litButtonsCount = 0;
	}
	
	private void sendMove(Integer buttonNumber) {
		
		if(state.equals("myMove") && buttons[buttonNumber].getText().equals("")) {
			producer.sendQueueMessage("btn"+buttonNumber,inGame);
			state = "hisMove";
			stateLbl.setText("wait for your opponent's move");
			lightUpButton(buttonNumber,symbol);
			if(litButtonsCount >= 3) checkForWin();
		}
	}
	
	private void lightUpButton(Integer number, String symbol) {
		buttons[number].setText(symbol);
		litButtonsCount++;
	}
	
	private void checkForWin() {
		Boolean hasWon = false, hasLost = false;
		String checkedSymbol;
		//vertically
		for(int i=0;i<3;i++) {
			checkedSymbol = buttons[i].getText(); //from top left
			if(buttons[i+3].getText().equals(checkedSymbol) && buttons[i+6].getText().equals(checkedSymbol)) {
				if(checkedSymbol.equals(symbol)) {
					hasWon = true;
					break;
				}
				else if(checkedSymbol.equals(otherSymbol)) {
					hasLost = true;
					break;
				}
			}
		}
		
		//horizontally
		if(!hasWon && !hasLost) 
		for(int i=0;i<3;i++) {
			checkedSymbol = buttons[i*3].getText(); //from top left
			if(buttons[i*3+1].getText().equals(checkedSymbol) && buttons[i*3+2].getText().equals(checkedSymbol)) {
				if(checkedSymbol.equals(symbol)) {
					hasWon = true;
					break;
				}
				else if(checkedSymbol.equals(otherSymbol)) {
					hasLost = true;
					break;
				}
			}
		}
		
		checkedSymbol = buttons[4].getText(); //diagonally
		if((buttons[0].getText().equals(checkedSymbol) && buttons[8].getText().equals(checkedSymbol)) ||
				(buttons[2].getText().equals(checkedSymbol) && buttons[6].getText().equals(checkedSymbol))) {
			if(checkedSymbol.equals(symbol)) hasWon = true;
			else if(checkedSymbol.equals(otherSymbol)) hasLost = true;
		}
		
		
		if(hasWon) {
			state = "won";
			stateLbl.setText("game finished");
			outcomeLbl.setText("You have won! :)");
			outcomeLbl.setVisible(true);
			restartBtn.setVisible(true);
			//now write it to outcomeLbl and send message to terminate the game and give the option to restart
		}
		else if(hasLost) {
			state = "lost";
			stateLbl.setText("game finished");
			outcomeLbl.setText("You have lost :(");
			outcomeLbl.setVisible(true);
			restartBtn.setVisible(true);
		}
		
		if(litButtonsCount >= 9 && (state.equals("myMove") || state.equals("hisMove"))) { //draw
			state = "draw";
			stateLbl.setText("game finished");
			outcomeLbl.setText("Ended in a draw!");
			outcomeLbl.setVisible(true);
			restartBtn.setVisible(true);
		}
	}
	
	private void restartGame(Boolean fromMessage) {
		if(state.equals("won") || (state.equals("draw") && fromMessage)) {
			state = "hisMove";
			stateLbl.setText("wait for opponent's move");
		}
		else if(state.equals("lost") || (state.equals("draw") && !fromMessage)) {
			state = "myMove";
			stateLbl.setText("your move");
		}
		outcomeLbl.setVisible(false);
		restartBtn.setVisible(false);
		prepareButtons();
	}
	
	private void reactToMessage(long timestamp, String text, String sender) {
		System.out.println("Got message \""+text+"\" at: "+String.valueOf(timestamp));
		System.out.println("State: "+ state);
		
		if(state.equals("waitForOtherStart") == false && text.equals("leaving")) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText("Information");
			alert.setContentText("Your opponent has quit the game. Ready to play against another player.");
			alert.showAndWait();
			provisionGame();
			return;
		}
		
		if((state.equals("lost") || state.equals("won") || state.equals("draw")) && text.equals("restart")) {
			restartGame(true);
			return;
		}
		
		if(state.equals("waitForOtherStart") && text.equals("start")) {
			if(timestamp > sentTimestamp) {
				state = "hisMove";
				symbol = "O";
				otherSymbol = "X";
				stateLbl.setText("wait for opponent's move");
				
				consumer.setSource(sender);
				
				producer.sendQueueMessage("startConfirm",inGame);
				
				inGame = true;
				consumer.setInGame(inGame);
				symbolLbl.setText(symbol);
			}			
			return;
		}
		if(state.equals("waitForOtherStart") && text.equals("startConfirm")) {
			consumer.setSource(sender);
			inGame = true;
			consumer.setInGame(inGame);
			
			state = "myMove";
			symbol = "X";
			otherSymbol = "O";
			stateLbl.setText("your move");
			symbolLbl.setText(symbol);
		}
		
		if(state.equals("hisMove") && text.substring(0, 3).equals("btn"))
		{
			state = "myMove";
			stateLbl.setText("your move");
			lightUpButton(new Integer(text.substring(3,4)),otherSymbol);
		}
		if(litButtonsCount >= 3) checkForWin();
	}
	
	public void closeConsumer() {
		
		PTPSyncConsumer cleaner = new PTPSyncConsumer(); //clean up unnecessary messages
		cleaner.receiveQueueMessages();
		
		if(!state.equals("waitForOtherStart")) { producer.sendQueueMessage("leaving",inGame); }
		consumer.finishReceiving();
		
		System.out.println("Consumer closed");
	}
	
	public class QueueAsynchConsumer implements MessageListener {
		
		@Override
		public void onMessage(Message message) {
				Platform.runLater(() -> {
					try {
						reactToMessage(message.getLongProperty("TIME"),message.getStringProperty("TEXT"),message.getStringProperty("HASH"));
					} catch (JMSException e) {
						e.printStackTrace();
					}
				});
		}
	}
}
