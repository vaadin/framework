package com.vaadin.tests.elements.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridDetailsTest extends MultiBrowserTest {

    private GridElement gridElement;

    @Override
    protected Class<?> getUIClass() {
        return GridUI.class;
    }

    @Before
    public void init() {
        openTestURL();
        gridElement = $(GridElement.class).first();
    }

    @Test
    public void gridDetails_gridDetailsOpen_elementReturned() {
        gridElement.getCell(0, 0).doubleClick();

        final TestBenchElement details = gridElement.getDetails(0);
        assertEquals("Foo = foo 0 Bar = bar 0",
                details.$(LabelElement.class).first().getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void gridDetails_gridDetailsClosed_exceptionThrown() {
        gridElement.getDetails(0);
    }
}
