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
package com.vaadin.tests.navigator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NavigatorViewBlocksBackButtonActionTest extends MultiBrowserTest {

    @Test
    public void testIfConfirmBack() {
        openTestURL();

        // keep URL of main view
        final String initialUrl = driver.getCurrentUrl();

        // do it 2 times to verify that login is not broken after first time
        for (int i = 0; i < 2; i++) {
            // go to prompted view
            WebElement button = $(ButtonElement.class).first();
            button.click();

            // click back button
            driver.navigate().back();

            // confirm "go back by clicking confirm button
            WebElement buttonConfirmView = $(ButtonElement.class).first();
            buttonConfirmView.click();

            // verify we are in main view and url is correct
            waitForElementPresent(By
                    .id(NavigatorViewBlocksBackButtonAction.LABEL_MAINVIEW_ID));
            String currentUrl = driver.getCurrentUrl();
            assertEquals(
                    "Current URL should be equal to initial main view URL",
                    initialUrl, currentUrl);
        }
    }

    @Test
    public void testIfCancelBack() {
        openTestURL();

        // go to prompted view
        WebElement button = $(ButtonElement.class).first();
        button.click();

        // keep URL of prompted view
        final String initialPromptedUrl = driver.getCurrentUrl();

        // click back button
        driver.navigate().back();

        // verify url is correct (is not changed)
        waitForElementPresent(By
                .id(NavigatorViewBlocksBackButtonAction.LABEL_PROMPTEDVIEW_ID));
        String currentUrl = driver.getCurrentUrl();
        assertEquals(
                "Current URL should be equal to initial prompted view URL",
                initialPromptedUrl, currentUrl);

        WebElement cancelButton = driver.findElement(By
                .className("v-window-closebox"));

        // click cancel button
        cancelButton.click();

        // verify we leave in prompted view and url is correct
        waitForElementPresent(By
                .id(NavigatorViewBlocksBackButtonAction.LABEL_PROMPTEDVIEW_ID));
        currentUrl = driver.getCurrentUrl();
        assertEquals(
                "Current URL should be equal to initial prompted view URL",
                initialPromptedUrl, currentUrl);
    }
}
