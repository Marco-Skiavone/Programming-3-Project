package project.utilities.requests;

import project.server.*;
import project.utilities.*;
import java.io.*;

/** Request used to require to the server an email to let the client read it. */
public class FetchMail extends RequestObj {
    public final MailHeader header;
    public FetchMail(String sender, MailHeader header) {
        super(sender);
        this.header = header;
    }

    /**
     * This method resolves the request object, returning the sending Email object to the client
     * @param output The output stream where the feedback will be written
     * @param model The model containing the server's methods
     * @param controller The calling controller
     * @throws Exception If something went wrong in the headers' file reading.
     */
    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {
            if (!model.checkAddress(this.getSender()))
                throw new UnknownAddressException("Erroneous sender address: " + this.getSender());

            output.writeObject(model.readEmailFile(header));
            output.flush();
            controller.writeOnLog("Fetch request served.");
        }  catch (Exception e) {
            output.writeObject(null);
            output.flush();
            controller.writeOnLog("Fetch request failed because: " + e.getCause());
            throw e;
        }
    }
}
