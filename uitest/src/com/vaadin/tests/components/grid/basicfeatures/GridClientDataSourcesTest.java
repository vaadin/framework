/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridClientDataSourcesTest extends MultiBrowserTest {

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void normalRestishDatasource() throws Exception {
        selectMenuPath("DataSources", "RESTish", "Use");
        assertCellPresent("cell 0 #0");

        scrollToBottom();
        assertCellPresent("cell 99 #0");
        assertCellNotPresent("cell 100 #0");
    }

    @Test
    public void growOnRequestRestishDatasource() throws Exception {
        selectMenuPath("DataSources", "RESTish", "Use");
        selectMenuPath("DataSources", "RESTish", "Next request +10");

        scrollToBottom();
        /* second scroll needed because of scrollsize change after scrolling */
        scrollToBottom();

        assertCellPresent("cell 109 #1");
        assertCellNotPresent("cell 110 #1");
    }

    @Test
    public void shrinkOnRequestRestishDatasource() throws Exception {
        selectMenuPath("DataSources", "RESTish", "Use");
        scrollToBottom();

        selectMenuPath("DataSources", "RESTish", "Next request -10");
        scrollToTop();

        assertCellPresent("cell 0 #1");
    }

    @Test
    public void pushChangeRestishDatasource() throws Exception {
        selectMenuPath("DataSources", "RESTish", "Use");
        selectMenuPath("DataSources", "RESTish", "Push data change");
        assertCellPresent("cell 0 #1");
        assertCellNotPresent("cell 0 #0");
    }

    @Test
    public void growOnPushRestishDatasource() throws Exception {
        selectMenuPath("DataSources", "RESTish", "Use");
        selectMenuPath("DataSources", "RESTish", "Push data change +10");
        assertCellPresent("cell 0 #1");
        assertCellNotPresent("cell 0 #0");
        scrollToBottom();
        assertCellPresent("cell 109 #1");
    }

    @Test
    public void shrinkOnPushRestishDatasource() throws Exception {
        selectMenuPath("DataSources", "RESTish", "Use");
        scrollToBottom();

        selectMenuPath("DataSources", "RESTish", "Push data change -10");
        assertCellPresent("cell 89 #1");
        assertCellNotPresent("cell 89 #0");
        assertCellNotPresent("cell 99 #1");
        assertCellNotPresent("cell 99 #0");
    }

    private void assertCellPresent(String content) {
        assertNotNull(findByXPath("//td[text()='" + content + "']"));
    }

    private void assertCellNotPresent(String content) {
        assertNull(findByXPath("//td[text()='" + content + "']"));
    }

    private void scrollToTop() {
        scrollVerticallyTo(0);
    }

    private void scrollToBottom() {
        scrollVerticallyTo(9999);
    }

    private WebElement findByXPath(String string) {
        try {
            return findElement(By.xpath(string));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private void scrollVerticallyTo(int px) {
        executeScript("arguments[0].scrollTop = " + px, findVerticalScrollbar());
    }

    private Object executeScript(String script, Object args) {
        @SuppressWarnings("hiding")
        final WebDriver driver = getDriver();
        if (driver instanceof JavascriptExecutor) {
            final JavascriptExecutor je = (JavascriptExecutor) driver;
            return je.executeScript(script, args);
        } else {
            throw new IllegalStateException("current driver "
                    + getDriver().getClass().getName() + " is not a "
                    + JavascriptExecutor.class.getSimpleName());
        }
    }

    private WebElement findVerticalScrollbar() {
        return getDriver().findElement(
                By.xpath("//div[contains(@class, "
                        + "\"v-grid-scroller-vertical\")]"));
    }

    private void selectMenu(String menuCaption) {
        WebElement menuElement = getMenuElement(menuCaption);
        Dimension size = menuElement.getSize();
        new Actions(getDriver()).moveToElement(menuElement, size.width - 10,
                size.height / 2).perform();
    }

    private WebElement getMenuElement(String menuCaption) {
        return getDriver().findElement(
                By.xpath("//td[text() = '" + menuCaption + "']"));
    }

    private void selectMenuPath(String... menuCaptions) {
        new Actions(getDriver()).moveToElement(getMenuElement(menuCaptions[0]))
                .click().perform();
        for (int i = 1; i < menuCaptions.length - 1; ++i) {
            selectMenu(menuCaptions[i]);
            new Actions(getDriver()).moveByOffset(20, 0).perform();
        }
        new Actions(getDriver())
                .moveToElement(
                        getMenuElement(menuCaptions[menuCaptions.length - 1]))
                .click().perform();
    }

}
