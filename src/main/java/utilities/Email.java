package utilities;

import java.io.*;
import java.time.*;
import java.util.*;

public class Email implements Serializable {
    /* the ID is generated in base of the ID of the sender and the timestamp of the email to gnerate. */
    private final int ID;
    private final String sender;
    private final List<String> receivers;
    private final String subject;
    private final String text;
    private final LocalDateTime date;

    public Email(String sender, List<String> receivers, String subject, String text, LocalDateTime date, int ID) {
        this.sender = sender;
        this.receivers = receivers;
        this.subject = subject;
        this.text = text;
        this.date = date;
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public String getSender() {
        return sender;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getDate() {
        return date;
    }
}