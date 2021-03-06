package search_implementation;

import java.util.ArrayList;
import java.util.PriorityQueue;


public class UniformCostSolver extends AbstractSolver {

	public UniformCostSolver(BoardState initialState) {
		super(initialState);
		queue = new PriorityQueue<BoardState>();
	}

	@Override
	protected void searchFunction(ArrayList<BoardState> moves) {
		for (BoardState move : moves) {
			backtrack.put(move, currentState);
			uniformCostFunction(move, currentState.getCost());
			queue.add(move);
		}
	}

	private void uniformCostFunction(BoardState state, int baseCost) {
		if (currentState.nextMoveHas(BoardState.BOX, state.getDirectionTaken()))
			state.setCost(baseCost + 2);
		else
			state.setCost(baseCost + 1);
	}
}
