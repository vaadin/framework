package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridAddAndRemoveDataOnInitTest extends MultiBrowserTest {

    @Test
    public void verifyGridSizes() {
        openTestURL();

        GridElement gridAdd = $(GridElement.class).first();
        if (!gridAdd.isElementPresent(By.vaadin("#cell[9][0]"))
                || gridAdd.isElementPresent(By.vaadin("#cell[10][0]"))) {
            Assert.fail("Grid with added data contained incorrect rows");
        }

        GridElement gridRemove = $(GridElement.class).get(1);
        if (!gridRemove.isElementPresent(By.vaadin("#cell[4][0]"))
                || gridRemove.isElementPresent(By.vaadin("#cell[5][0]"))) {
            Assert.fail("Grid with removed data contained incorrect rows");
        }
    }

}
