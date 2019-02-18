package clientapplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

public class ChooseModePageController implements Initializable {

    private Client clientapplication;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void HandleSingleModeAction(ActionEvent event) throws IOException, Exception {
    
        System.out.print("single mode");
    //Main.gotoPlay(new EasyLevel().getScene());
        EasyLevel level = new EasyLevel();
        level.start(Main.mainStage);
        
    }

    public void HandleMultiModeAction(ActionEvent event) throws IOException {
        Main mainApp = new Main();
        mainApp.goToScreen(event, "ChoosePlayerPage");
    }
}