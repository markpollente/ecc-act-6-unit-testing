package com.mvnmulti.service;

import com.mvnmulti.model.EditCellResult;
import com.mvnmulti.model.SearchResult;
import com.mvnmulti.model.Table;

import java.io.IOException;
import java.util.List;

public interface TableService {
    void loadTableFromFile(String fileName) throws IOException;
    void saveTableToFile(String fileName) throws IOException;
    void loadDefaultTable() throws IOException;
    Table getTable();
    boolean isAppDefault();
    String getCurrentFileName();
    void createTable(int rows, int columns);
    EditCellResult editCell(int rowIndex, int colIndex, String editType, String editedValue);
    List<SearchResult> searchTable(String searchTerm);
    void addRow(int numCells, int rowIndex);
    void sortTable(int rowIndex, String order);
    String generateRandomAscii(int length);
}