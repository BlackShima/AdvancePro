module se233.chapter51 {
    requires javafx.controls;
    requires javafx.fxml;


    opens se233.chapter51 to javafx.fxml;
    exports se233.chapter51;
}