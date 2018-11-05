package com.vaadin.v7.tests.components.grid.basicfeatures;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridSortingIndicatorsTest extends MultiBrowserTest {

    @Test
    public void testSortingIndicators() throws IOException {
        openTestURL();
        compareScreen("initialSort");

        $(ButtonElement.class).first().click();

        compareScreen("reversedSort");
    }
}
