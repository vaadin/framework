package com.vaadin.tests.components.grid;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;

@TestCategory("grid")
public class GridHeightByRowTest extends MultiBrowserTest {
    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void testHeightByRow() {
        GridElement grid = $(GridElement.class).first();
        ButtonElement addButton = $(ButtonElement.class).caption("Add Data")
                .first();
        ButtonElement removeButton = $(ButtonElement.class)
                .caption("Remove Data").first();

        // 10 rows and the header, each has height of 50
        Assert.assertEquals(550, grid.getSize().getHeight());

        addButton.click();

        Assert.assertEquals(600, grid.getSize().getHeight());

        addButton.click();

        Assert.assertEquals(650, grid.getSize().getHeight());

        removeButton.click();

        Assert.assertEquals(600, grid.getSize().getHeight());
    }
}
