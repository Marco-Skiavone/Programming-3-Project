package project.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/** Controller used to bind "model" and "view" for a single mail. */
public class MailController {

    @FXML
    private TextField subjectText;

    @FXML
    private Button sendBtn;


    @FXML
    protected void sendMail() {
        if (checkFields()) {
            // @todo finish it
        }
    }

    private boolean checkFields() {
        return (subjectText.getText() == null || subjectText.getText().isBlank()); // @todo finish this
    }
}
