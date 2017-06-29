package com.vaadin.tests.data;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;

public class GridChangeItemsTest extends SingleBrowserTest {
    @Test
    public void testDataItemsReplaced() {
        openTestURL();
        // Select item
        GridElement grid = $(GridElement.class).first();
        ButtonElement replaceItemsButton = $(ButtonElement.class).first();
        grid.scrollToRow(140);
        replaceItemsButton.click();
        assertNoErrorNotifications();
    }
}
