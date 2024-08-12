package project.utilities;


import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

public abstract class RequestObj implements Serializable {
    private final String sender;
    private final Timestamp timestamp;

    public RequestObj(String sender) {
        this.sender = sender;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public String getSender()
    {
        return sender;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
