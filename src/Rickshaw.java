import javax.microedition.midlet.*;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.*;
import java.io.*;
import javax.microedition.lcdui.Form;

public class Rickshaw extends MIDlet implements CommandListener,RecordComparator
{

	RickCanvas myRickCanvas;
	StartUpCanvas myStartUpCanvas;
        
	//Form and textField to take name of the player for record from key pad.
	Form myForm;
	TextField textField;
	
	//Record store for recording top 5 scorers
	RecordStore rs ;
	
        //Commands for varius operations	
	public Command quit,go;
	
	//Which displayable is paused.
	private Displayable paused;
	
	
	
	public Rickshaw() throws IOException{
		myForm = new Form("Enter Your Name");
		textField = new TextField("","",15,TextField.ANY);
		myForm.append("Congratulations !!! \n \n You are one of Top 5 scorer. \n \n ");
		myForm.append(textField);
		myRickCanvas = new RickCanvas(this);
		myStartUpCanvas = new StartUpCanvas(this);
		quit = new Command("quit",Command.EXIT,0);
		go = new Command("Go",Command.OK,0);
		myForm.addCommand(go);
		myForm.setCommandListener(this);
		paused = null;
	}
	
	public void startApp() throws MIDletStateChangeException{		
		if(paused==myRickCanvas){
		    Display.getDisplay(this).setCurrent(myRickCanvas);
		}else{
		            Display.getDisplay(this).setCurrent(myStartUpCanvas);
		}
	}
	
	public void destroyApp(boolean unconditional) throws MIDletStateChangeException{
		myRickCanvas.stopThread();
		myStartUpCanvas = null;
		myRickCanvas = null;
		System.gc();
	}
	
	public void pauseApp(){
		paused = Display.getDisplay(this).getCurrent();
		if(paused==myRickCanvas) myRickCanvas.pauseThread();		
	}
	
	public void commandAction(Command com,Displayable d){
		if(com==quit){
			try{
			destroyApp(true);
			notifyDestroyed();
			}catch(MIDletStateChangeException e){}
		}
		if(com==go){
		    //System.out.println("Name entered is:- "+textField.getString());
		    //If player has not entered the name ,do nothing.
		    if(textField.size()!=0){
			addPlayerRecord(Auto.score);
			Display.getDisplay(this).setCurrent(myStartUpCanvas);
		    }
			
		}
	}
	
	
	//Implementing the function of interface RecordComparator to sort players according to their scores
	public int compare(byte[] rec1,byte[] rec2){
		int result;
			//Take the player names and scores
			String firstPlayer = getPlayerName(rec1);
			int firstPlayerScore = getPlayerScore(rec1);
			String secondPlayer = getPlayerName(rec2);
			int secondPlayerScore = getPlayerScore(rec2);
			
			//System.out.println("first score="+firstPlayerScore+"   second score="+secondPlayerScore);
			
			//compare to make descending order.
			if(firstPlayerScore!=secondPlayerScore){
				result =  firstPlayerScore>secondPlayerScore?RecordComparator.PRECEDES:RecordComparator.FOLLOWS;
				
			}else{
				//compare player names
				int comp = firstPlayer.compareTo(secondPlayer);
				if(comp==0) result = RecordComparator.EQUIVALENT;
				else{
					if(comp<0) result = RecordComparator.PRECEDES;
					else result = RecordComparator.FOLLOWS;
				}
			}
			
		//System.out.println(result);
		return result;
	}
	
	
	//Some tool methods
	//this method is to extract player's name from byte[] array.
	public final static String getPlayerName(byte[] buffer){
		String name=null;
		try{
		DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(buffer));
		name = dataInputStream.readUTF();
		}catch(IOException ie){}
		return name;
	}
	
	//this method is to extract player's score from byte[] array.
	public final static int getPlayerScore(byte[] buffer){
		int score = 0;
		try{
		DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(buffer));
		dataInputStream.readUTF();
		score = dataInputStream.readInt();
		}catch(IOException ie){}
		return score;
	}
	
	//this method is to make byte[] array of player's name and his score.
	public final static  byte[] getByteRecord(String playerName,int playerScore){
		  byte[] result=null;;
		try{
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			DataOutputStream dOut = new DataOutputStream(bOut);
			dOut.writeUTF(playerName);
			dOut.writeInt(playerScore);
			
			result = bOut.toByteArray();
			dOut.close();
			bOut.close();
			return result;
		}catch(IOException ie){
			return result;
		}
	}
	
	//To check whether current score is addable in the records
	public boolean whetherToAddRecord(int currentScore){
	    boolean result = false;
	    try{
			rs = RecordStore.openRecordStore("playerRecords",true);
			RecordEnumeration enume = rs.enumerateRecords(null,this,false);
			int noOfRecords = enume.numRecords();
			if(noOfRecords<5){
			    //System.out.println("whetherToAddRecord0");
				result = true;
			}else{
			    //system.out.println("whetherToAddRecord1");
				int leastScoreRecId = 0;
				//check if current score is in top 5
				while(enume.hasNextElement()){
					//Print all the records
					leastScoreRecId = enume.nextRecordId();
					//String playerName = Rickshaw.getPlayerName(temp);
					//int playerScore = Rickshaw.getPlayerScore(temp);
					//System.out.println("Player Name:-"+playerName+"   Score:-"+playerScore);
				}
				int leastScore = Rickshaw.getPlayerScore(rs.getRecord(leastScoreRecId));
				if(leastScore>=currentScore) result = false;
				else  result = true;
			}
			enume.destroy();
			rs.closeRecordStore();
			return result;
		}catch(Exception e){
		    return result;
		}
	}
	    
		private void addPlayerRecord(int currentScore){
		try{
			rs = RecordStore.openRecordStore("playerRecords",true);
			RecordEnumeration enume = rs.enumerateRecords(null,this,false);
			int noOfRecords = enume.numRecords();
			if(noOfRecords<5){
				//Add the record if record has less than 5 records
				//Display.getDisplay(myRickshaw).setCurrent(myRickshaw.myForm);
				byte[] rec = getByteRecord(textField.getString(),currentScore);
				rs.addRecord(rec,0,rec.length);
				
			}else{
			     
				 int leastScoreRecId = 0;
				//check if current score is in top 5
				while(enume.hasNextElement()){
				     
					leastScoreRecId = enume.nextRecordId();
					//Print all the records
					//byte[] temp = rs.getRecord(leastScoreRecId);
					//String playerName = getPlayerName(temp);
					//int playerScore = getPlayerScore(temp);
					//System.out.println("Player Name:-"+playerName+"   Score:-"+playerScore);
				}
				
				int leastScore = Rickshaw.getPlayerScore(rs.getRecord(leastScoreRecId));

				//change the least score record.
				byte[] temp = Rickshaw.getByteRecord(textField.getString(),currentScore);
				rs.setRecord(leastScoreRecId,temp,0,temp.length);
			}
			enume.destroy();
			rs.closeRecordStore();
		}catch(Exception e){}
	}		
			
			
		
			
}