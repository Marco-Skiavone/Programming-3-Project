package project.client;

import java.io.*;
import java.net.*;
import java.util.*;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import project.utilities.*;
import project.utilities.requests.*;

public class LoginController {
    @FXML
    private TextField emailInput;  // the email input
    @FXML
    private Label errorText;    // the label string to show error messages

    private List<MailHeader> headersList;

    /** Function called when login button is pressed in the login view. */
    @FXML
    protected void onLoginButtonClick() {
        try {
            String emailString = emailInput.getText().trim().toLowerCase();
            // Above, we ensured the string is trimmed and lowercase
            if (!Utilities.checkSyntax(emailString))
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
            stage.setOnCloseRequest(event -> mailboxController.shutdownController());
            stage.show();
            mailboxController.initModel(emailName, this.headersList);
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
        try (Socket clientSocket = new Socket("127.0.0.1", Utilities.PORT)) {
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            output.writeObject(new LogIn(login));
            output.flush();
            Object inObj = input.readObject();
            if (inObj != null)
                headersList = Utilities.castToMailHeadersList(inObj);
            return inObj != null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            errorText.setText("Error occurred in server LogIn request.");
            return false;
        }
    }
}
