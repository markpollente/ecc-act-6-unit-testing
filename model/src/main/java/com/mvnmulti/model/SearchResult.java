package com.mvnmulti.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchResult {
    private final int keyOccurrences;
    private final String searchTerm;
    private final int valueOccurrences;
    private final int rowIndex;
    private final int colIndex;
}