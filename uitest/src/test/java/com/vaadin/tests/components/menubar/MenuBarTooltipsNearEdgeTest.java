package com.vaadin.tests.components.menubar;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if tooltips will render in the correct locations near the edges.
 *
 * @author Vaadin Ltd
 */
public class MenuBarTooltipsNearEdgeTest extends MultiBrowserTest {

    @Test
    public void testTooltipLocation() {
        openTestURL();

        final MenuBarElement menuBar = $(MenuBarElement.class).first();
        new Actions(getDriver()).moveToElement(menuBar).click()
                .moveByOffset(0, -40).perform();

        WebElement tooltip = getTooltipElement();
        assertTrue("Tooltip outside of the screen.",
                tooltip.getLocation().getX() > 0
                        && tooltip.getLocation().getY() > 0);
        assertThat("Tooltip too far to the right",
                tooltip.getLocation().getX() + tooltip.getSize().getWidth(),
                is(lessThan(menuBar.getLocation().getX()
                        + menuBar.getSize().getWidth() / 2)));
        assertThat("Tooltip too low on the screen",
                tooltip.getLocation().getY(),
                is(lessThan(menuBar.getLocation().getY())));
    }
}
