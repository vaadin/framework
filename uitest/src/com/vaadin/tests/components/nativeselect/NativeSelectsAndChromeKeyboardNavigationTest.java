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
package com.vaadin.tests.components.nativeselect;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NativeSelectsAndChromeKeyboardNavigationTest extends
        MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.CHROME);
    }

    @Test
    public void testValueChangeListenerWithKeyboardNavigation()
            throws InterruptedException {
        setDebug(true);
        openTestURL();
        Thread.sleep(1000);
        menu("Component");
        menuSub("Listeners");
        menuSub("Value change listener");

        getDriver().findElement(By.tagName("body")).click();

        WebElement select = getDriver().findElement(By.tagName("select"));
        select.sendKeys(Keys.ARROW_DOWN);
        select.sendKeys(Keys.ARROW_DOWN);
        select.sendKeys(Keys.ARROW_DOWN);

        String bodytext = getDriver().findElement(By.tagName("body")).getText();

        Assert.assertTrue(bodytext.contains("new value: 'Item 1'"));
        Assert.assertTrue(bodytext.contains("new value: 'Item 2'"));
        Assert.assertTrue(bodytext.contains("new value: 'Item 3'"));

    }

    @Override
    protected Class<?> getUIClass() {
        return NativeSelects.class;
    }

    private void menuSub(String string) {
        getDriver().findElement(By.xpath("//span[text() = '" + string + "']"))
                .click();
        new Actions(getDriver()).moveByOffset(100, 0).build().perform();
    }

    private void menu(String string) {
        getDriver().findElement(By.xpath("//span[text() = '" + string + "']"))
                .click();

    }

}
