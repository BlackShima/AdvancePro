package se233.chapter2.controller;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import se233.chapter2.model.CurrencyEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class FetchData {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static List<CurrencyEntity> fetchRange(String base, String symbol, int N) {
        String dateEnd = LocalDate.now().format(formatter);
        String dateStart = LocalDate.now().minusDays(N).format(formatter);
        String urlStr = String.format("https://cmu.to/SE233currencyapi?base=%s&symbol=%s&start_date=%s&end_date=%s", base, symbol, dateStart, dateEnd);
        List<CurrencyEntity> histList = new ArrayList<>();
        try {
            String retrievedJson = IOUtils.toString(new URL(urlStr), Charset.defaultCharset());
            JSONObject jsonOBJ = new JSONObject(retrievedJson);

            if (jsonOBJ.has("error")) {
                System.err.printf("API Error for symbol %s: %s\n", symbol, jsonOBJ.getString("error"));
                return histList;
            }

            JSONObject rates = jsonOBJ.getJSONObject("rates");
            Iterator keysToCopyIterator = rates.keys();

            while (keysToCopyIterator.hasNext()) {
                String key = (String) keysToCopyIterator.next();
                Double rate = Double.parseDouble(rates.get(key).toString());
                histList.add(new CurrencyEntity(rate, key));
            }

            histList.sort(Comparator.comparing(CurrencyEntity::getTimestamp));

        } catch (MalformedURLException e) {
            System.err.println("Encountered a Malformed URL exception");
        } catch (IOException e) {
            System.err.println("Encountered an IO exception");
        } catch (JSONException e) {
            System.err.printf("Failed to parse JSON response for symbol %s: %s\n", symbol, e.getMessage());
        }

        return histList;
    }
}