package se233.chapter2.controller;

import javafx.scene.control.Alert;
import se233.chapter2.Launcher;
import se233.chapter2.model.Currency;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * A task that checks the list of currencies and creates an alert
 * if any have fallen below their watched rate.
 */
public class WatchTask implements Callable<Void> {
    @Override
    public Void call() throws Exception {

        List<Currency> allCurrency = Launcher.getCurrencyList();
        String found = "";

        // Check each currency to see if its rate is below the watch rate.
        for (int i = 0; i < allCurrency.size(); i++) {
            if (allCurrency.get(i).getWatchRate() != 0 && allCurrency.get(i).getWatchRate() > allCurrency.get(i).getCurrent().getRate()) {
                if (found.equals("")) {
                    found = allCurrency.get(i).getShortCode();
                } else {
                    found = found + " and " + allCurrency.get(i).getShortCode();
                }
            }
        }

        // If any currencies were found, show an alert.
        if (!found.equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            if (found.length() > 3) {
                alert.setContentText(String.format("%s have become lower than the watch rate!", found));
            } else {
                alert.setContentText(String.format("%s has become lower than the watch rates!", found));
            }
            alert.showAndWait();
        }
        return null;
    }
}
