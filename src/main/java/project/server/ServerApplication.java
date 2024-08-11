package project.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("log-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 500);
            stage.setTitle("Server");
            stage.setScene(scene);
            stage.setResizable(false);
            ServerController serverController = fxmlLoader.getController();

            // Setting function for closing operation
            stage.setOnCloseRequest(event -> serverController.serverStop());
            stage.sizeToScene();
            stage.show();

            // Starting the server
            Runnable runner = serverController::serverStart; // setting the function sign to call in a new Thread.
            new Thread(runner).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
