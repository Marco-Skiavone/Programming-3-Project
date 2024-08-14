package project.utilities.requests;

import project.server.*;
import java.io.*;

public class FetchMail extends RequestObj {
    public FetchMail(String sender) {
        super(sender);
    }

    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {
            // @todo the fetch of an email from a sender to the receivers list
            controller.writeOnLog("Email fetch request served.");
        }  catch (Exception e) {
            controller.writeOnLog("Email fetch request failed because: " + e.getCause());
            throw e;
        }
    }
    //@todo: fill this class
}
