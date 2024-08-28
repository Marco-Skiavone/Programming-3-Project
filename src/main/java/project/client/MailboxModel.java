package project.client;

import java.io.*;
import java.net.Socket;
import java.util.*;
import javafx.application.Platform;
import javafx.collections.*;
import project.utilities.*;
import project.utilities.requests.*;

public class MailboxModel {
    private final String userMail;
    private final HashMap<MailHeader, Email> mailbox;
    private ObservableList<HeaderWrapper> headersList = FXCollections.observableArrayList();

    public MailboxModel(String userMail, List<MailHeader> headers) {
        this.userMail = userMail;
        this.mailbox = new HashMap<>();
        this.headersList.addAll(HeaderWrapper.toWrappedList(headers));
    }

    public String getUserMail() {
        return userMail;
    }

    public ObservableList<HeaderWrapper> getHeadersList() {
        return headersList;
    }

    /** It gets back the Email from the headers. If already saved,
     * the email is retrieved from the email list ({@link #mailbox}).
     * @param header The MailHeader object representing the email to retrieve.
     * @return The Email object represented by "header" parameter.<br>
     * It returns {@code null} if something went wrong with the server. */
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
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

    /** Functions used to send a REFRESH request to the server and get back any received Email.
     * @return 'true' if something new is arrived in the mailbox, 'false' otherwise. */
    public boolean sendRefreshRequest () {
        try (Socket clientSocket = new Socket("127.0.0.1", Utilities.PORT)) {
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            output.writeObject(new Refresh(userMail, getLastHeader()));
            output.flush();
            List<MailHeader> refreshedHeaders = (List<MailHeader>) input.readObject();
            if (!refreshedHeaders.isEmpty()) {
                refreshedHeaders.sort(Comparator.comparing(MailHeader::timestamp).reversed());
                Platform.runLater(() -> headersList.addAll(0, HeaderWrapper.toWrappedList(refreshedHeaders)));
                return true;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    /** @return The {@link MailHeader} with the most recent {@link java.sql.Timestamp} in the headersList.<br>
     * {@code Null} If no header is present. */
    private MailHeader getLastHeader() {
        try {
            // We assume the headersList is sorted whenever an element is added.
            return headersList.get(headersList.size() - 1).getHeader();
        } catch (Exception e) {
            return null;
        }
    }
}
