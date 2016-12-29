package com.vaadin.tests.components.table;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableScrollsOnSelectionTest extends MultiBrowserTest {

    @Test
    public void tableIsNotScrolledOnSelect() throws IOException {
        openTestURL();

        TableElement table = $(TableElement.class).first();

        scrollTable(table, 80, 79);

        table.getCell(79, 0).click();

        compareScreen("scrolled-down");
    }
}
