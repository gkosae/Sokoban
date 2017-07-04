package search_implementation;

public class SearchStatistics {
	
	String solution = "";
	int nodesGenerated = 0;
	int nodesExplored = 0;
	int previouslySeen = 0;
	int queueLength = 0;
	int visitedLength = 0;
	int numberOfMoves = 0;
	long timeElapsed = 0;
	
	public int getNodesGenerated() {
		return nodesGenerated;
	}

	public void setNodesGenerated(int nodesGenerated) {
		this.nodesGenerated = nodesGenerated;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}
	
	public int getNodesExplored() {
		return nodesExplored;
	}

	public void setNodesExplored(int nodesExplored) {
		this.nodesExplored = nodesExplored;
	}

	public int getPreviouslySeen() {
		return previouslySeen;
	}

	public void setPreviouslySeen(int previouslySeen) {
		this.previouslySeen = previouslySeen;
	}

	public int getQueueLength() {
		return queueLength;
	}

	public void setQueueLength(int queueLength) {
		this.queueLength = queueLength;
	}

	public int getVisitedLength() {
		return visitedLength;
	}

	public void setVisitedLength(int visitedLength) {
		this.visitedLength = visitedLength;
	}

	public long getTimeElapsed() {
		return timeElapsed;
	}

	public void setTimeElapsed(long timeElapsed) {
		this.timeElapsed = timeElapsed;
	}
	
	public void setMoveNumber(int numberOfMoves){
		this.numberOfMoves = numberOfMoves;
	}
	
	public int getMoveNumber(){
		return numberOfMoves;
	}
	
	public void clearStats(){
		solution = "";
		nodesExplored = 0;
		previouslySeen = 0;
		queueLength = 0;
		visitedLength = 0;
		numberOfMoves = 0;
		timeElapsed = 0;
	}

	public SearchStatistics(){}
}
