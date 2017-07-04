package ui;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class Baggage extends Actor {
	public boolean onGoal = false;
	
    public Baggage(int x, int y) {
        super(x, y);
        URL loc = this.getClass().getResource("images/baggage.png");
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
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
    		URL loc = this.getClass().getResource("images/baggage_on_goal.png");
    		ImageIcon iia = new ImageIcon(loc);
            Image image = iia.getImage();
            this.setImage(image);
    	}
    }
    
    public void changeToNonGoalImage(){
    	if(!onGoal){
    		URL loc = this.getClass().getResource("images/baggage.png");
    		ImageIcon iia = new ImageIcon(loc);
            Image image = iia.getImage();
            this.setImage(image);
    	}
    }
}
