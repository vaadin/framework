package com.vaadin.tests.components.embedded;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.EmbeddedElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

import static org.junit.Assert.assertEquals;

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

        // IE10 and IE11 sometimes click one pixel below the given position,
        // so does the Chrome(since 75)
        int tolerance;
        if (isIE() || isChrome()) {
            tolerance = 1;
        } else {
            tolerance = 0;
        }
        assertEquals(
                "Reported Y-coordinate from Embedded does not match click location",
                expectedY, y, tolerance);
    }

    private boolean isIE() {
        return BrowserUtil.isIE(getDesiredCapabilities());
    }

    private boolean isChrome() {
        return BrowserUtil.isChrome(getDesiredCapabilities());
    }

}
