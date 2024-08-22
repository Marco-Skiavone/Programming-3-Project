package project.client;

import java.io.*;
import java.net.Socket;
import java.util.*;
import javafx.application.Platform;
import javafx.collections.*;
import project.utilities.*;
import project.utilities.requests.*;

public class MailboxModel {
    private String userMail;
    private final HashMap<MailHeader, Email> mailbox;   // @todo change MailHeader with ID, if implemented
    private ObservableList<MailHeader> headersList = FXCollections.observableArrayList();

    public MailboxModel(String userMail, List<MailHeader> headers) {
        this.userMail = userMail;
        this.mailbox = new HashMap<>();
        this.headersList.addAll(headers);
    }

    public String getUserMail() {
        return userMail;
    }

    public ObservableList<MailHeader> getHeadersList() {
        return headersList;
    }

    /** It gets back the Email from the headers. If already saved,
     * the email is retrieved from the email list ({@link #mailbox}).
     * @param header The MailHeader object representing the email to retrieve.
     * @return The Email object represented by "header" parameter.<br>
     * It returns null if something went wrong with the server. */
    public Email retrieveEmail(MailHeader header) {
        if (mailbox.containsKey(header))
            return mailbox.get(header); // if already present, it returns it back quicker
        else {
            try (Socket clientSocket = new Socket("127.0.0.1", Utilities.PORT)) {
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                output.writeObject(new FetchMail(userMail, header));
                output.flush();
                Email fetchedEmail = (Email) input.readObject();
                mailbox.put(header, fetchedEmail);
                return fetchedEmail;
            } catch (Exception e) {
                e.printStackTrace();
                // @todo handle exception
            }
        }
        return null;
    }

    /** Functions used to send a REFRESH request to the server and get back any received Email. */
    public void sendRefreshRequest () {
        try (Socket clientSocket = new Socket("127.0.0.1", Utilities.PORT)) {
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            output.writeObject(new Refresh(userMail, getLastHeader()));
            output.flush();
            List<MailHeader> refreshedHeaders = (ArrayList<MailHeader>) input.readObject();
            if (!refreshedHeaders.isEmpty()) {
                Platform.runLater(() -> {
                    headersList.addAll(refreshedHeaders);
                    headersList.sort(Comparator.comparing(MailHeader::timestamp));
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            // @todo handle exception
        }
        /* @todo 1. send the request
         *       2. update any new mail present
         *       3. if new mails were present, send a warning message. */
    }

    /** @return The {@link MailHeader} with the most recent {@link java.sql.Timestamp} in the headersList. */
    private MailHeader getLastHeader() {
        try {
            // We assume the headersList is sorted whenever an element is added.
            return headersList.get(headersList.size() - 1);
        } catch (Exception e) {
            return null;
        }
    }
}
