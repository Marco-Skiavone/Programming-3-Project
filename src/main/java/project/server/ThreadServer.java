package project.server;

import java.io.*;
import java.net.*;
import project.utilities.*;

/* class that permits the running of the server using multi-thread.*/

public class ThreadServer implements Runnable
{
    private final Socket socket;
    private final ServerController controller;
    private final ServerModel model;

    public ThreadServer(Socket socket, ServerController controller, ServerModel model) {
        this.socket = socket;
        this.controller = controller;
        this.model = model;
    }

    /** This should be the main logic: (Still WIP)
     * - CHECK_ADDR: Verifies the existence of specified email receivers.
     * - DEL: Deletes specified emails for a user.
     * - FETCH: Fetches emails from server to a user.
     * - REFRESH: Refreshes emails since a certain ID for a user.
     *
     * @example "CHECK_ADDR.-/my-email@dom.it"
     */
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            Object inObject = input.readObject();
            if (!(inObject instanceof RequestObj))
                throw new IOException("Read of input failed.");
            RequestObj request = ((RequestObj) inObject);

            // resolve(request); @todo: is this a good idea?

            if (request.getClass().equals(CheckAddress.class)) {
                try {
                    output.writeBoolean(model.checkAddress(request.getSender()));
                    controller.writeOnLog("Check-Address request served." + request.getClass());
                } catch (Exception e) {
                    controller.writeOnLog("Check-Address request failed because: " + e.getCause());
                    throw e;
                }
            } else if (request.getClass().equals(DeleteMail.class)) {
                try {
                    // @todo the deletion of 1 or more mails, take care of 1-side delete
                    controller.writeOnLog("Email deletion request served.");
                }  catch (Exception e) {
                    controller.writeOnLog("Email deletion request failed because: " + e.getCause());
                    throw e;
                }
            } else if (request.getClass().equals(FetchMail.class)) {
                try {
                    // @todo the fetch of an email from a sender to the receivers list
                    controller.writeOnLog("Email fetch request served.");
                }  catch (Exception e) {
                    controller.writeOnLog("Email fetch request failed because: " + e.getCause());
                    throw e;
                }
            } else if (request.getClass().equals(Refresh.class)) {
                try {
                    // @todo the refresh request
                    controller.writeOnLog("Refresh request served.");
                }  catch (Exception e) {
                    controller.writeOnLog("Refresh request failed because: " + e.getCause());
                    throw e;
                }
            } else {
                throw new RuntimeException("Unknown request: " + request.getClass());
            }

            output.flush();
            output.reset();
        } catch (Exception e) {
            controller.writeOnLog("Exception caught: " + e.getMessage());
            System.err.println(e.getMessage());
        }
    }

    /** @todo: is this a good Idea?
     * This function checks the request subclass and
      */
    private void resolve(RequestObj request, ObjectOutputStream output) throws IOException {

    }
}
