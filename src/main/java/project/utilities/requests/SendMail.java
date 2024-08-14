package project.utilities.requests;

import project.server.*;
import java.io.*;
import project.utilities.*;

public class SendMail extends RequestObj {
    public final Email mail;

    public SendMail(String sender, Email mail) {
        super(sender);
        this.mail = mail;
    }

    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {
            // @todo the SendMail request
            model.checkAddress(this.getSender());
            for (String receiver : mail.getReceivers()) {
                if (!model.checkAddress(receiver)) {
                    throw new Exception("Erroneous receiver address: " + receiver);
                }
            }

            // manipolazione degli UserHeaders file e scrittura in ".../mails" delle mail

            controller.writeOnLog("SendMail request served.");
        } catch (Exception e) {
            controller.writeOnLog("SendMail request failed because: " + e.getCause());
            throw e;
        }
    }
    //@todo: fill this class
}
