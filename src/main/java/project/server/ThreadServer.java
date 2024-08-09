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

    /* -- @todo
     * 
     *        this should be the main logic:
     *        -: Logs in a user.
     *        -: Fetches emails for a user.
     *        -: Refreshes emails since a certain ID for a user.
     *        -: Deletes specified emails for a user.
     *        -: Verifies the existence of specified email receivers.
     *        -: Indicates readiness to handle incoming data.
     * 
    */
   public void run() 
   {
    try 
    { 
      ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
      ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

      /*                    debugging purpose                                */
      System.out.println("i am a thread :D\n");

      
      output.flush();
      output.reset();
    } 
    catch (Exception e) 
    {
      e.printStackTrace();
    }
    
     
   }
}
