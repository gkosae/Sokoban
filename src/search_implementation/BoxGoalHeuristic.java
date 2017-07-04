package search_implementation;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;


public class BoxGoalHeuristic implements Heuristic {

	@Override
	public void score(BoardState state) {
		Set<Point> goals = state.getGoals();
		Set<Point> boxes = state.getBoxes();

		Set<Point> intersection = new HashSet<Point>(boxes);
		intersection.retainAll(goals);

		// Difference because lower costs are better
		int manhattanCost = 0;
		for (Point box : boxes) {
			int minMarginalCost = Integer.MAX_VALUE;
			for (Point goal : goals) {
				int dist = getManhattanDistance(box, goal);
				if (dist < minMarginalCost)
					minMarginalCost = dist;
			}
			manhattanCost += minMarginalCost;
		}
		
		//A mixture of box goal heuristic and manhattan heuristic
		//A lower manhattan cost is better
		//A lower goal-box difference is better
		//So after adding them the lower sums are better
		state.setCost((goals.size() - intersection.size()) + manhattanCost);
	}
	
	private static int getManhattanDistance(Point p1, Point p2) {
		return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
	}
}
