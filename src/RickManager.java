import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

import java.io.IOException ;

public class RickManager extends LayerManager
{
	//Layers to add
	private Auto myAuto;
	private Road myRoad ;
	private Traffic myTraffic;
	
	//Object of RickCanvas class
	private RickCanvas myRickCanvas;
	
	//FinishLine 
	private Sprite finishLine ;
	
	
	public RickManager(RickCanvas myRickCanvasArg) throws IOException{
		super();
		myRickCanvas = myRickCanvasArg;
		myAuto = new Auto();
		finishLine = new Sprite(Image.createImage("/finish_line.png"));
		myRoad = new Road();
		myTraffic = new Traffic(this);
		
		myAuto = new Auto();
		//myAuto.setPosition(100,150);
		append(myAuto);
		finishLine = new Sprite(Image.createImage("/finish_line.png"));
		finishLine.setPosition(33,-18015);
		append(finishLine);
		myRoad = new Road();
		append(myRoad);
		
		myTraffic = new Traffic(this);
	}
	
	
	/*private void drawStartUp(Graphics target){
		target.drawImage(trafficLight,0,0,Graphics.TOP|Graphics.LEFT);
	}*/
	//to initialise all the variables
	public void init(){
		myAuto.init(100,150);
		//append(myAuto);
		setViewWindow(0,Auto.yPos-150,RickCanvas.CANVAS_WIDTH,RickCanvas.CANVAS_HEIGHT);
		//finishLine.setPosition(33,-18015);
		//append(finishLine);
		myRoad.init();
		//append(myRoad);	
		myTraffic.init();		
	}
		
	public void cycle(long deltaMS,Graphics target){
                
		myAuto.advance(deltaMS);
		myAuto.checkCollision(myTraffic.traffic);
		myAuto.render();
		myAuto.checkGameFinished();
		myTraffic.addTraffic(deltaMS);
		myRoad.wrap(Auto.yPos-150);
		setViewWindow(0,Auto.yPos-150,RickCanvas.CANVAS_WIDTH,RickCanvas.CANVAS_HEIGHT);
	}
	
}
