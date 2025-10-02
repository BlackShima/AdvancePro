package se233.chapter6_ex3.model;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Set;

public class PdfDocument {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setDocument(PDDocument document) {
        this.document = document;
    }

    public PDDocument getDocument(){
        return document;
    }

    public LinkedHashMap<String, Set<FileFreq>> getUniqueSets() {
        return uniqueSets;
    }

    public void setUniqueSets(LinkedHashMap<String, Set<FileFreq>> uniqueSets) {
        this.uniqueSets = uniqueSets;
    }

    private String filePath;
    private PDDocument document;
    private LinkedHashMap<String, Set<FileFreq>> uniqueSets; // Corrected generic type

    public PdfDocument(String filePath) throws IOException {
        this.name = Paths.get(filePath).getFileName().toString();
        this.filePath = filePath;
        File pdfFile = new File(filePath);
        this.document = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfFile));
    }

}
