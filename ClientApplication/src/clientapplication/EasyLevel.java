package clientapplication;


import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Application;
import static javafx.application.Application.launch;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
//import levels.Levels;
//import signInSignUp.ClientApp;
//import signInSignUp.Sign_up;


public class EasyLevel extends Application{
    
        private Scene scene;
	GridPane grid = new GridPane();
    
	private Boolean playable;

    private Tile[][] gui_board;
    private char[][] back_end_board;
    private char winner;
//    private ArrayList<String> positions= new ArrayList<>();
//    private ArrayList<Text> gameBoard= new ArrayList<>();
    String data="";

    
    public EasyLevel() {
        playable = true;
        gui_board = new Tile[3][3];
        back_end_board = new char[3][3];
        winner = '-';
    }
    public Scene getScene()
    {
       System.out.println(scene.getStylesheets());
        return this.scene;
    }

        @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	Button logout = new Button();
        logout.setText("Pause");
        logout.setId("logout");
       
        logout.setMaxWidth(Double.MAX_VALUE);
        grid.add(logout, 2,4 , 1, 16);
        
        logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                        Client.sendMessageOut(EasyLevel.this.pause());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("saved");
                        alert.setHeaderText(null);
                        alert.setContentText("Game is saved");
                        alert.show();
        //					Levels levels = new Levels();
//					levels.start(ClientApp.mainStage);
				} catch (Exception e)
                                {
					e.printStackTrace();
				}
            }
        });
        
        Label status = new Label("Player Turn"); 
        
        status.setId("status");
        status.setAlignment(Pos.CENTER);
        grid.add(status, 2,2 , 1, 40);
                  
        grid.add(new StackPane(new Text("")), 10, 20);
    	
        //End of chat
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        
        scene = new Scene(createContent(),880, 550);
      //  scene.getStylesheets().add(Sign_up.class.getResource("GameStyle.css").toExternalForm());

        primaryStage.setTitle("Tic Tac - Single Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        grid.requestFocus();
    }
    
    private class Tile extends StackPane {
        private Text text;
        private Rectangle rect;
        private int row, col;

        public Tile(int row, int col) {
            this.row = row;
            this.col = col;
            text = new Text();
            rect = new Rectangle(165, 165);
            rect.setId("rect");
            
            rect.setArcHeight(45.0d); 
            rect.setArcWidth(45.0d); 
                
            rect.setFill(Color.rgb(110, 54, 41 , 0.7));
            rect.setStroke(Color.rgb(131,159,14 ));
            text.setFont(Font.font(60));
            getChildren().addAll(rect, text);

            setOnMouseClicked(event -> {
                if (!playable) {
                    return;
                }
                if (event.getButton() == MouseButton.PRIMARY) {   //make left click on mouse
                    drawX();
                    try {
						checkWin();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    if (!playable) {
                        return;
                    }
                    computerPlay();
                    try {
			 checkWin();
                    } 
                    catch (Exception e)
                                        {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            });
        }

        public void computerPlay() {
        	
            //Choose center if available
            if (back_end_board[1][1] == '-') {
                back_end_board[1][1] = 'o';
                gui_board[1][1].drawO();
                return;
            }

            //Choose a corner if available 
            if (back_end_board[0][0] == '-') {
                back_end_board[0][0] = 'o';
                gui_board[0][0].drawO();
                return;
            }

            if (back_end_board[0][2] == '-') {
                back_end_board[0][2] = 'o';
                gui_board[0][2].drawO();
                return;
            }

            if (back_end_board[2][0] == '-') {
                back_end_board[2][0] = 'o';
                gui_board[2][0].drawO();
                return;
            }

            if (back_end_board[2][2] == '-') {
                back_end_board[2][2] = 'o';
                gui_board[2][2].drawO();
                return;
            }

            //Choose a random move
            for (int row = 0; row <= 2; row++) {
                for (int column = 0; column <= 2; column++) {
                    if (back_end_board[row][column] == '-') {
                        back_end_board[row][column] = 'o';
                        gui_board[row][column].drawO();
                        return;
                    }
                }
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("tie");
            alert.setHeaderText(null);
            alert.setContentText("Tie");
            Optional <ButtonType> result = alert.showAndWait();
//            if(result.get() == ButtonType.OK)
//            {
//                
//            }
        }

        private void drawX() {
            text.setText("x");
            back_end_board[row][col] = 'x';
        }

        private void drawO() {
            text.setText("o");
        }
    }
    
    private Parent createContent() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Tile tile = new Tile(i, j);
                grid.add(tile,6+j , 2+i);
                gui_board[i][j] = tile;
                back_end_board[i][j] = '-';
            }
        }
        return grid;
    }

    private boolean checkRows() {
        for (int i = 0; i < 3; i++) {
            if (back_end_board[i][0] == back_end_board[i][1]
                    && back_end_board[i][0] == back_end_board[i][2]
                    && back_end_board[i][0] != '-') {
                if (back_end_board[i][0] == 'x') {
                    winner = 'x';
                } else {
                    winner = 'o';
                }
                return true;
            }
        }
        return false;
    }

    private boolean checkCols() {
        for (int i = 0; i < 3; i++) {
            if (back_end_board[0][i] == back_end_board[1][i]
                    && back_end_board[0][i] == back_end_board[2][i]
                    && back_end_board[0][i] != '-') {
                if (back_end_board[0][i] == 'x') {
                    winner = 'x';
                } else {
                    winner = 'o';
                }
                return true;
            }
        }
        return false;
    }

    private boolean checkDs() {
        if (back_end_board[0][0] == back_end_board[1][1]
                && back_end_board[0][0] == back_end_board[2][2]
                && back_end_board[0][0] != '-') {
            if (back_end_board[0][0] == 'x') {
                winner = 'x';
            } else {
                winner = 'o';
            }
            return true;
        }
        if (back_end_board[0][2] == back_end_board[1][1]
                && back_end_board[0][2] == back_end_board[2][0]
                && back_end_board[0][2] != '-') {
            if (back_end_board[2][0] == 'x') {
                winner = 'x';
            } else {
                winner = 'o';
            }
            return true;
        }
        return false;
    }

    private void checkWin() throws Exception {
    	//check state of the game
        if (checkRows() || checkCols() || checkDs()) {
            System.out.println("============hhhhhh");

            playable = false;
            if(winner == 'x')
            {
                System.out.println("hhhhhhhhhhhhhhhhhh");
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Win");
                alert.setHeaderText(null);
                alert.setContentText("Congratulaion you win");
                alert.show();
//                Optional<ButtonType> result = alert.showAndWait();
//                if (result.get() == ButtonType.OK) {
////                	Levels levels = new Levels();
////					levels.start(ClientApp.mainStage);
//                }
            }
            else
            {
                System.out.println("iiiiiiiiiiiiii");
            	Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Lose");
                alert.setHeaderText(null);
                alert.setContentText("try another time");
                alert.show();
                //Optional<ButtonType> result = alert.showAndWait();
                //System.out.println(result.get());
//                if (result.get() == ButtonType.OK) {
////                	Levels levels = new Levels();
////					levels.start(ClientApp.mainStage);
//                }
            }
        }
    }
    
    public String pause()
    {
     data="";
     for(int i =0; i <this.back_end_board.length-1;i++)
     {
         for(int j=0; j<this.back_end_board.length-1;j++)
         {
             switch (this.back_end_board[i][j]) {
                 case 'x':
                     data+="1,";
                     break;
                 case 'o':
                     data+="-1,";
                     break;
                 default:
                     data +="0,";
                     break;
             }
             
         }

     }
     switch (this.back_end_board[2][2]) {
                 case 'x':
                     data+="1";
                     break;
                 case 'o':
                     data+="-1";
                     break;
                 default:
                     data +="0";
                     break;
    }
     //GAME SAVED data
              return "GAME SAVED "+ Client.getID()+" "+data;
    }
    public static void main (String args[])
    {
        launch(args);
    }
}