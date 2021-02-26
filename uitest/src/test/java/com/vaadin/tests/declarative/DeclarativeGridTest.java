package com.vaadin.tests.declarative;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DeclarativeGridTest extends MultiBrowserTest {

    @Test
    public void testMergedHeaderCell() {
        openTestURL();
        waitForElementPresent(By.className("v-label"));
        // ensure the grid gets loaded and has the merged header
        GridElement grid = $(GridElement.class).first();
        grid.getHeaderCellByCaption("Project and Status");
    }
}
