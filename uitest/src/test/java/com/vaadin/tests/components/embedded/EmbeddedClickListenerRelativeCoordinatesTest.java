package com.vaadin.tests.components.embedded;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.EmbeddedElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class EmbeddedClickListenerRelativeCoordinatesTest
        extends MultiBrowserTest {

    @Before
    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-embedded"));
    }

    @Test
    public void testRelativeClick() {
        clickAt(41, 22);
        checkLocation(41, 22);

        clickAt(1, 1);
        checkLocation(1, 1);
    }

    private void clickAt(int x, int y) {
        EmbeddedElement embedded = $(EmbeddedElement.class).first();

        embedded.click(getXOffset(embedded, x), getYOffset(embedded, y));
    }

    private void checkLocation(int expectedX, int expectedY) {
        LabelElement xLabel = $(LabelElement.class).id("x");
        LabelElement yLabel = $(LabelElement.class).id("y");

        int x = Integer.parseInt(xLabel.getText());
        int y = Integer.parseInt(yLabel.getText());

        assertEquals(
                "Reported X-coordinate from Embedded does not match click location",
                expectedX, x);

        // IE10 and IE11 sometimes click one pixel below the given position
        int tolerance = isIE() ? 1 : 0;
        assertEquals(
                "Reported Y-coordinate from Embedded does not match click location",
                expectedY, y, tolerance);
    }

    private boolean isIE() {
        return BrowserUtil.isIE(getDesiredCapabilities());
    }

}
