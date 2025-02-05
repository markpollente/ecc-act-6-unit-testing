package com.mvnmulti.utilities;

import com.mvnmulti.model.Cell;
import com.mvnmulti.model.Row;
import com.mvnmulti.model.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileTableTest {

    private FileTable fileTable;

    @BeforeEach
    public void setUp() {
        fileTable = new FileTable();
    }

    @Test
    public void testLoadTableFromContent() throws IOException {
        String content = "(key1" + (char)30 + "value1)" + (char)31 + "(key2" + (char)30 + "value2)\n" +
                         "(key3" + (char)30 + "value3)" + (char)31 + "(key4" + (char)30 + "value4)";
        fileTable.loadTableFromContent(content);

        Table table = fileTable.getTable();

        assertEquals(2, table.getRowCount());
        assertEquals(2, table.getColumnCount());

        Row row1 = table.getRow(0);
        Row row2 = table.getRow(1);

        assertEquals("key1", row1.getCells().get(0).getKey());
        assertEquals("value1", row1.getCells().get(0).getValue());
        assertEquals("key2", row1.getCells().get(1).getKey());
        assertEquals("value2", row1.getCells().get(1).getValue());

        assertEquals("key3", row2.getCells().get(0).getKey());
        assertEquals("value3", row2.getCells().get(0).getValue());
        assertEquals("key4", row2.getCells().get(1).getKey());
        assertEquals("value4", row2.getCells().get(1).getValue());
    }

    @Test
    public void testSaveTableToFile() throws IOException {
        Table table = fileTable.getTable();
        List<Cell> cells1 = new ArrayList<>();
        cells1.add(new Cell("key1", "value1"));
        cells1.add(new Cell("key2", "value2"));
        table.addRow(new Row(cells1));

        List<Cell> cells2 = new ArrayList<>();
        cells2.add(new Cell("key3", "value3"));
        cells2.add(new Cell("key4", "value4"));
        table.addRow(new Row(cells2));

        String expectedContent = "(key1" + (char)30 + "value1)" + (char)31 + "(key2" + (char)30 + "value2)\n" +
                                 "(key3" + (char)30 + "value3)" + (char)31 + "(key4" + (char)30 + "value4)\n";

        java.nio.file.Path path = java.nio.file.Files.createTempFile("testTable", ".txt");
        fileTable.saveTableToFile(path.toString());

        String actualContent = java.nio.file.Files.readString(path, StandardCharsets.UTF_8);
        assertEquals(expectedContent, actualContent);

        java.nio.file.Files.deleteIfExists(path);
    }
}