package com.vaadin.tests.components.grid.basicfeatures.server;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that removing and adding rows doesn't cause an infinite loop in the
 * browser.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridClearContainerTest extends MultiBrowserTest {

    private final String ERRORNOTE = "Unexpected cell contents.";

    @Test
    public void clearAndReadd() {
        openTestURL();
        ButtonElement button = $(ButtonElement.class)
                .caption("Clear and re-add").first();
        GridElement grid = $(GridElement.class).first();
        Assert.assertEquals(ERRORNOTE, "default", grid.getCell(0, 0).getText());
        Assert.assertEquals(ERRORNOTE, "default", grid.getCell(1, 0).getText());
        button.click();
        Assert.assertEquals(ERRORNOTE, "Updated value 1",
                grid.getCell(0, 0).getText());
        Assert.assertEquals(ERRORNOTE, "Updated value 2",
                grid.getCell(1, 0).getText());
    }
}
