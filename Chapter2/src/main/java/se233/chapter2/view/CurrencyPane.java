package se233.chapter2.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import se233.chapter2.controller.AllEventHandlers;
import se233.chapter2.controller.draw.DrawCurrencyInfoTask;
import se233.chapter2.controller.draw.DrawGraphTask;
import se233.chapter2.controller.draw.DrawTopAreaTask;
import se233.chapter2.model.Currency;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CurrencyPane extends BorderPane {

    private Currency currency;
    private final Button watch;
    private final Button unwatch;
    private final Button delete;

    public CurrencyPane(Currency currency) {
        this.watch = new Button("Watch");
        this.unwatch = new Button("Unwatch");
        this.delete = new Button("Delete");

        this.watch.setOnAction(event -> AllEventHandlers.onWatch(currency.getShortCode()));
        this.unwatch.setOnAction(event -> AllEventHandlers.onUnwatch(currency.getShortCode()));
        this.delete.setOnAction(event -> AllEventHandlers.onDelete(currency.getShortCode()));

        this.setPadding(new Insets(0));
        this.setPrefSize(640, 300);
        this.setStyle("-fx-border-color: black;");
        try {
            this.refreshPane(currency);
        } catch (ExecutionException e) {
            System.out.println("Encountered an execution exception.");
        } catch (InterruptedException e) {
            System.out.println("Encountered an interrupted exception.");
        }
    }

    public void refreshPane(Currency currency) throws ExecutionException, InterruptedException {
        this.currency = currency;

        // Manage button visibility
        if (currency.getWatch()) {
            watch.setVisible(false);
            watch.setManaged(false);
            unwatch.setVisible(true);
            unwatch.setManaged(true);
        } else {
            watch.setVisible(true);
            watch.setManaged(true);
            unwatch.setVisible(false);
            unwatch.setManaged(false);
        }

        // Create an ExecutorService to manage concurrent tasks
        ExecutorService executor = Executors.newFixedThreadPool(3);
        try {
            // Create and submit all three drawing tasks
            Future<VBox> graphFuture = executor.submit(new DrawGraphTask(currency));
            Future<VBox> infoFuture = executor.submit(new DrawCurrencyInfoTask(currency));
            Future<HBox> topAreaFuture = executor.submit(new DrawTopAreaTask(watch, unwatch, delete));

            // Retrieve the results from the tasks once they are complete
            VBox currencyGraph = graphFuture.get();
            VBox currencyInfo = infoFuture.get();
            HBox topArea = topAreaFuture.get();

            // Update the UI on the JavaFX Application Thread
            this.setTop(topArea);
            this.setLeft(currencyInfo);
            this.setCenter(currencyGraph);
        } finally {
            // Always shut down the executor
            executor.shutdown();
        }
    }
}
