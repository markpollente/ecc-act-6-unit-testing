package com.mvnmulti.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Row {
    private List<Cell> cells = new ArrayList<>();

    public Row(List<Cell> cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        List<String> cellStrings = new ArrayList<>();
        for (Cell cell : cells) {
            cellStrings.add(cell.toString());
        }
        return String.join("   ", cellStrings);
    }
}