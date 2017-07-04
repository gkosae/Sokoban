package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.Timer;

import search_implementation.BoardState;
import search_implementation.NoSolutionException;

public class Board extends JPanel { 

    private final int OFFSET = 30;
    private final int SPACE = 40;
    private final int LEFT_COLLISION = 1;
    private final int RIGHT_COLLISION = 2;
    private final int TOP_COLLISION = 3;
    private final int BOTTOM_COLLISION = 4;

    private ArrayList walls = new ArrayList();
    private ArrayList baggs = new ArrayList();
    private ArrayList areas = new ArrayList();
    private Player soko;
    private int w = 0;
    private int h = 0;
    private boolean completed = false;

    private String mapString;
    private String mapFilePath;
    
    private String solution;
    private ArrayList<Character> moves = new ArrayList<Character>();
    private String actualMoves;
    
    private int animationDelay = 250;
    
    private Timer timer = new Timer(animationDelay, new TimerListener());
    
    public Board(){
    	setFocusable(true);
    }
    
    public Board(String mapFilePath) {

        //addKeyListener(new TAdapter());
        setFocusable(true);
        this.mapFilePath = mapFilePath;
        initWorld();
    }
    
    public void changeMap(String pathToMapFile){
    	mapFilePath = pathToMapFile;
    	restartLevel();
    }
    
    public void loadMap() throws IOException{
        Scanner scanner;
        try {
            scanner = new Scanner(Paths.get(mapFilePath));
            scanner.nextLine();
            scanner.nextLine();
            mapString = scanner.useDelimiter("\\Z").next();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setSolution(String solution){
    	this.solution = solution;
    	for(char str:solution.toCharArray()){
    		if(Character.isAlphabetic(str))moves.add(str);
    	}
    	System.out.println(solution);
    }

    public int getBoardWidth() {
        return this.w;
    }

    public int getBoardHeight() {
        return this.h;
    }

    public final void initWorld() {
    	try {
			loadMap();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        int x = OFFSET;
        int y = OFFSET;

        Wall wall;
        Baggage b;
        Area a;


        for (int i = 0; i < mapString.length(); i++) {

            char item = mapString.charAt(i);

            if (item == '\n') {
                y += SPACE;
                if (this.w < x) {
                    this.w = x;
                }

                x = OFFSET;
            } else if (item == '#') {
                wall = new Wall(x, y);
                walls.add(wall);
                x += SPACE;
            } else if (item == '$') {
                b = new Baggage(x, y);
                baggs.add(b);
                x += SPACE;
            } else if (item == '.') {
                a = new Area(x, y);
                areas.add(a);
                x += SPACE;
            } else if (item == '@') {
                soko = new Player(x, y);
                x += SPACE;
            } else if (item == '+'){ 
            	soko = new Player(x, y);
            	a = new Area(x,y);
            	areas.add(a);
            	x += SPACE;
            } else if (item == '*'){
            	b = new Baggage(x,y);
            	a = new Area(x,y);
            	baggs.add(b);
            	areas.add(a);
            	x += SPACE;
            } else if (item == ' ') {
                x += SPACE;
            }

            h = y;
        }
        
        onCompletion();
    }

    public void buildWorld(Graphics2D g) {
    	
    	g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g.setColor(new Color(250, 240, 170));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        ArrayList world = new ArrayList();
        world.addAll(walls);
        world.addAll(areas);
        world.addAll(baggs);
        world.add(soko);

        for (int i = 0; i < world.size(); i++) {

            Actor item = (Actor) world.get(i);

            if ((item instanceof Player)
                    || (item instanceof Baggage)) {
                g.drawImage(item.getImage(), item.x() + 2, item.y() + 2, this);
            } else {
                g.drawImage(item.getImage(), item.x(), item.y(), this);
            }

            if (completed) {
                g.setColor(new Color(0, 0, 0));
                g.drawString("Completed", 25, 20);
            }

        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        buildWorld((Graphics2D)g);
    }
    
    public void animateSolution(){
    	System.out.println(mapString);
    	System.out.println();
    	timer.start();
    }
    
    public void stopAnimation(){
    	timer.stop();
    }
    
    class TimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0){
			if(moves.isEmpty())return;
			char move = moves.get(0);
			moves.remove(0);
			
			if(move == 'l'){
				if (checkWallCollision(soko,
                        LEFT_COLLISION)) {
                    return;
                }

                if (checkBagCollision(LEFT_COLLISION)) {
                    return;
                }
                
//                System.out.print("l, ");
                soko.move(-SPACE, 0);

			} else if(move == 'r'){
				if (checkWallCollision(soko,
                        RIGHT_COLLISION)) {
                    return;
                }

                if (checkBagCollision(RIGHT_COLLISION)) {
                    return;
                }
                
//                System.out.print("r, ");
                soko.move(SPACE, 0);
				
			} else if(move == 'u'){
				if (checkWallCollision(soko,
                        TOP_COLLISION)) {
                    return;
                }

                if (checkBagCollision(TOP_COLLISION)) {
                    return;
                }
                
//                System.out.print("u, ");
                soko.move(0, -SPACE);

			} else if(move == 'd'){
				 if (checkWallCollision(soko,
	                        BOTTOM_COLLISION)) {
	                    return;
	                }

	                if (checkBagCollision(BOTTOM_COLLISION)) {
	                    return;
	                }
	                
//	                System.out.print("d, ");
	                soko.move(0, SPACE);
			}
			repaint();
			if(completed)timer.stop();
		}
    }
    
    public String getActualMoves(){
    	return actualMoves;
    }
    
    private boolean checkWallCollision(Actor actor, int type) {

        if (type == LEFT_COLLISION) {

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = (Wall) walls.get(i);
                if (actor.isLeftCollision(wall)) {
                    return true;
                }
            }
            return false;

        } else if (type == RIGHT_COLLISION) {

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = (Wall) walls.get(i);
                if (actor.isRightCollision(wall)) {
                    return true;
                }
            }
            return false;

        } else if (type == TOP_COLLISION) {

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = (Wall) walls.get(i);
                if (actor.isTopCollision(wall)) {
                    return true;
                }
            }
            return false;

        } else if (type == BOTTOM_COLLISION) {

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = (Wall) walls.get(i);
                if (actor.isBottomCollision(wall)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean checkBagCollision(int type) {

        if (type == LEFT_COLLISION) {

            for (int i = 0; i < baggs.size(); i++) {

                Baggage bag = (Baggage) baggs.get(i);
                if (soko.isLeftCollision(bag)) {

                    for (int j=0; j < baggs.size(); j++) {
                        Baggage item = (Baggage) baggs.get(j);
                        if (!bag.equals(item)) {
                            if (bag.isLeftCollision(item)) {
                                return true;
                            }
                        }
                        
                        if (checkWallCollision(bag,
                                LEFT_COLLISION)) {
                            return true;
                        }
                    }
                    bag.move(-SPACE, 0);
                    onCompletion();
                }
            }
            return false;

        } else if (type == RIGHT_COLLISION) {

            for (int i = 0; i < baggs.size(); i++) {

                Baggage bag = (Baggage) baggs.get(i);
                if (soko.isRightCollision(bag)) {
                    for (int j=0; j < baggs.size(); j++) {

                        Baggage item = (Baggage) baggs.get(j);
                        if (!bag.equals(item)) {
                            if (bag.isRightCollision(item)) {
                                return true;
                            }
                        }
                        if (checkWallCollision(bag,
                                RIGHT_COLLISION)) {
                            return true;
                        }
                    }
                    bag.move(SPACE, 0);
                    onCompletion();                   
                }
            }
            return false;

        } else if (type == TOP_COLLISION) {

            for (int i = 0; i < baggs.size(); i++) {

                Baggage bag = (Baggage) baggs.get(i);
                if (soko.isTopCollision(bag)) {
                    for (int j = 0; j < baggs.size(); j++) {

                        Baggage item = (Baggage) baggs.get(j);
                        if (!bag.equals(item)) {
                            if (bag.isTopCollision(item)) {
                                return true;
                            }
                        }
                        if (checkWallCollision(bag,
                                TOP_COLLISION)) {
                            return true;
                        }
                    }
                    bag.move(0, -SPACE);
                    onCompletion();
                }
            }

            return false;

        } else if (type == BOTTOM_COLLISION) {

            for (int i = 0; i < baggs.size(); i++) {

                Baggage bag = (Baggage) baggs.get(i);
                if (soko.isBottomCollision(bag)) {
                    for (int j = 0; j < baggs.size(); j++) {

                        Baggage item = (Baggage) baggs.get(j);
                        if (!bag.equals(item)) {
                            if (bag.isBottomCollision(item)) {
                                return true;
                            }
                        }
                        if (checkWallCollision(bag,
                                BOTTOM_COLLISION)) {
                            return true;
                        }
                    }
                    bag.move(0, SPACE);
                    onCompletion();
                }
            }
        }

        return false;
    }

    public boolean isCompleted(){
    	return completed;
    }
    
    public void onCompletion() {

        int num = baggs.size();
        int compl = 0;

        for (int i = 0; i < num; i++) {
            Baggage bag = (Baggage) baggs.get(i);
            for (int j = 0; j < num; j++) {
                Area area = (Area) areas.get(j);
                if (bag.x() == area.x() && bag.y() == area.y()) {
                	      bag.setOnGoal(true);
                	      bag.changeToGoalImage();
                	      compl += 1;
                	      break;
                } else {
                	bag.setOnGoal(false);
                	bag.changeToNonGoalImage();
                }                	
            }
        }

        if (compl == num) {
            completed = true;
            repaint();
        }
    }

    public void restartLevel() {

        areas.clear();
        baggs.clear();
        walls.clear();
        initWorld();
        if (completed) {
            completed = false;
        }
        
        if(timer.isRunning())timer.stop();
        repaint();
    }
}