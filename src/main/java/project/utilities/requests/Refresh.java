package project.utilities.requests;

import project.server.*;
import java.io.*;

public class Refresh extends RequestObj {
    public Refresh(String sender) {
        super(sender);
    }

    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {
            // @todo the refresh request
            controller.writeOnLog("Refresh request served.");
        }  catch (Exception e) {
            controller.writeOnLog("Refresh request failed because: " + e.getCause());
            throw e;
        }
    }
    //@todo: fill this class
}
