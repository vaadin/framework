package com.vaadin.tests.tb3.newelements;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ServerClass;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/*
 Suggestions for new elemental api for Window
 */
@ServerClass("com.vaadin.ui.Window")
public class WindowElement extends com.vaadin.testbench.elements.WindowElement {

    private final String restoreBoxClass = "v-window-restorebox";
    private final String maximizeBoxClass = "v-window-maximizebox";

    public void restore() {
        if(isMaximized()) {
            getRestoreButton().click();
        } else {
            throw new AssertionError("Window is not maximized, cannot be restored.");
        }
    }

    private boolean isMaximized() {
        return isElementPresent(By.className(restoreBoxClass));
    }

    private WebElement getRestoreButton() {
        return this.findElement(By.className("v-window-restorebox"));
    }

    public void maximize() {
        if(!isMaximized()) {
            getMaximizeButton().click();
        } else {
            throw new AssertionError("Window is already maximized, cannot maximize.");
        }
    }

    private WebElement getMaximizeButton() {
        return this.findElement(By.className(maximizeBoxClass));
    }

    public void move(int xOffset, int yOffset) {
        Actions action = new Actions(getDriver());
        action.moveToElement(this.findElement(org.openqa.selenium.By.className("v-window-wrap")), 5, 5);
        action.clickAndHold();
        action.moveByOffset(xOffset, yOffset);
        action.release();
        action.build().perform();
    }
}
