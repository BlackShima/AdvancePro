module se233.chapter6_ex5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;


    opens se233.chapter6_ex5 to javafx.fxml;
    exports se233.chapter6_ex5;
}