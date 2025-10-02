package se233.chapter6_ex3.controller;

import se233.chapter6_ex3.model.FileFreq;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class WordCountReduceTask implements Callable<LinkedHashMap<String,List<
        FileFreq>>> {
private Map<String, FileFreq>[] wordMap;

public WordCountReduceTask(Map<String, FileFreq>[] wordMap) {
    this.wordMap = wordMap;
}
    LinkedHashMap<String, List<FileFreq>> uniqueSets;
@Override
public LinkedHashMap<String, List<FileFreq>> call() throws Exception {

    List<Map<String, FileFreq>> wordMapList = new ArrayList<>(Arrays.asList(wordMap));

    uniqueSets = wordMapList.stream()
            .flatMap(m -> m.entrySet().stream())
            .collect(Collectors.groupingBy(
                    e->e.getKey(),
                    Collector.of(
                            () -> new ArrayList<FileFreq>(), // Supplier: new ArrayList for each group
                            (list, item) -> list.add(item.getValue()), // Accumulator: add FileFreq to the list
                            (current_list, new_items) -> { // Combiner: combine two lists
                                current_list.addAll(new_items);
                                return current_list;
                            })
            ))
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(e->e.getKey(),e->e.getValue(),(v1,v2 )->v1, () -> new LinkedHashMap<>())); // Use LinkedHashMap to preserve order
    return uniqueSets;
}


public LinkedHashMap<String, List<FileFreq>> getUniqueSets() {
    return uniqueSets;
}

public void setUniqueSets(LinkedHashMap<String, List<FileFreq>> uniqueSets) {
    this.uniqueSets = uniqueSets;
}
}
