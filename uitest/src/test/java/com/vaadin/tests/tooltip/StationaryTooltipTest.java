package com.vaadin.tests.tooltip;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class StationaryTooltipTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // With IEDriver, the cursor seems to jump to default position after the
        // mouse move,
        // so we are not able to test the tooltip behaviour properly.
        return getBrowsersExcludingIE();
    }

    @Test
    public void tooltipShouldBeStationary() throws InterruptedException {
        openTestURL();

        Mouse mouse = getMouse();

        moveMouseToButtonUpperLeftCorner(mouse);
        sleep(3000); // wait for the tooltip to become visible
        int originalTooltipLocationX = getTooltipLocationX();

        moveMouseToButtonBottomRightCorner(mouse);
        int actualTooltipLocationX = getTooltipLocationX();

        assertThat(actualTooltipLocationX, is(greaterThan(0)));
        assertThat(actualTooltipLocationX, is(originalTooltipLocationX));
    }

    private Coordinates getButtonCoordinates() {
        return getCoordinates(getButtonElement());
    }

    private ButtonElement getButtonElement() {
        return $(ButtonElement.class).first();
    }

    private void moveMouseToButtonBottomRightCorner(Mouse mouse) {
        Coordinates buttonCoordinates = getButtonCoordinates();
        Dimension buttonDimensions = getButtonDimensions();

        mouse.mouseMove(buttonCoordinates, buttonDimensions.getWidth() - 1,
                buttonDimensions.getHeight() - 1);
    }

    private void moveMouseToButtonUpperLeftCorner(Mouse mouse) {
        Coordinates buttonCoordinates = getButtonCoordinates();

        mouse.mouseMove(buttonCoordinates, 0, 0);
    }

    private org.openqa.selenium.Dimension getButtonDimensions() {
        ButtonElement buttonElement = getButtonElement();

        return buttonElement.getWrappedElement().getSize();
    }

    private int getTooltipLocationX() {
        return getTooltipElement().getLocation().getX();
    }

}