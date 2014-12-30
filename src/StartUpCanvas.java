import java.io.*;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.rms.*;

import javax.microedition.midlet.*;

public class StartUpCanvas extends Canvas
{
	
	private Rickshaw myRickshaw;
	
	private Image splashScreen,scroll,blackTrans;
	
	//whether painting is first time called
	boolean firstTime = true;
	
	//Stores which menu item is to be shown on the screen
	private int item;
	
	//Record store from where records are to be read.
	RecordStore rs;
	
	public StartUpCanvas(Rickshaw myRickshawArg) throws IOException{
		myRickshaw = myRickshawArg;
		splashScreen = Image.createImage("/splash.png");
		scroll = Image.createImage("/panel.png");
		blackTrans = Image.createImage("/black_trans.png");
		setFullScreenMode(true);
	}
	
	private void addQuitCommand(){
		this.addCommand(myRickshaw.quit);
	}
	
	private void removeQuitCommand(){
		this.removeCommand(myRickshaw.quit);
	}
	
	//This method is to simply read all the records from record store and displaying them on to graphics target.
	private void drawRecords(Graphics target){
		target.drawString(": Top Five Scores :",88,10,Graphics.TOP|Graphics.HCENTER);
		try{
		rs = rs.openRecordStore("playerRecords",true);
		RecordEnumeration enume = rs.enumerateRecords(null,myRickshaw,false);
		int i = 0;
		target.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_PLAIN,Font.SIZE_SMALL));
		while(enume.hasNextElement()){
			byte[] rec = enume.nextRecord();
			String playerName = Rickshaw.getPlayerName(rec);
			int score = Rickshaw.getPlayerScore(rec);
			target.drawString(playerName,25,50+i*15,Graphics.TOP|Graphics.LEFT);
			target.drawString(""+score+"",125,50+i*15,Graphics.TOP|Graphics.LEFT);
			i++;
		}
		rs.closeRecordStore();
		}catch(Exception e){}
	}
	
	//To handle the key events
	protected void keyPressed(int keyCode){
		int action = getGameAction(keyCode);
		switch(action){
			case LEFT:
			if(firstTime) firstTime=false;
			else{
			      item--;
			      if(item<0) item = 4;
			      if(item==3) addQuitCommand();
			      else removeQuitCommand();
			}
			      repaint();
			   break;
			case RIGHT:
			if(firstTime) firstTime=false;
			else{
			      item++;
			      if(item>4) item=0;
			      if(item==3) addQuitCommand();
			      else removeQuitCommand();
			}
			      repaint();
			   break;
			case FIRE:
			if(firstTime){ firstTime=false; repaint(); }
			else{
			   switch(item){
				   case 0:
				   Display.getDisplay(myRickshaw).setCurrent(myRickshaw.myRickCanvas);
				   myRickshaw.myRickCanvas.startThread();
				   break;
				   
				   case 3:
				   try{
			            myRickshaw.destroyApp(true);
			            myRickshaw.notifyDestroyed();
			            }catch(MIDletStateChangeException e){}
				    break;
			   }
			}
			default:
			       firstTime=false;
			       repaint();
		}
	}
	
	protected void paint(Graphics target){
		if(firstTime){
		   target.drawImage(splashScreen,0,0,Graphics.TOP|Graphics.LEFT);
		   target.setColor(0xeda22b);
		   target.drawString("Press Any Key.",88,195,Graphics.HCENTER|Graphics.BOTTOM);
		}
		else{
		target.drawImage(splashScreen,0,0,Graphics.TOP|Graphics.LEFT);
                target.drawImage(blackTrans,0,0,Graphics.TOP|Graphics.LEFT);
		target.drawImage(scroll,88,170,Graphics.HCENTER|Graphics.TOP);
		switch(item){
			case 0:
			   target.setColor(0xede6e6);
			   target.drawString("New Game",88,173,Graphics.HCENTER|Graphics.TOP);
			   target.setColor(0xffffff);
			   target.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_PLAIN,Font.SIZE_SMALL));
			   target.drawString("Take the Rickshaw to ",88,30,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("finishline ",88,45,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("Press 5 to select",88,208,Graphics.HCENTER|Graphics.BOTTOM);
			   target.drawString("Press 4 & 6 to scroll.",88,145,Graphics.HCENTER|Graphics.BOTTOM);
			   break;
			case 1:
			target.setColor(0xede6e6);
			target.drawString("Help",88,173,Graphics.HCENTER|Graphics.TOP);
			   target.setColor(0xffffff);
			   target.drawString(": Controls :",88,30,Graphics.HCENTER|Graphics.TOP);
			   target.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_PLAIN,Font.SIZE_SMALL));
			   target.drawString("4 - Left , 6-Right",88,55,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("2 - Accelerate",88,70,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("8 - Deccelerate",88,85,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("1 - Toggle Pause",88,100,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("0 - Power Brake",88,115,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("* - Stop Game",88,130,Graphics.HCENTER|Graphics.TOP);
			   break;
			case 2:
			   target.setColor(0xede6e6);
			   target.drawString("Records",88,173,Graphics.HCENTER|Graphics.TOP);
			   target.setColor(0xffffff);
			   drawRecords(target);
			   break;
			case 3:
			   target.setColor(0xede6e6);
			   target.drawString("Quit",88,173,Graphics.HCENTER|Graphics.TOP);
			   target.setColor(0xffffff);
			   target.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_PLAIN,Font.SIZE_SMALL));
			   target.drawString("Press 5 to quit.",88,208,Graphics.HCENTER|Graphics.BOTTOM);
			   target.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_BOLD,Font.SIZE_LARGE));
			   target.setColor(0xdeb042);
			   target.drawString(": GAME EXIT :",88,90,Graphics.HCENTER|Graphics.TOP);
			   
			   break;
			case 4:
			   target.setColor(0xede6e6);
			   target.drawString("About",88,173,Graphics.HCENTER|Graphics.TOP);
			   target.setColor(0xf9f5e0);
			   target.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_PLAIN,Font.SIZE_SMALL));
			   target.drawString("This game is made by Himanshu",88,10,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("Gupta, Student IIT Kanpur,",88,25,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("for J2ME Contest held jointly",88,40,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("by Paradox Studios and Sun",88,55,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("Microsystems.",88,70,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("MIDlet-Name: RickshawRage",88,100,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("MIDlet-Version: 1.0",88,115,Graphics.HCENTER|Graphics.TOP);
			   target.drawString("MIDP-2.0 , CLDC-1.1",88,130,Graphics.HCENTER|Graphics.TOP);
			   
			   break;
			   
		}
		}
	}
	
	
	
	
}
		
                
