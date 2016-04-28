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

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 */
public class DateFieldIsValidTest extends MultiBrowserTest {

    @Test
    public void testInvalidText() throws Exception {
        openTestURL();

        waitForElementVisible(By.id("Log"));
        waitForElementVisible(By.className("v-datefield"));
        WebElement dateTextbox = $(DateFieldElement.class).first().findElement(
                By.className("v-textfield"));
        ButtonElement button = $(ButtonElement.class).first();

        dateTextbox.sendKeys("01/01/01", Keys.TAB);
        assertLogText("1. valueChange: value: 01/01/01, is valid: true");
        button.click();
        assertLogText("2. buttonClick: value: 01/01/01, is valid: true");

        dateTextbox.sendKeys("lala", Keys.TAB);
        assertLogText("3. valueChange: value: null, is valid: false");
        button.click();
        assertLogText("4. buttonClick: value: null, is valid: false");

        dateTextbox.clear();
        dateTextbox.sendKeys("02/02/02", Keys.TAB);
        assertLogText("5. valueChange: value: 02/02/02, is valid: true");
        button.click();
        assertLogText("6. buttonClick: value: 02/02/02, is valid: true");
    }

    private void assertLogText(String expected) throws Exception {
        String text = findElement(By.vaadin("PID_SLog_row_0")).getText();
        Assert.assertTrue("Expected '" + expected + "' found '" + text + "'",
                text.equals(expected));
    }
}
