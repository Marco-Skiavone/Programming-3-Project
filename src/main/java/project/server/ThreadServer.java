package project.server;

import java.io.*;
import java.net.*;

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
            if (!(inObject instanceof String))
                throw new IOException("Read of input failed.");
            String[] request = ((String) inObject).split(".-/");
            switch(request[0]) {
                case "CHECK_ADDR":
                    try {
                        output.writeBoolean(model.checkAddress(request[1]));
                        controller.writeOnLog("Check-Address request served.");
                    } catch (Exception e) {
                        controller.writeOnLog("Check-Address request failed because: " + e.getCause());
                        throw e;
                    }
                    break;
                case "DEL":
                    try {
                        // @todo the deletion of 1 or more mails, take care of 1-side delete
                        controller.writeOnLog("Email deletion request served.");
                    }  catch (Exception e) {
                        controller.writeOnLog("Email deletion request failed because: " + e.getCause());
                        throw e;
                    }
                    break;
                case "FETCH":
                    try {
                        // @todo the fetch of an email from a sender to the receivers list
                        controller.writeOnLog("Email fetch request served.");
                    }  catch (Exception e) {
                        controller.writeOnLog("Email fetch request failed because: " + e.getCause());
                        throw e;
                    }
                    break;
                case "REFRESH":
                    try {
                        // @todo the refresh request
                        controller.writeOnLog("Refresh request served.");
                    }  catch (Exception e) {
                        controller.writeOnLog("Refresh request failed because: " + e.getCause());
                        throw e;
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown request: " + request[0]);
            }
            output.flush();
            output.reset();
        } catch (Exception e) {
            controller.writeOnLog("Exception caught: " + e.getMessage());
            System.err.println(e.getMessage());
        }
    }
}
