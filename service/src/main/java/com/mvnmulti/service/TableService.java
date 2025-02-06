package com.mvnmulti.service;

import com.mvnmulti.model.Cell;
import com.mvnmulti.model.Row;
import com.mvnmulti.model.Table;
import com.mvnmulti.model.EditCellResult;
import com.mvnmulti.model.SearchResult;
import com.mvnmulti.utilities.FileTable;

import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

public class TableService {
    private final FileTable fileTable;

    public TableService(FileTable fileTable) {
        this.fileTable = fileTable;
    }

    public void loadTableFromFile(String fileName) throws IOException {
        fileTable.loadTableFromFile(fileName);
    }

    public void saveTableToFile(String fileName) throws IOException {
        fileTable.saveTableToFile(fileName);
    }

    public void loadDefaultTable() throws IOException {
        fileTable.loadDefaultTable();
    }

    public Table getTable() {
        return fileTable.getTable();
    }

    public boolean isAppDefault() {
        return fileTable.isAppDefault();
    }

    public String getCurrentFileName() {
        return fileTable.getCurrentFileName();
    }

    public String generateRandomAscii(int length) {
        Random random = new Random();
        StringBuilder rand = new StringBuilder();
        for (int i = 0; i < length; i++) {
            rand.append((char) (random.nextInt(94) + 33));
        }
        return rand.toString();
    }

    public void createTable(int rows, int columns) {
        Table table = fileTable.getTable();
        table.clear();
        Set<String> generatedKeys = new HashSet<>();

        for (int i = 0; i < rows; i++) {
            List<Cell> rowCells = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                String key;
                do {
                    key = generateRandomAscii(3);
                } while (generatedKeys.contains(key));
                generatedKeys.add(key);
                String value = generateRandomAscii(3);
                rowCells.add(new Cell(key, value));
            }
            table.addRow(new Row(rowCells));
        }
    }

    public EditCellResult editCell(int rowIndex, int colIndex, String editType, String editedValue) {
        Table table = fileTable.getTable();
        if (rowIndex < 0 || rowIndex >= table.getRowCount()) {
            throw new IllegalArgumentException("Invalid row index.");
        }

        Row row = table.getRow(rowIndex);
        if (colIndex < 0 || colIndex >= row.getCells().size()) {
            throw new IllegalArgumentException("Invalid column index.");
        }

        Cell cell = row.getCells().get(colIndex);
        String oldKey = cell.getKey();
        String oldValue = cell.getValue();

        String newKey = oldKey;
        String newValue = oldValue;

        switch (editType) {
            case "key":
                if (table.containsKey(editedValue)) {
                    throw new IllegalArgumentException("Duplicate key found: " + editedValue);
                }
                newKey = editedValue;
                cell.setKey(newKey);
                break;
            case "value":
                newValue = editedValue;
                cell.setValue(newValue);
                break;
            case "both":
                String[] editedKeyValue = editedValue.split(",", 2);
                if (editedKeyValue.length == 2) {
                    if (table.containsKey(editedKeyValue[0])) {
                        throw new IllegalArgumentException("Duplicate key found: " + editedKeyValue[0]);
                    }
                    newKey = editedKeyValue[0];
                    newValue = editedKeyValue[1];
                    cell.setKey(newKey);
                    cell.setValue(newValue);
                } else {
                    throw new IllegalArgumentException("Invalid input for both key and value. Use 'key,value' format");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid edit type.");
        }

        return new EditCellResult(rowIndex, colIndex, oldKey, oldValue, newKey, newValue);
    }

    public List<SearchResult> searchTable(String searchTerm) {
        List<SearchResult> results = new ArrayList<>();

        Table table = fileTable.getTable();
        for (int i = 0; i < table.getRowCount(); i++) {
            Row row = table.getRow(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                Cell cell = row.getCells().get(j);
                int keyOccurrences = (cell.getKey().length() - cell.getKey().replace(searchTerm, "").length()) / searchTerm.length();
                int valueOccurrences = (cell.getValue().length() - cell.getValue().replace(searchTerm, "").length()) / searchTerm.length();

                if (keyOccurrences > 0 || valueOccurrences > 0) {
                    results.add(new SearchResult(keyOccurrences, searchTerm, valueOccurrences, i, j));
                }
            }
        }
        return results;
    }

    public void addRow(int numCells, int rowIndex) {
        List<Cell> newCells = new ArrayList<>();
        for (int i = 0; i < numCells; i++) {
            newCells.add(new Cell(generateRandomAscii(3), generateRandomAscii(3)));
        }

        if (rowIndex < 0 || rowIndex > fileTable.getTable().getRowCount()) {
            throw new IllegalArgumentException("Invalid row index.");
        }
        fileTable.getTable().getRows().add(rowIndex, new Row(newCells));
    }

    public void sortTable(int rowIndex, String order) {
        Table table = fileTable.getTable();
        if (rowIndex < 0 || rowIndex >= table.getRowCount()) {
            throw new IllegalArgumentException("Invalid row index.");
        }

        List<Cell> rowToSortList = table.getRow(rowIndex).getCells();
        Comparator<Cell> comparator = Comparator.comparing(cell -> (cell.getKey() + cell.getValue()));

        if (order.equals("desc")) {
            comparator = comparator.reversed();
        }

        rowToSortList.sort(comparator);
    }
}