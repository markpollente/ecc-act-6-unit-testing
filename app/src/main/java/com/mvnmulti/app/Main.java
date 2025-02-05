package com.mvnmulti.app;

import com.mvnmulti.service.TableService;
import com.mvnmulti.utilities.FileTable;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        FileTable fileTable = new FileTable();
        TableService tableService = new TableService(fileTable);
        TableActions tableActions = new TableActions(tableService, scan);
        String fileName = args.length > 0 ? args[0] : null;

        if (fileName == null || fileName.trim().isEmpty()) {
            System.out.println("No file name provided. Loading default table from JAR...");
            fileName = "default.txt";
        }

        try {
            if ("default.txt".equalsIgnoreCase(fileName)) {
                tableActions.loadDefaultTable();
            } else {
                tableActions.loadTableFromFile(fileName);
            }
        } catch (Exception e) {
            System.out.println("Error loading table from file: " + e.getMessage());
            try {
                tableActions.loadDefaultTable();
            } catch (IOException ioException) {
                System.out.println("Error loading default table: " + ioException.getMessage());
            }
        }

        Menu menu = new Menu(tableActions);
        menu.show(scan);
    }
}