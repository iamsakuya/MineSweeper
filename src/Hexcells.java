import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Hexcells extends Application {
	
	Game game;
	GamePane gamePane;
	Pane topPane;
	int padding;
	Stage popUpStage;
	
	public Hexcells () {
		super();
		this.game = new Game(10);
		padding = 15;
		topPane = new Pane();
		topPane.setPadding(new Insets(0, 0, padding, 0));
		popUpStage = new Stage();
	}
	
	@Override
	public void start (Stage primaryStage) {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(padding, padding, padding, padding));

		gamePane = new GamePane(game.board);

		gamePane.setOnMouseClicked(e -> {
			if (game.isOver())	return;

			int[] cords = whichCell(e.getX(), e.getY());
			if (cords[0]<0 || cords[1]<0)	return;

			if (e.getButton()==MouseButton.PRIMARY) {
				game.explore(cords[0], cords[1]);
				gamePane.drawCells();
				drawTopPane();
				if (game.isOver())	popUp();
			} else if (e.getButton()==MouseButton.SECONDARY) {
				game.triggerFlag(cords[0], cords[1]);
				gamePane.drawCells();
				drawTopPane();
				if (game.isOver())	popUp();
			}
		});

		pane.setCenter(gamePane);
		
		drawTopPane();
		pane.setTop(topPane);

		Scene scene = new Scene(pane);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Hexcells");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void drawTopPane () {
		topPane.getChildren().clear();
		
		double rectWidth = 4*gamePane.cellRadius;
		double rectHeight = 2*gamePane.cellRadius;
		double gamePaneWidth = 2*gamePane.cellRadius + (game.size-1)*(3*gamePane.cellRadius/2+gamePane.gap);

		Rectangle rect1 = new Rectangle(0, 0, rectWidth, rectHeight);
		rect1.setFill(Color.CORNFLOWERBLUE);
		topPane.getChildren().add(rect1);
		Rectangle rect2 = new Rectangle(gamePaneWidth-rectWidth, 0, rectWidth, rectHeight);
		rect2.setFill(Color.CORNFLOWERBLUE);
		topPane.getChildren().add(rect2);
		
		int bombRemained = game.numOfBombs-game.numOfFlagged;
		Text text1 = new Text(rectWidth/4.5, rectHeight/2.5, "Remain:\n     "+bombRemained);
		text1.setFill(Color.WHITE);
		text1.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		topPane.getChildren().add(text1);
		Text text2 = new Text(gamePaneWidth-rectWidth+rectWidth/3, rectHeight/2.5, "Miss:\n    "+game.numOfMiss);
		text2.setFill(Color.WHITE);
		text2.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		topPane.getChildren().add(text2);
	}
	
	private int[] whichCell (double x, double y) {
		int[] result = new int[2];
		
		int col = 0, row = 0;

		if ((x<gamePane.cellRadius)) {
			col = 0;
			row = (int)(y / (gamePane.cellHeight+gamePane.gap));
		}	else if ((x>game.size*(3*gamePane.cellRadius/2+gamePane.gap)+gamePane.cellRadius)) {
			col = game.size-1;
			row = (int)((y-gamePane.cellHeight/2)/(gamePane.cellHeight+gamePane.gap));
		} else {
			// the target cell will be in either column a or column b
			int a = (int)((x - 2*gamePane.cellRadius)/(3*gamePane.cellRadius/2+gamePane.gap));
			int b = a+1;
			if (b%2==0) {
				b = a;
				a = a+1;
			}
			// the target cell will be in P1(a, p) or P2(b, q)
			int p = (int)(y / (gamePane.cellHeight+gamePane.gap));
			int q = (int)((y-gamePane.cellHeight/2)/(gamePane.cellHeight+gamePane.gap));

			// calculate distance between center of P1 and (x , y)
			double tempX = gamePane.cellRadius + a*(3*gamePane.cellRadius/2+gamePane.gap);
			double tempY = gamePane.cellHeight/2 + p*(gamePane.cellHeight+gamePane.gap);
			double dist1 = Math.sqrt(Math.pow((tempX-x), 2) + Math.pow((tempY-y), 2));

			// calculate distance between center of P2 and (x , y)
			tempX = gamePane.cellRadius + b*(3*gamePane.cellRadius/2+gamePane.gap);
			tempY = gamePane.cellHeight + p*(gamePane.cellHeight+gamePane.gap);
			double dist2 = Math.sqrt(Math.pow((tempX-x), 2) + Math.pow((tempY-y), 2));
			
			// the cell is in the cell whose center is closer to (x , y)
			if (dist1<dist2) {
				col = a;
				row = p;
			} else {
				col = b;
				row = q;
			}
		}

		result[0] = col;
		result[1] = row;

		return result;
	}
	
	private void popUp () {
		VBox popPane = new VBox();
		popPane.setSpacing(20);
		popPane.setMinWidth(200);
		popPane.setMinHeight(60);
		popPane.setPadding(new Insets(15, 15, 15, 15));
		popPane.setAlignment(Pos.CENTER);
		String displayText = "Your num of mistake: "+game.numOfMiss+".";
		Label label = new Label(displayText);
		label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		label.setTextFill(Color.STEELBLUE);
		popPane.getChildren().add(label);
		Button btn = new Button("OK");
		btn.setTextFill(Color.STEELBLUE);
		btn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		popPane.getChildren().add(btn);
		Scene popScene = new Scene(popPane);
		popUpStage.setScene(popScene);
		popUpStage.setTitle("Complete!");
		popUpStage.show();
		btn.setOnMouseClicked(e -> {
			popUpStage.hide();
		});
	}
	
	public static void main (String[] args) {
		Application.launch();
	}

}	// end class Hexcells



class GamePane extends Pane {
	
	Cell[][] board;
	double gap;
	double cellRadius;
	double cellHeight;

	public GamePane (Cell[][] board) {
		super();
		this.board = board;
		gap = 9;
		cellRadius = 30;
		cellHeight = Math.sqrt(3)*cellRadius;
		drawCells();
	}
	
	public void drawCells () {
		this.getChildren().clear();
		for (int i=0; i<board.length; i++) {
			double x = i*3*cellRadius/2 + cellRadius + gap*i;
			for (int j=0; j<board[i].length; j++) {
				double y = gap*j;
				if (i%2==0)	y += (cellHeight/2 + j*cellHeight);
				else	y += (j*cellHeight+cellHeight);
				Polygon polygon = new Polygon();
				ObservableList<Double> list = polygon.getPoints();
				for (int k=0; k<6; k++) {
					list.add(x + Math.cos(2*k*Math.PI/6)*cellRadius);
					list.add(y + Math.sin(2*k*Math.PI/6)*cellRadius);
				}	// end inner for (k)

				this.getChildren().add(polygon);
				
				switch (board[i][j].status) {
					case Cell.HIDDEN:
						polygon.setFill(Color.ORANGE);
						break;
					case Cell.EXPLORED:
						polygon.setFill(Color.GREY);
						Text text = null;
						if (board[i][j].showType==Cell.ADJACENT)	text = new Text(x-cellRadius/3, y+cellRadius/6, "{"+String.valueOf(board[i][j].numOfAdjacentBombs+"}"));
						else	text = new Text(x-cellRadius/8, y+cellRadius/6, String.valueOf(board[i][j].numOfBombs));
						text.setFill(Color.WHITE);
						text.setFont(Font.font("Arial", FontWeight.BOLD, 16));
						this.getChildren().add(text);
						break;
					case Cell.FLAGED:
						polygon.setFill(Color.CORNFLOWERBLUE);
						break;
				}	// end switch case
				
				if (board[i][j].isMissed){
					polygon.setStroke(Color.RED);
					polygon.setStrokeWidth(2);
				}
			}	// end inner for (j)
		}	// end outer for (i)
	}	// end method drawCells

}	// end class GamePane