package com.vaadin.tests.components.menubar;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
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

        Coordinates elementCoordinates = getCoordinates(
                $(MenuBarElement.class).first());
        sleep(1000);

        Mouse mouse = ((HasInputDevices) getDriver()).getMouse();

        mouse.click(elementCoordinates);
        mouse.mouseMove(elementCoordinates, 15, 40);

        sleep(1000);

        assertThat(getTooltipElement().getLocation().getX(),
                is(lessThan(-1000)));

        sleep(3000);

        assertThat(getTooltipElement().getLocation().getX(),
                is(greaterThan(elementCoordinates.onPage().getX())));
        assertThat(getTooltipElement().getText(), is("TOOLTIP 1"));
    }
}
