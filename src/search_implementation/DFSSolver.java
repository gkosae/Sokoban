package search_implementation;

import java.util.ArrayList;
import java.util.LinkedList;


public class DFSSolver extends AbstractSolver {

	public DFSSolver(BoardState initialState) {
		super(initialState);
		queue = new LinkedList<BoardState>();
	}

	@Override
	protected void searchFunction(ArrayList<BoardState> validMoves) {
		for (BoardState move : validMoves) {
			backtrack.put(move, currentState);
			((LinkedList<BoardState>) queue).push(move);
		}
	}
}
