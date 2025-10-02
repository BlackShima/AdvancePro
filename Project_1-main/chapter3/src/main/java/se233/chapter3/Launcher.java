package se233.chapter3;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

//Imports are omitted
public class Launcher extends Application {
    public static HostServices getHs() {
        return hs;
    }

    public static void setHs(HostServices hs) {
        Launcher.hs = hs;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        Launcher.primaryStage = primaryStage;
    }

    public static Stage primaryStage;
    public static HostServices hs;
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        hs = getHostServices();
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Indexer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static void main(String[] args) { launch(args); }
}
