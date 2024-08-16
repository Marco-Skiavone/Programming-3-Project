package project.utilities.requests;

import project.server.*;
import project.utilities.*;
import java.io.*;

public class FetchMail extends RequestObj {
    private final MailHeader header;

    public FetchMail(String sender, MailHeader header) {
        super(sender);
        this.header = header;
    }

    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {
            // @todo the fetch of an email from a sender and returns it back.
            controller.writeOnLog("Email fetch request served.");
        }  catch (Exception e) {
            controller.writeOnLog("Email fetch request failed because: " + e.getCause());
            throw e;
        }
    }
}
