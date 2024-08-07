package project.server;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ServerController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Server!");
    }
}
