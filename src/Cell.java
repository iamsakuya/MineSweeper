public class Cell {

	public static final int HIDDEN = 0;
	public static final int EXPLORED = 1;
	public static final int FLAGED = 2;

	public static final int NORMAL = 4;
	public static final int ADJACENT = 5;
	
	boolean isBomb;
	int numOfBombs;
	int numOfAdjacentBombs;
	int status;
	int showType;
	boolean isMissed;
	
	public Cell (boolean isBomb) {
		this.isBomb = isBomb;
		numOfBombs = 0;
		numOfAdjacentBombs = 0;
		status = Cell.HIDDEN;
		showType = Cell.NORMAL;
		isMissed = false;
	}

}