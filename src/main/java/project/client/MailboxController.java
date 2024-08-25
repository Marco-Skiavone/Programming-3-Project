package project.client;

import java.io.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import project.utilities.*;

public class MailboxController {
    private String userAddress;

    @FXML
    private Button deleteBtn;
    @FXML
    private Button errorMsgLabel;
    @FXML
    private ListView<MailHeader> listView = new ListView<>();

    /** List of controllers to let the Mailbox handle all the opened mails. */
    private final List<MailController> mailsList = new ArrayList<>();
    private MailboxModel model;

    /** The Set representing the selection of the listView
     * @todo could require different implementation */
    private final HashSet<MailHeader> selectionSet = new HashSet<>();
    /** Used to loop RefreshRequests towards the server. */
    private ScheduledExecutorService refreshScheduler;
    /** Used to make error messages appear and disappear after a while in the client view. */
    private ScheduledExecutorService errorExecutor;

    /** Function that HAS TO BE CALLED when a new Mailbox View is loaded.
     * @param emailAddress The String representing the email address of the client. Accepted after login session.
     * @param headersList A list representing all the emails headers received until now by the user. */
    public void initModel(String emailAddress, List<MailHeader> headersList) {
        this.userAddress = emailAddress;
        this.model = new MailboxModel(userAddress, headersList);

        deleteBtn.setDisable(true); // When no mail is selected, the button should be disabled

        listView.setItems(model.getHeadersList());
        listView.setCellFactory(lc -> createListCell());    // Here we set "cell" items for the ListView.
        startScheduledRefresh();
    }

    /** Private function called to create a CUSTOM cell for the ListView. */
    private ListCell<MailHeader> createListCell () {
        return new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();
            private final Label sender = new Label();
            private final Label subject = new Label();
            private final Label time = new Label();
            private final HBox hbox = new HBox(20);
            private MailHeader header;
            {
                hbox.setPadding(new Insets(0, 3, 0, 3));
                sender.setPrefWidth(200);
                subject.setPrefWidth(320);
                hbox.getChildren().addAll(checkBox, sender, subject, time);
                // Setting the "DOUBLE-CLICK" to open a mail
                hbox.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 &&
                            !listView.getItems().isEmpty()) {
                        if (header != null)
                            openReadableMailView(header);
                        else
                            System.err.println("'Header' not found when double-clicked on: " + event.getTarget());
                    }
                });
            }

            /** Function used by JavaFX layer to update list items. */
            @Override
            protected void updateItem(MailHeader header, boolean empty) {
                super.updateItem(header, empty);
                if (this.header == null)
                    this.header = header;
                if (empty || header == null) {
                    setGraphic(null);
                } else {
                    // bidirectional checkBox selection
                    checkBox.setSelected(selectionSet.contains(header));
                    checkBox.setOnAction(ev -> {
                        if (checkBox.isSelected()) {
                            selectionSet.add(header);
                            deleteBtn.setDisable(false);
                        } else {
                            selectionSet.remove(header);
                            deleteBtn.setDisable(deleteBtn.isDisabled() && selectionSet.isEmpty());
                        }
                    });
                    sender.setText(header.sender());
                    time.setText((header.timestamp().toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm - d/MM/uuuu"))));
                    subject.setText(header.subject());
                    setGraphic(hbox);
                }
            }
        };
    }

    /** Function called when "Delete" button is pressed. */
    @FXML
    public void deleteMails() {
        // 1. group all the headers selected in a Collection
        // 2. send a DELETE request
        // 3. shows a feedback to the client -> setErrorText(...)
    }

    /** Function called when "New Mail" button is pressed. */
    @FXML
    public void newMailBtnPressed () {
        openNewMailView();
    }

    /** Function called when "Refresh" button is pressed.
     * if new mails arrived, it shows a message on the client for few seconds. */
    @FXML
    public void sendRefreshRequest () {
        if (model.sendRefreshRequest()) {
            setErrorText("New Mails arrived!", "#0000fa");
            System.out.println("New Mails arrived!");
        }
    }

    /** Function called when "Select All" button is pressed. */
    @FXML
    public void selectAllBtn () {
        selectionSet.addAll(model.getHeadersList());
        // @todo listView.setSelectionModel(model.getHeadersList());
    }

    /** Function that opens up a new "mail-view" giving an Email to open up on "read-mode". */
    @FXML
    public void openReadableMailView (MailHeader header) {
        try {
            Email emailToRead = model.retrieveEmail(header);
            if (emailToRead == null) {
                setErrorText("Cannot retrieve the selected email.", null);
                throw new IOException("Cannot retrieve the email from the server.");
            }
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
            mailController.readMail(userAddress, emailToRead);
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
            mailController.startNewMailView(userAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Function called at the initModel. It starts a cycle of requests to the server, asking for Refresh. */
    private void startScheduledRefresh() {
        try {
            if (refreshScheduler == null || refreshScheduler.isShutdown())
                refreshScheduler = Executors.newSingleThreadScheduledExecutor();
            refreshScheduler.scheduleWithFixedDelay(this::sendRefreshRequest, 10, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            setErrorText("Cannot refresh automatically.", null);
        }
    }

    /** Function called to shut down all the pending requests the client could try to send.
     * It has to show an alert that every pending request will be not sent to the server. */
    public void shutdownController() {
        for (MailController mc : mailsList)
            mc.shutdownEditor();

        if (!refreshScheduler.isShutdown())
            refreshScheduler.shutdown();
        if (errorExecutor != null && !errorExecutor.isShutdown())
            errorExecutor.shutdown();
        // @todo if pending requests -> close them and shutdown
    }

    /** Synchronized function used to show error messages to the client view, as feedback
     * of the operations requested by the user.
     * @param text The error to show in the client view (in yellow).
     * @param colorHex It has to be a string formatted as "#xxxxxx", where the "x" are hexadecimal values.
     * If {@code colorHex == null}, then the color picked is the "default" warning yellow. (something went wrong) */
    private synchronized void setErrorText(String text, String colorHex) {
        try {
            errorExecutor = Executors.newSingleThreadScheduledExecutor();
            errorMsgLabel.setText(text);
            colorHex = colorHex != null ? colorHex : "#ffd400";     // "warning-yellow" if colorHex is null
            errorMsgLabel.setTextFill(Paint.valueOf(colorHex));
            errorExecutor.schedule(() -> errorMsgLabel.setText(""), 2, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("Error in \"errorExecutor\" scheduling: " + e.getMessage());
        } finally {
            if (errorExecutor != null && !errorExecutor.isShutdown())
                errorExecutor.shutdown();
        }
    }
}
