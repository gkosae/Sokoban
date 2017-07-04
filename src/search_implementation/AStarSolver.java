package search_implementation;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class AStarSolver extends AbstractSolver {
	private Heuristic heuristic;

	private AStarSolver(BoardState initialBoard) {
		super(initialBoard);
		queue = new PriorityQueue<BoardState>();
	}

	public AStarSolver(BoardState initialBoard, Heuristic heuristic) {
		this(initialBoard);
		this.heuristic = heuristic;
	}

	@Override
	protected void searchFunction(ArrayList<BoardState> validMoves) {
		for (BoardState move : validMoves) {
			backtrack.put(move, currentState);
			heuristic.score(move);
			
			move.setMoveCost(currentState.getMoveCost() + 1);
			move.addCostToMoveCost();
			queue.add(move);
		}
	}
}
