package com.vaadin.tests.themes.valo;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridDisabledTest extends MultiBrowserTest {

    @Test
    public void disabledGrid() throws IOException {
        openTestURL();
        waitUntilLoadingIndicatorNotVisible();
        GridElement disabledGrid = $(GridElement.class).id("disabled-grid");

        assertFalse(disabledGrid.getClassNames().toString().contains("v-disabled"));
        $(ButtonElement.class).caption("Disable").first().click();

        assertTrue(disabledGrid.getClassNames().toString().contains("v-disabled"));
    }
}
