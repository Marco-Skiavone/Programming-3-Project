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
            stage.setScene(scene);
            /*
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("mail-view.fxml"));  // DEB
            Scene scene = new Scene(fxmlLoader.load(), 600, 400); // DEB
            stage.setTitle("Mail - New Mail");  // DEB
            MailController controller = fxmlLoader.getController();   // DEB
            controller.setUpNewMail(new MailModel(), "riccardo.saccu@prog3.edu");     // DEB
            */
            stage.setTitle("Client - LogIn");
            stage.setResizable(false);
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
