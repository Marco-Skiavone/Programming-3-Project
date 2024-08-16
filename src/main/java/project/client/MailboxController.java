package project.client;

import java.io.*;
import java.util.*;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import project.utilities.*;
import project.utilities.requests.*;

public class MailboxController {
    private String userAddress;

    @FXML
    private Button deleteBtn;
    @FXML
    private Button newBtn;
    @FXML
    private Button refreshBtn;
    @FXML
    private Button selectAllBtn;
    @FXML
    private ListView<MailHeader> listView = new ListView<>();

    /** List of MailHeaders shown to the user via ListView. */
    private List<MailHeader> headersList;
    /** List of controllers to let the Mailbox handle all the opened mails. */
    private final List<MailController> mailsList = new ArrayList<>();
    private MailboxModel model;
    // (Maybe) We will need this to handle loop of requests, in case the server is down:
    // private ScheduledExecutorService refreshScheduler;

    /** Function that HAS TO BE CALLED when a new Mailbox View is loaded.
     * @param emailAddress The String representing the email address of the client. Accepted after login session.
     * @param headersList A list representing all the emails headers received until now by the user. */
    public void initModel(String emailAddress, List<MailHeader> headersList) {
        this.userAddress = emailAddress;
        this.model = new MailboxModel(userAddress, headersList);

        listView.setItems(model.getHeadersList());
        /* Here we set "cell" items for the ListView. */
        listView.setCellFactory(line -> createListCell());
        // @todo it could be necessary to start a delayed refresh session for the user!
    }

    /** Private function called to crete a CUSTOM cell for the ListView. */
    private ListCell<MailHeader> createListCell () {
        return new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();
            private final Label sender = new Label();
            private final Label subject = new Label();
            private final Label time = new Label();
            private final HBox hbox = new HBox(20);
            {
                sender.setPrefWidth(200);
                subject.setPrefWidth(200);
                hbox.getChildren().addAll(checkBox, sender, subject, time);
                hbox.setPadding(new Insets(0, 3, 0, 3));
            }

            /** Function used by JavaFX layer to update list items. */
            @Override
            protected void updateItem(MailHeader header, boolean empty) {
                super.updateItem(header, empty);
                if (empty || header == null) {
                    setGraphic(null);
                } else {
                    sender.setText(header.sender());
                    time.setText((header.timestamp().toString().substring(0,16)));
                    subject.setText(header.subject());
                    setGraphic(hbox);
                }
            }
        };
    }

    /** Function called when "Delete" button is pressed. */
    @FXML
    public void deleteMails() {
        System.out.println("deleteBtn clicked");      // DEBUG
    }

    /** Function called when "New Mail" button is pressed. */
    @FXML
    public void newMailBtnPressed () {
        System.out.println("newBtn clicked");     // DEBUG
        openNewMailView();
    }

    /** Function called when "Refresh" button is pressed. */
    @FXML
    public void sendRefreshRequest () {
        System.out.println("refreshBtn clicked");     // DEBUG
        model.sendRefreshRequest();
    }

    /** Function called when "Select All" button is pressed. */
    @FXML
    public void selectAllBtn () {
        System.out.println("selectAllBtn clicked");   // DEBUG
    }

    /** Function that opens up a new "mail-view" giving an Email to open up on "read-mode". */
    @FXML
    public void openReadableMailView (MailHeader header) {
        System.out.println("Readable Mail clicked: " + header);     // DEBUG
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MailController.class.getResource("mail-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setScene(scene);
            stage.setTitle("Mail - View: " + header.subject());
            stage.setResizable(false);

            MailController mailController = fxmlLoader.getController();
            this.mailsList.add(mailController);
            stage.setOnCloseRequest(event -> {
                mailsList.remove(mailController);
            });
            stage.show();
            // mailController.initModel(userAddress, retrieveEmail(header)); @todo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Function that opens up a new "mail-view" with empty fields (except for the sender). */
    public void openNewMailView () {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MailController.class.getResource("mail-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setScene(scene);
            stage.setTitle("Mail - New Mail");
            stage.setResizable(false);

            MailController mailController = fxmlLoader.getController();
            this.mailsList.add(mailController);
            stage.setOnCloseRequest(event -> {
                mailsList.remove(mailController);
            });
            stage.show();
            // mailController.initModel(userAddress, null); @todo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Function called to shut down all the pending requests the client could try to send.
     * It has to show an alert that every pending request will be not sent to the server. */
    public void shutdownController() {
        for (MailController mc : mailsList) {
            mc.closeWindow();
        }
        // @todo if pending requests -> close them and shutdown
    }
}
