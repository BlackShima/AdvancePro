package se233.chapter2.view;

import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import se233.chapter2.model.Currency;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A container pane that holds and manages multiple CurrencyPane instances.
 */
public class CurrencyParentPane extends FlowPane {

    public CurrencyParentPane(List<Currency> currencyList) throws ExecutionException, InterruptedException {
        this.setPadding(new Insets(0));
        refreshPane(currencyList);
    }

    public void refreshPane(List<Currency> currencyList) throws ExecutionException, InterruptedException {
        this.getChildren().clear();
        for (int i = 0; i < currencyList.size(); i++) {
            CurrencyPane cp = new CurrencyPane(currencyList.get(i));
            // The graph for each CurrencyPane needs to be drawn and added.
            // This logic will be handled in the updated Launcher.
            this.getChildren().add(cp);
        }
    }
}
