package se233.chapter2.controller;

import se233.chapter2.Launcher;
import se233.chapter2.model.Currency;
import se233.chapter2.model.CurrencyEntity;

import java.util.ArrayList;
import java.util.List;

public class Initialize {

    public static List<Currency> initializeApp(int days) {
        // Initial target currency
        Currency c = new Currency("USD");
        List<CurrencyEntity> cList = FetchData.fetchRange(Launcher.getBaseCurrency(), c.getShortCode(), days);

        if (cList != null && !cList.isEmpty()) {
            c.setHistorical(cList);
            c.setCurrent(cList.get(cList.size() - 1));
        }

        List<Currency> currencyList = new ArrayList<>();
        currencyList.add(c);
        return currencyList;
    }
}
