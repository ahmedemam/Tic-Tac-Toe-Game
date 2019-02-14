/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package player;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.shape.Rectangle;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author MKhaled
 */
public class Player extends Application {

    private boolean myTurn = true, playable = true;
    private ArrayList<Combo> combos = new ArrayList<>();
    private Tile[][] board = new Tile[3][3];
    Pane root = new Pane();

    private Socket socket;
    private PrintStream ps;
    private DataInputStream dis;
    private int myId, opponentId;
    private int oppPlayX, oppPlayY;
    private String myPlayDescription, oppoPlayDescription = "";
    private String[] oppoPlayDescriptionArray;

    //start Player Constructor      
    public Player() {

        try {

            //Connecting to server      
            socket = new Socket("192.168.1.165", 5552);
            ps = new PrintStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
// storing recieved id from server 
            myId = Integer.parseInt(dis.readLine());

            System.out.println("My ID : " + myId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

// Start Thread For any Play come from opponent        
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    while (true) {
                        try {

                            oppoPlayDescription = dis.readLine();
                            System.out.println(oppoPlayDescription);
                              oppoPlayDescriptionArray=oppoPlayDescription.split(" ") ;
                              
                              for (int i = 0; i < oppoPlayDescriptionArray.length; i++) {
                                  System.out.println("item "+i+" "+oppoPlayDescriptionArray[i]);
                            }
                              oppPlayX = Integer.parseInt(oppoPlayDescriptionArray[0]) ;
                              oppPlayY = Integer.parseInt(oppoPlayDescriptionArray[1])  ;
                              
                              if (oppPlayX > -1 && oppPlayY > -1)
                              {
                              System.out.println("Recieved play from opponent at "+oppPlayX
                                      +" Row and Col : "+oppPlayY);
                               if (myId%2 != 0 )
                              board[oppPlayY][oppPlayX].drawO();
                               if(myId %2== 0)
                                   board[oppPlayY][oppPlayX].drawX();
                               board[oppPlayY][oppPlayX].usedBefore = true;
                                myTurn = true ; 
                                
                 // use Platform for Updating GUI (drawing win line ) in thread                
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                         checkState();
                                                }
                                                     });
                                  
                              }
                          
                        } catch (IOException ex) {
                            //ex.printStackTrace();
                            System.out.println(ex.getMessage());
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    private Parent createContent() {

        // Border Size 
        root.setPrefSize(800, 600);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Tile tile = new Tile();
                tile.setTranslateX(j * 200);
                tile.setTranslateY(i * 200);

                root.getChildren().add(tile);
                board[j][i] = tile;
            }
        }

        // Cases on it Player Win     
        //Horizontal
        for (int i = 0; i < 3; i++) {
            combos.add(new Combo(board[0][i], board[1][i], board[2][i]));
        }

        // Vertical 
        for (int i = 0; i < 3; i++) {
            combos.add(new Combo(board[i][0], board[i][1], board[i][2]));

        }
        // Diagonal 
        combos.add(new Combo(board[0][0], board[1][1], board[2][2]));
        combos.add(new Combo(board[2][0], board[1][1], board[0][2]));

        return root;
    }

//    Player Stage 
    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(createContent());

        primaryStage.setTitle("Tic Tac Toe App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

//    check if somebody win  
    private void checkState() {

        for (Combo combo : combos) {
            if (combo.isComplete()) {
                playable = false;
                playWinAnimation(combo);
                break;
            }
        }
    }
    Line line;

    // Win Animation
    private void playWinAnimation(Combo combo) {
        line = new Line();
        line.setStartX(combo.tiles[0].getCenterX());
        line.setStartY(combo.tiles[0].getCenterY());
        line.setEndX(combo.tiles[0].getCenterX());
        line.setEndY(combo.tiles[0].getCenterY());

        root.getChildren().add(line);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(2),
                new KeyValue(line.endXProperty(), combo.tiles[2].getCenterX()),
                new KeyValue(line.endYProperty(), combo.tiles[2].getCenterY())
        ));
        timeline.play();
    }

    //    Combo Class
    private class Combo {

        private Tile[] tiles;

        public Combo(Tile... tiles) {
            this.tiles = tiles;
        }

        public boolean isComplete() {
            if (tiles[0].getValue().isEmpty()) {
                return false;
            }

            return tiles[0].getValue().equals(tiles[1].getValue())
                    && tiles[0].getValue().equals(tiles[2].getValue());
        }
    }

//    end Combo Class
//    Start Tile Class
    private class Tile extends StackPane {

        private Text text = new Text();
        private boolean usedBefore = false;

        public Tile() {

            Rectangle border = new Rectangle(200, 200); // Tile Size 200 x 200 
            border.setFill(null);
            border.setStroke(Color.BLACK); // Tile Border 
            setAlignment(Pos.CENTER); // start writing from Center 

            text.setFont(Font.font(72)); // increase Text (x or O ) Size
            getChildren().addAll(border, text);

            // onClick Action 
            setOnMouseClicked(event -> {
                if (!playable) {
                    return;
                }

                if (event.getButton() == MouseButton.PRIMARY) {
                     // Intialize Play Info to be sent to server 
                    if (myTurn && !usedBefore) {

                        if (myId % 2 != 0) {
                            drawX();
                        }
                        if (myId % 2 == 0) {
                            drawO();
                        }

                        myTurn = false;

                       
                       findClickedTile() ;
                        checkState();

                    }

                    usedBefore = true;

                } 
            });
        }

        private void findClickedTile(){
            myPlayDescription = "";
             // Find Clicked Tile Position 
                        for (int col = 0; col < 3; col++) {
                            for (int row = 0; row < 3; row++) {
                                if (board[col][row] == this) {                                    
                                    // Special if for Testing 
                                    if (myId % 2 != 0) {// if my id odd 
                                        opponentId = myId + 1; // define opponent id  
                                    } 
                                    else if (myId % 2 == 0) {
                                        opponentId = myId - 1;
                                    }
                                    
                                  // Send Clicked Tile Data to Server 
                                    myPlayDescription = "GAME PLAY " + myId + " " + opponentId + " " + row + " " + col;
                                    ps.println(myPlayDescription);

                                    System.out.println("Play Info Sent");
                                }

                            }

                        }
        }
        
        private void drawX() {
            text.setFill(Color.RED);
            text.setText("X");

        }

        private void drawO() {
            text.setFill(Color.BLUE);
            text.setText("O");

        }

        public String getValue() {
            return text.getText();
        }

        public double getCenterX() {
            return getTranslateX() + 100;
        }

        public double getCenterY() {
            return getTranslateY() + 100;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        launch(args);
    }

}
