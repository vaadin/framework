/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.components.popupview;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Popup view with extension should not throw an exception.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class PopupViewWithExtensionTest extends MultiBrowserTest {

    @Test
    public void testPopupView() {
        setDebug(true);
        openTestURL();

        WebElement view = driver.findElement(By.className("v-popupview"));
        view.click();

        Assert.assertFalse(
                "Popup view with extension should not throw an exception. "
                        + "(Error notification window is shown).",
                isElementPresent(By.className("v-Notification-error")));
    }

}
