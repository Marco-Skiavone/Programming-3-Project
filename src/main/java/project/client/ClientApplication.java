package project.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);
            stage.setTitle("Client - LogIn");
            stage.setScene(scene);
            stage.setResizable(false);
            LoginController loginController = fxmlLoader.getController();
            stage.sizeToScene();
            stage.show();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
