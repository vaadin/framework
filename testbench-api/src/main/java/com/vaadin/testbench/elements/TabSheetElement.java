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

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.TabSheet")
public class TabSheetElement extends AbstractComponentContainerElement {

    // a locator that does not lead to selecting tabs from a contained inner
    // TabSheet (#13735)
    protected org.openqa.selenium.By byTabCell = By
            .xpath("./div/table/tbody/tr/td[contains(normalize-space(concat(' ', @class, ' ')),"
                    + "normalize-space(' v-tabsheet-tabitem '))]");
    private static org.openqa.selenium.By byCaption = By
            .className("v-captiontext");
    private static org.openqa.selenium.By byClosable = By
            .className("v-tabsheet-caption-close");

    /**
     * Gets a list of Tabs inside the Tab container.
     * 
     * @return List of tabs
     */
    public List<String> getTabCaptions() {
        List<String> tabCaptions = new ArrayList<String>();
        for (WebElement tab : findElements(byTabCell)) {
            tabCaptions.add(getTabCaption(tab));
        }
        return tabCaptions;
    }

    /**
     * Gets the number of tabs contained in this tab sheet.
     * 
     * @return Number of tabs.
     */
    public int getTabCount() {
        return findElements(byTabCell).size();
    }

    /**
     * Opens the tab with the given index.
     * 
     * @param index
     *            The zero-based index of the tab to be opened.
     */
    public void openTab(int index) {
        List<WebElement> tabs = findElements(byTabCell);
        if (index < 0 || index >= tabs.size()) {
            throw new NoSuchElementException(
                    "The tab sheet does not contain a tab with index " + index
                            + ".");
        }
        openTab(tabs.get(index));
    }

    /**
     * Opens a Tab that has caption equal to given tabCaption.
     * 
     * @param tabCaption
     *            Caption of the tab to be opened
     */
    public void openTab(String tabCaption) {
        for (WebElement tabCell : findElements(byTabCell)) {
            String currentCaption = getTabCaption(tabCell);
            boolean captionMatches = (currentCaption != null
                    && currentCaption.equals(tabCaption))
                    || (currentCaption == null && tabCaption == null);
            if (captionMatches) {
                openTab(tabCell);
                return;
            }
        }
        throw new NoSuchElementException(
                "Tab with caption " + tabCaption + " was not found.");
    }

    /**
     * Opens the given tab by clicking its caption text or icon. If the tab has
     * neither text caption nor icon, clicks at a fixed position.
     * 
     * @param tabCell
     *            The tab to be opened.
     */
    private void openTab(WebElement tabCell) {
        // Open the tab by clicking its caption text if it exists.
        List<WebElement> tabCaptions = tabCell.findElements(byCaption);
        if (tabCaptions.size() > 0) {
            tabCaptions.get(0).click();
            return;
        }
        // If no caption text was found, click the icon of the tab.
        List<WebElement> tabIcons = tabCell
                .findElements(By.className("v-icon"));
        if (tabIcons.size() > 0) {
            tabIcons.get(0).click();
            return;
        }
        // If neither text nor icon caption was found, click at a position that
        // is unlikely to close the tab.
        ((TestBenchElement) tabCell).click(10, 10);
    }

    /**
     * If the tab with given index is closable, closes it.
     * 
     * @param index
     *            The index of the tab to be closed
     */
    public void closeTab(int index) {
        List<WebElement> tabs = findElements(byTabCell);
        if (index < 0 || index >= tabs.size()) {
            throw new NoSuchElementException(
                    "The tab sheet does not contain a tab with index " + index
                            + ".");
        }
        WebElement tabCell = tabs.get(index);
        closeTab(tabCell);
    }

    /**
     * If tab with given caption is closable, closes it.
     * 
     * @param tabCaption
     *            Caption of the tab to be closed
     */
    public void closeTab(String tabCaption) {
        for (WebElement tabCell : findElements(byTabCell)) {
            String currentCaption = getTabCaption(tabCell);
            boolean captionMatches = (currentCaption != null
                    && currentCaption.equals(tabCaption))
                    || (currentCaption == null && tabCaption == null);
            if (captionMatches) {
                closeTab(tabCell);
                return;
            }
        }
    }

    /**
     * Closes the given tab if it is closable.
     * 
     * @param tabCell
     *            The tab to be closed
     */
    private void closeTab(WebElement tabCell) {
        try {
            tabCell.findElement(byClosable).click();
            // Going further causes a StaleElementReferenceException.
            return;
        } catch (NoSuchElementException e) {
            // Do nothing.
        }
    }

    /**
     * Gets TabSheet content and wraps it in given class.
     * 
     * @param clazz
     *            Components element class
     * @return TabSheet content wrapped in given class
     */
    public <T extends AbstractElement> T getContent(Class<T> clazz) {
        return TestBench.createElement(clazz,
                $$(AbstractComponentElement.class).first().getWrappedElement(),
                getCommandExecutor());
    }

    /**
     * Returns the caption text of the given tab. If the tab has no caption,
     * returns null.
     * 
     * @param tabCell
     *            A web element representing a tab, as given by
     *            findElements(byTabCell).get(index).
     * @return The caption of tabCell or null if tabCell has no caption.
     */
    private String getTabCaption(WebElement tabCell) {
        List<WebElement> captionElements = tabCell.findElements(byCaption);
        if (captionElements.size() == 0) {
            return null;
        } else {
            return captionElements.get(0).getText();
        }
    }
}
