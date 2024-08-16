package project.utilities;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

public record MailHeader(
        String sender,
        List<String>receivers,
        String subject,
        Timestamp timestamp
         ) implements Serializable {

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
}
