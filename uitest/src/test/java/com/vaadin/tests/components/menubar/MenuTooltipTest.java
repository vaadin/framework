package com.vaadin.tests.components.menubar;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if tooltips on menu items obscure other items on the menu.
 *
 * @author Vaadin Ltd
 */
public class MenuTooltipTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingIE();
    }

    @Test
    public void testToolTipDelay() throws InterruptedException {
        openTestURL();

        final MenuBarElement menuBar = $(MenuBarElement.class).first();
        // Open menu bar and move on top of the first menu item
        new Actions(getDriver()).moveToElement(menuBar).click()
                .moveByOffset(0, menuBar.getSize().getHeight()).perform();

        // Make sure tooltip is outside of the screen
        assertThat(getTooltipElement().getLocation().getX(),
                is(lessThan(-1000)));

        // Wait for tooltip to open up
        sleep(3000);

        // Make sure it's the correct tooltip
        assertThat(getTooltipElement().getLocation().getX(),
                is(greaterThan(menuBar.getLocation().getX())));
        assertThat(getTooltipElement().getText(), is("TOOLTIP 1"));
    }
}
