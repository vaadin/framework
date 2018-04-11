package com.vaadin.tests.components.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TableParentEnabledStateChangeTest extends SingleBrowserTest {

    @Test
    public void tableEnabledShouldFollowParent() {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        TableRowElement row = table.getRow(0);

        ButtonElement button = $(ButtonElement.class).first();

        row.click();
        assertTrue(isSelected(row));

        // Disable
        button.click();
        assertTrue(isSelected(row));
        row.click(); // Should have no effect
        assertTrue(isSelected(row));

        // Enable
        button.click();
        assertTrue(isSelected(row));
        row.click(); // Should deselect
        assertFalse(isSelected(row));
    }

    private boolean isSelected(TableRowElement row) {
        return hasCssClass(row, "v-selected");
    }
}
