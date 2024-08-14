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
    private String user;    // The client address linked to this controller-view

    /** Function called by FXML to bind the ListView to the logMsgList */
    @FXML
    public void initialize() {

    }

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

    public void setUpForward (MailModel model, String userAddress, Email email) {

    }

    public void setUpReply (MailModel model, String userAddress, Email email, boolean replyAll) {

    }

    public void setUpNewMail (MailModel model, String userAddress) {
        try {
            this.user = userAddress;    // @todo check
            sender.setText(user);
            /* We remove the common part of the title and extract the type of editing we want to do. */
            String type = ((Stage) sender.getScene().getWindow()).getTitle().split(" ")[2];
            if (!type.equals("View") && user != null)
                sender.setText(user);
            switch (type) {
                case "View":
                    // Can NOT modify anything, but can trigger "response" buttons
                    subjectField.setDisable(true);
                    receiversField.setDisable(true);
                    mailText.setDisable(true);
                    sendBtn.setDisable(true);
                    break;
                case "Forward":
                    // can modify Receivers
                    sendBtn.setDisable(true);
                    subjectField.setDisable(true);
                    mailText.setDisable(true);
                    setDisableResponseButton(true);
                    break;
                case "Reply":
                case "Reply All":
                    // can modify Text (and trigger Send)
                    subjectField.setDisable(true);
                    receiversField.setDisable(true);
                    setDisableResponseButton(true);
                    break;
                case "New":
                    // can modify everything, but the "response" buttons
                    setDisableResponseButton(true);
                    break;
                default:
                    throw new RuntimeException("Unknown Email Type.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.model = model;
        }
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
            if (!LoginController.checkSyntax(adr.trim()) || !this.model.checkAddress(adr))
                return false;
        }
        return true;
    }
}
