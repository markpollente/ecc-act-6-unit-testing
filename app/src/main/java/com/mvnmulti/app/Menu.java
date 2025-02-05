package com.mvnmulti.app;

import java.util.Scanner;

public class Menu {
    private TableActions tableActions;

    public Menu(TableActions tableActions) {
        this.tableActions = tableActions;
    }

    public void show(Scanner scan) {
        boolean running = true;

        while (running) {
            printMenu();

            if (!scan.hasNextLine()) {
                break;
            }
            String action = scan.nextLine().trim();

            switch (action) {
                case "search":
                    tableActions.searchTable();
                    break;
                case "edit":
                    tableActions.editCell();
                    break;
                case "add_row":
                    tableActions.addRow();
                    break;
                case "sort":
                    tableActions.sortTable();
                    break;
                case "print":
                    tableActions.printTable();
                    break;
                case "reset":
                    tableActions.createTable("reset");
                    break;
                case "x":
                    tableActions.handleSaveTable();
                    System.out.println("Exiting application.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid action.");
            }
        }
    }

    private void printMenu() {
        System.out.println("MENU:");
        System.out.println("[ search ] - Search");
        System.out.println("[ edit ] - Edit");
        System.out.println("[ add_row ] - Add Row");
        System.out.println("[ print ] - Print");
        System.out.println("[ sort ] - Sort");
        System.out.println("[ reset ] - Reset");
        System.out.println("[ x ] - Exit");
        System.out.print("Action: ");
    }
}