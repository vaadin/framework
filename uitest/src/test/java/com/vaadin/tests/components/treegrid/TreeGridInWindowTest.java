package com.vaadin.tests.components.treegrid;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeGridInWindowTest extends MultiBrowserTest {

    @Test
    public void collapse_and_expand_first_child_multiple_times() {
        setDebug(true);
        openTestURL();

        ButtonElement openWindowButton = $(ButtonElement.class).first();
        openWindowButton.click();

        TreeGridElement grid = $(TreeGridElement.class).first();

        for (int i = 0; i < 10; i++) {
            // Collapse first child node
            grid.getExpandElement(1, 0).click();
            waitUntil(webDriver -> grid.getRowCount() == 5);

            // Expand first child node
            grid.getExpandElement(1, 0).click();
            waitUntil(webDriver -> grid.getRowCount() == 7);
        }

        assertNoErrorNotifications();
    }
}
