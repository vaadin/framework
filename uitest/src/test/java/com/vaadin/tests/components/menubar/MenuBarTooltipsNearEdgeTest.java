package com.vaadin.tests.components.menubar;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;

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
        Mouse mouse = ((HasInputDevices) getDriver()).getMouse();
        WebElement menu = $(MenuBarElement.class).first().getWrappedElement();
        Coordinates menuLocation = ((Locatable) menu).getCoordinates();
        mouse.click(menuLocation);
        mouse.mouseMove(menuLocation, 5, -40);
        WebElement tooltip = getTooltipElement();
        assertThat(tooltip.getLocation().x, is(lessThan(
                menuLocation.onPage().x - tooltip.getSize().getWidth())));
    }
}
