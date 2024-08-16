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
        try {
            boolean result = model.checkAddress(this.getSender());
            if (result) {
                output.writeBoolean(true);
                output.flush();
                output.reset();
                // @todo it sends back a {@link project.utilities.MailHeader} list
                output.writeObject(new ArrayList<>());
                controller.writeOnLog("LogIn request served.");
            } else {
                output.writeBoolean(false);
                controller.writeOnLog("LogIn request denied. (Unknown user)");
            }
        } catch (Exception e) {
            controller.writeOnLog("LogIn request failed because: " + e.getCause());
            throw e;
        }
    }
}
