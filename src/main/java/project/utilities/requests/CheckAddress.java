package project.utilities.requests;

import project.server.*;
import java.io.*;

/** Request used to ask the server whether an email address is valid or not. */
public class CheckAddress extends RequestObj {
    public CheckAddress(String address) {
        super(address);
        System.out.println(address);
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
            output.writeBoolean(model.checkAddress(this.getSender()));
            output.flush();
            controller.writeOnLog("Address check request served.");
        } catch (Exception e) {
            controller.writeOnLog("Address check request failed because: " + e.getCause());
            throw e;
        }
    }
}
