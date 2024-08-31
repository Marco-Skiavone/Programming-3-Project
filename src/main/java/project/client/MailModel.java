package project.client;

import javafx.beans.property.SimpleStringProperty;
import project.utilities.*;
import project.utilities.requests.*;
import java.io.*;
import java.net.*;
import java.util.*;

/** Model used to define methods for a single mail visualization/editing. */
public class MailModel {
    /** The person identified by the client address */
    private String userAddress;

    /** Sender of the email, sometimes can be the same as {@link #userAddress}. */
    private final SimpleStringProperty senderPrt;
    /* Properties are used to link up with controller fields */
    private final SimpleStringProperty receiverPrt;
    /* Properties are used to link up with controller fields */
    private final SimpleStringProperty subjectPrt;
    /* Properties are used to link up with controller fields */
    private final SimpleStringProperty bodyPrt;

    private MailModel() {
        senderPrt = new SimpleStringProperty();
        receiverPrt = new SimpleStringProperty();
        subjectPrt = new SimpleStringProperty();
        bodyPrt = new SimpleStringProperty();
    }

    /** Constructor used to set up a "New Mail" view. */
    public MailModel(String sender) {
        this();
        this.userAddress = sender;
        senderPrt.setValue(userAddress);
        receiverPrt.setValue("");
        subjectPrt.setValue("");
        bodyPrt.setValue("");
    }

    /** Constructor used to set up read-mode and "response" views: Reply, Reply All and Forward. */
    public MailModel(String userAddress, Email email) {
        this();
        this.userAddress = userAddress; // userAddress can be different from sender!
        senderPrt.setValue(email.getSender());
        String receiversString = email.getReceivers().toString();
        receiverPrt.setValue(receiversString.substring(1, receiversString.length() - 1));
        subjectPrt.setValue(email.getSubject());
        bodyPrt.setValue(email.getText());
    }

    public String getUserAddress() {
        return userAddress;
    }

    public List<String> getReceiversList() {
        return Arrays.stream(receiverPrt.getValue().split(",")).toList();
    }

    public SimpleStringProperty getSenderPrt() {
        return senderPrt;
    }

    public SimpleStringProperty getReceiverPrt() {
        return receiverPrt;
    }

    public SimpleStringProperty getSubjectPrt() {
        return subjectPrt;
    }

    public SimpleStringProperty getBodyPrt() {
        return bodyPrt;
    }

    public String valueOfSenderPrt() {
        return senderPrt.getValue();
    }

    public String valueOfReceiverPrt() {
        return receiverPrt.getValue();
    }

    public String valueOfSubjectPrt() {
        return subjectPrt.getValue();
    }

    public String valueOfBodyPrt() {
        return bodyPrt.getValue();
    }

    /** Function that asks the server if the string passed is a valid address or not.
     * @param adr The address to make the server check.
     * @return a boolean in base of the response from the server. */
    public boolean checkAddress(String adr) {
        try (Socket clientSocket = new Socket("127.0.0.1", Utilities.PORT)) {
            if ( adr == null || adr.isBlank())
                throw new RuntimeException("Invalid \"To:\" field.");
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            output.writeObject(new CheckAddress(adr));
            output.flush();
            return input.readBoolean();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean serverCheck() {
        return checkAddress(userAddress);
    }
}
