package com.mvnmulti.service;

import com.mvnmulti.model.Cell;
import com.mvnmulti.model.EditCellResult;
import com.mvnmulti.model.Row;
import com.mvnmulti.model.SearchResult;
import com.mvnmulti.model.Table;
import com.mvnmulti.utilities.FileTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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

    @InjectMocks
    private TableServiceImpl tableService;

    @Mock
    private FileTable fileTableMock;

    private Table table;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        table = new Table();
        when(fileTableMock.getTable()).thenReturn(table);
    }

    private List<Cell> createCells(String... keysAndValues) {
        List<Cell> cells = new ArrayList<>();
        for (String keyValue : keysAndValues) {
            String[] parts = keyValue.split(",");
            cells.add(new Cell(parts[0], parts[1]));
        }
        return cells;
    }

    private void addRowToTable(List<Cell> cells) {
        Row row = new Row(cells);
        table.getRows().add(row);
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
            tableService.createTable(rows, columns);

            assertEquals(rows, table.getRowCount());
            assertEquals(columns, table.getColumnCount());
        }

        @Test
        public void testCreateTable() {
            tableService.createTable(2, 2);

            assertEquals(2, table.getRowCount());
            assertEquals(2, table.getColumnCount());
        }

        @Test
        public void testCreateTableWithDuplicateKeysInWhileLoop() {
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
            addRowToTable(createCells("key,value"));

            EditCellResult result = tableService.editCell(0, 0, "key", "newKey");

            assertEquals("key", result.getOldKey());
            assertEquals("newKey", result.getNewKey());
            assertEquals("value", result.getOldValue());
            assertEquals("value", result.getNewValue());
        }

        @Test
        public void testEditCellChangeValue() {
            addRowToTable(createCells("key,value"));

            EditCellResult result = tableService.editCell(0, 0, "value", "newValue");

            assertEquals("key", result.getOldKey());
            assertEquals("key", result.getNewKey());
            assertEquals("value", result.getOldValue());
            assertEquals("newValue", result.getNewValue());
        }

        @Test
        public void testEditCellChangeBoth() {
            addRowToTable(createCells("key,value"));

            EditCellResult result = tableService.editCell(0, 0, "both", "newKey,newValue");

            assertEquals("key", result.getOldKey());
            assertEquals("newKey", result.getNewKey());
            assertEquals("value", result.getOldValue());
            assertEquals("newValue", result.getNewValue());
        }

        @Test
        public void testEditCellInvalidEditType() {
            addRowToTable(createCells("key,value"));

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, 0, "invalid", "newKey");
            });
        }

        @Test
        public void testEditCellInvalidIndexes() {
            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(-1, 0, "key", "newKey");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, -1, "key", "newKey");
            });
        }

        @Test
        public void testEditCellWithDuplicateKey() {
            addRowToTable(createCells("key1,value1", "key2,value2"));

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, 1, "key", "key1");
            });
        }

        @Test
        public void testEditCellChangeBothWithDuplicateKey() {
            addRowToTable(createCells("key1,value1", "key2,value2"));

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, 1, "both", "key1,newValue");
            });
        }

        @Test
        public void testEditCellWithInvalidBothInput() {
            addRowToTable(createCells("key,value"));

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, 0, "both", "invalidInput");
            });
        }

        @Test
        public void testEditCellWithInvalidColumnIndexNegative() {
            addRowToTable(createCells("key,value"));

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, -1, "key", "newKey");
            });
        }

        @Test
        public void testEditCellWithInvalidColumnIndexOutOfBounds() {
            addRowToTable(createCells("key,value"));

            assertThrows(IllegalArgumentException.class, () -> {
                tableService.editCell(0, 10, "key", "newKey");
            });
        }
    }

    @Nested
    class SearchTableTests {

        @Test
        public void testSearchTableKeyOnly() {
            addRowToTable(createCells("key,value"));

            List<SearchResult> results = tableService.searchTable("key");

            assertEquals(1, results.size());
            assertEquals(1, results.get(0).getKeyOccurrences());
            assertEquals(0, results.get(0).getValueOccurrences());
        }

        @Test
        public void testSearchTableValueOnly() {
            addRowToTable(createCells("key,value"));

            List<SearchResult> results = tableService.searchTable("value");

            assertEquals(1, results.size());
            assertEquals(0, results.get(0).getKeyOccurrences());
            assertEquals(1, results.get(0).getValueOccurrences());
        }

        @Test
        public void testSearchTableBothKeyAndValue() {
            addRowToTable(createCells("keyvalue,keyvalue"));

            List<SearchResult> results = tableService.searchTable("keyvalue");

            assertEquals(1, results.size());
            assertEquals(1, results.get(0).getKeyOccurrences());
            assertEquals(1, results.get(0).getValueOccurrences());
        }

        @Test
        public void testSearchTableNoOccurrences() {
            addRowToTable(createCells("key,value"));

            List<SearchResult> results = tableService.searchTable("nonexistent");

            assertTrue(results.isEmpty());
        }
    }

    @Nested
    class AddRowTests {

        @Test
        public void testAddRow() {
            tableService.addRow(2, 0);

            assertEquals(1, table.getRowCount());
            assertEquals(2, table.getRow(0).getCells().size());
        }

        @Test
        public void testAddRowWithInvalidRowIndexNegative() {
            assertThrows(IllegalArgumentException.class, () -> {
                tableService.addRow(2, -1);
            });
        }

        @Test
        public void testAddRowWithInvalidRowIndexOutOfBounds() {
            assertThrows(IllegalArgumentException.class, () -> {
                tableService.addRow(2, 10);
            });
        }
    }

    @Nested
    class SortTableTests {

        @Test
        public void testSortTableInvalidIndex() {
            assertThrows(IllegalArgumentException.class, () -> {
                tableService.sortTable(-1, "asc");
            });
        }

        @Test
        public void testSortTableAscending() {
            addRowToTable(createCells("b,2", "a,1"));

            tableService.sortTable(0, "asc");

            assertEquals("a", table.getRow(0).getCells().get(0).getKey());
            assertEquals("b", table.getRow(0).getCells().get(1).getKey());
        }

        @Test
        public void testSortTableDescending() {
            addRowToTable(createCells("b,2", "a,1"));

            tableService.sortTable(0, "desc");

            assertEquals("b", table.getRow(0).getCells().get(0).getKey());
            assertEquals("a", table.getRow(0).getCells().get(1).getKey());
        }

        @Test
        public void testSortTableInvalidOrder() {
            assertThrows(IllegalArgumentException.class, () -> {
                tableService.sortTable(0, "invalidOrder");
            });
        }
    }
}