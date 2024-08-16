package project.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import project.utilities.*;

/** Controller used to bind "model" and "view" for a single mail. */
public class MailController {
    @FXML
    private Label sender;
    @FXML
    private TextField subjectField;
    @FXML
    private TextField receiversField;
    @FXML
    private TextArea mailText;
    @FXML
    private Label errorText;    // Label that pops up a red message if some fields are wrong.
    @FXML
    private Button replyBtn;
    @FXML
    private Button replyAllBtn;
    @FXML
    private Button forwardBtn;
    @FXML
    private Button sendBtn;

    private MailModel model;
    private String userAddress;    // The client address linked to this controller-view

    /** Function that sets the response button to "disabled".
     * @Note: The 3 response buttons are:<br>
     * - {@link #forwardBtn}<br>
     * - {@link #replyBtn}<br>
     * - {@link #replyAllBtn} */
    private void setDisableResponseButton(boolean disabled) {
        forwardBtn.setDisable(disabled);
        replyBtn.setDisable(disabled);
        replyAllBtn.setDisable(disabled);
    }

    /** Function called when the "send" button is pressed in the "mail-view".
     * It checks all the fields calling {@link #checkFields}, then it calls the model function to FETCH a new Email. */
    @FXML
    protected void sendMail() {
        if (!checkFields())
            errorText.setText("Invalid Arguments");
        else {
            // @todo finish it with a model method call
        }
    }

    /** Function that checks the input fields of an email.
     * @return 'true' - If the fields are filled and the receivers are correct.
     * (it also contacts the server through the model) */
    private boolean checkFields() {
        boolean condition = !subjectField.getText().isBlank() && !receiversField.getText().isBlank() &&
        !mailText.getText().isBlank();
        if (!condition) return false;
        for (String field : receiversField.getText().split(",")) {
            String adr = field.trim();
            if (!LoginController.checkSyntax(adr) || !this.model.checkAddress(adr))
                return false;
        }
        return true;
    }

    /** Function that closes the window, without saving the email, if it is in "write-mode". */
    public void closeWindow() {
        ((Stage) subjectField.getScene().getWindow()).close();
    }
}
