package se233.chapter2.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import se233.chapter2.Launcher;
import se233.chapter2.model.Currency;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * A background task that periodically refreshes data and checks for watch rate alerts.
 */
public class RefreshTask extends Task<Void> {

    @Override
    protected Void call() throws InterruptedException {
        // This is an infinite loop to keep the task running.
        for (;;) {
            try {
                // Pause the task for 60 seconds.
                Thread.sleep((long) (60 * 1e3));
            } catch (InterruptedException e) {
                System.out.println("Encountered an interrupted exception");
            }

            // Perform the UI update on the JavaFX Application Thread.
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Manually trigger the UI and data refresh.
                        Launcher.refreshPane();

                        // Get the updated list of currencies.

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            FutureTask futureTask = new FutureTask(new WatchTask());
            Platform.runLater(futureTask);
            try{
                futureTask.get();
                }catch (InterruptedException e){
                System.out.println("Encounteredaninterrupted exception");
                }catch (ExecutionException e){
                System.out.println("Encounteredanexecution exception");
                }
        }
    }
}
