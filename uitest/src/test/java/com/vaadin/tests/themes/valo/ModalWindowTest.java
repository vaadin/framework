package com.vaadin.tests.themes.valo;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.ModalWindow;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ModalWindowTest extends SingleBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ModalWindow.class;
    }

    @Test
    public void modalAnimationsAreDisabled() {
        openTestURL("theme=tests-valo-disabled-animations");

        openModalWindow();

        WebElement modalityCurtain = findElement(
                By.className("v-window-modalitycurtain"));

        assertThat(modalityCurtain.getCssValue("-webkit-animation-name"),
                is("none"));
    }

    @Test
    public void modal_curtains_close_correctly() {
        openTestURL();

        openModalWindow();
        new Actions(getDriver())
                .moveToElement(findHeaderElement())
                .clickAndHold().moveByOffset(1, 1).perform();
        assertTrue(isElementPresent(By.className("v-window-draggingCurtain")));
        new Actions(getDriver()).sendKeys(findHeaderElement(), Keys.ESCAPE)
                .release().perform();
        verifyCurtainsNotPresent();

        openModalWindow();
        new Actions(getDriver())
                .moveToElement(findResizingElement())
                .clickAndHold().moveByOffset(1, 1).perform();
        assertTrue(isElementPresent(By.className("v-window-resizingCurtain")));
        new Actions(getDriver()).sendKeys(findResizingElement(), Keys.ESCAPE)
                .release().perform();
        verifyCurtainsNotPresent();
    }

    private void openModalWindow() {
        $(ButtonElement.class).get(1).click();
    }

    private WebElement findHeaderElement() {
        return findElement(By.className("v-window-header"));
    }

    private WebElement findResizingElement() {
        return findElement(By.className("v-window-resizebox"));
    }

    private void verifyCurtainsNotPresent() {
        assertFalse(isElementPresent(By.className("v-window-modalitycurtain")));
        assertFalse(isElementPresent(By.className("v-window-draggingCurtain")));
        assertFalse(isElementPresent(By.className("v-window-resizingCurtain")));
    }
}
