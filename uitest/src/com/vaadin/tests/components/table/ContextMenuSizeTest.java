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
package com.vaadin.tests.components.table;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for context menu position and size.
 * 
 * @author Vaadin Ltd
 */
public class ContextMenuSizeTest extends MultiBrowserTest {

    @Test
    public void testContextMenuBottom() {
        openTestURL();

        WebElement menu = openContextMenu();
        int initialHeight = menu.getSize().getHeight();
        int y = menu.getLocation().getY();

        closeContextMenu();

        Dimension size = getDriver().manage().window().getSize();

        int windowHeight = y + initialHeight - 10;
        if (isElementPresent(By.className("v-ff"))) {
            // FF does something wrong with window height
            windowHeight = y + initialHeight + 90;
        } else if (isElementPresent(By.className("v-ch"))) {
            // Chrome does something wrong with window height
            windowHeight = y + initialHeight + 20;
        }
        getDriver().manage().window()
                .setSize(new Dimension(size.getWidth(), windowHeight));

        menu = openContextMenu();
        int height = menu.getSize().getHeight();

        Assert.assertEquals("Context menu height has been changed after "
                + "window height update which allows to show context as is",
                initialHeight, height);

    }

    @Test
    public void testContextMenuSize() {
        openTestURL();

        WebElement menu = openContextMenu();
        int initialHeight = menu.getSize().getHeight();
        int y = menu.getLocation().getY();

        closeContextMenu();

        Dimension size = getDriver().manage().window().getSize();

        int windowHeight = initialHeight - 10;
        if (isElementPresent(By.className("v-ch"))) {
            // Chrome does something wrong with window height
            windowHeight = y + initialHeight;
        }
        getDriver().manage().window()
                .setSize(new Dimension(size.getWidth(), windowHeight));

        menu = openContextMenu();
        int height = menu.getSize().getHeight();

        Assert.assertTrue(
                "Context menu height has not been descreased after "
                        + "window height update to value lower than context menu initial height",
                initialHeight > height);
        closeContextMenu();

        getDriver().manage().window()
                .setSize(new Dimension(size.getWidth(), size.getHeight()));
        menu = openContextMenu();
        height = menu.getSize().getHeight();
        Assert.assertEquals("Context menu height has not been reset after "
                + "window height reset", initialHeight, height);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> browsers = new ArrayList<DesiredCapabilities>(
                getAllBrowsers());

        // context menu doesn't work in phantom JS and works wired with IE8 and
        // selenium.
        browsers.remove(Browser.PHANTOMJS.getDesiredCapabilities());
        browsers.remove(Browser.IE8.getDesiredCapabilities());
        return browsers;
    }

    private WebElement openContextMenu() {
        Actions actions = new Actions(getDriver());
        actions.contextClick(findElement(By.className("v-table-cell-wrapper")));
        actions.perform();
        return findElement(By.className("v-contextmenu"));
    }

    private void closeContextMenu() {
        Actions actions = new Actions(getDriver());
        actions.click().build().perform();
    }

}
