package project.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/* class that permits the running of the server using multi-thread.*/

public class ThreadServer implements Runnable
{
    private final Socket socket;
    ThreadServer(Socket socket)
    { 
      this.socket = socket; 
    }

    /** @todo
     * this should be the main logic:
     * - CHECK_ADDR: Verifies the existence of specified email receivers.
     * - DEL: Deletes specified emails for a user.
     * - FETCH: Fetches emails from server to an user.
     * - REF: Refreshes emails since a certain ID for an user.
     * - ___: Indicates readiness to handle incoming data. (?)
     *
     * @example "CHECK_ADDR.-/my-email@dom.it"
     */
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            String[] request = input.readUTF().split(".-/");
            String[] accountList;   // @todo gain the information about the mailing list
            switch(request[0]) {
                case "CHECK_ADDR":
                    output.writeBoolean(true); // @todo end up this part
                    break;
                case "DEL":
                    // @todo
                    break;
                case "FETCH":
                    // @todo
                    break;
                case "REF":
                    // @todo
                    break;
                default:
            }
            output.flush();
            output.reset();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // log error in the log-view.
        }
    }
}
