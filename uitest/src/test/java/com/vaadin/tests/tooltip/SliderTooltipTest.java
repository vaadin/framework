package com.vaadin.tests.tooltip;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.SliderElement;
import com.vaadin.tests.tb3.TooltipTest;

/**
 * Test that sliders can have tooltips
 *
 * @author Vaadin Ltd
 */
public class SliderTooltipTest extends TooltipTest {

    @Test
    public void sliderHasTooltip() throws Exception {
        openTestURL();
        WebElement slider = $(SliderElement.class).first();
        checkTooltipNotPresent();
        checkTooltip(slider, "Tooltip");
        clearTooltip();
        checkTooltipNotPresent();
    }
}
