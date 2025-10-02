module Chapter6_Ex1 { // ชื่อ module ควรตรงกับ artifactId
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    exports se233.chapter6_ex1;
    exports se233.chapter6_ex1.controller;
    exports se233.chapter6_ex1.model;
    exports se233.chapter6_ex1.model.character;
    exports se233.chapter6_ex1.model.item;
    exports se233.chapter6_ex1.view;
}