package com.vaadin.tests.components.table;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TableReadOnlyTextFieldTest extends MultiBrowserTest {

    @Test
    public void selectRowOnTextFieldClick() {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        assertFalse(isSelected(table.getRow(0)));
        findElement(By.className("v-textfield-readonly")).click();
        assertTrue(
                "The row should be selected, if read-only TextField is clicked",
                isSelected(table.getRow(0)));

    }

    private boolean isSelected(TableRowElement row) {
        return hasCssClass(row, "v-selected");
    }
}
