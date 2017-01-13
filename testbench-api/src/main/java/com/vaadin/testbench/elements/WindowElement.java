/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.testbench.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elementsbase.ServerClass;

/**
 * Element API for the Window class.
 *
 * Note that parts of the Window element API has limitations on IE8 and Phantom.
 */
@ServerClass("com.vaadin.ui.Window")
public class WindowElement extends PanelElement {

    private static final String HEADER_CLASS = "v-window-header";
    private static final String RESTORE_BOX_CLASS = "v-window-restorebox";
    private static final String MAXIMIZE_BOX_CLASS = "v-window-maximizebox";
    private static final String CLOSE_BOX_CLASS = "v-window-closebox";

    /**
     * Clicks the close button of the window
     */
    public void close() {
        getCloseButton().click();
    }

    /**
     * Clicks the restore button of the window
     */
    public void restore() {
        if (isMaximized()) {
            getRestoreButton().click();
        } else {
            throw new IllegalStateException(
                    "Window is not maximized, cannot be restored.");
        }
    }

    /**
     * Check if this window is currently maximized
     */
    public boolean isMaximized() {
        return isElementPresent(By.className(RESTORE_BOX_CLASS));
    }

    /**
     * Clicks the maximize button of the window
     */
    public void maximize() {
        if (!isMaximized()) {
            getMaximizeButton().click();
        } else {
            throw new IllegalStateException(
                    "Window is already maximized, cannot maximize.");
        }
    }

    private WebElement getRestoreButton() {
        return findElement(By.className(RESTORE_BOX_CLASS));
    }

    private WebElement getMaximizeButton() {
        return findElement(By.className(MAXIMIZE_BOX_CLASS));
    }

    private WebElement getCloseButton() {
        return findElement(By.className(CLOSE_BOX_CLASS));
    }

    /**
     * @return the caption of the window
     */
    @Override
    public String getCaption() {
        return findElement(By.className(HEADER_CLASS)).getText();
    }

    /**
     * Moves the window by given offset.
     *
     * @param xOffset
     *            x offset
     * @param yOffset
     *            y offset
     */
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
}
