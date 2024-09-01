package project.utilities.requests;

import project.server.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import project.utilities.*;

/** Request used to notify the server about a client sending an email. */
public class SendMail extends RequestObj {
    public final Email mail;

    public SendMail(String sender, Email mail) {
        super(sender);
        this.mail = mail;
    }

    /**
     * This method resolves the request object, writing the emailFile and updates receivers' headerFiles using multiple Threads
     * @param output The output stream where the feedback will be written
     * @param model The model containing the server's methods
     * @param controller The calling controller
     * @throws Exception when sender or receivers aren't an eligible account name, etc.
     */
    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {
            if (!model.checkAddress(this.getSender()))
                throw new UnknownAddressException("Erroneous sender address: " + this.getSender());

            for (String receiver : mail.getReceivers())
                if (!model.checkAddress(receiver))
                    throw new UnknownAddressException("Erroneous receiver address: " + receiver);

            // Writes the eMail in the file
            model.writeEmailFile(mail);

            // Updates receivers headersFile
            try (ExecutorService pool = Executors.newFixedThreadPool(Math.min(mail.getReceivers().size(), 5))) {
                for (String receiver : mail.getReceivers()) {
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
            output.writeBoolean(true); // feedback for the client.
            // If something went wrong, the client will send the request again.
            controller.writeOnLog("Send request served.");
        } catch (Exception e) {
            controller.writeOnLog("Send request failed because: " + e.getCause());
            throw e;
        } finally {
            output.flush();
        }
    }

    /**
     * Support method which is executed by every thread
     * @param receiver a String containing the username corresponding to the headerFile name which will be updated
     * @param model the model containing the server's methods
     * @throws Exception Check out {@link ServerModel#updateHeaderFile} Exceptions
     */
    private void updateReceiversHeaders(String receiver, ServerModel model) throws Exception {
        model.updateHeaderFile(receiver, Collections.singletonList(mail.getHeader()), false);
    }
}
