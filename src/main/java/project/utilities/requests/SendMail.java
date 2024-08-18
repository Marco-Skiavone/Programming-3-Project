package project.utilities.requests;

import project.server.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            if (!model.checkAddress(this.getSender())) {
                throw new Exception("Erroneous sender address: " + this.getSender());
            }
            for (String receiver : mail.getReceivers()) {
                if (!model.checkAddress(receiver)) {
                    throw new Exception("Erroneous receiver address: " + receiver);
                }
            }

            ExecutorService pool = Executors.newFixedThreadPool(mail.getReceivers().size());
            for (String receiver : mail.getReceivers()) {
                // Apre un thread che accede al file header per ogni receiver e lo aggiorna
                Runnable r = () -> {
                    try {
                        updateReceiversHeaders(receiver);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
                pool.execute(r);

            }
            // manipolazione degli UserHeaders file e scrittura in ".../mails" della mail

            controller.writeOnLog("SendMail request served.");
        } catch (Exception e) {
            controller.writeOnLog("SendMail request failed because: " + e.getCause());
            throw e;
        }
    }

    private void updateReceiversHeaders(String receiver) throws Exception {
        ArrayList<MailHeader> headers = ServerModel.getMailHeaders(receiver);
        headers.add(mail.getHeader());

    }
    //@todo: fill this class
}
