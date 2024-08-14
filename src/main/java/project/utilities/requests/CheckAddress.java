package project.utilities.requests;

import project.server.*;
import java.io.*;

public class CheckAddress extends RequestObj {

    public CheckAddress(String address) {
        super(address);
    }

    /** It checks if the address passed via constructor is a valid email address for the server. */
    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {
            output.writeBoolean(model.checkAddress(this.getSender()));
            controller.writeOnLog("Check-Address request served.");
        } catch (Exception e) {
            controller.writeOnLog("Check-Address request failed because: " + e.getCause());
            throw e;
        }
    }
}
