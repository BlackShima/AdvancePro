module se233.labadvancepro {
    requires javafx.controls;
    requires javafx.fxml;


    opens se233.labadvancepro to javafx.fxml;
    exports se233.labadvancepro;
}