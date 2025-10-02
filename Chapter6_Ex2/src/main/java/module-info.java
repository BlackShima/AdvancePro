module se233.chapter6_ex2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.apache.commons.io;
    requires org.json;


    opens se233.chapter6_ex2 to javafx.fxml;
    exports se233.chapter6_ex2;
}