package project.utilities;

import java.io.*;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

public class Email implements Serializable {
    /* the ID is generated in base of the ID of the sender and the timestamp of the email to gnerate. */

    private final MailHeader header;
    private final String text;
    private int referencesCounter = 0;

    public Email(String sender, Collection<String> receivers, String subject, String text, LocalDateTime date) {
        this.header = new MailHeader(sender, receivers, subject, Timestamp.from(date.toInstant(ZoneOffset.UTC)));
        this.text = text;
    }

    public MailHeader getHeader() {return this.header;}

    public String getSender() {
        return header.sender();
    }

    public void setReferencesCounter(int i) {
        referencesCounter = i;
    }

    public void decreaseReferencesCounter() {
        referencesCounter--;
    }

    public int getReferencesCounter() {
        return referencesCounter;
    }

    public Collection<String> getReceivers() {
        return header.receivers();
    }

    public String getSubject() {
        return header.subject();
    }

    public String getText() {
        return text;
    }

    public Timestamp getTimestamp() {
        return header.timestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(header, email.header) && Objects.equals(text, email.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, text);
    }

    @Override
    public String toString() {
        return "Email{" +
                "header=" + header +
                "\n text='" + text + '\'' +
                '}';
    }
}