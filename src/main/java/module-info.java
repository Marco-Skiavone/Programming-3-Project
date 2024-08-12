module project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens project.client to javafx.fxml;
    exports project.client;
    opens project.server to javafx.fxml;
    exports project.server;
}
