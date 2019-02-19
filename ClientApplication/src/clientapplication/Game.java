/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientapplication;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
public class Game extends Application {

    private boolean myTurn = true, playable = true;
    private ArrayList<Combo> combos = new ArrayList<>();
    private Tile[][] board = new Tile[3][3];
    Pane root = new Pane();
    Parent rTest ;

    private Socket socket;
    private PrintStream ps;
    private DataInputStream dis;
    private int myId , opponentId ;
    private static int myScore = 0  , oppScore = 0 ;  
    private int oppPlayX, oppPlayY;
    private String myPlayDescription, myUserName , msgFromServer , oppName;
    private String[] msgFromServerArray;
    private int movesCounter = 0 ; 
    private boolean iWin= false , oppWin=false ,tie = false; 
    private Label userNameLabel , myScoreLabel , oppScoreLabel ;     
   private char c; 
        //start Game Constructor      
    public Game( int myId ,String myUserName , int myScore,int opponentId , String oppName , int oppScore ,char c ) {

        this.myId = myId ; 
        this.myUserName = myUserName ; 
        this.myScore = myScore ;
        this.opponentId = opponentId ;
        this.oppName = oppName; 
        this.oppScore = oppScore ;  
        this.c =c ;
        
//        try {

            //Connecting to server      
//            socket = new Socket("192.168.1.143", 4444);
//            ps = new PrintStream(socket.getOutputStream());
//            dis = new DataInputStream(socket.getInputStream());    
//         
            userNameLabel = new Label() ;
            myScoreLabel = new Label() ;
            oppScoreLabel = new Label () ; 
            
             
            
            
//            msgFromServer = dis.readLine() ; 
//            System.out.println("Message : "+msgFromServer);
//            
//            msgFromServerArray = msgFromServer.split(" ") ;
//            
//            if ("YOUR".equals(msgFromServerArray[0]) && "DATA".equals(msgFromServerArray[1]))
//            myId = Integer.parseInt(msgFromServerArray[3]);
//            myUserName = msgFromServerArray[4] ; 
//            opponentId = Integer.parseInt(msgFromServerArray[5]);
           System.out.println("My ID : " + myId);
//            System.out.println("MY UserName : "+myUserName);
       
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

// Start Thread For any Play come from opponent        
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    while (true) {
                        try {
                              
                if (!Client.readTokens().equals("pre") ){
                   
                    String msgFromServerArray [] = Client.readTokens().split(" ") ;
                    oppPlayX = Integer.parseInt(msgFromServerArray[4]) ;
                    oppPlayY = Integer.parseInt(msgFromServerArray[5])  ;
                    Client.clearTokens();
                    System.out.println("rec X : "+oppPlayX);
                    System.out.println("rec Y: "+oppPlayY);
//                              
                              
                              System.out.println("Recieved play from opponent at "+oppPlayX
                                      +" Row and Col : "+oppPlayY);
                              
                              if ( c =='o')
                              board[oppPlayY][oppPlayX].drawO();
                               if(c =='x')
                               board[oppPlayY][oppPlayX].drawX();
                               
                              
                               board[oppPlayY][oppPlayX].usedBefore = true;
                                myTurn = true ; 
                                
                 // use Platform for Updating GUI (drawing win line ) in thread                
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                         checkState();  }  
                });                       }
                          
                        } catch (Exception ex) {
                            //ex.printStackTrace();
                            System.out.println(ex.getMessage());
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

public void updateLabels(){
        
        System.out.println("UserName: "+myUserName +"My Score : "+myScore+"Opp Score : "+oppScore);
        userNameLabel.setText("UserName : "+ myUserName);
        myScoreLabel.setText("Your Score : "+ myScore);
        oppScoreLabel.setText("Opponent Score : "+ oppScore) ; 
}
    
    
    public Parent createContent() {
        
       updateLabels() ; 
        // Border Size 
        root.setPrefSize(600, 650);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Tile tile = new Tile();
                tile.setTranslateX(j * 200);
                tile.setTranslateY(50 +(i * 200));

                root.getChildren().add(tile);
                board[j][i] = tile;
            }
        }
        userNameLabel.setTranslateX(50);
        userNameLabel.setTranslateY(25);
        
        
        myScoreLabel.setTranslateX(250);
        myScoreLabel.setTranslateY(25);
        
        oppScoreLabel.setTranslateX(450);
        oppScoreLabel.setTranslateY(25);
        
        root.getChildren().addAll(userNameLabel , myScoreLabel , oppScoreLabel) ;
        

        // Cases on it Game Win     
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

    
    
    
//    Game Stage 
    @Override
    public void start(Stage primaryStage) throws IOException {

         
        Scene PlayScene = new Scene(createContent());
//        Scene YouWinScene = new Scene (createWinContent()) ; 
        
        primaryStage.setTitle("Tic Tac Toe App");
        
            primaryStage.setScene(PlayScene);
//        
        primaryStage.show();
    }

//    check if somebody win  
    private void checkState() {
          
        movesCounter++ ;
        System.out.println("Moves Count = "+movesCounter);     
        for (Combo combo : combos) {
            if (combo.isComplete()) {
                playable = false;
                playAnimation(combo);
                break;
            }
        }
        if(movesCounter == 9 && !iWin && !oppWin){  // on Tie Case 
             tie = true ; 
             AfterGame b = new AfterGame() ;
             root.getChildren().add(b);
        }
    }
    Line line;

    // Win Animation
    private void playAnimation(Combo combo) {
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
         
        AfterGame a = new AfterGame() ;
        root.getChildren().add(a);
    }
    
    private class AfterGame extends StackPane{
      
        Rectangle rect ; 
        Text msg ;
        Button playAgain ; 
        public AfterGame(){
            
        
        rect = new Rectangle() ;
        msg = new Text() ; 
        playAgain = new Button("Play Again") ; 
        
        rect.setFill(Color.BLACK);
        rect.setStroke(Color.BLACK);
        rect.setOpacity(0.3); 
        rect.setX(0);
        rect.setY(0);
        rect.setWidth(600);
        rect.setHeight(0);
      
        this.setAlignment(Pos.CENTER) ; // start writing from Center 
        this.msg.setFont(Font.font(0)) ; // increase Text (x or O ) Size 
        
        if (myTurn && !tie){
                
            this.msg.setFill(Color.RED);
            this.msg.setText("You Lose -_-");
            oppScore++ ;
            updateLabels() ;
            System.out.println("OPP Score : "+oppScore);
            oppWin = true ; 
     
        }
        else if (!myTurn && !tie) 
        {
            this.msg.setFill(Color.WHITE);
            this.msg.setText("You Win :D ");
            myScore++ ; 
            updateLabels() ;
            iWin = true ; 
        }        
        else if (tie){
            this.msg.setFill(Color.AZURE);
            this.msg.setText("Tie");
            
        }
        
        
        playAgain.setOnAction((event) -> {
            System.out.println("Clicked !!!");
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3 ; j++) {
                    board[i][j].freeTile();
                    board[i][j].usedBefore = false ; 
                   
                }
            }
            root.getChildren().removeAll(this,line) ;
            playable = true ; 
            movesCounter = 0 ; 
            tie = false ; 
        });
        
        playAgain.setTranslateY(150);
        
        getChildren().addAll(rect, msg , playAgain); 
        //root.getChildren().addAll(rect,msg);
        
         Timeline timeline = new Timeline();
      
         timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(2),
              
                new KeyValue(rect.heightProperty(), 650) ,
                new KeyValue(msg.fontProperty(),Font.font(72))
        ));
        timeline.play();
        
    }
    
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
            border.setFill(Color.LIGHTSEAGREEN);
            border.setStroke(Color.GREY); // Tile Border
            border.setStrokeWidth(5);
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

                        if (c == 'o') {
                            drawX();
                        }
                        if (c == 'x') {
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
//                                    // Special if for Testing 
//                                    if (myId % 2 != 0) {// if my id odd 
//                                        opponentId = myId + 1; // define opponent id  
//                                    } 
//                                    else if (myId % 2 == 0) {
//                                        opponentId = myId - 1;
//                                    }
//                                    
                                  // Send Clicked Tile Data to Server 
                                    myPlayDescription = "GAME PLAY " + myId + " " + opponentId + " " + row + " " + col;
                                    try {
                                        //ps.println(myPlayDescription);
                                        Client.sendMessageOut(myPlayDescription);
                                    } catch (IOException ex) {
                                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    System.out.println(myPlayDescription);
                                    System.out.println("Play Info Sent");
                                }

                            }

                        }
        }
        
        private void freeTile(){
        text.setText(null);
        }
        
        private void drawX() {
            text.setFill(Color.GREY);
            text.setText("X");

        }

        private void drawO() {
            text.setFill(Color.WHITE);
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
