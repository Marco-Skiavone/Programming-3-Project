package project.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import project.utilities.*;
import project.utilities.requests.*;
import java.io.*;
import java.net.Socket;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

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
    /** Executor to make error appear and disappear after a while. */
    private ScheduledExecutorService errorExecutor;
    /** Executor to send the requests even if the server is offline. It will start a cycle of sending. */
    private ScheduledExecutorService sendRequestScheduler;

    @FXML
    private void initialize() {
        // Used to remove the automatic cursor selection of the subjectField.
        Platform.runLater(() -> sendBtn.getScene().getRoot().requestFocus());
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

    /** Function called when the "send" button is pressed in the "mail-view".
     * It checks all the fields calling {@link #checkFields}, then it calls the model function to FETCH a new Email. */
    @FXML
    protected void sendMail() {
        if (model.serverCheck())
            directlySendMail();
        else {
            setErrorText("Cannot contact the server. \"Send Schedule\" started. (Do not close this tab)", null);
            scheduledSendMail();
        }
    }

    /** It sends an email to the server, which is assumed to be online. */
    private void directlySendMail () {
        if (!checkFields())     // can throw a RuntimeException
            setErrorText("Invalid Arguments!", "#fa0000");
        else {
            Email email = new Email(sender.getText(), model.getReceiversList(), model.valueOfSubjectPrt(),
                    model.valueOfBodyPrt(), LocalDateTime.now());
            try (Socket clientSocket = new Socket("127.0.0.1", Utilities.PORT)) {
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                output.writeObject(new SendMail(sender.getText(), email));
                output.flush();
                if (input.readBoolean()) {
                    shutdownEditor();
                } else {
                    setErrorText("Server is not responding.", "#fa0000");
                    System.err.println("Server is not responding.");
                }
            } catch (Exception e) {
                setErrorText("Error occurred while sending an email.", "#fa0000");
                e.printStackTrace();
            }
        }
    }

    /** Triggers a scheduler to send an email. */
    private void scheduledSendMail () {
        try {
            if (sendRequestScheduler == null || sendRequestScheduler.isShutdown())
                sendRequestScheduler = Executors.newSingleThreadScheduledExecutor();

            sendRequestScheduler.scheduleWithFixedDelay(() -> {
                if (model.serverCheck() && !sendRequestScheduler.isShutdown())
                    directlySendMail();
            }, 20, 20, TimeUnit.SECONDS);
        } catch(Exception e) {
            setErrorText("Error occurred while scheduling the email send.", "#fa0000");
            e.printStackTrace();
        }
    }

    /** --- FORWARD ---<br>
     * This function modifies the actual Mail-view (resetting Model) to correctly fill some fields. */
    @FXML
    protected void forwardMail() {
        String forwardText = "--->>> Forwarded by " + model.valueOfSenderPrt() + " <<<---\n\n" + model.valueOfBodyPrt();

        Email emailToForward = new Email(model.getUserAddress(), new ArrayList<>(), model.valueOfSubjectPrt(),
                forwardText, LocalDateTime.now());
        model = new MailModel(model.getUserAddress(), emailToForward);
        mailPropertyBinding();
        setDisableResponseButton(true);
        sendBtn.setDisable(false);
        receiversField.setEditable(true);   // Setting editable fields (the others will remain as in "read-mode")
        ((Stage) forwardBtn.getScene().getWindow()).setTitle("Mail - Forward: \"" + model.valueOfSubjectPrt() + "\"");
    }

    /** --- REPLY ---<br>
     * This function modifies the actual Mail-view (resetting Model) to correctly fill some fields. */
    @FXML
    protected void replyMail() {
        String replyText = "Reply to " + model.valueOfSenderPrt() + ":\n\n" + model.valueOfBodyPrt() + "\n-------\n\n";

        Email emailToReply = new Email(model.getUserAddress(), Collections.singletonList(model.valueOfSenderPrt()),
                model.valueOfSubjectPrt(), replyText, LocalDateTime.now());
        model = new MailModel(model.getUserAddress(), emailToReply);
        mailPropertyBinding();
        setDisableResponseButton(true);
        sendBtn.setDisable(false);
        mailText.setEditable(true);   // Setting editable fields (the others will remain as in "read-mode")
        ((Stage) forwardBtn.getScene().getWindow()).setTitle("Mail - Reply: \"" + model.valueOfSubjectPrt() + "\"");
    }

    /** --- REPLY ALL ---<br>
     * This function modifies the actual Mail-view (resetting Model) to correctly fill some fields. */
    @FXML
    protected void replyAllMail() {
        ArrayList<String> receiversList = new ArrayList<>(model.getReceiversList());
        receiversList.remove(model.getUserAddress());
        receiversList.add(model.valueOfSenderPrt());

        String replyText = receiversList.toString();
        replyText = "Reply to " + replyText.substring(1, replyText.length()-1) + ":\n\n" + model.valueOfBodyPrt() +
                "\n-------\n\n";

        Email emailToReply = new Email(model.getUserAddress(), receiversList, model.valueOfSubjectPrt(), replyText,
                LocalDateTime.now());
        model = new MailModel(model.getUserAddress(), emailToReply);
        mailPropertyBinding();
        setDisableResponseButton(true);
        sendBtn.setDisable(false);
        mailText.setEditable(true);   // Setting editable fields (the others will remain as in "read-mode")
        ((Stage) forwardBtn.getScene().getWindow()).setTitle("Mail - Reply All: \"" + model.valueOfSubjectPrt() + "\"");
    }

    /** Function that binds the client-view fields to the model variables. */
    private void mailPropertyBinding() {
        sender.textProperty().bindBidirectional(model.getSenderPrt());
        receiversField.textProperty().bindBidirectional(model.getReceiverPrt());
        subjectField.textProperty().bindBidirectional(model.getSubjectPrt());
        mailText.textProperty().bindBidirectional(model.getBodyPrt());
    }

    /** Method called when a New Mail has to be written. It disables the "response" buttons.
     * @param userAddress The sender of the Email */
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

    /** Synchronized function used to show error messages to the client view, as feedback
     * of the operations requested by the user.
     * @param text The error to show in the client view (in yellow).
     * @param colorHex It has to be a string formatted as "#xxxxxx", where the "x" are hexadecimal values.
     * If {@code colorHex == null}, then the color picked is the "default" warning yellow. (something went wrong) */
    private synchronized void setErrorText(String text, String colorHex) {
        try {
            errorExecutor = Executors.newSingleThreadScheduledExecutor();
            colorHex = colorHex != null ? colorHex : "#ffd400";     // "warning-yellow" if colorHex is null
            errorText.setTextFill(Paint.valueOf(colorHex));
            errorText.setText(text);
            errorExecutor.schedule(() -> Platform.runLater(()-> errorText.setText("")), 2, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("Error in \"errorExecutor\" scheduling: " + e.getMessage());
        } finally {
            if (errorExecutor != null && !errorExecutor.isShutdown())
                errorExecutor.shutdown();
        }
    }

    /** Function that closes the window, without saving the email, if it is in "write-mode". */
    public void shutdownEditor() {
        if (sendRequestScheduler != null && !sendRequestScheduler.isShutdown())
            sendRequestScheduler.shutdown();
        if (errorExecutor != null && !errorExecutor.isShutdown())
            errorExecutor.shutdown();
        ((Stage) subjectField.getScene().getWindow()).close();
    }
}
