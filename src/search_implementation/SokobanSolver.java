package search_implementation;

import java.io.IOException;


/**
 * A really shoddy command line interface for solving Sokoban with:
 * - BFS
 * - DFS
 * - Uniform cost search
 * - Greedy best first search
 * - A* search
 *
 * @author Stephen Zhou
 * @uni szz2002
 *
 */
public class SokobanSolver {
	
	SearchStatistics stats = new SearchStatistics();
	String algorithm = Algorithms.BREADTH_FIRST;
	String pathToMapFile = "";
	
	public SokobanSolver(){}
	
	public SokobanSolver(String algorithm, String pathToMapFile) throws IOException, NoSolutionException, OutOfMemoryError{
		this.algorithm = algorithm;
		this.pathToMapFile = pathToMapFile;
		
		BoardState initialBoard = new BoardState().parseBoardInput(pathToMapFile);
		AbstractSolver solver = null;
		
		if (algorithm.equals(Algorithms.BREADTH_FIRST)) {
			solver = new BFSSolver(initialBoard);
		}
		else if (algorithm.equals(Algorithms.DEPTH_FIRST)) {
			solver = new DFSSolver(initialBoard);
		}
		else if (algorithm.equals(Algorithms.UNIFORM_COST)) {
			solver = new UniformCostSolver(initialBoard);
		}
		else if (algorithm.equals(Algorithms.ASTAR_BOX_GOAL_HEURISTIC)) {
			solver = new AStarSolver(initialBoard, new BoxGoalHeuristic());
		}
		else if (algorithm.equals(Algorithms.GREEDY_BEST_FIRST_BOX_GOAL_HEURISTIC)) {
			solver = new GreedyBFSSolver(initialBoard, new BoxGoalHeuristic());
		}
		else if (algorithm.equals(Algorithms.ASTAR_MANHATTAN_HEURISTIC)) {
			solver = new AStarSolver(initialBoard, new ManhattanHeuristic());
		}
		else if (algorithm.equals(Algorithms.GREEDY_BEST_FIRST_MANHATTAN_HEURISTIC)) {
			solver = new GreedyBFSSolver(initialBoard, new ManhattanHeuristic());
		}
		
		if (solver != null) {
			
			String solution = solver.search();
			
			stats.setSolution(solution);
			stats.setNodesGenerated(solver.getNodesGenerated());
			stats.setNodesExplored(solver.getNodesExplored());
			stats.setPreviouslySeen(solver.getPreviouslySeen());
			stats.setQueueLength(solver.getFringeLength());
			stats.setVisitedLength(solver.getVisitedLength());
			stats.setTimeElapsed(solver.getElapsedTimeMillis());
			stats.setMoveNumber(solution.replace(" ","").split(",").length);
		}
	}
	
	public void parseArguments(String[] args) throws IOException, NoSolutionException{
			// TODO some form of input validation
			String flag = args[0];
			String puzzlePath = args[1];
			BoardState initialBoard = new BoardState().parseBoardInput(puzzlePath);
			AbstractSolver solver = null;
			System.out.println(initialBoard);
			if (flag.equals(Algorithms.BREADTH_FIRST)) {
				solver = new BFSSolver(initialBoard);
			}
			else if (flag.equals(Algorithms.DEPTH_FIRST)) {
				solver = new DFSSolver(initialBoard);
			}
			else if (flag.equals(Algorithms.UNIFORM_COST)) {
				solver = new UniformCostSolver(initialBoard);
			}
			else if (flag.equals(Algorithms.ASTAR_BOX_GOAL_HEURISTIC)) {
				solver = new AStarSolver(initialBoard, new BoxGoalHeuristic());
			}
			else if (flag.equals(Algorithms.GREEDY_BEST_FIRST_BOX_GOAL_HEURISTIC)) {
				solver = new GreedyBFSSolver(initialBoard, new BoxGoalHeuristic());
			}
			else if (flag.equals(Algorithms.ASTAR_MANHATTAN_HEURISTIC)) {
				solver = new AStarSolver(initialBoard, new ManhattanHeuristic());
			}
			else if (flag.equals(Algorithms.GREEDY_BEST_FIRST_MANHATTAN_HEURISTIC)) {
				solver = new GreedyBFSSolver(initialBoard, new ManhattanHeuristic());
			}
			else {
				System.out.println("Invalid command");
			}

			if (solver != null) {
				stats.setSolution(solver.search());
				stats.setNodesExplored(solver.getNodesExplored());
				stats.setPreviouslySeen(solver.getPreviouslySeen());
				stats.setQueueLength(solver.getFringeLength());
				stats.setVisitedLength(solver.getVisitedLength());
				stats.setTimeElapsed(solver.getElapsedTimeMillis());
//				System.out.println("Solution: " + solution);
//				System.out.println("Nodes explored: " + nodesExplored);
//				System.out.println("Previously seen: " + previouslySeen);
//				System.out.println("Fringe: " + queueLength);
//				System.out.println("Explored set: " + visitedLength);
//				System.out.println("Millis elapsed: " + timeElapsed);
			}
	}
	
	public SearchStatistics getSearchStats(){
		return stats;
	}
}
