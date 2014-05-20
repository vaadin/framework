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

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldFastForwardTest extends MultiBrowserTest {

    @Test
    public void testFastForwardOnRightMouseClick() throws Exception {
        openTestURL();
        String firstMonth = getSelectedMonth();
        WebElement nextMonthButton = driver.findElement(By
                .className("v-button-nextmonth"));

        // Click and hold left mouse button to start fast forwarding.
        new Actions(driver).clickAndHold(nextMonthButton).perform();
        sleep(1000);

        // Right click and release the left button.

        new Actions(driver).contextClick(nextMonthButton)
                .release(nextMonthButton).perform();

        // Now the fast forwarding should be ended, get the expected month.
        String expectedMonth = getSelectedMonth();

        // Make browser context menu disappear, since it will crash IE
        $(VerticalLayoutElement.class).first().click();

        Assert.assertFalse("Month did not change during fast forward",
                firstMonth.equals(expectedMonth));

        // Wait for a while.
        Thread.sleep(1000);

        // Verify that we didn't fast forward any further after the left button
        // was released.
        String actualMonth = getSelectedMonth();
        assertEquals(expectedMonth, actualMonth);
    }

    private String getSelectedMonth() {
        return driver.findElement(
                By.className("v-inline-datefield-calendarpanel-month"))
                .getText();
    }

}
