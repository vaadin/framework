package com.vaadin.tests.components.tabsheet;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TabSheetErrorTooltipTest extends MultiBrowserTest {

    @Test
    public void checkTooltips() throws IOException {
        openTestURL();

        assertTabHasNoTooltipNorError(0);

        assertTabHasTooltipAndError(1, "", "Error!");

        assertTabHasTooltipAndError(2, "This is a tab", "");

        assertTabHasTooltipAndError(3,
                "This tab has both an error and a description", "Error!");
    }

    private void assertTabHasTooltipAndError(int index, String tooltip,
            String errorMessage) {
        showTooltip(index);
        assertTooltip(tooltip);
        assertErrorMessage(errorMessage);
    }

    private void assertTabHasNoTooltipNorError(int index) {
        showTooltip(index);
        WebElement tooltip = getCurrentTooltip();

        assertThat(tooltip.getText(), is(""));

        WebElement errorMessage = getCurrentErrorMessage();
        assertThat(errorMessage.isDisplayed(), is(false));

    }

    private void showTooltip(int index) {
        Coordinates elementCoordinates = ((Locatable) getTab(index))
                .getCoordinates();
        Mouse mouse = ((HasInputDevices) getDriver()).getMouse();
        mouse.mouseMove(elementCoordinates);
    }

    private WebElement getTab(int index) {
        return vaadinElement(
                "/VTabsheet[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild["
                        + index + "]/domChild[0]");
    }

    private WebElement getCurrentTooltip() {
        return getDriver()
                .findElement(By.xpath("//div[@class='v-tooltip-text']"));
    }

    private WebElement getCurrentErrorMessage() {
        return getDriver().findElement(
                By.xpath("//div[contains(@class, 'v-errormessage')]"));
    }

    private void assertTooltip(String tooltip) {
        Assert.assertEquals(tooltip, getCurrentTooltip().getText());
    }

    private void assertErrorMessage(String message) {
        Assert.assertEquals(message, getCurrentErrorMessage().getText());
    }
}
