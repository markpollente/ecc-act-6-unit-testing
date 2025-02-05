package com.mvnmulti.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Table {
    private List<Row> rows = new ArrayList<>();

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return rows.isEmpty() ? 0 : rows.get(0).getCells().size();
    }

    public void addRow(Row row) {
        rows.add(row);
    }

    public Row getRow(int index) {
        return rows.get(index);
    }

    public void setRow(int index, Row row) {
        rows.set(index, row);
    }

    public void clear() {
        rows.clear();
    }

    public boolean containsKey(String key) {
        for (Row row : rows) {
            for (Cell cell : row.getCells()) {
                if (cell.getKey().equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }
}