package com.vaadin.tests.tooltip;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class StationaryTooltipTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingTooltip();
    }

    @Test
    public void tooltipShouldBeStationary() throws InterruptedException {
        openTestURL();
        ButtonElement button = getButtonElement();

        // Top left corner
        new Actions(getDriver()).moveToElement(button, 2, 2).perform();
        sleep(3000); // wait for the tooltip to become visible

        int originalTooltipLocationX = getTooltipLocationX();
        assertThat("Tooltip not displayed", originalTooltipLocationX,
                is(greaterThan(0)));

        // Bottom right corner
        new Actions(getDriver()).moveToElement(button,
                button.getSize().width - 2, button.getSize().height - 2)
                .perform();
        int actualTooltipLocationX = getTooltipLocationX();

        assertThat("Tooltip should not move", actualTooltipLocationX,
                is(originalTooltipLocationX));
    }

    private ButtonElement getButtonElement() {
        return $(ButtonElement.class).first();
    }

    private int getTooltipLocationX() {
        return getTooltipElement().getLocation().getX();
    }

}
