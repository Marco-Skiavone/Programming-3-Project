package project.client;

import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LoginController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Login?");
    }


/*
 * 
 *      "127.0.0.1" is an IP used for connections between a client and a server runnig in the same host.
 * 
 *      to do:
 *      - All the logic of the login
 * 
 */

 public void getConnection(String login)
 {
     try{
        Socket clientSocket = new Socket("127.0.0.1",69420);
        ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
        output.writeObject(login);
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
 }
}
