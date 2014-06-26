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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SelectAllConstantViewportTest extends MultiBrowserTest {

    @Test
    public void testViewportUnchangedwithMultiSel() throws IOException {
        openTestURL();

        CheckBoxElement checkbox = $(CheckBoxElement.class).first();

        WebElement row = $(TableElement.class).first().getCell(190, 0);
        final WebElement scrollPositionDisplay = getDriver().findElement(
                By.className("v-table-scrollposition"));
        waitUntilNot(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return scrollPositionDisplay.isDisplayed();
            }
        }, 10);

        int rowLocation = row.getLocation().getY();

        // use click x,y with non-zero offset to actually toggle the checkbox.
        // (#13763)
        checkbox.click(5, 5);
        int newRowLocation = row.getLocation().getY();

        assertThat(newRowLocation, is(rowLocation));

    }

    @Test
    public void testViewportChangedwithKeyboardSel() throws IOException {
        openTestURL();

        WebElement cell = $(TableElement.class).first().getCell(190, 0);
        final WebElement scrollPositionDisplay = getDriver().findElement(
                By.className("v-table-scrollposition"));
        waitUntilNot(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return scrollPositionDisplay.isDisplayed();
            }
        }, 10);

        int rowLocation = cell.getLocation().getY();

        // select downwards with shift (#14094)

        cell.click();

        final WebElement row = getDriver().findElement(
                By.className("v-selected"));

        assertThat(row.getAttribute("class"), containsString("selected"));

        // for some reason phantomJS does not support keyboard actions

        Actions action = new Actions(getDriver());
        action.keyDown(Keys.SHIFT)
                .sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ARROW_DOWN,
                        Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ARROW_DOWN,
                        Keys.ARROW_DOWN).keyUp(Keys.SHIFT).build().perform();

        int newRowLocation = cell.getLocation().getY();

        assertThat(newRowLocation, is(not(rowLocation)));

    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // phantomJS does not support keyboard actions
        return getBrowsersExcludingPhantomJS();
    }
}
