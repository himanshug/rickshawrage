
import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.lcdui.Image;
import java.io.IOException;

public class Road extends TiledLayer{
	
	private static int ROAD_HEIGHT;
	private static int CYCLE = 16;
	
	private int wrapCounter,lastAutoYpos;
	
	
	public Road() throws IOException{
		super(1,16,Image.createImage("/road_strip.png"),176,52);
		for(int i=0;i<16;i++)
		{
			setCell(0,i,1);
		}
		
	}
	
	//Initialise everything 
	public void init(){
		ROAD_HEIGHT = getHeight();
		setPosition(0,-(ROAD_HEIGHT-RickCanvas.CANVAS_HEIGHT));
		wrapCounter = 0;
		lastAutoYpos = 0;
	}
	
        //wraping the road 	
        public void wrap(int auto){
		int dy = auto-lastAutoYpos;
		lastAutoYpos = auto;
		wrapCounter-=dy;		
		if(wrapCounter>416){
			move(0,-wrapCounter);
			wrapCounter = 0;
		}
	}
}
