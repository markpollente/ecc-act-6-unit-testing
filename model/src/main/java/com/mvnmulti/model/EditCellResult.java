package com.mvnmulti.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditCellResult {
    private final int rowIndex;
    private final int colIndex;
    private final String oldKey;
    private final String oldValue;
    private final String newKey;
    private final String newValue;
}