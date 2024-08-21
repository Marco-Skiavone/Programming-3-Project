package project.utilities.requests;

import project.server.*;
import java.io.*;
import java.util.*;

public class LogIn extends RequestObj {
    public LogIn(String sender) {
        super(sender);
    }

    /** It checks if the address sent via constructor is a valid email address for the server.<br>
     * Furthermore, it sends back a {@link project.utilities.MailHeader} list. */
    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        boolean result = false;
        try {
            result = model.checkAddress(this.getSender());
            if (result) {
                output.writeObject(model.readHeaderFile(getSender()));
                output.flush();
                output.reset();
                controller.writeOnLog("LogIn request served.");
            }
        } catch (Exception e) {
            controller.writeOnLog("LogIn request failed because: " + e.getCause());
            throw e;
        } finally {
            if (!result) {
                output.writeBoolean(false);
                controller.writeOnLog("LogIn request denied. (Unknown user)");
            }
        }
    }
}
