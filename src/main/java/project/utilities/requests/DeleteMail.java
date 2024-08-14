package project.utilities.requests;


import project.server.*;
import java.io.*;

public class DeleteMail extends RequestObj {
    public DeleteMail(String sender) {
        super(sender);
    }

    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {
            // @todo the deletion of 1 or more mails, take care of 1-side delete
            controller.writeOnLog("Email deletion request served.");
        }  catch (Exception e) {
            controller.writeOnLog("Email deletion request failed because: " + e.getCause());
            throw e;
        }
    }
    //@todo: fill this class
}
