package com.mvnmulti.service;

import com.mvnmulti.model.Cell;
import com.mvnmulti.model.EditCellResult;
import com.mvnmulti.model.Row;
import com.mvnmulti.model.SearchResult;
import com.mvnmulti.model.Table;
import com.mvnmulti.utilities.FileTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

public class TableServiceImplTest {

    private TableServiceImpl tableService;
    private FileTable fileTableMock;

    @BeforeEach
    public void setUp() {
        fileTableMock = Mockito.mock(FileTable.class);
        tableService = new TableServiceImpl(fileTableMock);
    }

    @Nested
    class BasicOperationsTests {

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
        public void testGetTable() {
            Table table = new Table();
            when(fileTableMock.getTable()).thenReturn(table);

            Table result = tableService.getTable();

            assertNotNull(result);
            assertEquals(table, result);
        }

        @Test
        public void testIsAppDefault() {
            when(fileTableMock.isAppDefault()).thenReturn(true);

            boolean result = tableService.isAppDefault();

            assertTrue(result);
        }

        @Test
        public void testGetCurrentFileName() {
            String fileName = "test.txt";
            when(fileTableMock.getCurrentFileName()).thenReturn(fileName);

            String result = tableService.getCurrentFileName();

            assertEquals(fileName, result);
        }
    }

    @Nested
    class CreateTableTests {

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        public void testCreateTableWithDifferentSizes(int size) {
            Table table = new Table();
            when(fileTableMock.getTable()).thenReturn(table);

            tableService.createTable(size, size);

            assertEquals(size, table.getRowCount());
            assertEquals(size, table.getColumnCount());
        }

        @ParameterizedTest
        @CsvSource({
            "1, 2",
            "2, 3",
            "3, 4"
        })
        public void testCreateTableWithDifferentRowsAndColumns(int rows, int columns) {
            Table table = new Table();
            when(fileTableMock.getTable()).thenReturn(table);

            tableService.createTable(rows, columns);

            assertEquals(rows, table.getRowCount());
            assertEquals(columns, table.getColumnCount());
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
        public void testCreateTableWithDuplicateKeysInWhileLoop() {
            Table table = new Table();
            when(fileTableMock.getTable()).thenReturn(table);

            TableService spyTableService = spy(tableService);

            doReturn("dup").doReturn("dup").doReturn("dup").doReturn("unique1").doReturn("unique2").when(spyTableService).generateRandomAscii(anyInt());

            spyTableService.createTable(1, 2);

            assertEquals(1, table.getRowCount());
            assertEquals(2, table.getColumnCount());

            HashSet<String> generatedKeys = new HashSet<>();
            generatedKeys.add("dup");
            generatedKeys.add("unique1");
            generatedKeys.add("unique2");

            assertTrue(generatedKeys.contains(table.getRow(0).getCells().get(0).getKey()));
            assertTrue(generatedKeys.contains(table.getRow(0).getCells().get(1).getKey()));
            assertNotEquals(table.getRow(0).getCells().get(0).getKey(), table.getRow(0).getCells().get(1).getKey());
        }
    }

    @Nested
    class EditCellTests {

        @Test
        public void testEditCellChangeKey() {
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
        public void testEditCellChangeValue() {
            Table table = new Table();
            List<Cell> cells = new ArrayList<>();
            Cell cell = new Cell("key", "value");
            cells.add(cell);
            Row row = new Row(cells);
            table.getRows().add(row);

            when(fileTableMock.getTable()).thenReturn(table);

            EditCellResult result = tableService.editCell(0, 0, "value", "newValue");

            assertEquals("key", result.getOldKey());
            assertEquals("key", result.getNewKey());
            assertEquals("value", result.getOldValue());
            assertEquals("newValue", result.getNewValue());
        }

        @Test
        public void testEditCellChangeBoth() {
            Table table = new Table();
            List<Cell> cells = new ArrayList<>();
            Cell cell = new Cell("key", "value");
            cells.add(cell);
            Row row = new Row(cells);
            table.getRows().add(row);

            when(fileTableMock.getTable()).thenReturn(table);

            EditCellResult result = tableService.editCell(0, 0, "both", "newKey,newValue");

            assertEquals("key", result.getOldKey());
            assertEquals("newKey", result.getNewKey());
            assertEquals("value", result.getOldValue());
            assertEquals("newValue", result.getNewValue());
        }

        @Test
        public void testEditCellInvalidEditType() {
            Table table = new Table();
            List<Cell> cells = new ArrayList<>();
            Cell cell = new Cell("key", "value");
            cells.add(cell);
            Row row = new Row(cells);
            table.getRows().add(row);

            when(fileTableMock.getTable()).thenReturn(table);

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, 0, "invalid", "newKey");
            });
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
        public void testEditCellWithDuplicateKey() {
            Table table = new Table();
            List<Cell> cells = new ArrayList<>();
            cells.add(new Cell("key1", "value1"));
            cells.add(new Cell("key2", "value2"));
            table.addRow(new Row(cells));

            when(fileTableMock.getTable()).thenReturn(table);

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, 1, "key", "key1");
            });
        }

        @Test
        public void testEditCellChangeBothWithDuplicateKey() {
            Table table = new Table();
            List<Cell> cells = new ArrayList<>();
            cells.add(new Cell("key1", "value1"));
            cells.add(new Cell("key2", "value2"));
            table.addRow(new Row(cells));

            when(fileTableMock.getTable()).thenReturn(table);

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, 1, "both", "key1,newValue");
            });
        }

        @Test
        public void testEditCellWithInvalidBothInput() {
            Table table = new Table();
            List<Cell> cells = new ArrayList<>();
            cells.add(new Cell("key", "value"));
            table.addRow(new Row(cells));

            when(fileTableMock.getTable()).thenReturn(table);

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, 0, "both", "invalidInput");
            });
        }

        @Test
        public void testEditCellWithInvalidColumnIndexNegative() {
            Table table = new Table();
            List<Cell> cells = new ArrayList<>();
            cells.add(new Cell("key", "value"));
            table.addRow(new Row(cells));

            when(fileTableMock.getTable()).thenReturn(table);

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, -1, "key", "newKey");
            });
        }

        @Test
        public void testEditCellWithInvalidColumnIndexOutOfBounds() {
            Table table = new Table();
            List<Cell> cells = new ArrayList<>();
            cells.add(new Cell("key", "value"));
            table.addRow(new Row(cells));

            when(fileTableMock.getTable()).thenReturn(table);

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, 10, "key", "newKey");
            });
        }
    }

    @Nested
    class SearchTableTests {

        @Test
        public void testSearchTableKeyOnly() {
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
        public void testSearchTableValueOnly() {
            Table table = new Table();
            List<Cell> cells = new ArrayList<>();
            Cell cell = new Cell("key", "value");
            cells.add(cell);
            Row row = new Row(cells);
            table.getRows().add(row);

            when(fileTableMock.getTable()).thenReturn(table);

            List<SearchResult> results = tableService.searchTable("value");

            assertEquals(1, results.size());
            assertEquals(0, results.get(0).getKeyOccurrences());
            assertEquals(1, results.get(0).getValueOccurrences());
        }

        @Test
        public void testSearchTableBothKeyAndValue() {
            Table table = new Table();
            List<Cell> cells = new ArrayList<>();
            Cell cell = new Cell("keyvalue", "keyvalue");
            cells.add(cell);
            Row row = new Row(cells);
            table.getRows().add(row);

            when(fileTableMock.getTable()).thenReturn(table);

            List<SearchResult> results = tableService.searchTable("keyvalue");

            assertEquals(1, results.size());
            assertEquals(1, results.get(0).getKeyOccurrences());
            assertEquals(1, results.get(0).getValueOccurrences());
        }

        @Test
        public void testSearchTableNoOccurrences() {
            Table table = new Table();
            List<Cell> cells = new ArrayList<>();
            cells.add(new Cell("key", "value"));
            table.addRow(new Row(cells));

            when(fileTableMock.getTable()).thenReturn(table);

            List<SearchResult> results = tableService.searchTable("nonexistent");

            assertTrue(results.isEmpty());
        }
    }

    @Nested
    class AddRowTests {

        @Test
        public void testAddRow() {
            Table table = new Table();
            when(fileTableMock.getTable()).thenReturn(table);

            tableService.addRow(2, 0);

            assertEquals(1, table.getRowCount());
            assertEquals(2, table.getRow(0).getCells().size());
        }

        @Test
        public void testAddRowWithInvalidRowIndexNegative() {
            Table table = new Table();
            when(fileTableMock.getTable()).thenReturn(table);

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.addRow(2, -1);
            });
        }

        @Test
        public void testAddRowWithInvalidRowIndexOutOfBounds() {
            Table table = new Table();
            when(fileTableMock.getTable()).thenReturn(table);

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.addRow(2, 10);
            });
        }
    }

    @Nested
    class SortTableTests {

        @Test
        public void testSortTableInvalidIndex() {
            Table table = new Table();
            when(fileTableMock.getTable()).thenReturn(table);

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.sortTable(-1, "asc");
            });
        }

        @Test
        public void testSortTableAscending() {
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
        public void testSortTableDescending() {
            Table table = new Table();
            List<Cell> cells = new ArrayList<>();
            cells.add(new Cell("b", "2"));
            cells.add(new Cell("a", "1"));
            Row row = new Row(cells);
            table.getRows().add(row);

            when(fileTableMock.getTable()).thenReturn(table);

            tableService.sortTable(0, "desc");

            assertEquals("b", table.getRow(0).getCells().get(0).getKey());
            assertEquals("a", table.getRow(0).getCells().get(1).getKey());
        }

        @Test
        public void testSortTableInvalidOrder() {
            Table table = new Table();
            when(fileTableMock.getTable()).thenReturn(table);

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.sortTable(0, "invalidOrder");
            });
        }
    }
}