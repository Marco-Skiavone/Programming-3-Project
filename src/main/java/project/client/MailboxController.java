package project.client;

import java.net.*;
import java.util.*;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import project.utilities.*;
import project.utilities.requests.*;

public class MailboxController {
    /* The listView here will use a complex class to have:
     * - a CheckBox,
     * - a String (to show),
     * - an ID associated (hidden) */


    private final MailboxModel model;

    public MailboxController() {
        this.model = new MailboxModel();
    }
}
