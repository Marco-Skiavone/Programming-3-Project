package project.server;

import project.utilities.*;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.*;
import java.util.concurrent.*;

public class ServerController {
    @FXML
    private ListView<String> logList = new ListView<>();
    /* logMsgList is used to automatically add messages into the log (ListView) */
    private final ObservableList<String> logMsgList = FXCollections.observableArrayList();
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private final ServerModel model;

    /** Function called by FXML to bind the ListView to the logMsgList */
    @FXML
    public void initialize() {
        logList.setItems(logMsgList);
        // Used to remove the automatic cursor selection of the first listView element.
        Platform.runLater(() -> logList.getScene().getRoot().requestFocus());
    }

    public ServerController() {
        try {
            serverSocket = new ServerSocket(Utilities.PORT);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            model = new ServerModel();
        }
    }

    /** Method used by the ServerApp to start the server. The following classes have to be imported:
     *      - ServerSocket: To create a Socket that opens connections to the "outside"
     *      - Socket: To create the real connection between server and clients
     *      - ExecutorService: To create Threads, with the goal of managing simultaneously connections with clients */
    public void serverStart()
    {
        try {
            pool = Executors.newFixedThreadPool(10);
            writeOnLog("Server Started.");
            System.out.println("Server is up at port " + serverSocket.getLocalPort() + ".\n");
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ThreadServer(clientSocket, this, this.model));
            }
        } catch (SocketException ignored) { /* Ignoring on Server closure. */ }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
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
            writeOnLog("Server Stopped.");
            System.out.println("Server stopped correctly.");
        } catch (Exception e) {
            writeOnLog("Error occurred while stopping server.");
            System.out.println("Error occurred while stopping server.");
            System.err.println(e.getMessage());
        }
    }

    /** Function used to write on the server "log-view".
     * @param msg The string message to show in the view.
     * @Note: This function will add the message to {@link ServerController#logMsgList} that will automatically add the messages.
     * Also, we need to ENSURE that any updates to JavaFX UI components, such as updating the log view, are done on the JavaFX Application Thread.
     * Achieved using Platform.runLater(), which schedules the provided code to run on the JavaFX Application Thread. */
    public void writeOnLog(String msg) {
        if (msg != null && !msg.isEmpty())
            Platform.runLater(() -> logMsgList.add(msg));
    }
}
