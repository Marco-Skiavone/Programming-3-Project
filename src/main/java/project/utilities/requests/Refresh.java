package project.utilities.requests;

import project.server.*;
import project.utilities.*;
import java.io.*;

public class Refresh extends RequestObj {
    private final MailHeader lastHeader;

    public Refresh(String sender, MailHeader lastHeader) {
        super(sender);
        this.lastHeader = lastHeader;
    }

    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {
            // @todo the refresh request
            // It must handle "null" header case
            controller.writeOnLog("Refresh request served.");
        }  catch (Exception e) {
            controller.writeOnLog("Refresh request failed because: " + e.getCause());
            throw e;
        }
    }
}
