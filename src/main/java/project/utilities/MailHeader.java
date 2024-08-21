package project.utilities;

import java.io.Serializable;
import java.sql.*;
import java.time.ZoneOffset;
import java.util.*;

public record MailHeader (
        String sender,
        Collection<String> receivers,
        String subject,
        Timestamp timestamp
         ) implements Serializable, Comparable<MailHeader> {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MailHeader that)) return false;
        return Objects.equals(sender, that.sender) && Objects.equals(subject, that.subject) && Objects.equals(timestamp, that.timestamp) && Objects.equals(receivers, that.receivers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receivers, subject, timestamp);
    }

    @Override
    public int compareTo(MailHeader o) {
        return timestamp.compareTo(o.timestamp());
    }
}
