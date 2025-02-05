package com.mvnmulti.app;

import com.mvnmulti.model.Table;
import com.mvnmulti.model.Row;
import com.mvnmulti.model.EditCellResult;
import com.mvnmulti.model.SearchResult;
import com.mvnmulti.service.TableService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class TableActions {
    private TableService tableService;
    private Scanner scan;

    public TableActions(TableService tableService, Scanner scan) {
        this.tableService = tableService;
        this.scan = scan;
    }

    public void loadTableFromFile(String fileName) throws IOException {
        tableService.loadTableFromFile(fileName);
    }

    public void loadDefaultTable() throws IOException {
        tableService.loadDefaultTable();
    }

    public void printTable() {
        Table table = tableService.getTable();
        for (Row row : table.getRows()) {
            System.out.println(row.toString());
        }
    }

    public void searchTable() {
        System.out.print("Search term: ");
        String searchTerm = scan.nextLine().trim();
        List<SearchResult> searchResults = tableService.searchTable(searchTerm);
        System.out.println("Output:");
        for (SearchResult result : searchResults) {
            StringBuilder output = new StringBuilder();
            if (result.getKeyOccurrences() > 0) {
                output.append(result.getKeyOccurrences())
                    .append(" <")
                    .append(result.getSearchTerm())
                    .append("> at key");
            }
            if (result.getValueOccurrences() > 0) {
                if (output.length() > 0) {
                    output.append(" and ");
                }
                output.append(result.getValueOccurrences())
                    .append(" <")
                    .append(result.getSearchTerm())
                    .append("> at value");
            }
            if (output.length() > 0) {
                output.append(" of [")
                    .append(result.getRowIndex())
                    .append(",")
                    .append(result.getColIndex())
                    .append("]");
                System.out.println(output);
            }
        }
    }

    public void createTable(String action) {
        boolean validInput = false;
        int rows = 0, columns = 0;

        while (!validInput) {
            System.out.print("Input table dimension (rows x columns): ");

            if (!scan.hasNextLine()) return;
            String dimensions = scan.nextLine().trim();
            String[] dimParts = dimensions.split("x");

            if (dimParts.length != 2) {
                System.out.println("Invalid input format. Please use 'rows x columns'");
                continue;
            }

            try {
                rows = Integer.parseInt(dimParts[0].trim());
                columns = Integer.parseInt(dimParts[1].trim());

                if (rows <= 0 || columns <= 0) {
                    throw new NumberFormatException("Rows and columns must be positive integers.");
                }
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input format. Please use 'rows x columns' with positive integers.");
            }
        }

        tableService.createTable(rows, columns);
        printTable();

        System.out.println(action.equalsIgnoreCase("reset") ? "Table reset." : "New table generated.");
        handleSaveTable();
    }

    public void editCell() {
        System.out.print("Edit [row x column]: ");
        String cellPosition = scan.nextLine().trim();
        if (!cellPosition.matches("\\[\\d+,\\d+\\]")) {
            System.out.println("Invalid input format. Please use '[row,column]'");
            return;
        }
        String[] position = cellPosition.substring(1, cellPosition.length() - 1).split(",");

        int editRow = Integer.parseInt(position[0].trim());
        int editCol = Integer.parseInt(position[1].trim());

        Table table = tableService.getTable();
        if (editRow >= table.getRowCount() || editCol >= table.getColumnCount()) {
            System.out.println("Invalid row or column number.");
            return;
        }

        System.out.print("key, value or both? : ");
        if (!scan.hasNextLine()) return;
        String editType = scan.nextLine().trim().toLowerCase();

        System.out.print("Input new: ");
        if (!scan.hasNextLine()) return;
        String editedValue = scan.nextLine().trim();

        try {
            EditCellResult result = tableService.editCell(editRow, editCol, editType, editedValue);

            System.out.println(result.getOldKey() + "," + result.getOldValue() + " -> " + result.getNewKey() + "," + result.getNewValue());

            handleSaveTable();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addRow() {
        System.out.print("No. of Cells: ");
        if (!scan.hasNextInt()) {
            System.out.println("Invalid number of cells.");
            scan.nextLine();
            return;
        }
        int numCells = scan.nextInt();
        scan.nextLine();

        System.out.print("Insert at row (0 to " + tableService.getTable().getRowCount() + "): ");
        if (!scan.hasNextInt()) {
            System.out.println("Invalid row index.");
            scan.nextLine();
            return;
        }
        int rowIndex = scan.nextInt();
        scan.nextLine();

        try {
            tableService.addRow(numCells, rowIndex);
            System.out.println("Row added at index " + rowIndex + ".");
            handleSaveTable();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sortTable() {
        System.out.print("Row to sort: ");
        if (!scan.hasNextLine()) return;
        String input = scan.nextLine().trim();
        String[] parts = input.split(" - ");
        if (parts.length != 2) {
            System.out.println("Invalid input format. Please use 'row - <asc/desc>'.");
            return;
        }

        int rowToSort;
        try {
            rowToSort = Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid row number.");
            return;
        }

        String order = parts[1].trim().toLowerCase();

        if (!order.equals("asc") && !order.equals("desc")) {
            System.out.println("Invalid order. Please use 'asc' or 'desc'.");
            return;
        }

        try {
            tableService.sortTable(rowToSort, order);
            System.out.println("Sorted row " + rowToSort + " in " + order + " order.");
            handleSaveTable();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void saveTable(String fileName) {
        try {
            tableService.saveTableToFile(fileName);
            System.out.println("Table saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving table: " + e.getMessage());
        }
    }

    public void handleSaveTable() {
        String currentFileName = tableService.getCurrentFileName();
        if (tableService.isAppDefault()) {
            System.out.print("Enter a file name to save the table: ");
            if (scan.hasNextLine()) {
                String newFileName = scan.nextLine().trim();
                if (!newFileName.isEmpty()) {
                    saveTable(newFileName);
                    return;
                }
            }
        }
        saveTable(currentFileName);
    }
    
    public Scanner getScanner() {
        return scan;
    }
}