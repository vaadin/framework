package com.vaadin.tests.tooltip;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if tooltips obey quickOpenDelay when moving between directly
 * adjacent elements.
 *
 * @author Vaadin Ltd
 */
public class AdjacentElementsWithTooltipsTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingTooltip();
    }

    @Test
    public void tooltipsHaveQuickOpenDelay() throws InterruptedException {
        openTestURL();

        new Actions(getDriver()).moveToElement(getButton("Button 0")).perform();
        sleep(1000);
        assertThat(getTooltipElement().getLocation().getX(),
                is(greaterThan(0)));

        ButtonElement button1 = getButton("Button 1");
        new Actions(getDriver()).moveToElement(button1).perform();
        assertThat(getTooltipElement().getLocation().getX(),
                is(lessThan(-1000)));

        sleep(1000);
        assertThat(getTooltipElement().getLocation().getX(),
                is(greaterThan(button1.getLocation().getX())));
    }

    private ButtonElement getButton(String caption) {
        return $(ButtonElement.class).caption(caption).first();
    }
}