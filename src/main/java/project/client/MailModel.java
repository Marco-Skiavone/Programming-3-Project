package project.client;

import project.utilities.*;

import java.io.*;
import java.net.*;

/** Model used to define methods for a single mail visualization/editing. */
public class MailModel {

    /** Function that asks the server if the string passed is a valid address or not.
     * @param adr The address to make the server check.
     * @return a boolean in base of the response from the server. */
    public boolean checkAddress(String adr) {
        try (Socket clientSocket = new Socket("127.0.0.1", Utilities.PORT)) {
            if ( adr == null || adr.isBlank())
                throw new RuntimeException("Subject field is empty");
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            output.writeObject("CHECK_ADDR.-/" + adr);  // @todo refactor this line
            output.flush();
            return input.readBoolean();
        } catch(Exception e) {
            return false;
        }
    }
}
