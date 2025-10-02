package se233.chapter3.controller;

import se233.chapter3.model.FileFreq;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class WordCountReduceTask implements Callable<LinkedHashMap<String, List<FileFreq>>> {
    private final Map<String, FileFreq>[] wordMap;
    private LinkedHashMap<String, List<FileFreq>> uniqueSets;

    public WordCountReduceTask(Map<String, FileFreq>[] wordMap) {
        this.wordMap = wordMap;
    }

    @Override
    public LinkedHashMap<String, List<FileFreq>> call() {
        List<Map<String, FileFreq>> wordMapList = new ArrayList<>(Arrays.asList(wordMap));

        uniqueSets = wordMapList.stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ))
                .entrySet()
                .stream()
                // The sorting logic is changed here to sort by total frequency in descending order
                .sorted(Comparator.<Map.Entry<String, List<FileFreq>>>comparingInt(
                                e -> e.getValue().stream().mapToInt(FileFreq::getFreq).sum()
                        ).reversed()
                )
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
        return uniqueSets;
    }

    public LinkedHashMap<String, List<FileFreq>> getUniqueSets() {
        return uniqueSets;
    }

    public void setUniqueSets(LinkedHashMap<String, List<FileFreq>> uniqueSets) {
        this.uniqueSets = uniqueSets;
    }
}
