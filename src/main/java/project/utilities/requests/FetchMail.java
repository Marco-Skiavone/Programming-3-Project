package project.utilities.requests;

import project.server.*;
import project.utilities.*;
import java.io.*;

public class FetchMail extends RequestObj {
    public final MailHeader header;
    public FetchMail(String sender, MailHeader header) {
        super(sender);
        this.header = header;
    }

    /**
     * This method resolves the request object, returning the sending Email object to the client
     * @param output the output stream where the feedback will be written
     * @param model the model containing the server's methods
     * @param controller the calling controller
     * @throws Exception @todo: verificare quali  ececzioni possono verificarsi
     */
    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {
            output.writeObject(model.readEmailFile(header));
            controller.writeOnLog("Email fetch request served.");
        }  catch (Exception e) {
            controller.writeOnLog("Email fetch request failed because: " + e.getCause());
            throw e;
        }
    }
}
