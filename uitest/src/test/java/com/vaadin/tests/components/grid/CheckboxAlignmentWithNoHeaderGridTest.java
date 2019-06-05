package com.vaadin.tests.components.grid;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CheckboxAlignmentWithNoHeaderGridTest extends SingleBrowserTest {

    GridElement grid;

    @Before
    public void init() {
        openTestURL();
        grid = $(GridElement.class).first();
    }

    @Test
    public void alignments_are_correct() throws IOException {
        Assert.assertTrue("This should be an empty grid",
                grid.getRowCount() == 0);

        for (int i =0; i<5; i++) {
            $(ButtonElement.class).first().click();
        }
        sleep(100);

        Assert.assertTrue("This should be an empty grid",
                grid.getRowCount() == 5);
        compareScreen("alignment");
    }
}
