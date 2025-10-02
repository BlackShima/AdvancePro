module se233.chapter6_ex3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires org.apache.pdfbox.io;
    requires org.apache.logging.log4j;

    opens se233.chapter6_ex3 to javafx.fxml;
    opens se233.chapter6_ex3.controller to javafx.fxml;

    exports se233.chapter6_ex3;
    exports se233.chapter6_ex3.controller;
}