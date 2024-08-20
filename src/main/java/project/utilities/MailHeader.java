package project.utilities;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

public record MailHeader (
        String sender,
        List<String>receivers,
        String subject,
        Timestamp timestamp
         ) implements Serializable, Comparable<MailHeader> {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MailHeader that)) return false;
        return Objects.equals(sender, that.sender) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, timestamp);
    }

    @Override
    public int compareTo(MailHeader o) {
        return timestamp.compareTo(o.timestamp());
    }
}
