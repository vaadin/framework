package com.vaadin.tests.tb3.newelements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

/*
 Suggestions for new elemental api for Window
 */
@ServerClass("com.vaadin.ui.Window")
public class WindowElement extends com.vaadin.testbench.elements.WindowElement {

    private final String restoreBoxClass = "v-window-restorebox";
    private final String maximizeBoxClass = "v-window-maximizebox";
    private final String closeBoxClass = "v-window-closebox";

    public void restore() {
        if (isMaximized()) {
            getRestoreButton().click();
        } else {
            throw new AssertionError(
                    "Window is not maximized, cannot be restored.");
        }
    }

    private boolean isMaximized() {
        return isElementPresent(By.className(restoreBoxClass));
    }

    private WebElement getRestoreButton() {
        return findElement(By.className("v-window-restorebox"));
    }

    public void maximize() {
        if (!isMaximized()) {
            getMaximizeButton().click();
        } else {
            throw new AssertionError(
                    "Window is already maximized, cannot maximize.");
        }
    }

    private WebElement getMaximizeButton() {
        return findElement(By.className(maximizeBoxClass));
    }

    public void move(int xOffset, int yOffset) {
        Actions action = new Actions(getDriver());
        action.moveToElement(
                findElement(org.openqa.selenium.By.className("v-window-wrap")),
                5, 5);
        action.clickAndHold();
        action.moveByOffset(xOffset, yOffset);
        action.release();
        action.build().perform();
    }

    /**
     * @return the caption of the window
     */
    @Override
    public String getCaption() {
        return findElement(By.className("v-window-header")).getText();
    }

    private WebElement getCloseButton() {
        return findElement(By.className(closeBoxClass));
    }

    public void close() {
        getCloseButton().click();

    }
}
