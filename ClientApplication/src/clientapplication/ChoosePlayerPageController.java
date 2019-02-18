package clientapplication;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author rahma
 */
public class ChoosePlayerPageController implements Initializable {

    String recieverId;
    String revieverName;
    String revieverScore;

    String senderId = PlayerDataSession.playerDataSession.get("PlayerId");
    String senderName = PlayerDataSession.playerDataSession.get("PlayerName");

    @FXML
    private ListView<String> usersList;
    @FXML
    private Button refresh;
    SessionList sessionList = new SessionList();

//    @Override
//    public void online(String userName, String userScore) {
//        usersList.getItems().add(userName + " -> " + userScore);
//    }
//
//    @Override
//    public void offline(String userName, String userScore) {
//
//        this.usersList.getItems().removeAll(userName + " -> " + userScore);
//    
    public void sendInvitation() throws IOException {
        //INVITATION REQUEST SENDER_ID RECIVER_ID
        String invitationRequest = "INVITATION REQUEST " + senderId + " " + recieverId+" "+senderName;
        Message.senderName = senderName;
        Message.isMessageSend = 1;
        Message.request = invitationRequest;
    }

    public void updateUsersList() {
        usersList.getItems().clear();
        ArrayList<Session> newUsersSessions = SessionList.usersSessionLists;
        for (Session next : newUsersSessions) {
            String player = next.getUserID() + " " + next.getUserName() + " " + next.getUserScore();
            usersList.getItems().add(player);
            
         
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        ArrayList<Session> usersSessions = SessionList.usersSessionLists;
//        LinkedHashMap<String, String> playerData = new LinkedHashMap<>();
//        for (Iterator<Session> iterator = usersSessions.iterator(); iterator.hasNext();) {
//            Session next = iterator.next();
//            usersList.getItems().add(next.getUserID() + " " + next.getUserName() + " " + next.getUserScore());
//            usersList.setCellFactory(lv -> new ListCell<String>() {
//                @Override
//                public void updateItem(String playerDate, boolean empty) {
//                    super.updateItem(playerDate, empty);
//                    setText(empty ? null : next.getUserName() + " " + next.getUserScore());
//                }
//            });
//        }
//        playerData = PlayerDataSession.playerDataSession;
        usersList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                try {
                    String selectedPlayer = usersList.getSelectionModel().getSelectedItem();
                    String[] playerArr = selectedPlayer.split(" ");
                    recieverId = playerArr[0];
                    revieverName = playerArr[1];
                    revieverScore = playerArr[2];
                    System.out.println(recieverId + " " + revieverName + " " + revieverScore);
                    String invitationRequest = "INVITATION REQUEST " + senderId + " " + recieverId+" "+senderName;
                    Client.sendMessageOut(invitationRequest);
//                          String GameAction;
//                          Client.sendMessageOut(GameAction);
                } catch (IOException ex) {
                    Logger.getLogger(ChoosePlayerPageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
