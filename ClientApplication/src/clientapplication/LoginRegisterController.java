package clientapplication;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class LoginRegisterController implements Initializable {

    ArrayList<Session> currentOpenSessions = new ArrayList<>();
    @FXML
    TextField loginEmailTxt;
    @FXML
    TextField loginPasswdTxt;
    @FXML
    TextField signupEmailTxt;
    @FXML
    TextField signupPasswdTxt;
    @FXML
    TextField signupPasswdConfTxt;
    @FXML
    TextField signupUsernameTxt;

    Client client=new Client("192.168.1.143", 4444);
    static String sessionUserEmail;
    static String sessionUserPassword;
    String userEmail;
    String userPassword;
    String userConfPassword;
    String userUserName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public static String getSessionLoginEmail() {

        return sessionUserEmail;
    }

    public static String getSessionLoginPassword() {

        return sessionUserPassword;
    }

    
    
    @FXML
    public void HandleLoginAction(ActionEvent event) throws IOException {
        userEmail = loginEmailTxt.getText();
        userPassword = loginPasswdTxt.getText();
        if (!client.connectToServer()) {
            System.out.println("can't connect to server");
        } else {
            //TODO handel incorrect error  
            if(client.login(userEmail, userPassword))
            {
            Main mainApp = new Main();
            mainApp.goToScreen(event, "ChooseModePage");
            }
        }
    }

    public void HandleSignUpAction(ActionEvent event) throws IOException {
        userUserName = signupUsernameTxt.getText();
        System.out.println(userUserName);
        userEmail = signupEmailTxt.getText();
        System.out.println(userEmail);
        userPassword = signupPasswdTxt.getText();
        System.out.println(userPassword);
        userConfPassword = signupPasswdConfTxt.getText();
        System.out.println(userConfPassword);
        if (userPassword.equals(userConfPassword)) {
            System.out.println("password matches");
            if (!client.connectToServer()) {
                System.out.println("can't connect to server");
            } else {
                if( client.register(userUserName, userEmail, userPassword))
                {
                Main mainApp = new Main();
                mainApp.goToScreen(event, "LoginRegisterPage");
                }

            }
        } else {
            System.out.println("password don't match please retype your password");
        }
    }

}
