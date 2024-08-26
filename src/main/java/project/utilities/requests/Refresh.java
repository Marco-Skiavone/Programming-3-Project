package project.utilities.requests;

import project.server.*;
import project.utilities.*;
import java.io.*;
import java.util.*;

public class Refresh extends RequestObj {
    private final MailHeader lastHeader;

    public Refresh(String sender, MailHeader lastHeader) {
        super(sender);
        this.lastHeader = lastHeader;
    }

    /** It checks if the address passed via constructor is a valid email address for the server.
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

            List<MailHeader> headers = model.readHeaderFile(getSender());
            if (headers == null)
                throw new ArrayIndexOutOfBoundsException("Invalid list of mail headers");

            if (headers.isEmpty() || (headers.get(0).equals(lastHeader) && headers.size() == 1))
                output.writeObject(new ArrayList<MailHeader>());
            else if (lastHeader == null)
                output.writeObject(headers);    // headers.size() > 1
            else
                 output.writeObject(headers.subList(headers.indexOf(lastHeader) + 1, headers.size()));
            output.flush();
            controller.writeOnLog("Refresh request served.");
        }  catch (Exception e) {
            controller.writeOnLog("Refresh request failed because: " + e.getCause());
            throw e;
        }
    }
}
