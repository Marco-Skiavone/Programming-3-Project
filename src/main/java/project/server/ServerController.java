package project.server;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController {
    @FXML
    private Label welcomeText;
    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    /*
     *  @param poolSize: how many clients it is possible to manage in the same time, it is provided from the server app
     * 
     */

   public ServerController(int port, int poolSize) throws IOException 
   {
     serverSocket = new ServerSocket(port);
     pool = Executors.newFixedThreadPool(poolSize);
   }


    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Server!");
    }

    /*
     *      Method used by the ServerApp to start the server
     *      To create this method those following classes have been imported:
     *          -ServerSocket: to create a Socket that opens connections to the "outside"
     *          -Socket : to create the real connection between a "server-thread" and the client
     *          -ExecutorService: to create threads with the goal to manage different connections for the almost certain possibility
     *                             to communicate with multiple clients at the same time
     *          
     */
    public void serverStart()
    {
        try
        {
            System.out.println("Server is up at port "+ serverSocket.getLocalPort() + "!!\n");


            while (!Thread.currentThread().isInterrupted()) 
            {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ThreadServer(serverSocket.accept()));
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void ServerStop()
    {
        try {
            if(!pool.isShutdown())
                pool.shutdown();
            if(!serverSocket.isClosed())
                serverSocket.close();

        } catch (Exception e) {
            System.out.println("not shut down correctly");
            e.printStackTrace();
        }
    }

    /**
     *  This method retrieves the header-file of a client and return it
     *
     * @param clientName is a string containing the username of the client (mail address). It will be used as fileName
     * @return the file content. @todo: should we return a list of Headers? Or just a string that will be parsed by the client?
     *
     * @todo: should I throw an error on a erroneous clientName?
     */
    public String headersRequest(String clientName) {
        String response = "";
        /*
         retrieves header file in Programming-3-Project/persistence/headers/<clientName>.txt
        */

        /*
         parses the file
        */

        /*
         send the data
        */
        return response;
    }


}
