package project.server;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.*;
import java.util.concurrent.*;

public class ServerController {
    @FXML
    private ListView<String> logList;
    // logMsgList is used to automatically add messages into the log (ListView)
    private ObservableList<String> logMsgList = FXCollections.observableArrayList();
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private final ServerModel model;

    public ServerController() {
        try {
            serverSocket = new ServerSocket(ServerModel.getPORT());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            model = new ServerModel();
        }
    }

    /*
     *      Method used by the ServerApp to start the server
     *      To create this method those following classes have been imported:
     *          -ServerSocket: to create a Socket that opens connections to the "outside"
     *          -Socket : to create the real connection between a "server-thread" and the client
     *          -ExecutorService: to create threads with the goal to manage different connections for the almost certain possibility
     *                             to communicate with multiple clients at the same time
     */
    public void serverStart()
    {
        try {

            pool = Executors.newFixedThreadPool(10);
            writeOnLog("Server Started");
            System.out.println("Server is up at port " + serverSocket.getLocalPort() + ".\n");
        } catch(Exception e) {
            e.printStackTrace();
        }

        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                try {
                    Socket clientSocket = serverSocket.accept();
                    pool.execute(new ThreadServer(clientSocket));
                } catch (Exception e) {
                    serverStop(); // @todo correctly shutdown the server!
                    break;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Function called by the {@link ServerApplication} when closing event is fired for the server.
     * @note It ENSURES all server services being properly closed before exiting the program.
     * */
    public void serverStop()
    {
        try {
            if(!pool.isShutdown())
                pool.shutdown();
            if(!serverSocket.isClosed())
                serverSocket.close();
            writeOnLog("Server Stopped");
        } catch (Exception e) {
            System.out.println("Error during server shut down.");
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

    /** Function used to write on the server "log-view".
     * @param msg The string message to show in the view.
     * @note This function will add the message to {@link ServerController#logMsgList} that will automatically add the messages. */
    public void writeOnLog(String msg) {
        if (msg != null && !msg.isEmpty())
            logMsgList.add(msg);
    }
}
