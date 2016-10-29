public class Game {
	
	int size;
	Cell[][] board;
	int numOfBombs;
	int numOfFlagged;
	int numOfExplored;
	int numOfMiss;
	
	public Game (int size) {
		this.size = size;
		reset();
	}
	
	public boolean isOver () {
		return (numOfFlagged+numOfExplored)==(size*size);
	}
	
	public int numOfBombsLeft () {
		return numOfBombs - numOfFlagged;
	}
	
	public void reset () {
		board = new Cell[size][size];
		numOfBombs = size*size/3;
		numOfFlagged = 0;
		numOfExplored = 0;
		numOfMiss = 0;

		initialize();
		shuffle();
		setUpNumOfBombs();
		setUpAdjacentBombs();
	}
	
	public void explore (int i, int j) {
		if (isOver())	return;

		if (validatePosition(i, j) && (board[i][j].status==Cell.HIDDEN)) {
			if (board[i][j].isBomb) {
				board[i][j].isMissed = true;
				numOfMiss++;
				triggerFlag(i, j);
			} else {
				board[i][j].status = Cell.EXPLORED;
				numOfExplored++;
			}
		}
	}
	
	public void triggerFlag (int i, int j) {
		if (isOver())	return;

		if (validatePosition(i, j) && (board[i][j].status==Cell.HIDDEN)) {
			if (board[i][j].isBomb) {
				board[i][j].status = Cell.FLAGED;
				numOfFlagged++;
			} else {
				numOfMiss++;
				board[i][j].isMissed = true;
				explore(i, j);
			}
		}
	}
	

	// helper methods
	
	private void initialize () {
		int numOfShowing = size*size/8;
		int counter = 0;
		for (int i=0; i<board.length; i++) {
			for (int j=0; j<board[0].length; j++) {
				if (counter<numOfBombs) {
					board[i][j] = new Cell(true);
				} else {
					board[i][j] = new Cell(false);
					if (counter < (numOfBombs+numOfShowing)) {
						board[i][j].status = Cell.EXPLORED;
						numOfExplored++;
					}
				}	// end if
				counter++;
			}	// end inner for
		}	// end outer for
	}	// end method initialize
	
	private void shuffle () {
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				int a = (int)(Math.random()*size);
				int b = (int)(Math.random()*size);
				Cell temp = board[a][b];
				board[a][b] = board[i][j];
				board[i][j] = temp;
			}
		}
	}
	
	private void setUpAdjacentBombs () {
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				if ((!board[i][j].isBomb) && (board[i][j].numOfBombs>1)) {
					if (validatePosition(i, j-1) && validatePosition(i+1, j-1) && validatePosition(i+1, j) && validatePosition(i, j+1) && validatePosition(i-1, j) && validatePosition(i-1, j-1)) {
						boolean[] temp = new boolean[6];
						temp[0] = board[i][j-1].isBomb;
						if (i%2==0) {
              temp[1] = board[i+1][j-1].isBomb;
              temp[2] = board[i+1][j].isBomb;
						} else {
              temp[1] = board[i+1][j].isBomb;
              temp[2] = board[i+1][j+1].isBomb;
						}
						temp[3] = board[i][j+1].isBomb;
						if (i%2==0) {
              temp[4] = board[i-1][j].isBomb;
              temp[5] = board[i-1][j-1].isBomb;
						} else {
              temp[4] = board[i-1][j+1].isBomb;
              temp[5] = board[i-1][j].isBomb;
						}
						int numOfAdj = numOfAdjacentTrue(temp);
						if (numOfAdj>1) {
							board[i][j].numOfAdjacentBombs = numOfAdj;
							board[i][j].showType = Cell.ADJACENT;
							if (Math.random()>0.5) {
								board[i][j].status = Cell.EXPLORED;
								numOfExplored++;
							}
						}	// end inner inner if
					}	// end inner if
				}	// end outer if
			}	// end inner for
		}	// end outer for
	}	// end method setUpAjacentBombs
	
	private int numOfAdjacentTrue (boolean[] arg) {
		boolean first = arg[0];
		int p = 0;
		while ((arg[p] == first) && p<arg.length)	p++;
		if (p==arg.length)	return first ? 6:0;
		
		int s = p;
		while ((p<arg.length) && (arg[p]!=first))	p++;
		int e = p;
		
		while (p<arg.length) {
			if (arg[p] != first)	return 0;
			p++;
		}
		
		return first ? (arg.length - e + s):(e - s);
	}
	
	/*
	private int maxNumOfAdjacentTrue (boolean[] arg) {
		int result = 0;
		
		int i=0, temp=0;
		boolean trigger = false;
		while (i<arg.length) {
			if (arg[i]) {
				trigger = true;
				temp++;
			} else {
				if (trigger) {
					result = result<temp ? temp:result;
					trigger = false;
					temp = 0;
				}
			}
			i++;
		}
		if (trigger)	result = result<temp ? temp:result;
		
		return result;
	}
	*/
	
	private void setUpNumOfBombs () {
		for (int i=0; i<board.length; i++) {
			for (int j=0; j<board[0].length; j++) {
				if (board[i][j].isBomb) {
					if (validatePosition(i, j-1))	board[i][j-1].numOfBombs++;
					if (validatePosition(i, j+1))	board[i][j+1].numOfBombs++;
					if (i%2==0) {
            if (validatePosition(i-1, j-1))	board[i-1][j-1].numOfBombs++;
            if (validatePosition(i-1, j))	board[i-1][j].numOfBombs++;
            if (validatePosition(i+1, j-1))	board[i+1][j-1].numOfBombs++;
            if (validatePosition(i+1, j))	board[i+1][j].numOfBombs++;
					} else {
            if (validatePosition(i-1, j))	board[i-1][j].numOfBombs++;
            if (validatePosition(i-1, j+1))	board[i-1][j+1].numOfBombs++;
            if (validatePosition(i+1, j))	board[i+1][j].numOfBombs++;
            if (validatePosition(i+1, j+1))	board[i+1][j+1].numOfBombs++;
					}	// end inner if
				}	// end outer if
			}	// end inner for
		}	// end outer for
	}	// end method setUpNumOfBombs
	
	private boolean validatePosition (int i, int j) {
		return (i>=0 && i<size && j>=0 && j<size);
	}
	
}	// end class Game