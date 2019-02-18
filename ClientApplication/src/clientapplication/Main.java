package clientapplication;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
  static Stage mainStage;
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("StartGamePage.fxml"));
        Scene scene = new Scene(root);
        mainStage=stage;
        stage.setScene(scene);
        stage.show();
        
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                  //  Client.loggoff();
                    stop();
                    stage.close();
                    Platform.exit();
                    System.exit(0);
                } catch (Exception exception) {
                    System.err.println("> close application problem: "+exception.getMessage());
                }
            }
        });
    }

    public void goToScreen(ActionEvent event, String pageName) throws IOException {
        Parent nextView = FXMLLoader.load(getClass().getResource(pageName + ".fxml"));
        Scene nextScene = new Scene(nextView);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(nextScene);
        window.show();

    }

   
    
    public static void gotoPlay(Scene scene)
    {  
        
        mainStage.setScene(scene);
      mainStage.show();
    }
}
