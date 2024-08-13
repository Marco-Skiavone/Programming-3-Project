package project.utilities.requests;


import project.server.ServerController;
import project.server.ServerModel;

import java.io.ObjectOutputStream;
import java.io.Serializable;
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

    public abstract void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception;
}
