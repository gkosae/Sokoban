package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;

import search_implementation.Algorithms;
import search_implementation.NoSolutionException;
import search_implementation.SearchStatistics;
import search_implementation.SokobanSolver;


public final class Sokoban extends JFrame {
	
    private final int OFFSET = 20;
    
    private final String SOLUTION = "1";
    private final String STATISTICS = "2";
    private final String LOG = "3";
    
    private Component superRoot = this;
    
    //flags
    private boolean boxHeuristic = false;
    private boolean manhattanHeuristic = false;
    private boolean greedyBestFirst = false;
    private boolean aStar = false;
    
    //Menus
    private JMenu heuristicsMenu;
    private JMenu mapsMenu;
    private JMenu algorithmsMenu;
    private JMenu runMenu;
    
    private Board board;
    private String algorithm;
    private SearchStatistics searchStats;
    private String selectedMapFile;
    
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);
    private JPanel infoArea = new JPanel(new BorderLayout());
    private JTextArea solutionArea = new JTextArea();
    private JTextArea searchStatsArea = new JTextArea();
    private JTextArea logArea = new JTextArea();
    
    private GridBagLayout layout = new GridBagLayout();
    private JPanel gridBagPanel = new JPanel(layout);
    
    private GridBagConstraints constraints = new GridBagConstraints();
    
    private SokobanSolver solver;
    private HashMap<String,String>maps = new HashMap<String, String>();
    private String map;
    private String solution;
    
    private JButton solutionButton, statsButton, logButton;
    
    public Sokoban() {
        InitUI();
    }

    public void InitUI() {
    	
    	loadMaps();
    	board = new Board(map);
    	board.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
    	
    	JPanel boardPanel = new JPanel();
    	boardPanel.add(board);
    	
    	createMenubar();
    	buildInfoArea();
    	
    	constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 10;
    	addToMainPanel(board, 0, 0, 1, 1);
    	
    	constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 2;
    	addToMainPanel(infoArea, 1, 0, 1, 0);
    	
    	add(gridBagPanel);
                
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(board.getBoardWidth() + 2*OFFSET,
                (board.getBoardHeight()) + board.getBoardHeight());
        setLocationRelativeTo(null);
        setTitle("Sokoban");
    }
    
    private void addToMainPanel(Component component, int row, int column, int width, int height){
		
		constraints.gridx = column; // set gridx
		constraints.gridy = row; // set gridy
		constraints.gridwidth = width; // set gridwidth
		constraints.gridheight = height; // set gridheight
		layout.setConstraints( component, constraints ); // set constraints
		gridBagPanel.add( component ); // add component
	}
    
    private void createMenubar(){
    	
    	buildHeuristicsMenu(); //Has to come first cos its used in buildAlgorithmsMenu()
    	buildAlgorithmsMenu();
    	buildMapsMenu();
    	buildRunMenu();
    	
    	JMenuBar menubar = new JMenuBar();
    	menubar.add(algorithmsMenu);
    	menubar.add(heuristicsMenu);
    	menubar.add(mapsMenu);
    	menubar.add(runMenu);
    
    	setJMenuBar(menubar);
    }
    
    boolean solutionFound = false;
    
    private void buildRunMenu() {
    	JMenuItem solveItem = new JMenuItem("Find Solution");
    	JMenuItem animateItem = new JMenuItem("Animate Solution");
    	
    	solveItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				if(greedyBestFirst && boxHeuristic)algorithm = Algorithms.GREEDY_BEST_FIRST_BOX_GOAL_HEURISTIC;
				if(greedyBestFirst && manhattanHeuristic)algorithm = Algorithms.GREEDY_BEST_FIRST_MANHATTAN_HEURISTIC;
				if(aStar && boxHeuristic)algorithm = Algorithms.ASTAR_BOX_GOAL_HEURISTIC;
				if(aStar && manhattanHeuristic)algorithm = Algorithms.ASTAR_MANHATTAN_HEURISTIC;
				
				try {
					solver = new SokobanSolver(algorithm, map);
					searchStats = solver.getSearchStats();
					solution = searchStats.getSolution();
					solutionFound = true;
					
					updateSolutionArea();
					updateSearchStatsArea();
					switchTo(SOLUTION);
					writeToLog("Solution found");
					
				} catch (OutOfMemoryError e) {
					writeToLog("Out of Memory");
					switchTo(LOG);
				} catch (IOException e) {
					writeToLog("Puzzle file not found");
					switchTo(LOG);
				} catch (NoSolutionException e) {
					writeToLog("No solution found");
					switchTo(LOG);
				}
			}
    		
    	});
    	
    	animateItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				if(!board.isCompleted()){
					if(solutionFound){
						board.setSolution(solution);
						board.animateSolution();
						writeToLog("Solution animated");
						board.setSolution("");
					}
				}	
			}
    	});
    	
    	runMenu = new JMenu("Run");
    	runMenu.add(solveItem);
    	runMenu.add(animateItem);
    	runMenu.setMnemonic(KeyEvent.VK_R);
    }
    
    private void buildAlgorithmsMenu(){
    	JRadioButtonMenuItem depthFirstItem = new JRadioButtonMenuItem("Depth-First");
    	JRadioButtonMenuItem breadthFirstItem = new JRadioButtonMenuItem("Breadth-First");
    	JRadioButtonMenuItem uniformCostItem = new JRadioButtonMenuItem("Uniform-Cost");
    	JRadioButtonMenuItem greedyBestFirstItem = new JRadioButtonMenuItem("Greedy Best-First");
    	JRadioButtonMenuItem aStarItem = new JRadioButtonMenuItem("A*");
    	
    	depthFirstItem.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent event) {
				if(event.getStateChange() == ItemEvent.SELECTED){
					algorithm = Algorithms.DEPTH_FIRST;
					heuristicsMenu.setEnabled(false);
					clearInfoArea();
				}
			}
    	});
    	
    	depthFirstItem.setSelected(true);
    	
    	breadthFirstItem.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent event) {
				if(event.getStateChange() == ItemEvent.SELECTED){
					algorithm = Algorithms.BREADTH_FIRST;
					heuristicsMenu.setEnabled(false);
					solutionFound = false;
					clearInfoArea();
				}
			}
    	});
    	
    	uniformCostItem.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent event) {
				if(event.getStateChange() == ItemEvent.SELECTED){
					algorithm = Algorithms.UNIFORM_COST;
					heuristicsMenu.setEnabled(false);
					solutionFound = false;
					clearInfoArea();
				}
			}
    	});
    	
    	greedyBestFirstItem.addItemListener(new ItemListener(){
    		public void itemStateChanged(ItemEvent event){
    			if(event.getStateChange() == ItemEvent.SELECTED){
    				greedyBestFirst = true;
    				heuristicsMenu.setEnabled(true);
    				solutionFound = false;
    				clearInfoArea();
    			}
    			if(event.getStateChange() == ItemEvent.DESELECTED){
    				greedyBestFirst = false;
    				clearInfoArea();
    			}
    		}
    	});
    	
    	aStarItem.addItemListener(new ItemListener(){
    		public void itemStateChanged(ItemEvent event){
    			if(event.getStateChange() == ItemEvent.SELECTED){
    				aStar = true;
    				heuristicsMenu.setEnabled(true);
    				solutionFound = false;
    				clearInfoArea();
    			}
    			if(event.getStateChange() == ItemEvent.DESELECTED){
    				aStar = false;
    				clearInfoArea();
    			}
    		}
    	});
    	
    	ButtonGroup algoGroup = new ButtonGroup();
    	algoGroup.add(depthFirstItem);
    	algoGroup.add(breadthFirstItem);
    	algoGroup.add(uniformCostItem);
    	algoGroup.add(greedyBestFirstItem);
    	algoGroup.add(aStarItem);
    	
    	algorithmsMenu = new JMenu("Algorithm");
    	algorithmsMenu.add(depthFirstItem);
    	algorithmsMenu.add(breadthFirstItem);
    	algorithmsMenu.add(uniformCostItem);
    	algorithmsMenu.add(greedyBestFirstItem);
    	algorithmsMenu.add(aStarItem);
    	algorithmsMenu.setMnemonic(KeyEvent.VK_A);
    }
    
    private void buildHeuristicsMenu(){
    	JRadioButtonMenuItem boxNumberItem = new JRadioButtonMenuItem("Number of boxes on goal");
    	JRadioButtonMenuItem manhattanItem = new JRadioButtonMenuItem("Manhattan");
    	
    	boxNumberItem.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent event) {
				if(event.getStateChange() == ItemEvent.SELECTED)boxHeuristic = true;
				if(event.getStateChange() == ItemEvent.DESELECTED)boxHeuristic = false;
				solutionFound = false;
				clearInfoArea();
			}
    	});
    	
    	manhattanItem.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent event) {
				if(event.getStateChange() == ItemEvent.SELECTED)manhattanHeuristic = true;
				if(event.getStateChange() == ItemEvent.DESELECTED)manhattanHeuristic = false;
				solutionFound = false;
				clearInfoArea();
			}
    	});
    	
    	ButtonGroup heuriGroup = new ButtonGroup();
    	heuriGroup.add(boxNumberItem);
    	heuriGroup.add(manhattanItem);
    	
    	heuristicsMenu = new JMenu("Heuristic");
    	heuristicsMenu.add(boxNumberItem);
    	heuristicsMenu.add(manhattanItem);
    	heuristicsMenu.setMnemonic(KeyEvent.VK_H);
    	
    	boxNumberItem.setSelected(true);
    }
    
    private void buildMapsMenu(){
    	mapsMenu = new JMenu("Map");
    	mapsMenu.setMnemonic(KeyEvent.VK_M);
    	refreshMapsMenu();
    }
    
    private void refreshMapsMenu(){
    	
    	mapsMenu.removeAll();
    	
    	Set<Entry<String, String>> entries = maps.entrySet();
    	JRadioButtonMenuItem item;
    	
    	ButtonGroup mapsGroup = new ButtonGroup();
    	
    	boolean firstItem = true;
    	for(Entry<String, String> entry : entries){
    		item = createMapMenuItem(entry.getKey());
    		mapsGroup.add(item);
    		
    		if(firstItem){
    			firstItem = false;
    			item.setSelected(true);
    		}
 
    		mapsMenu.add(item);
    	}
    	
    	JMenuItem addMapItem = new JMenuItem("Add a new Map");
    	addMapItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				JFileChooser mapChooser = new JFileChooser();
				
				File mapsFolder = new File("maps");
				if(mapsFolder.exists())mapChooser.setCurrentDirectory(new File("maps"));
				else 
					mapChooser.setCurrentDirectory(new File("."));
				
				if(mapChooser.showOpenDialog(superRoot) == JFileChooser.APPROVE_OPTION){
					File newMap = mapChooser.getSelectedFile();
					if(maps.containsValue(newMap.getAbsolutePath())){}
					else {
						maps.put(newMap.getName(), newMap.getAbsolutePath());
						refreshMapsMenu();
					}
				}
				
			}
		});
    	
    	mapsMenu.addSeparator();
    	mapsMenu.add(addMapItem);
    	
    	JMenuItem resetMapItem = new JMenuItem("Reset Map");
    	resetMapItem.addActionListener(new ActionListener(){
    		
    		@Override
    		public void actionPerformed(ActionEvent event){
    			board.restartLevel();
    			solutionFound = false;
    			clearInfoArea();
    		}
    	});
    	
    	mapsMenu.add(resetMapItem);
    }
     
    private void clearInfoArea(){
    	solutionArea.setText("");
		searchStatsArea.setText("");
    }
    
    private JRadioButtonMenuItem createMapMenuItem(String text){
    	
    	JRadioButtonMenuItem item = new JRadioButtonMenuItem(text);
		item.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent event){
				if(event.getStateChange() == ItemEvent.SELECTED){
					JRadioButtonMenuItem source = (JRadioButtonMenuItem)event.getSource();
					map = maps.get(source.getText());
					selectedMapFile = source.getText();
					board.changeMap(map);
					solutionFound = false;
					clearInfoArea();
				}
			}
		});
		
		return item;
    }
    
    private void loadMaps(){
    	File mapsFolder = new File("maps");
    	File[] mapFiles = mapsFolder.listFiles();
    	for(File file : mapFiles)maps.put(file.getName(), file.getAbsolutePath());
    	
    	map = mapFiles[0].getAbsolutePath();
    	selectedMapFile = mapFiles[0].getName();
    }
    
    private void buildInfoArea(){    	
    	
    	searchStatsArea.setBorder(BorderFactory.createEmptyBorder());
    	cardPanel.add(new JScrollPane(solutionArea), SOLUTION);
    	cardPanel.add(new JScrollPane(searchStatsArea), STATISTICS);
    	cardPanel.add(new JScrollPane(logArea), LOG);
    	
    	solutionButton = new JButton("<html><div style=\"font-size:9px\"><b><i>Solution</i></b></div>");
    	statsButton = new JButton("<html><div style=\"font-size:9px\"><b><i>Search Stats</i></b></div>");
    	logButton = new JButton("<html><div style=\"font-size:9px\"><b><i>Log</i></b></div>");
    	
    	cardLayout.show(cardPanel, SOLUTION);
    	solutionButton.setContentAreaFilled(false);
    	
    	solutionButton.addActionListener(new ActionListener(){
    		
    		@Override
    		public void actionPerformed(ActionEvent event){
    			switchTo(SOLUTION);
    		}
    	});
    	
    	statsButton.addActionListener(new ActionListener(){
    		
    		@Override
    		public void actionPerformed(ActionEvent event){
    			switchTo(STATISTICS);
    		}
    	});
    	
    	logButton.addActionListener(new ActionListener(){
    		
    		@Override
    		public void actionPerformed(ActionEvent event){
    			switchTo(LOG);
    		}
    	});
    	
    	JPanel buttonPane = new JPanel(new GridLayout(1,3));
    	buttonPane.add(solutionButton);
    	buttonPane.add(statsButton);
    	buttonPane.add(logButton);
    	buttonPane.setBorder(BorderFactory.createEmptyBorder(5,20,0,20));
    	
    	infoArea.add(buttonPane, BorderLayout.NORTH);
    	infoArea.add(cardPanel);
    	configureInfoArea();
    }
    
    private void switchTo(String id){
    	if(id.equals(SOLUTION)){
    		cardLayout.show(cardPanel, SOLUTION);
			solutionButton.setContentAreaFilled(false);
			statsButton.setContentAreaFilled(true);
			logButton.setContentAreaFilled(true);
    	} else if(id.equals(STATISTICS)){
    		cardLayout.show(cardPanel, STATISTICS);
			solutionButton.setContentAreaFilled(true);
			statsButton.setContentAreaFilled(false);
			logButton.setContentAreaFilled(true);
    	} else if(id.equals(LOG)){
    		cardLayout.show(cardPanel, LOG);
			solutionButton.setContentAreaFilled(true);
			statsButton.setContentAreaFilled(true);
			logButton.setContentAreaFilled(false);
    	}
    }
    
    private void configureInfoArea(){
    	
    	infoArea.setBorder(BorderFactory.createEmptyBorder(0,20,5,20));
    	
    	//solutionArea settings
    	solutionArea.setFont(new Font("", Font.BOLD + Font.ITALIC, 12));
    	solutionArea.setEditable(false);
    	solutionArea.setForeground(Color.BLUE);
    	solutionArea.setLineWrap(true);
    	
    	//searchStatsArea settings
    	searchStatsArea.setFont(new Font("", Font.BOLD + Font.ITALIC, 12));
    	searchStatsArea.setForeground(Color.BLUE);
    	searchStatsArea.setEditable(false);
    	
    	//searchStatsArea settings
    	logArea.setFont(new Font("", Font.BOLD + Font.ITALIC, 12));
    	logArea.setForeground(Color.RED);
    	logArea.setEditable(false);
    }
    
    private void updateSolutionArea(){
    	solutionArea.setText("Algorithm: " + getAlgorithmName(algorithm)+ "\n"
    					   + "Heuristic: " + getHeuristicName(algorithm)+ "\n"
    					   + "Solution: " + solution);
    }
    
    private String getAlgorithmName(String flag){
    	String algorithm = "";
    	
    	if(flag.equals(Algorithms.DEPTH_FIRST))algorithm = "Depth-First";
    	else if(flag.equals(Algorithms.BREADTH_FIRST))algorithm = "Breadth-First";
    	else if(flag.equals(Algorithms.UNIFORM_COST))algorithm = "Uniform Cost";
    	else if(flag.equals(Algorithms.ASTAR_MANHATTAN_HEURISTIC) || flag.equals(Algorithms.ASTAR_BOX_GOAL_HEURISTIC))algorithm = "A*";
    	else if(flag.equals(Algorithms.GREEDY_BEST_FIRST_MANHATTAN_HEURISTIC) || flag.equals(Algorithms.GREEDY_BEST_FIRST_BOX_GOAL_HEURISTIC))algorithm = "Greedy Best-First";
    	
    	return algorithm;
    }
    
    private String getHeuristicName(String flag){
    	String heuristic = "";
    	
    	if(flag.equals(Algorithms.ASTAR_BOX_GOAL_HEURISTIC) || flag.equals(Algorithms.GREEDY_BEST_FIRST_BOX_GOAL_HEURISTIC))heuristic = "Number of boxes on goal";
    	else if(flag.equals(Algorithms.ASTAR_MANHATTAN_HEURISTIC) || flag.equals(Algorithms.GREEDY_BEST_FIRST_MANHATTAN_HEURISTIC))heuristic = "Manhattan";
    	
    	return heuristic;
    }
        
    private void updateSearchStatsArea(){
    	searchStatsArea.setText("Number of nodes generated: " + searchStats.getNodesGenerated() + "\n"
    						  + "Number of nodes previously generated: " + searchStats.getPreviouslySeen() + "\n" 
    						  + "Number of nodes explored: " + searchStats.getNodesExplored() + "\n"
    						  + "Number of nodes visited: " + searchStats.getVisitedLength() + "\n"
    						  + "Number of nodes yet to be explored: " + searchStats.getQueueLength() + "\n"
    						  + "Number of moves: " + searchStats.getMoveNumber() + "\n"
    						  + "Time elapsed: " + searchStats.getTimeElapsed() + " milliseconds \n");
    }
        
    private void writeToLog(String log){
    	String timeStamp = new SimpleDateFormat("h:mm:ss a").format(Calendar.getInstance().getTime());
    	logArea.append("<Algorithm:" + getAlgorithmName(algorithm) + "><Heuristic:" + getHeuristicName(algorithm) +"><MapFile:"
    	               + selectedMapFile + ">" + log + " (" + timeStamp + ")\n");
    }
    
    public static void main(String[] args) {
    	try {
    		// Set cross-platform Java L&F (also called "Metal")
    		UIManager.setLookAndFeel(
    				UIManager.getSystemLookAndFeelClassName());
    	} catch (UnsupportedLookAndFeelException e) {
    		// handle exception
    	}
    	catch (ClassNotFoundException e) {
    		// handle exception
    	}
    	catch (InstantiationException e) {
    		// handle exception
    	}
    	catch (IllegalAccessException e) {
    		// handle exception
    	}
        Sokoban sokoban = new Sokoban();
        sokoban.setVisible(true);
    }
}