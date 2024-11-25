module org.textprocessing.textprocessing {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.textprocessing.textprocessing to javafx.fxml;
    exports org.textprocessing.textprocessing;
}