package com.vaadin.tests.tooltip;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.TooltipTest;

/**
 * Tests that tooltip sizes do not change when moving between adjacent elements
 *
 * @author Vaadin Ltd
 */
public class ButtonTooltipsTest extends TooltipTest {

    @Test
    public void tooltipSizeWhenMovingBetweenElements() throws Exception {
        openTestURL();

        WebElement buttonOne = $(ButtonElement.class).caption("One").first();
        WebElement buttonTwo = $(ButtonElement.class).caption("Two").first();

        checkTooltip(buttonOne, ButtonTooltips.longDescription);
        int originalWidth = getTooltipElement().getSize().getWidth();
        int originalHeight = getTooltipElement().getSize().getHeight();

        clearTooltip();
        checkTooltip(buttonTwo, ButtonTooltips.shortDescription);
        moveMouseTo(buttonOne, 5, 5);
        sleep(100);
        assertThat(getTooltipElement().getSize().getWidth(), is(originalWidth));
        assertThat(getTooltipElement().getSize().getHeight(),
                is(originalHeight));
    }
}
