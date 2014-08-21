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

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class CtrlShiftMultiselectTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> browsers = super.getBrowsersToTest();
        // Shift + click doesn't select all rows correctly on these browsers
        browsers.remove(Browser.FIREFOX.getDesiredCapabilities());
        browsers.remove(Browser.PHANTOMJS.getDesiredCapabilities());

        return browsers;
    }

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Test
    public void testSelectionRangeDragging() throws IOException {
        openTestURL();
        clickRow(3);
        new Actions(driver).keyDown(Keys.SHIFT).perform();
        clickRow(8);
        new Actions(driver).keyUp(Keys.SHIFT).perform();
        dragRows(5, 700, 0);
        compareScreen("draggedMultipleRows");
        new Actions(driver).release().perform();
    }

    private void clickRow(int index) {
        List<WebElement> rows = getAllRows();
        rows.get(index).click();
    }

    private void dragRows(int dragIdx, int xOffset, int yOffset) {
        List<WebElement> rows = getAllRows();
        new Actions(driver).moveToElement(rows.get(dragIdx)).clickAndHold()
                .moveByOffset(5, 0).perform();
        new Actions(driver).moveByOffset(xOffset - 5, yOffset).perform();

    }

    private List<WebElement> getAllRows() {
        WebElement table = vaadinElement("/VVerticalLayout[0]/VVerticalLayout[0]/VScrollTable[0]");
        return table.findElements(By.cssSelector(".v-table-table tr"));

    }
}
