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
package com.vaadin.tests.components.datefield;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class PopupDateFieldValueChangeEventsTest extends MultiBrowserTest {

    @Test
    public void tb2test() throws Exception {
        openTestURL();
        getPopUpButton().click();
        getCalendarDayElement(2, 1).click();
        Assert.assertEquals("1. Value changes: 1", getLogRow(0));
        getPopUpButton().click();
        new Select(getHoursSelect()).selectByValue("01");
        new Select(getMinutesSelect()).selectByValue("02");
        new Select(getSecondsSelect()).selectByValue("03");
        new Actions(driver).sendKeys(getSecondsSelect(), Keys.ENTER).perform();
        $(LabelElement.class).first().click();
        Assert.assertEquals("2. Value changes: 2", getLogRow(0));
        getResolutionSelect().selectByText("Month");
        getPopUpButton().click();
        getNextMonthButton().click();
        getNextYearButton().click();
        getNextMonthButton().click();
        getNextMonthButton().sendKeys(Keys.ENTER);
        Assert.assertEquals("3. Value changes: 3", getLogRow(0));
    }

    public WebElement getPopUpButton() {
        DateFieldElement datefield = $(DateFieldElement.class).first();
        return datefield.findElement((By.className("v-datefield-button")));
    }

    public WebElement getCalendarDayElement(int row, int col) {
        return findElement(By
                .xpath("//table[@id='PID_VAADIN_POPUPCAL']/tbody/tr[2]/td/table/tbody/tr["
                        + (row + 2) + "]/td[" + (col + 2) + "]/span"));
    }

    public WebElement getHoursSelect() {
        return findElement(By
                .xpath("//table[@id='PID_VAADIN_POPUPCAL']/tbody/tr[3]/td/div/select"));
    }

    public WebElement getMinutesSelect() {
        return findElement(By
                .xpath("//table[@id='PID_VAADIN_POPUPCAL']/tbody/tr[3]/td/div/select[2]"));
    }

    public WebElement getSecondsSelect() {
        return findElement(By
                .xpath("//table[@id='PID_VAADIN_POPUPCAL']/tbody/tr[3]/td/div/select[3]"));
    }

    public NativeSelectElement getResolutionSelect() {
        return $(NativeSelectElement.class).first();
    }

    public WebElement getNextMonthButton() {
        return findElement((By.className("v-button-nextmonth")));
    }

    public WebElement getNextYearButton() {
        return findElement((By.className("v-button-nextyear")));
    }
}
