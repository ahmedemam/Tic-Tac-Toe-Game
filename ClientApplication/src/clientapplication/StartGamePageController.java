package clientapplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author rahma
 */
public class StartGamePageController implements Initializable {

    @FXML
    private Button startbtn;
    @FXML
    private Label label;
    //public void gotoNextScreen(ActionEvent event) throws IOException
//  { System.out.println("on my way to screen1");
//      Parent nextView =FXMLLoader.load(getClass().getResource("StartGamePage.fxml"));
//    Scene nextScene =new Scene (nextView);
//    Stage window =(Stage)((Node)event.getSource()).getScene().getWindow();
//    window.setScene(nextScene);
//    window.show();
//  }

    public void HandleStartAction(ActionEvent event) throws IOException {
        Main mainApp = new Main();
        mainApp.goToScreen(event, "LoginRegisterPage");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
