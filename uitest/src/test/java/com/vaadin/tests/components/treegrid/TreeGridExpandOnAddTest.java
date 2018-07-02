package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeGridExpandOnAddTest extends SingleBrowserTest {

    @Test
    public void testNoException() {
        setDebug(true);
        openTestURL();

        $(ButtonElement.class).first().click();

        TreeGridElement treeGrid = $(TreeGridElement.class).first();
        assertEquals("Parent node not added", "Parent",
                treeGrid.getCell(0, 0).getText());
        assertEquals("Child node not added", "Child",
                treeGrid.getCell(1, 0).getText());

        assertNoErrorNotifications();
    }

}
