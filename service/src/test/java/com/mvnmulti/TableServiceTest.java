package com.mvnmulti.service;

import com.mvnmulti.model.Cell;
import com.mvnmulti.model.EditCellResult;
import com.mvnmulti.model.Row;
import com.mvnmulti.model.SearchResult;
import com.mvnmulti.model.Table;
import com.mvnmulti.utilities.FileTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TableServiceTest {

    private TableService tableService;
    private FileTable fileTableMock;

    @BeforeEach
    public void setUp() {
        fileTableMock = Mockito.mock(FileTable.class);
        tableService = new TableService(fileTableMock);
    }

    @Test
    public void testLoadTableFromFile() throws IOException {
        doNothing().when(fileTableMock).loadTableFromFile(anyString());
        tableService.loadTableFromFile("test.txt");
        verify(fileTableMock, times(1)).loadTableFromFile("test.txt");
    }

    @Test
    public void testSaveTableToFile() throws IOException {
        doNothing().when(fileTableMock).saveTableToFile(anyString());
        tableService.saveTableToFile("test.txt");
        verify(fileTableMock, times(1)).saveTableToFile("test.txt");
    }

    @Test
    public void testLoadDefaultTable() throws IOException {
        doNothing().when(fileTableMock).loadDefaultTable();
        tableService.loadDefaultTable();
        verify(fileTableMock, times(1)).loadDefaultTable();
    }

    @Test
    public void testCreateTable() {
        Table table = new Table();
        when(fileTableMock.getTable()).thenReturn(table);

        tableService.createTable(2, 2);

        assertEquals(2, table.getRowCount());
        assertEquals(2, table.getColumnCount());
    }

    @Test
    public void testEditCell() {
        Table table = new Table();
        List<Cell> cells = new ArrayList<>();
        Cell cell = new Cell("key", "value");
        cells.add(cell);
        Row row = new Row(cells);
        table.getRows().add(row);

        when(fileTableMock.getTable()).thenReturn(table);

        EditCellResult result = tableService.editCell(0, 0, "key", "newKey");

        assertEquals("key", result.getOldKey());
        assertEquals("newKey", result.getNewKey());
        assertEquals("value", result.getOldValue());
        assertEquals("value", result.getNewValue());
    }

    @Test
    public void testEditCellInvalidIndexes() {
        Table table = new Table();
        when(fileTableMock.getTable()).thenReturn(table);

        assertThrows(IllegalArgumentException.class, () -> {
            tableService.editCell(-1, 0, "key", "newKey");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            tableService.editCell(0, -1, "key", "newKey");
        });
    }

    @Test
    public void testSearchTable() {
        Table table = new Table();
        List<Cell> cells = new ArrayList<>();
        Cell cell = new Cell("key", "value");
        cells.add(cell);
        Row row = new Row(cells);
        table.getRows().add(row);

        when(fileTableMock.getTable()).thenReturn(table);

        List<SearchResult> results = tableService.searchTable("key");

        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getKeyOccurrences());
        assertEquals(0, results.get(0).getValueOccurrences());
    }

    @Test
    public void testSearchTableNoResults() {
        Table table = new Table();
        when(fileTableMock.getTable()).thenReturn(table);

        List<SearchResult> results = tableService.searchTable("nonexistent");

        assertTrue(results.isEmpty());
    }

    @Test
    public void testAddRow() {
        Table table = new Table();
        when(fileTableMock.getTable()).thenReturn(table);

        tableService.addRow(2, 0);

        assertEquals(1, table.getRowCount());
        assertEquals(2, table.getRow(0).getCells().size());
    }

    @Test
    public void testAddRowInvalidIndex() {
        Table table = new Table();
        when(fileTableMock.getTable()).thenReturn(table);

        assertThrows(IllegalArgumentException.class, () -> {
            tableService.addRow(2, -1);
        });
    }

    @Test
    public void testSortTable() {
        Table table = new Table();
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell("b", "2"));
        cells.add(new Cell("a", "1"));
        Row row = new Row(cells);
        table.getRows().add(row);

        when(fileTableMock.getTable()).thenReturn(table);

        tableService.sortTable(0, "asc");

        assertEquals("a", table.getRow(0).getCells().get(0).getKey());
        assertEquals("b", table.getRow(0).getCells().get(1).getKey());
    }

    @Test
    public void testSortTableInvalidIndex() {
        Table table = new Table();
        when(fileTableMock.getTable()).thenReturn(table);

        assertThrows(IllegalArgumentException.class, () -> {
            tableService.sortTable(-1, "asc");
        });
    }
}