package se233.chapter3.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import se233.chapter3.model.FileFreq;
import se233.chapter3.model.PdfDocument;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class WordCountMapTask implements Callable<Map<String,FileFreq>> {
    private Map<String, FileFreq> wordCount;
    private PdfDocument doc;
    public WordCountMapTask(PdfDocument doc) throws IOException {
        this.doc = doc;
    }
    @Override
    public Map<String, FileFreq> call() throws Exception {
        PDFTextStripper reader = new PDFTextStripper();
        String s = reader.getText(doc.getDocument());
        Pattern pattern = Pattern.compile("\\s+"); // Improved pattern for splitting words
        this.wordCount = pattern.splitAsStream(s)
                .map(word -> word.replaceAll("[^a-zA-Z]", "").toLowerCase().trim())
                .filter(word -> word.length() > 3)
                .map(word -> new AbstractMap.SimpleEntry<>(word, 1))
                .collect(toMap(e-> e.getKey(), e-> e.getValue(), (v1, v2) -> v1 + v2))
                .entrySet()
                .stream()
                .filter(e -> e.getValue() > 1)
                .collect(Collectors.toMap(e-> e.getKey(), e -> new FileFreq(doc.getName(), doc.getFilePath(), e.getValue())));
        return wordCount;
    }
}
