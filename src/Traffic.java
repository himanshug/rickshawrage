import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image ;
import javax.microedition.lcdui.game.Sprite;
import java.util.Random;


public class Traffic
{
	
	Sprite[] traffic;
	private RickManager myRickManager;
	private Random myRandom;
	
	
	//Whether to put a new traffic or PotHole object 
	private boolean createVehicle;
	private boolean createPotHole;
	
	
	//holding the position of view window
	private int myCurrentY;
	
	//How many traffic vehicles to put
	private int vehicleAtOneTime ;
	
	//When to start shifting Bus or car laterally.
	private int startShiftX_bus,startShiftX_car;
	
	//Distance by which bus or car is to be shifted laterally
	private int distShiftX_car,distShiftX_bus;
	
	//which vehicles are under use
	private int vehicle1,vehicle2;
	
	//Velocity of traffic vehicle
	private double yVel1,yVel2,yVel;
	
	//for putting fuel
	private int fuelPut,fuelLastCreated;
	private long fuelTime;
	private boolean blink;
	public static boolean fuelTaken ;
	
	//to remember small times
	private long fluff;
	
	
	public Traffic(RickManager rickManagerArg)throws IOException{
		myRickManager = rickManagerArg;
		myRandom = new Random();
		traffic = new Sprite[6];
		//Car
		traffic[0] = new Sprite(Image.createImage("/car.png"));
		//Taxi
		traffic[1] = new Sprite(Image.createImage("/taxi.png"));
		//Bus
		traffic[2] = new Sprite(Image.createImage("/bus.png"));
		//Bullock-cart
		traffic[3] = new Sprite(Image.createImage("/bull-cart.png"));
		//Fuel
		traffic[4] = new Sprite(Image.createImage("/fuel.png"));
		//Pot-hole
		traffic[5] = new Sprite(Image.createImage("/pothole.png"));
		
	}
	
	//To initialise all the variables
	public void init(){
		createVehicle = true;
		createPotHole = true;
		fuelTaken = false;
		fuelLastCreated = 0;
		myCurrentY = 0;
		//Insert all of these in RickManager 
		for(int i=0;i<6;i++){
			myRickManager.insert(traffic[i],i+1);
		        traffic[i].setVisible(false);
		}
	}
		
	
	//Generate a random number between num1 and num2
	private int generateRandom(int num1,int num2){
		return (myRandom.nextInt(num2-num1)+num1);
	}
	
	//Initialise the vehicle
	private void init(int i,int y1,int y2){
		traffic[i].setVisible(true);
		myCurrentY = Auto.yPos-150;
		switch(i){
			//if it's a bus, create in right side.
			case 2:
			   traffic[i].setPosition(generateRandom(88,113),myCurrentY-generateRandom(y1,y2));
                              yVel=11.5;
			      startShiftX_bus = traffic[i].getY()-generateRandom(200,250);
			      distShiftX_bus = generateRandom(35,traffic[i].getX()-30);
			   break;
			//if it's a car, create in left side. 
			case 0:
			   traffic[i].setPosition(generateRandom(35,72),myCurrentY-generateRandom(y1,y2));
			     yVel = 13;
			      startShiftX_car = traffic[i].getY()-generateRandom(300,400);
			      distShiftX_car = generateRandom(88,122);
			   break;
			//if it's a taxi, create in any side. 
			case 1:
			   traffic[i].setPosition(generateRandom(35,121),myCurrentY-generateRandom(y1,y2));
			     yVel = 12;
			   break;
			//if it's a bull-cart, create in right side. 
			case 3:
			   traffic[i].setPosition(generateRandom(88,113),myCurrentY-generateRandom(y1,y2));
			     yVel = 9;
			   break;
			//Fuel
			case 4:
			   traffic[i].setPosition(generateRandom(58,118),myCurrentY-generateRandom(y1,y2));
			   fuelTime = System.currentTimeMillis();
			   fuelTaken = false;
			   break;
			//Pot Hole
			case 5:
			   traffic[i].setPosition(generateRandom(35,121),myCurrentY-generateRandom(y1,y2));
			   break;
		}
	}
	
	public void addTraffic(long deltaMS){
		
		if(createVehicle){
			createVehicle = false;
			//either 1 or 2 vehicle at one time.
			int num = generateRandom(0,10);
			if(num<5) 
			     vehicleAtOneTime = 2;
			else 
			     vehicleAtOneTime = 1;
			
			//create traffic vehicles accordingly.
			if(vehicleAtOneTime==1){
				vehicle1 = generateRandom(0,4);
				init(vehicle1,70,80);
				yVel1=yVel;
			}
			if(vehicleAtOneTime==2){
				vehicle1 = generateRandom(0,4);
					while(true){				
					    vehicle2 = generateRandom(0,4);
					    if(vehicle1!=vehicle2) break;
					}
				if(vehicle1<vehicle2){
					int swap = vehicle1;
					vehicle1 = vehicle2;
					vehicle2 = swap;
				}
				init(vehicle1,70,80);
				yVel1 = yVel;
				//if vehicle1 is bus then create other vehicle far enough from it to avoid collision
				if(vehicle1==2)  init(vehicle2,160,180);
				else init(vehicle2,120,140);
				yVel2 = yVel;
				
				}
			} 
			cycle(deltaMS);
			addPotHole();
			addFuel();
		}
	
	//for the lateral movement of car and bus
	private void moveTraffic(int vehicleNum,int ticks){
		switch(vehicleNum){
			case 0:
			if(traffic[vehicleNum].getY()<=startShiftX_car){
				if(traffic[vehicleNum].getX()<=distShiftX_car)
			             traffic[vehicleNum].move(2*ticks,-5*ticks);
			}
			    break;		
			case 2:
			if(traffic[vehicleNum].getY()<=startShiftX_bus){
			      if(traffic[vehicleNum].getX()>=distShiftX_bus)
			           traffic[vehicleNum].move(-2*ticks,-(int)3*ticks);
			}
			    break;
		}
	}
	
	private void cycle(long deltaMS){
		int ticks = (int)(deltaMS+fluff)/100;
		fluff+=deltaMS-ticks*100;
		myCurrentY = Auto.yPos-150;
		
		switch(vehicleAtOneTime){
			
			case 2:
			if((traffic[vehicle2].getY()<=(myCurrentY+215) && traffic[vehicle2].getY()>=(myCurrentY-traffic[vehicle2].getHeight()))||
			           (traffic[vehicle1].getY()<=(myCurrentY+215) &&traffic[vehicle1].getY()>=(myCurrentY-traffic[vehicle1].getHeight()))){
				traffic[vehicle2].move(0,-(int)yVel2*ticks);
				moveTraffic(vehicle2,ticks);
                       
			}
			if(traffic[vehicle2].getY()>(myCurrentY+230)){
				//System.out.println("ceate tru executed");
				createVehicle=true;
			}
			   
		        case 1:
		        if((traffic[vehicle2].getY()<=(myCurrentY+215)&&(traffic[vehicle1].getY()>=(myCurrentY+215)))||
			           (traffic[vehicle1].getY()<=(myCurrentY+215) &&traffic[vehicle1].getY()>=(myCurrentY-traffic[vehicle1].getHeight()))){
				traffic[vehicle1].move(0,-(int)yVel1*ticks);
				moveTraffic(vehicle1,ticks);
			}
			if(vehicleAtOneTime==1){
				if(traffic[vehicle1].getY()>(myCurrentY+230)){
				//System.out.println("ceate tru executed");
				createVehicle=true;
			}
			}
			break;
			   
		}
		
		 //Move fuel with some speed.if it's in the view window
		 if((traffic[4].getY()<=(myCurrentY+215) && traffic[4].getY()>=(myCurrentY-traffic[4].getHeight()))){
			 traffic[4].move(0,-5*ticks);
		 }
		 
	}
	
	private void addPotHole(){
		int num = generateRandom(0,35);
		if(num==0){
		if(createPotHole){
			createPotHole = false;
			init(5,10,50);
		}
		else{
			if(traffic[5].getY()>(myCurrentY+208))
				createPotHole = true;
		}
		}
		
	}
	
	private void addFuel(){
		fuelPut += (fuelLastCreated-myCurrentY);
		fuelLastCreated = myCurrentY;
		if((System.currentTimeMillis()-fuelTime>7000) && !fuelTaken){
			if(System.currentTimeMillis()-fuelTime>10000) traffic[4].setVisible(false);
			else{
				traffic[4].setVisible(blink);
			        blink = !blink;
			}
		}
		if(fuelPut>1700){
			init(4,20,22);
			fuelPut=0;
		}
	}
		
}
