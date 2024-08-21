package project.utilities.requests;

import project.server.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import project.utilities.*;

public class SendMail extends RequestObj {
    public final Email mail;

    public SendMail(String sender, Email mail) {
        super(sender);
        this.mail = mail;
    }

    /**
     * This method resolves the request object, writing the emailFile and updates receivers' headerFiles using multiple Threads
     * @param output the output stream where the feedback will be written
     * @param model the model containing the server's methods
     * @param controller the calling controller
     * @throws Exception when sender or receivers aren't an eligible account name, or... @todo: verificare quali altre ececzioni possono verificarsi nei thread e nella write EmailFile
     */
    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {

            if (!model.checkAddress(this.getSender())) {
                throw new Exception("Erroneous sender address: " + this.getSender());
            }
            for (String receiver : mail.getReceivers()) {
                if (!model.checkAddress(receiver)) {
                    throw new Exception("Erroneous receiver address: " + receiver);
                }
            }

            //writes the eMail
            model.writeEmailFile(mail);

            //updates receivers headersFile
            try (ExecutorService pool = Executors.newFixedThreadPool(mail.getReceivers().size())) {
                for (String receiver : mail.getReceivers()) {
                    // Apre un thread che accede al file header per ogni receiver e lo aggiorna
                    Runnable r = () -> {
                        try {
                            updateReceiversHeaders(receiver, model);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    };
                    pool.execute(r);
                }
            }

            controller.writeOnLog("SendMail request served.");
        } catch (Exception e) {
            controller.writeOnLog("SendMail request failed because: " + e.getCause());
            throw e;
        }
    }

    /**
     * Support method which is executed by every thread
     * @param receiver a String containing the username corresponding to the headerFile name which will be updated
     * @param model the model containing the server's methods
     * @throws Exception @todo check updateHeaderFile exeptions
     */
    private void updateReceiversHeaders(String receiver, ServerModel model) throws Exception {
        model.updateHeaderFile(receiver, Collections.singletonList(mail.getHeader()), false);
    }
    //@todo: fill this class
}
