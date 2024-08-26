package project.server;

import java.io.*;
import java.net.*;
import project.utilities.requests.*;

/** Class that permits the running of the server using multi-thread.*/

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

    /** This should be the main logic:
     * - LOGIN: Verifies the existence of specified email and logs in the user.
     * - CHECK_ADDR: Verifies the existence of specified email receivers.
     * - DELETE: Deletes specified emails for a user.
     * - FETCH: Fetches emails from server to a user.
     * - REFRESH: Refreshes emails since a certain ID for a user.
     * - SEND_MAIL: Refreshes emails since a certain ID for a user.
     * Every request that requires no response from the server HAS to return a "true" boolean message,
     * if everything is gone right. */
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            Object inObject = input.readObject();
            if (!(inObject instanceof RequestObj request))
                throw new IOException("Read of input failed.");

            request.resolve(output, model, controller);
            // Correct output requires a final "true" + flush() for DELETE and SEND_MAIL
        } catch (Exception e) {
            controller.writeOnLog("Exception caught: " + e.getMessage());
            System.err.println(e.getMessage());
        }
    }
}
