package project.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import project.utilities.*;
import project.utilities.requests.FetchMail;
import project.utilities.requests.SendMail;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

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
            Email email = new Email(sender.getText(), model.getReceiversList(), model.valueOfSubjectPrt(),
                    model.valueOfBodyPrt(), LocalDateTime.now());
            try (Socket clientSocket = new Socket("127.0.0.1", Utilities.PORT)) {
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                output.writeObject(new SendMail(sender.getText(), email));
                output.flush();
                if (input.readBoolean()) {
                    // @todo output that is all good
                    shutdownEditor();
                } else {
                    // @todo warning message
                }
            } catch (Exception e) {
                e.printStackTrace();
                // @todo handle exception
            }
        }
    }

    private void mailPropertyBinding() {
        sender.textProperty().bindBidirectional(model.getSenderPrt());
        receiversField.textProperty().bindBidirectional(model.getReceiverPrt());
        subjectField.textProperty().bindBidirectional(model.getSubjectPrt());
        mailText.textProperty().bindBidirectional(model.getBodyPrt());
    }

    /** Method called when a New Mail has to be written. It disables the "response" buttons.
     * @param userAddress The sender of the Email
     */
    public void startNewMailView(String userAddress) {
        model = new MailModel(userAddress);
        mailPropertyBinding();
        setDisableResponseButton(true);
    }

    public void readMail(String userAddress, Email email) {
        model = new MailModel(userAddress, email);
        mailPropertyBinding();
        subjectField.setEditable(false);
        receiversField.setEditable(false);
        mailText.setEditable(false);
        setDisableResponseButton(false);
        sendBtn.setDisable(true);
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
            if (!Utilities.checkSyntax(adr) || !model.checkAddress(adr))
                return false;
        }
        return true;
    }

    /** Function that closes the window, without saving the email, if it is in "write-mode". */
    public void shutdownEditor() {
        // @todo add the shutdown of the schedulers !
        ((Stage) subjectField.getScene().getWindow()).close();
    }
}
