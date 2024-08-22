package project.utilities.requests;

import project.server.*;
import project.utilities.*;
import java.io.*;

public class CheckAddress extends RequestObj {

    public CheckAddress(String address) {
        super(address);
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

            output.writeBoolean(model.checkAddress(this.getSender()));
            output.flush();
            controller.writeOnLog("Check-Address request served.");
        } catch (Exception e) {
            controller.writeOnLog("Check-Address request failed because: " + e.getCause());
            throw e;
        }
    }
}
