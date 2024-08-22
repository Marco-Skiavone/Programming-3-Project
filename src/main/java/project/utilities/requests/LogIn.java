package project.utilities.requests;

import project.server.*;
import java.io.*;

public class LogIn extends RequestObj {
    public LogIn(String sender) {
        super(sender);
    }

    /** It checks if the address sent via constructor is a valid email address for the server.<br>
     * Furthermore, it sends back a {@link project.utilities.MailHeader} list.
     * @param output The output stream where the feedback will be written
     * @param model The model containing the server's methods
     * @param controller The calling controller
     */
    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {
            if (model.checkAddress(this.getSender())) {
                output.writeObject(model.readHeaderFile(getSender()));
                controller.writeOnLog("LogIn request served.");
            } else {
                output.writeObject(null);
                controller.writeOnLog("LogIn request denied. (Unknown user)");
            }
        } catch (Exception e) {
            controller.writeOnLog("LogIn request failed because: " + e.getCause());
            throw e;
        } finally {
            output.flush();
        }
    }
}
