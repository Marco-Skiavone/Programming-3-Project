package project.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerApplication extends Application {
    private String userEmail;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("log-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        ServerController controller = new ServerController(69420, 0);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
