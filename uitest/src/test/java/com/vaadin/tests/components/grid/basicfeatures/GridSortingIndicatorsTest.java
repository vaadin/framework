package com.vaadin.tests.components.grid.basicfeatures;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

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
