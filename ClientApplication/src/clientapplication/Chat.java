/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientapplication;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author rahma
 */
public class Chat extends Application {
    private TextArea messages = new TextArea();
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
       primaryStage.show();
    
    }
    
      public static void main(String[] args) {
        launch(args);
}
      
      
         private Parent createContent() {
        messages.setFont(Font.font(72));
        messages.setPrefHeight(550);
        TextField input = new TextField();
        input.setOnAction(event -> {
            String message = null;// = isServer ? "Server: " : "Client: ";
            message += input.getText();
            input.clear();

            messages.appendText(message + "\n");

            try {
               // Client.serverOut.write(senderId rasiverId message.getBytes());
            }
            catch (Exception e) {
                messages.appendText("Failed to send\n");
            }
        });

        VBox root = new VBox(20, messages, input);
        root.setPrefSize(600, 600);
        return root;
}
    
}


