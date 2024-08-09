package project.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class ServerApplication extends Application {
    private String userEmail;

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("log-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 500);
            ServerController serverController = fxmlLoader.getController();
            stage.setTitle("Server");
            stage.setScene(scene);
            stage.setResizable(false);

            // Setting function for closing operation
            stage.setOnCloseRequest(event -> serverController.serverStop());
            stage.sizeToScene();
            stage.show();

            // Starting the server
            serverController.serverStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
