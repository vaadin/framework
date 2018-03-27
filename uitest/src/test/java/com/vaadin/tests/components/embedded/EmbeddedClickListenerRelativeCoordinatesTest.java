package com.vaadin.tests.components.embedded;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

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

        clickAt(0, 0);
        checkLocation(0, 0);
    }

    private void clickAt(int x, int y) {
        EmbeddedElement embedded = $(EmbeddedElement.class).first();

        // IE8 consistently clicks two pixels left and above of the given
        // position
        if (isIE8()) {
            x += 2;
            y += 2;
        }
        embedded.click(x, y);
    }

    private void checkLocation(int expectedX, int expectedY) {
        LabelElement xLabel = $(LabelElement.class).id("x");
        LabelElement yLabel = $(LabelElement.class).id("y");

        int x = Integer.parseInt(xLabel.getText());
        int y = Integer.parseInt(yLabel.getText());

        Assert.assertEquals(
                "Reported X-coordinate from Embedded does not match click location",
                expectedX, x);

        // IE10 and IE11 sometimes click one pixel below the given position
        int tolerance = isIE() ? 1 : 0;
        Assert.assertTrue(
                "Reported Y-coordinate from Embedded does not match click location",
                Math.abs(expectedY - y) <= tolerance);
    }

    private boolean isIE() {
        return BrowserUtil.isIE(getDesiredCapabilities());
    }

    private boolean isIE8() {
        return BrowserUtil.isIE8(getDesiredCapabilities());
    }

}
