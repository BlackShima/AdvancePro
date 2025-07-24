package se233.chapter2.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import se233.chapter2.Launcher;
import se233.chapter2.model.Currency;
import se233.chapter2.model.CurrencyEntity;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class AllEventHandlers {

    public static void onRefresh() {
        try {
            int days = Launcher.getTopPane().getDaySelector().getValue();
            String base = Launcher.getBaseCurrency();
            List<Currency> currencyList = Launcher.getCurrencyList();
            for (Currency currency : currencyList) {
                // Do not fetch data for the currency if it's the same as the base
                if (currency.getShortCode().equals(base)) continue;

                List<CurrencyEntity> cList = FetchData.fetchRange(base, currency.getShortCode(), days);
                currency.setHistorical(cList);
                if (cList != null && !cList.isEmpty()) {
                    currency.setCurrent(cList.get(cList.size() - 1));
                }
            }
            Launcher.refreshPane();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onAdd() {
        while (true) {
            try {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add Currency");
                dialog.setContentText("Currency code:");
                dialog.setHeaderText(null);
                dialog.setGraphic(null);
                Optional<String> code = dialog.showAndWait();

                if (code.isPresent() && !code.get().isEmpty()) {
                    String currencyCode = code.get().toUpperCase();
                    String base = Launcher.getBaseCurrency();
                    int days = Launcher.getTopPane().getDaySelector().getValue();

                    // Prevent adding a currency that is the same as the base
                    if (currencyCode.equals(base)) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setHeaderText("Cannot Add Base Currency");
                        alert.setContentText(String.format("You cannot add '%s' as it is the current base currency.", currencyCode));
                        alert.showAndWait();
                        continue; // Continue to re-prompt
                    }

                    List<CurrencyEntity> cList = FetchData.fetchRange(base, currencyCode, days);

                    if (cList != null && !cList.isEmpty()) {
                        List<Currency> currencyList = Launcher.getCurrencyList();
                        Currency c = new Currency(currencyCode);
                        c.setHistorical(cList);
                        c.setCurrent(cList.get(cList.size() - 1));
                        currencyList.add(c);
                        Launcher.setCurrencyList(currencyList);
                        Launcher.refreshPane();
                        break;
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Invalid Currency Code");
                        alert.setContentText(String.format("Could not retrieve data for '%s'.\nPlease enter a valid 3-letter currency code.", currencyCode));
                        alert.showAndWait();
                    }
                } else {
                    break;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void onDelete(String code) {
        try {
            List<Currency> currencyList = Launcher.getCurrencyList();
            currencyList.removeIf(currency -> currency.getShortCode().equals(code));
            Launcher.setCurrencyList(currencyList);
            Launcher.refreshPane();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void onWatch(String code) {
        try {
            List<Currency> currencyList = Launcher.getCurrencyList();
            currencyList.stream()
                    .filter(c -> c.getShortCode().equals(code))
                    .findFirst()
                    .ifPresent(currency -> {
                        TextInputDialog dialog = new TextInputDialog();
                        dialog.setTitle("Add Watch");
                        dialog.setContentText("Rate:");
                        dialog.setHeaderText(null);
                        dialog.setGraphic(null);
                        Optional<String> retrievedRate = dialog.showAndWait();
                        if (retrievedRate.isPresent() && !retrievedRate.get().isEmpty()) {
                            try {
                                double rate = Double.parseDouble(retrievedRate.get());
                                currency.setWatch(true);
                                currency.setWatchRate(rate);
                                Launcher.refreshPane();
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid number format for watch rate.");
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onUnwatch(String code) {
        try {
            List<Currency> currencyList = Launcher.getCurrencyList();
            currencyList.stream()
                    .filter(c -> c.getShortCode().equals(code))
                    .findFirst()
                    .ifPresent(currency -> {
                        currency.setWatch(false);
                        currency.setWatchRate(0.0);
                    });
            Launcher.refreshPane();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}