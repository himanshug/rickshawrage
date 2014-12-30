import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.game.GameCanvas ;
import java.io.*;

import javax.microedition.midlet.*;

public class RickCanvas extends GameCanvas implements Runnable
{
	//RickManager is the Layer manager managing all the layers of traffic,auto,road etc
	private RickManager myRickManager ;
	
	//to check pauseAPP
	private Rickshaw myRickshaw;
	
	//current thread
	Thread t;
	
	
	//Variables and Images for drawing position and fuel of Rickshaw
	Image strip,autoPrototype,fuelPanel,fuelOver,speedImage;
	int viewWindowY,yPos,dy;
	
	//Screen dimensions
	public static int CANVAS_WIDTH = 176;
	public static int CANVAS_HEIGHT = 208;
	
	//start up state
	boolean startUpState;
	
	//Whether in paused state
	boolean isPaused ;
	
	//TrafficLight Image
	Image trafficLight;
	
	//Score Image
	Image score;
	
	
	public RickCanvas(Rickshaw rick) throws IOException{
		super(false);
		myRickshaw = rick;
		myRickManager = new RickManager(this);
		strip = Image.createImage("/strip.png");
		autoPrototype = Image.createImage("/auto_prototype.png");
		fuelPanel = Image.createImage("/fuel_panel.png");
		fuelOver = Image.createImage("/fuelover.png");
		speedImage = Image.createImage("/speed.png");
		trafficLight = Image.createImage("/trafficlight.png");
		score = Image.createImage("/score.png");
		setFullScreenMode(true);
		
	}
	
	//To initialise all the varibles
	private void init(){
		myRickManager.init();
		yPos = 154;
		startUpState = true;
		viewWindowY=0;
	}
	
	
	public void startThread(){
		isPaused = false;
		t = new Thread(this);
		t.start();
	}
	
	public void stopThread(){
		t = null;
	}
	
	public void pauseThread(){
	  isPaused = true;
        }
  
        public synchronized void resumeThread(){
	      isPaused = false ;
	      notify();
        }
	
	
	//To draw speed display,fuel display,score display and auto prototype on the screen
	private void drawExtraStuff(Graphics g){
		dy+=(viewWindowY-(Auto.yPos-150));
		viewWindowY = Auto.yPos-150;
		//Graphics g = getGraphics();
		g.drawImage(strip,5,15,Graphics.TOP|Graphics.LEFT);
		//Draw Auto prototype
		if(dy>=128){
			yPos--;
			Auto.score+=2;
			//System.out.println("autoPrototype yPos ="+yPos);
			dy=0;
		}
			
		g.drawImage(autoPrototype,6,yPos,Graphics.TOP|Graphics.LEFT);
		//Draw fuel
		g.drawImage(fuelPanel,176,208,Graphics.BOTTOM|Graphics.RIGHT);
		g.setColor(0);
		g.fillRect(153,182,6,2);
		if(Auto.fuel>=0){
		g.setColor(0xfd9d06);			
		g.fillRect(153,184+(18-Auto.fuel/5),6,Auto.fuel/5);
		}
		//Draw Score
		g.drawImage(score,176,0,Graphics.TOP|Graphics.RIGHT);
		g.setColor(0xffffff);
		g.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_PLAIN,Font.SIZE_SMALL));
		g.drawString(""+Auto.score+"",157,17,Graphics.TOP|Graphics.HCENTER);
		
		//Draw fuel over state
		if(Auto.outOfFuelState){
			g.setColor(0xfd9d06);
			g.drawImage(fuelOver,Auto.xPos+11,140,Graphics.HCENTER|Graphics.BOTTOM);
		}
		//Draw speed of auto
		        g.drawImage(speedImage,-3,208,Graphics.BOTTOM|Graphics.LEFT);
			g.setColor(0);
			if(Auto.yVel<20){
				g.fillRect(28,190,4,15);
				if(Auto.yVel<15){
					g.fillRect(23,190,4,13);
					if(Auto.yVel<11){
						g.fillRect(18,190,4,11);
						if(Auto.yVel<8){
							g.fillRect(14,190,3,7);
							if(Auto.yVel<5){
								g.fillRect(10,190,3,5);
								if(Auto.yVel<3){
									g.fillRect(6,190,3,3);
									if(Auto.yVel<1){
										g.fillRect(2,190,3,2);
									}
								}
							}
						}
					}
				}
			}
	}
	
	
	//To draw final score of the player after reaching the finish line
	private void drawFinalScore(Graphics target){
		
		target.setColor(0xffffff);
		target.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
		target.drawString("You Rocked !!",88,20,Graphics.TOP|Graphics.HCENTER);
		target.setFont(Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_PLAIN,Font.SIZE_SMALL));
		target.drawString("Fuel Left = "+Auto.fuel,88,45,Graphics.TOP|Graphics.HCENTER);
		target.drawString("High-Score = "+Auto.score,88,60,Graphics.TOP|Graphics.HCENTER);
	}
	
	//Handling key input	
	public void input(){
		int keyState = getKeyStates();
		switch(keyState){
			case UP_PRESSED:
			    Auto.keyPressed = 2;
			    break;
			case DOWN_PRESSED:
			    Auto.keyPressed = 8;
			    break;
			case LEFT_PRESSED:
			    Auto.keyPressed = 4;
			    break;
			case RIGHT_PRESSED:
			   Auto.keyPressed = 6;
			   break;
			    
		}
	}
	
	protected void keyPressed(int keyCode){
		switch(keyCode){
			//press 1 to toggle pause game
			case KEY_NUM1:
		             isPaused=!isPaused;
		              if(!isPaused){
				      resumeThread();
		              }
                              break;
			 //*(star) to stop the game.
			case KEY_STAR:
			     Display.getDisplay(myRickshaw).setCurrent(myRickshaw.myStartUpCanvas);
			     stopThread();
			// 0 for power brake
			case KEY_NUM0:
			     if(!isPaused)
			     Auto.keyPressed = 5;
			     break;
		}
	}

	//Main run method running the thread.
	public void run(){
		//Initialise Everything
		init();	
		Thread thisThread = Thread.currentThread();
		long lastCycleCalled = System.currentTimeMillis();
		//int fps = 0;
		//to check 3 min for the dist.
		//int counter = 0;
		int startcount = 0;
		long ticks=System.currentTimeMillis();
		long pauseTime = 0;
		
		/*try{
			trafficLight = Image.createImage("/trafficlight");
		}catch(IOException ie){
			System.out.println("Traffic light image could not be loaded.");
		}*/
		Graphics target;
		while(t==thisThread){
		   if(isPaused){
			pauseTime = System.currentTimeMillis();
			target = getGraphics();
			target.drawString("Paused",88,104,Graphics.HCENTER|Graphics.TOP);
			flushGraphics();
			synchronized(this){
		          while(isPaused){
			  try{
			      wait();
			  }catch(Exception e) {}
		          } 
		       }
		       pauseTime = System.currentTimeMillis()-pauseTime;
		  }
			//fps++;
			//target.setColor(0);
			//target.fillRect(0,0,176,208);
			target = getGraphics();
			target.setClip(0,0,176,208);
			if(startUpState){
				myRickManager.paint(target,0,0);
				drawExtraStuff(target);
				target.drawImage(trafficLight,146,0,Graphics.TOP|Graphics.LEFT);
				target.setColor(0x3b3b3b);
				
				switch(startcount){
					case 0:
					   target.fillRoundRect(149,23,13,12,13,12);
					   target.fillRoundRect(149,45,13,12,13,12);
					   break;
					case 1:
					   target.fillRoundRect(149,1,13,12,13,12);
					   target.fillRoundRect(149,45,13,12,13,12);
					   break;
					case 2:
					   target.fillRoundRect(149,1,13,12,13,12);
					   target.fillRoundRect(149,23,13,12,13,12);
					   break;
				} 
				flushGraphics();
				   startcount++;
				   try{
					Thread.sleep(1500);
				}catch(InterruptedException e){}
				if(startcount==3){
					   startUpState = false;
					   ticks = System.currentTimeMillis();
					   lastCycleCalled = System.currentTimeMillis();
				   }
			}else{
			input();
			myRickManager.cycle((System.currentTimeMillis()-lastCycleCalled-pauseTime),target);
			pauseTime = 0;
			lastCycleCalled = System.currentTimeMillis();
			myRickManager.paint(target,0,0);
			drawExtraStuff(target);
			flushGraphics();
			
			//printing fps
			long tim = System.currentTimeMillis()-ticks;
			if(tim>=1000){
				if(Auto.fuel>0) Auto.fuel--;
				//counter++;
				//System.out.println("fps is "+fps+" counter ="+counter+" fuel="+Auto.fuel);
				ticks = System.currentTimeMillis();
				//if(counter!=180)
				  //  fps = 0;
			}
			
			if(Auto.outOfFuelState||Auto.finishLineReachedState){
				if(Auto.yVel<=0){
				    //increase the score
					//if(Auto.finishLineReachedState){
						Auto.score+=Auto.fuel*5;
                   if(Auto.finishLineReachedState) {
					//show the final score
					  drawFinalScore(target);
					  flushGraphics();
				    }
					//sleep a little
					try{
						Thread.sleep(3000);
					}catch(InterruptedException ie){}
					
					
					if(Auto.finishLineReachedState && myRickshaw.whetherToAddRecord(Auto.score)){
					  //myRickshaw.textField.delete(0,myRickshaw.textField.size());
					  Display.getDisplay(myRickshaw).setCurrent(myRickshaw.myForm);
					}
					else{
					  Display.getDisplay(myRickshaw).setCurrent(myRickshaw.myStartUpCanvas);
					}
					stopThread();
				}
			}
					
			/*if(((Auto.yPos-150)<=-18000)){ //||Auto.fuel<=0
				//isRunning = false;
				System.out.println("fps is "+fps+" counter ="+counter);
				System.out.println("Distance Moved="+(Auto.yPos-150));
			}*/
			//input();
			}
		}
	}
	
}
