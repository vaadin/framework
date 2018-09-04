package com.vaadin.tests.components.label;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class LabelTooltipTest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Test
    public void testLabelTooltip() throws IOException {
        openTestURL();
        assertTooltips();
    }

    @Test
    public void testLabelToolTipChameleonTheme() throws IOException {
        openTestURL("theme=chameleon");
        assertTooltips();
    }

    @Test
    public void testLabelToolTipRunoTheme() throws IOException {
        openTestURL("theme=runo");
        assertTooltips();
    }

    private void assertTooltips() throws IOException {
        $(LabelElement.class).get(2).showTooltip();
        Assert.assertEquals("Default tooltip content",
                getTooltipElement().getText());

        /*
         * Some cases tooltip doesn't disappear without some extra mouse events
         */
        $(LabelElement.class).get(1).showTooltip();

        $(LabelElement.class).get(4).showTooltip();
        Assert.assertEquals(
                "Error inside tooltip together with the regular tooltip message.",
                getTooltipErrorElement().getText());
        Assert.assertEquals("Default tooltip content",
                getTooltipElement().getText());

        /* Visual comparison */
        compareScreen("tooltipVisible");
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // this test also works on IEs, but Firefox has problems with tooltips
        return getBrowsersExcludingFirefox();
    }

}
