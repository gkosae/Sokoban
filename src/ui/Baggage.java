package ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Baggage extends Actor {
	public boolean onGoal = false;
	
    public Baggage(int x, int y) {
        super(x, y);
        
        BufferedImage image;
		try {
			image = ImageIO.read(new File("images/baggage.png"));
			this.setImage(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void move(int x, int y) {
        int nx = this.x() + x;
        int ny = this.y() + y;
        this.setX(nx);
        this.setY(ny);
    }
    
    public boolean isCollidedWithActor(Actor actor){
    	if((this.x() == actor.x()) && (this.y() == actor.y()))return true;
    	return false;
    }
    
    public void setOnGoal(boolean onGoal){
    	this.onGoal = onGoal;
    }
    
    public boolean isOnGoal(){
    	return onGoal;
    }
    
    public void changeToGoalImage(){
    	if(onGoal){
    		 BufferedImage image;
    		 try {
    			image = ImageIO.read(new File("images/baggage_on_goal.png"));
    			this.setImage(image);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }
    
    public void changeToNonGoalImage(){
    	if(!onGoal){
    		 BufferedImage image;
    		 try {
    			image = ImageIO.read(new File("images/baggage.png"));
    			this.setImage(image);
    		 } catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		 }
    	}
    }
}
