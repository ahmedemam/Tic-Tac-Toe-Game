package clientapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Client {

    private final String serverName;
    private final int serverPort;
    private Socket clientSocket;
    private InputStream serverIn;
    private static OutputStream serverOut;
    private BufferedReader bufferedIn;
    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList();
    private ArrayList<MessageListener> messageListeners = new ArrayList();
    private LinkedHashMap<String, String> playerData = new LinkedHashMap<String, String>();
    //private LinkedHashMap<String, String> playerData = new LinkedHashMap<String, String>();
    Alert alert = new Alert(AlertType.CONFIRMATION);
    public  static String setterToken="pre";
    
    SessionList sessionList = new SessionList();
    PlayerDataSession playerDataSession = new PlayerDataSession();

    //constractor 
    public Client(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        // System.out.println(serverName +" "+serverPort);
    }

    public static void sendMessageOut(String message) throws IOException {
        message += "\n";
        serverOut.write(message.getBytes());
        serverOut.flush();
    }

    public static String readTokens(){
        System.out.println("SetterToken : "+setterToken);
        return setterToken;
    }
    public static void clearTokens(){
        setterToken ="pre" ; 
    }
    /*public static void main(String[] args) throws IOException {
     // TODO code application logic here
     Client newClient = new Client("localhost", 4444);
     // replaced 
     newClient.addUserStatusListener(new UserStatusListener() {
     @Override
     public void online(String userName, String userScore) {
     System.out.println("Online:" + userName + " " + userScore);
     }

     @Override
     public void offline(String userName, Strinremoveg userScore) {
     System.out.println("Offline:" + userName + " " + userScore);
     }
     });

     newClient.addMessageListener(new MessageListener() {
     @Override
     public void onMessage(String fromLoginEmail, String msgBody) {
     System.out.print("You got message from" + fromLoginEmail + "==>" + msgBody);
     }
     });

     if (!newClient.connectToServer()) {
     System.err.println("Connect failed ");
     } else {
     System.out.println("Connect successful");
     //String serverResponse=
     newClient.login("hager@gmail.com", "123456");
     //newClient.register("Karam","karam@gmail","123456");
     //newClient.login("ali@gmail", "123456");

     //newClient.msg("jim", "Hello World");
     }

     }*/
    public boolean connectToServer() {
        try {
            Socket clientSokect = new Socket(serverName, serverPort);
            //System.out.println("Client port is " + clientSokect.getLocalPort());
            this.serverOut = clientSokect.getOutputStream();
            this.serverIn = clientSokect.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException ex) {
            System.out.println("> exception: " + ex.getMessage());
        }

        return false;

    }

    public boolean login(String userEmail, String userPassword) throws IOException {

        String cmd = "LOGIN " + userEmail + " " + userPassword + "\n";
        serverOut.write(cmd.getBytes());
        String serverResponse = bufferedIn.readLine();
        System.out.println("Server Responce :" + serverResponse);

        boolean isSuccessLogin = false;
        if ("FAILED LOGIN".equals(serverResponse) || "WEIRD TOKENS".equals(serverResponse)) {
            isSuccessLogin = false;
        } else {
            isSuccessLogin = true;
            //storing returned data in playerDate hashmap
            String[] serverTokens = serverResponse.split(" ");
            String playerDataToStore = "";
            if ("SUCCESS".equals(serverTokens[0]) && "LOGIN".equals(serverTokens[1])) {
                playerDataToStore = serverTokens[2];
                storePlayerData(playerDataToStore);
            }
            startMessageReader();
            startMessageSender();
        }
        return isSuccessLogin;
    }

    public boolean register(String userName, String userEmail, String userPassword) throws IOException {
        String cmd = "INSERT PLAYER " + userName + " " + userEmail + " " + userPassword + "\n";
        serverOut.write(cmd.getBytes());
        String serverResponse = bufferedIn.readLine();
        String[] tokens = serverResponse.split(" ");
        if (tokens.length == 2) {
            return false;
        } else {
            return true;
        }
    }

       public static void loggoff() throws IOException {
        String cmd = "SIGNOUT PLAYER ";
       String playerOffline="";
        for (Map.Entry<String, String> entrySet : PlayerDataSession.playerDataSession.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            playerOffline+=key+":"+value+",";
        }
        
        cmd+=playerOffline;
        
        
        serverOut.write(cmd.getBytes());
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void startMessageReader() {
        Thread messagesThread = new Thread() {
            public void run() {
                readMessageLoop();
            }
        };
        messagesThread.start();
    }

    public void startMessageSender() {
        Thread messagesThread = new Thread() {
            public void run() {
                sendMessageLoop();
            }
        };
        messagesThread.start();
    }

    private void sendMessageLoop() {
        while (true) { //check if there is a requset ->"invite bottun clicked" --> 
            //to overcome sending the same request more than once 

            if (Message.isMessageSend == 1) {
                System.out.println(Message.isMessageSend);
                System.out.println(Message.request);
                String msgToServer = Message.request;
                try {
                    System.out.println(msgToServer);
                    serverOut.write(msgToServer.getBytes());
                    Message.isMessageSend = 0;
                } catch (IOException ex) {
                    System.out.println("can't send msg to server");
                }
            }
        }
    }

    private void readMessageLoop() {
        try {
            // TOKENS TYPES

            String clientMessage;
            int counter = 0;
            while ((clientMessage = bufferedIn.readLine()) != null) {
                counter++;
                String[] inputTokens = clientMessage.split(" ");

                if (inputTokens.length > 0) {
                    String cmd = inputTokens[0];

                    if ("ONLINE".equals(inputTokens[0])) {
                        handleOnline(inputTokens);
                    } else if ("OFFLINE".equalsIgnoreCase(cmd)) {
                        handleOffline(inputTokens);
                    }

                    // game play 
                    if ("GAME".equals(inputTokens[0]) && "PLAY".equals(inputTokens[1])) { 
                        setterToken = clientMessage ; 
                        readTokens() ;
//                 s       System.out.println("Changed S : "+setterToken ); 
                    }
                    
                    
                    ////////////handing invitaions
                    if ("INVITATION".equals(inputTokens[0]) && "REQUEST".equals(inputTokens[1])) {   //TO DO
                        Platform.runLater(new Runnable() {
                            
                            int senderId = Integer.parseInt(inputTokens[3]) ;
                            int recieverId =  Integer.parseInt(inputTokens[2]) ;
                            
                            @Override
                            public void run() {
                                Alert alert = new Alert(AlertType.CONFIRMATION);
                                alert.setTitle("Invitation");
                                alert.setContentText("new invitation from " + inputTokens[4]);
                                ButtonType buttonAccept = new ButtonType("accept");
                                ButtonType buttonRejct = new ButtonType("reject");
                                alert.getButtonTypes().setAll(buttonAccept, buttonRejct);
                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == buttonAccept) {
                                    try {
                                        String messageRespone="INVITATION RESPONSE "+inputTokens[3]+" "+inputTokens[2]+" "+inputTokens[4]+" YES"; 
                                       sendMessageOut(messageRespone);
                                       
                                          Game game=new Game(senderId,"Player 2 ",0,recieverId,"Player 1",300,'x');
                                       Scene s = new Scene (game.createContent()) ; 
                                       Main.gotoPlay(s);
                                       alert.close();
                                    } catch (IOException ex) {
                                        System.out.println("error in accept");
                                    }
                                } else if (result.get() == buttonRejct) {
                                       String messageRespone="INVITATION RESPONSE "+inputTokens[3]+" "+inputTokens[2]+" "+inputTokens[4]+" NO"; 
                                    try {
                                        sendMessageOut(messageRespone);
                                        alert.close();
                                    } catch (IOException ex) {
                                        System.out.println("error in reject");
                                    }
                                }
                            }
                        });

                    }
                    if ("INVITATION".equals(inputTokens[0]) && "RESPONSE".equals(inputTokens[1]))
                    {
                        
                        int senderId = Integer.parseInt(inputTokens[3]) ;
                            int recieverId =  Integer.parseInt(inputTokens[2]) ;
                        System.out.println("yes message state");
                        if("YES".equals(inputTokens[5]))
                        { System.out.println("yes");
                            Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Alert alert = new Alert(AlertType.CONFIRMATION);
                                alert.setTitle("Invitation Acceptance ");
                                alert.setContentText("your invetation to "+inputTokens[4]+"is accepted" );
                                ButtonType buttonPlay = new ButtonType("Start Playing ");
                               
                                alert.getButtonTypes().setAll(buttonPlay);
                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == buttonPlay) {
                                    
//                                        /////
                                       Game game=new Game(senderId,"Player 1",0,recieverId,"Player 2",500,'o');
                                       Scene s = new Scene (game.createContent()) ; 
                                       Main.gotoPlay(s);
                                       
                                       alert.close();
                                 
                                } 
                            }
                        });
                                                 /////////////////////
                            
                            
                            
                            
                        }
                        if(inputTokens[5]=="NO")
                        {
                            
                                     Platform.runLater(new Runnable() {

                            @Override
                            public void run() {
                                Alert alert = new Alert(AlertType.CONFIRMATION);
                                alert.setTitle("Invitation Rejection ");
                                alert.setContentText("sorry your invetation to "+inputTokens[4]+"is rejected" );
                                ButtonType buttonOk = new ButtonType("Ok");
                               
                                alert.getButtonTypes().setAll(buttonOk);
                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == buttonOk) {
                            
                                    alert.close();
                                     
                                } 
                            }
                        });
                            
                            
                        }
                    
                    
                    }

//                else if ("msg".equalsIgnoreCase(cmd))
//                {
//                   String [] tokensMsg=clientMessage.split(" ");
//                   handeleMessage(tokensMsg);
//                }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                clientSocket.close();
            } catch (IOException ex1) {
                ex1.printStackTrace();
            }

        }

    }

    public void handleOnline(String[] tokens) {
        String userId = tokens[1];
        String userName = tokens[2];
        String userScore = tokens[3];
        Session sessionUser = new Session(userId, userName, userScore);
        SessionList.usersSessionLists.add(sessionUser);
        System.out.println("handleOnline(): " + tokens[1] + " " + tokens[2] + " " + tokens[3]);
        // UserStatusListner
        for (UserStatusListener listener : userStatusListeners) {
            listener.online(userName, userScore);
        }
    }

    public void handleOffline(String[] tokens) {
        String userId = tokens[1];
        String userName = tokens[2];
        String userScore = tokens[3];
        // UserStatusListner
        for (UserStatusListener listener : userStatusListeners) {
            listener.online(userName, userScore);
        }

    }

    private void msg(String sendTo, String msgBody) throws IOException {

        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());

    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    private void handeleMessage(String[] tokensMsg) {

        String userEmail = tokensMsg[1];
        String msgBody = tokensMsg[2];
        for (MessageListener listener : messageListeners) {
            listener.onMessage(userEmail, msgBody);

        }

    }

    private void storePlayerData(String playerDataToStore) {

        String[] playerKeyValues = playerDataToStore.split(",");
        for (String playerKeyValue : playerKeyValues) {
            String[] keyValuePair = playerKeyValue.split(":");
            String key = keyValuePair[0];
            String value = keyValuePair[1];
            playerData.put(key, value);
            //System.out.println(playerData.get(keyValuePair[0]));
        }


        PlayerDataSession.playerDataSession = playerData;
        //PlayerDataSession.playerData=playerArray;
        for (Map.Entry<String, String> entrySet : PlayerDataSession.playerDataSession.entrySet()) {
            Object key = entrySet.getKey();
            Object value = entrySet.getValue();
            System.out.println(key + " " + value);
        }

    }
        public static String getID()
    {  
        return PlayerDataSession.playerDataSession.get("PlayerId");
}
}
