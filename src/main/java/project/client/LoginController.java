package project.client;

import java.net.Socket;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;

public class LoginController {
    /** Check out {@link project.client.LoginController#checkSyntax(String)} method to gain further information. */
    private static final String SYNTAX_PATTERN = "^(?=.{2,32}@)[a-z0-9_-]+(\\.[a-z0-9_-]+)*@[^-][a-z0-9-]+(\\.[a-z-]+)*(\\.[a-z]{2,})$";

    @FXML
    private TextField emailInput;  // the email input
    @FXML
    private Label errorText;    // the label string to show error messages

    /** Function called when login button is pressed in the login view. */
    @FXML
    protected void onLoginButtonClick() {
        try {
            String emailString = emailInput.getText().trim().toLowerCase();
            // Above, we ensured the string is trimmed and lowercase
            if (!checkSyntax(emailString))
                errorText.setText("Invalid email format.");
            else if (!loginConnection(emailString))
                errorText.setText("Unknown email address.");
            else {
                openMailboxView(emailString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorText.setText("Invalid email format.");
        }
    }

    /** It requires lowercase letters and trimmed string, to ensure a simpler input commitment.
     * @param input a string that represents the e-mail to check.
     * @return true if input matches the regex, false otherwise.
     * @note It'll let you insert 24 chars before '@', '-' and '_' (but not strictly before '@'), needs at least 2 letters after domain. */
    public static boolean checkSyntax(String input){
        if(input == null || input.length() < 9)     //e.g: at least ma@dom.it
            return false;
        return input.matches(SYNTAX_PATTERN);
    }

    /** Function that opens up the mailbox view. Called if login check is passed.
     * @param emailName The string representing the formatted email of the user. */
    private void openMailboxView(String emailName){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MailboxController.class.getResource("mailbox-view.fxml"));
            // Following line allows us to get the login-view window and replace the content inside.
            Stage stage = (Stage) emailInput.getScene().getWindow();

            Scene scene = new Scene(fxmlLoader.load(), 800, 530);
            stage.setTitle("MailBox - " + emailName);
            stage.setScene(scene);
            stage.setResizable(false);

            MailboxController mailboxController = fxmlLoader.getController();
            // stage.setOnCloseRequest(event -> mailboxController.shutdownController());    @todo
            stage.show();
            // mailboxController.initModel(emailName);  @todo
        } catch (IOException e) {
            e.printStackTrace();
            errorText.setText("Error occurred while opening the mailbox.");
        }
    }

    /**
     * "127.0.0.1" is an IP used for connections between a client and a server running in the same host.
     * @param login The string representing the email to check.
     */
    public boolean loginConnection(String login) {
        try (Socket clientSocket = new Socket("127.0.0.1", project.server.ServerModel.getPORT())) {
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            output.writeObject("CHECK_ADDR.-/" + login);
            output.flush();
            return input.readBoolean();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
