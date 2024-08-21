package project.utilities.requests;


import project.server.*;
import project.utilities.MailHeader;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteMail extends RequestObj {
    private final Collection<MailHeader> headers;

    public DeleteMail(String sender, Collection<MailHeader> headers) {
        super(sender);
        this.headers = headers;
    }

    /**
     * This method resolves the request object, updating the emailFile and removing headers from the sender's headerFile
     * @param output the output stream where the feedback will be written
     * @param model the model containing the server's methods
     * @param controller the calling controller
     * @throws Exception @todo: verificare quali  ececzioni possono verificarsi
     */
    @Override
    public void resolve(ObjectOutputStream output, ServerModel model, ServerController controller) throws Exception {
        try {
            // @todo the deletion of 1 or more mails, take care of 1-side delete
            //updates receivers headersFile
            model.updateHeaderFile(getSender(), headers, true);
            output.writeBoolean(true); //@todo: impostare il feedback desiderato
            controller.writeOnLog("Email deletion request served.");
        }  catch (Exception e) {
            controller.writeOnLog("Email deletion request failed because: " + e.getCause());
            throw e;
        }
    }

    //@todo: fill this class
}
