package se233.chapter2.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import se233.chapter2.Launcher;
import se233.chapter2.controller.AllEventHandlers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TopPane extends FlowPane {

    private Button refresh;
    private Button add;
    private Label update;
    private ChoiceBox<Integer> daySelector;
    private ComboBox<String> baseCurrencySelector;

    public TopPane() {
        this.setPadding(new Insets(10));
        this.setHgap(10);
        this.setPrefSize(640, 20);

        add = new Button("Add");
        refresh = new Button("Refresh");
        daySelector = new ChoiceBox<>(FXCollections.observableArrayList(7, 15, 30));
        daySelector.setValue(30);

        baseCurrencySelector = new ComboBox<>(FXCollections.observableArrayList("THB", "USD", "EUR", "JPY", "GBP", "CNY"));
        baseCurrencySelector.setValue(Launcher.getBaseCurrency());
        baseCurrencySelector.setOnAction(event -> {
            String selectedCurrency = baseCurrencySelector.getValue();
            Launcher.setBaseCurrency(selectedCurrency);
            AllEventHandlers.onRefresh();
        });


        refresh.setOnAction(event -> AllEventHandlers.onRefresh());
        add.setOnAction(event -> AllEventHandlers.onAdd());

        update = new Label();
        refreshPane();

        this.getChildren().addAll(new Label("Base:"), baseCurrencySelector, new Label("Days:"), daySelector, refresh, add, update);
    }

    public void refreshPane() {
        update.setText(String.format("Last update: %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    }

    public ChoiceBox<Integer> getDaySelector() {
        return daySelector;
    }
}
