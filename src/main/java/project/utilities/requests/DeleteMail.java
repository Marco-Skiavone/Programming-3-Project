package project.utilities.requests;

import project.server.*;
import project.utilities.*;
import java.io.*;
import java.util.*;

/** Request used to notify the server about a client wanting to delete some emails. */
public class DeleteMail extends RequestObj {
    private final Collection<MailHeader> headers;

    public DeleteMail(String sender, Collection<MailHeader> headers) {
        super(sender);
        this.headers = headers;
    }

    /**
     * This method resolves the request object, updating the emailFile and removing headers from the sender's headerFile
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

            // It updates receivers headersFile
            model.updateHeaderFile(getSender(), headers, true);
            output.writeBoolean(true); // feedback for the client.
            // If something went wrong, the client will send the request again.
            controller.writeOnLog("Deletion request served.");
        }  catch (Exception e) {
            controller.writeOnLog("Deletion request failed because: " + e.getCause());
            throw e;
        } finally {
            output.flush();
        }
    }
}
