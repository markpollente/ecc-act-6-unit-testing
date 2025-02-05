package com.mvnmulti.utilities;

import com.mvnmulti.model.Cell;
import com.mvnmulti.model.Row;
import com.mvnmulti.model.Table;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

public class FileTable {
    private static final char CELL_DELIMITER = (char) 31;
    private static final char KEY_VALUE_DELIMITER = (char) 30;
    private static final String DEFAULT_MARKER = "APP_DEFAULT_TABLE";
    private Table table;
    private String currentFileName;
    private boolean isAppDefault;

    public FileTable() {
        this.table = new Table();
    }

    public Table getTable() {
        return table;
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public boolean isAppDefault() {
        return isAppDefault;
    }

    public void loadTableFromFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IOException("File not found: " + fileName);
        }

        List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
        loadTableFromLines(lines);
        currentFileName = fileName;
        isAppDefault = checkIfAppDefault(lines);
        System.out.println("Loaded table from file.");
    }

    public void loadTableFromContent(String content) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            loadTableFromLines(lines);
            isAppDefault = checkIfAppDefault(lines);
        }
    }

    public void loadDefaultTable() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("app_default.txt")) {
            if (inputStream == null) {
                throw new IOException("Default table file not found.");
            }
            String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            loadTableFromContent(content);
            currentFileName = "app_default.txt";
            isAppDefault = true;
            System.out.println("Loaded default table from JAR.");
        } catch (IOException e) {
            System.out.println("Error loading default table: " + e.getMessage());
            throw e;
        }
    }

    private void loadTableFromLines(List<String> lines) {
        table.clear();
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            List<Cell> cells = parseLineToCells(line);
            table.addRow(new Row(cells));
        }
    }

    private List<Cell> parseLineToCells(String line) {
        List<Cell> cells = new ArrayList<>();
        String[] cellArray = line.split(String.valueOf(CELL_DELIMITER));
        for (String cellStr : cellArray) {
            cells.add(parseCell(cellStr));
        }
        return cells;
    }

    private Cell parseCell(String cellStr) {
        if (cellStr.startsWith("(") && cellStr.endsWith(")")) {
            String cleanCellStr = cellStr.substring(1, cellStr.length() - 1);
            String[] keyValue = cleanCellStr.split(String.valueOf(KEY_VALUE_DELIMITER), 2);
            if (keyValue.length == 2) {
                return new Cell(keyValue[0], keyValue[1]);
            } else {
                System.out.println("Invalid key-value pair: " + cleanCellStr);
            }
        } else {
            System.out.println("Invalid cell format: " + cellStr);
        }
        return new Cell("Invalid", "Invalid");
    }

    public void saveTableToFile(String fileName) throws IOException {
        File file = new File(fileName);    
        List<String> lines = new ArrayList<>();
        for (Row row : table.getRows()) {
            List<String> formattedCells = new ArrayList<>();
            for (Cell cell : row.getCells()) {
                formattedCells.add("(" + cell.getKey() + KEY_VALUE_DELIMITER + cell.getValue() + ")");
            }
            lines.add(String.join(String.valueOf(CELL_DELIMITER), formattedCells));
        }
        FileUtils.writeLines(file, lines, "\n");
        currentFileName = fileName;
        isAppDefault = false;
    }

    private boolean checkIfAppDefault(List<String> lines) {
        return !lines.isEmpty() && lines.get(0).equals(DEFAULT_MARKER);
    }
}