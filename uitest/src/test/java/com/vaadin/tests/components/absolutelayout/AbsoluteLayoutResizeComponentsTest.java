package com.vaadin.tests.components.absolutelayout;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Optional;

/**
 * Tests for component positioning after width changes from defined to relative
 * and relative to defined
 */
public class AbsoluteLayoutResizeComponentsTest extends MultiBrowserTest {

    @Test
    public void testFullWithComponentWithRightAlignmentShouldMoveRightWhenSettingAbsoluteWidth() {
        openTestURL();

        String componentId = "expanding-panel";

        Optional<WebElement> panelWrapper = getComponentWrapper(componentId);

        Assert.assertTrue("No wrapper element found for expanding panel [ID: "
                + componentId + "]", panelWrapper.isPresent());

        String left = panelWrapper.get().getCssValue("left");
        Assert.assertEquals(
                "Component wrapper was missing left:0; from its css positioning",
                "0px", left);

        WebElement panelComponent = findElement(By.id(componentId));
        Assert.assertEquals("Panel is not on the left side of the screen", 0,
                panelComponent.getLocation().getX());

        // Click button to change component size
        $(ButtonElement.class).id(componentId + "-button").click();

        // Not testing "left" here as testing for AUTO doesn't work in chrome
        // version 40 which calculates the actual left value, testing width
        // instead of the wrapper instead
        String width = panelWrapper.get().getCssValue("width");
        Assert.assertEquals("Width was more that it should have been.", "250px",
                width);

        Assert.assertNotEquals("Panel is still on the left side of the screen",
                0, panelComponent.getLocation().getX());
    }

    @Test
    public void testDefinedWidthComponentShouldExpandToFullWidth() {
        openTestURL();

        String componentId = "small-panel";

        Optional<WebElement> panelWrapper = getComponentWrapper(componentId);

        Assert.assertTrue(
                "No wrapper element found for panel [ID: " + componentId + "]",
                panelWrapper.isPresent());

        String width = panelWrapper.get().getCssValue("width");
        Assert.assertEquals("Width was more that it should have been.", "250px",
                width);

        WebElement panelComponent = findElement(By.id(componentId));
        Assert.assertNotEquals(
                "Panel is positioned to the left side of the screen", 0,
                panelComponent.getLocation().getX());

        // Click button to change component size
        $(ButtonElement.class).id(componentId + "-button").click();

        String left = panelWrapper.get().getCssValue("left");
        Assert.assertEquals(
                "Component wrapper was missing left:0; from its css positioning",
                "0px", left);

        width = panelWrapper.get().getCssValue("width");
        Assert.assertNotEquals("Width hasn't changed from the initial value.",
                "250px", width);

        Assert.assertEquals("Panel is not on the left side of the screen", 0,
                panelComponent.getLocation().getX());
    }

    @Test
    public void testDefinedWidthAbsoluteLayoutToFullWidthShouldBeFullWidth() {
        openTestURL();

        String componentId = "absolute-expanding";

        Optional<WebElement> panelWrapper = getComponentWrapper(componentId);

        Assert.assertTrue("No wrapper element found for AbsoluteLayout [ID: "
                + componentId + "].", panelWrapper.isPresent());

        String width = panelWrapper.get().getCssValue("width");
        Assert.assertEquals("Width was more that it should have been.", "250px",
                width);

        WebElement panelComponent = findElement(By.id(componentId));
        Assert.assertNotEquals(
                "Panel is positioned to the left side of the screen", 0,
                panelComponent.getLocation().getX());

        // Click button to change component size
        $(ButtonElement.class).id(componentId + "-button").click();

        String left = panelWrapper.get().getCssValue("left");
        Assert.assertEquals(
                "Component wrapper was missing left:0; from its css positioning",
                "0px", left);

        width = panelWrapper.get().getCssValue("width");
        Assert.assertNotEquals("Width hasn't changed from the initial value.",
                "250px", width);

        Assert.assertEquals("Panel is not on the left side of the screen", 0,
                panelComponent.getLocation().getX());
    }

    /**
     * Search for the AbsoluteLayout wrapper element that contains component for
     * componentId
     *
     * @param componentId
     *            Id of component contained in Wrapper component
     * @return WrapperComponent or null
     */
    private Optional<WebElement> getComponentWrapper(String componentId) {
        WebElement panelWrapper = null;

        for (WebElement wrapper : findElements(
                By.className("v-absolutelayout-wrapper"))) {
            // Check if this wrapper contains element with the wanted id.
            if (!wrapper.findElements(By.id(componentId)).isEmpty()) {
                panelWrapper = wrapper;
                break;
            }
        }
        return Optional.ofNullable(panelWrapper);
    }
}
