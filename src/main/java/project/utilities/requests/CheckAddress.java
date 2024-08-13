package project.utilities.requests;

import project.server.ServerController;
import project.server.ServerModel;

import java.io.ObjectOutputStream;

public class CheckAddress extends RequestObj {

    public CheckAddress(String sender) {
        super(sender);
    }

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
