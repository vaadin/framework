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
package com.vaadin.tests.components.textfield;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class AutomaticImmediateTest extends MultiBrowserTest {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.tb3.AbstractTB3Test#getUIClass()
     */
    @Override
    protected Class<?> getUIClass() {
        return AutomaticImmediate.class;
    }

    @Test
    public void test() {
        openTestURL();

        WebElement field = getDriver().findElement(
                By.id(AutomaticImmediate.FIELD));

        WebElement toggle = getDriver().findElement(
                By.xpath("//input[@type = 'checkbox']"));

        WebElement explicitFalseButton = getDriver().findElement(
                By.id(AutomaticImmediate.EXPLICIT_FALSE));

        WebElement hitServerButton = getDriver().findElement(
                By.id(AutomaticImmediate.BUTTON));

        String string = getRandomString();
        field.sendKeys(string + Keys.ENTER);

        // Non immediate, just the initial server side valuechange
        assertLastLog("1. fireValueChange");

        hitServerButton.click();

        // No value change, but value sent to server
        assertLastLog("2. fireValueChange");

        // listener on -> immediate on
        toggle.click();

        string = getRandomString();
        String delSequence = "" + Keys.BACK_SPACE + Keys.BACK_SPACE;
        field.sendKeys(delSequence + string + Keys.ENTER);
        assertLastLog("4. Value changed: " + string);

        // listener off -> immediate off
        String lastvalue = string;
        toggle.click();
        string = getRandomString();
        field.sendKeys(delSequence + string + Keys.ENTER);
        // No new value change should happen...
        assertLastLog("4. Value changed: " + lastvalue);
        hitServerButton.click();
        // ... but server should receive value with roundtrip
        assertLastLog("5. fireValueChange");

        // explicitly non immediate, but with listener
        explicitFalseButton.click();
        toggle.click();

        string = getRandomString();
        field.sendKeys(delSequence + string + Keys.ENTER);
        // non immediate, no change...
        assertLastLog("5. fireValueChange");
        // ... until server round trip
        hitServerButton.click();
        assertLastLog("7. Value changed: " + string);

    }

    private String getRandomString() {
        String string = RandomStringUtils.randomAlphanumeric(2);
        return string;
    }

    private void assertLastLog(String string) {
        String text = getDriver().findElement(By.id("Log_row_0")).getText();
        Assert.assertEquals(string, text);
    }

}
