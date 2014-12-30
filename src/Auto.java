import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image ;
import javax.microedition.lcdui.game.Sprite;
import java.io.IOException;

public class Auto extends Sprite
{ 
	//Position of vehicle
	 static int xPos,yPos;
	
	//speed of vehicle
	  static double yVel;
	  private double xVel;
	  
	//score
	 static int score;
	  
	  //maximum speed 
	  private float max_yVel = 20;
		
	 //this holds which key is pressed
	 static int keyPressed ;
	 
	 //holding the value of current frame
	 private int currentFrame;
	 
	 //How much fuel does auto have
	 public static int fuel;
	 
	 //which state is Auto in
	 public static boolean outOfFuelState ;
	 public static boolean finishLineReachedState ;
	 private boolean dead ;
	 private boolean deadVisibility;
	 int deadTime;
	 
	 //Variables for checking collisions
	 boolean whetherToCheckCollision;
	 boolean fromBelow;
	 
	//to remember the missed bit of times
	private long fluff;
	
	public Auto() throws IOException {
		super(Image.createImage("/auto1.png"),22,28);
		/*fuel = 90;
		xPos = x;
		yPos = y;
		yVel = 0;*/
	}
	
	//to initialise all the variables
	public void init(int x,int y){
	    score = 0;
		fuel = 90;
		yVel = 0;
		xPos = x;
		yPos = y;
		//set initial position
		setPosition(xPos,yPos);
		
		outOfFuelState = false;
		finishLineReachedState = false;
		dead = false;
		deadVisibility = false;
		whetherToCheckCollision = true;
		currentFrame = 0;
		setFrame(currentFrame);
		setVisible(true);
	}
		
	//to render the current frame and position of auto on the screen
	public void render(){
		setFrame(currentFrame);
		setPosition(xPos,yPos);
	}
	
	//checks if out of fuel or finish line has reached
	public void checkGameFinished(){
	  if(!finishLineReachedState){
		if(fuel<=0){
			outOfFuelState = true;
			currentFrame = 3;
			setVisible(true);
		}
	  
		if(yPos<=-18000){
			finishLineReachedState = true;
			currentFrame = 0;
			setVisible(true);
		}
	  }
	}
	
	//For checking collision of Auto with traffic vehicles and pot hole and fuel
	public void checkCollision(Sprite[] myTrafficArg){
		boolean result=false;
		if(whetherToCheckCollision){
		for(int i =0;i<myTrafficArg.length;i++){
			if(i==4) continue;  //No need to check collision with fuel ,we're doing it seperately.
			if((myTrafficArg[i].getY()<=(yPos+33)) && myTrafficArg[i].getY()>=((yPos-5)-myTrafficArg[i].getHeight())){
				result = collidesWith(myTrafficArg[i],true);
				if(result){
					if(yPos<myTrafficArg[i].getY()) fromBelow = true;
					else fromBelow = false;
					responseToCollision(i,myTrafficArg[i]);
					//return result;
				}
			}
		}
		}
		//Now check collision with fuel
		checkCollisionWithFuel(myTrafficArg[4]);
	}
	
	//Response if there is any collision.
	private void responseToCollision(int num,Sprite fuel){
		whetherToCheckCollision = false;
		switch(num){
			//if colliding vehicle is car reduce speed of Auto by 70%
			case 0:
			   yVel-=(70*yVel/100);
			   currentFrame = 2;
			   deadTime =40;
			   dead = true;
			   //Giving a bounce on collision 
			   if(fromBelow) xPos-=5;
			   else xPos+=5;
			   break;
			//if colliding vehicle is taxi reduce speed of Auto by 70%
			case 1:
			   yVel-=(70*yVel/100);
			    currentFrame = 2;
			    deadTime =40;
			   dead = true;
			   //Giving a bounce on collision 
			   if(fromBelow) xPos-=5;
			   else xPos+=5;
			   break;
			//if colliding vehicle is bus reduce speed of Auto by 80%
			case 2:
			   yVel-=(80*yVel/100);
			    currentFrame = 2;
			    deadTime =40;
			   dead = true;
			   //Giving a bounce on collision 
			   if(fromBelow) xPos-=5;
			   else xPos+=5;
			   break;
			//if colliding vehicle is bull-cart reduce speed of Auto by 60%
			case 3:
			   yVel-=(60*yVel/100);
			    currentFrame = 2;
			    deadTime =40;
			   dead = true;
			   //Giving a bounce on collision 
			   if(fromBelow) xPos-=5;
			   else xPos+=5;
			   break;
			//if colliding with PotHole, reduce speed of Auto by 40%
			case 5:
			   yVel-=(20*yVel/100);
			   currentFrame = 2;
			   dead = true;
			   deadTime = 40;
		}
	}
			   
	//Checking if the fuel is taken
	private void checkCollisionWithFuel(Sprite fuelArg){
		boolean result=false;
		if((fuelArg.getY()<=(yPos+33)) && fuelArg.getY()>=((yPos-5)-fuelArg.getHeight())){
			result = collidesWith(fuelArg,true);
			if(result){
				//Increase fuel and score by 5.
				fuel+=5;
				score+=5;
				fuelArg.setVisible(false);
				Traffic.fuelTaken = true;
			}
		}
	}
			
				
	
	public void advance(long deltaMS){
		
		int ticks = (int)(deltaMS+fluff)/100;
		
		//remember the small times we miss
		fluff+=deltaMS-ticks*100;
		
		if(ticks>0){
		 if(outOfFuelState || finishLineReachedState){
			 if(finishLineReachedState) yVel=0;
			 else yVel-=1;
			}
		  else{
			if(dead){
				 deadTime+=ticks;
				 deadVisibility=!deadVisibility;
				 if(currentFrame == 4||currentFrame == 5)currentFrame = 3;
				if(deadTime>40){
				currentFrame++;
				deadTime=0;
				}
				if(currentFrame>3){
					currentFrame = 0;
					dead = false;
					deadVisibility = true;
					whetherToCheckCollision = true;
				}
				setVisible(deadVisibility);
				
				
			}
			else{
			//taking the key events
			switch(keyPressed){
				//Up(NumKey 2) pressed
				case 2:
				    if(yVel<max_yVel/3) yVel+=0.5;
				    else{
					    if(yVel<max_yVel/1.5) yVel+=0.3;
					    else{
						    if(yVel<max_yVel) yVel+=0.2;
					    }
				    }
				    currentFrame = 0;
				    break;
				 //Down(NumKey 8) pressed
				 case 8:
				    yVel-=0.4;
				    if(yVel<0) yVel = 0;
				    currentFrame = 0;
				    break;
				    //yVel = 0;
				 //No keys pressed
				 case -1:
				    currentFrame = 0;
				    break;
				case 5:
				   yVel=0;
				   currentFrame = 0;
				   break;
			   }
			}
			switch(keyPressed){
				//Left(NumKey 4) pressed
				case 4:
				if(dead){
					//if dead then move it laterally with constant speed no matter whatz rhe vertical speed.
					xVel = -2;
					currentFrame = 4;
				}
				   else{
				    xVel = -yVel/3;
				    if(yVel!=0 && yVel>1){
					    if(xVel>-1) xVel = -1;
				    }
				    currentFrame = 1;	    
				   }			   
				    break;
				//Right(NumKey 6) Pressed.
				case 6:
				if(dead){
					//if dead then move it laterally with constant speed no matter whatz rhe vertical speed.
					xVel = 2;
					currentFrame = 5;
				}else
				{
				    xVel = yVel/3;
				    if(yVel!=0 && yVel>1){
					    if(xVel<1) xVel = 1;
				    }
				   // System.out.println("xVel="+xVel+" yVel="+);
					    currentFrame = 2;
				}
				    break;
				//Up(NumKey 2) pressed
				
			   }
		      }
				    
				    
			if(yVel>0){
				if(yVel>max_yVel){
					//System.out.println("Maximum reached.");
					yVel = max_yVel;
				}
				yPos-=(int)yVel*ticks;
			}
			if(xVel!=0){
				xPos+=(int)xVel*ticks;
				xVel=0;
			}
			

                        //reset the keyPress
                               keyPressed = -1;			
			//check if it's out of the edges of the road.
			if(xPos<(RickCanvas.CANVAS_WIDTH/2-54)) xPos = (RickCanvas.CANVAS_WIDTH/2-54);
			else{
				if(xPos>(RickCanvas.CANVAS_WIDTH/2+50-22)) xPos = (RickCanvas.CANVAS_WIDTH/2+50-22);
			}
	    }
	}
}
