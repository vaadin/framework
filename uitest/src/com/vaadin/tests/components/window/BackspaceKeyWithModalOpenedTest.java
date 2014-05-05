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
package com.vaadin.tests.components.window;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class BackspaceKeyWithModalOpenedTest extends MultiBrowserTest {

    @Test
    public void testWindowScrollbars() throws Exception {
        openTestURL();

        WebElement nextButton = driver.findElement(By
                .id(BackspaceKeyWithModalOpened.BTN_NEXT_ID));

        nextButton.click();

        WebElement openModalButton = driver.findElement(By
                .id(BackspaceKeyWithModalOpened.BTN_OPEN_MODAL_ID));

        openModalButton.click();

        // Try to send back actions to the browser.
        new Actions(getDriver()).sendKeys(Keys.BACK_SPACE).perform();

        WebElement textField = driver.findElement(By
                .id(BackspaceKeyWithModalOpened.TEXT_FIELD_IN_MODAL));

        // Try to delete characters in a text field.
        textField.sendKeys("textt");
        textField.sendKeys(Keys.BACK_SPACE);

        compareScreen(getScreenshotBaseName());
    }
}
