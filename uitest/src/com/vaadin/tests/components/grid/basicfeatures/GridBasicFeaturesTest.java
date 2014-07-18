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

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.components.grid.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public abstract class GridBasicFeaturesTest extends MultiBrowserTest {

    @Override
    protected DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities dCap = super.getDesiredCapabilities();
        if (BrowserUtil.isIE(dCap)) {
            dCap.setCapability("requireWindowFocus", true);
        }
        return super.getDesiredCapabilities();
    }

    @Override
    protected Class<?> getUIClass() {
        return GridBasicFeatures.class;
    }

    protected void selectSubMenu(String menuCaption) {
        selectMenu(menuCaption);
        new Actions(getDriver()).moveByOffset(100, 0).build().perform();
    }

    protected void selectMenu(String menuCaption) {
        getDriver().findElement(
                By.xpath("//span[text() = '" + menuCaption + "']")).click();
    }

    protected void selectMenuPath(String... menuCaptions) {
        selectMenu(menuCaptions[0]);
        for (int i = 1; i < menuCaptions.length; i++) {
            selectSubMenu(menuCaptions[i]);
        }
    }

    protected GridElement getGridElement() {
        return $(GridElement.class).id("testComponent");
    }

    protected void scrollGridVerticallyTo(double px) {
        executeScript("arguments[0].scrollTop = " + px,
                getGridVerticalScrollbar());
    }

    protected List<TestBenchElement> getGridHeaderRowCells() {
        List<TestBenchElement> headerCells = new ArrayList<TestBenchElement>();
        for (int i = 0; i < getGridElement().getHeaderCount(); ++i) {
            headerCells.addAll(getGridElement().getHeaderCells(i));
        }
        return headerCells;
    }

    protected List<TestBenchElement> getGridFooterRowCells() {
        List<TestBenchElement> footerCells = new ArrayList<TestBenchElement>();
        for (int i = 0; i < getGridElement().getFooterCount(); ++i) {
            footerCells.addAll(getGridElement().getFooterCells(i));
        }
        return footerCells;
    }

    private Object executeScript(String script, WebElement element) {
        final WebDriver driver = getDriver();
        if (driver instanceof JavascriptExecutor) {
            final JavascriptExecutor je = (JavascriptExecutor) driver;
            return je.executeScript(script, element);
        } else {
            throw new IllegalStateException("current driver "
                    + getDriver().getClass().getName() + " is not a "
                    + JavascriptExecutor.class.getSimpleName());
        }
    }

    private WebElement getGridVerticalScrollbar() {
        return getDriver()
                .findElement(
                        By.xpath("//div[contains(@class, \"v-grid-scroller-vertical\")]"));
    }
}
