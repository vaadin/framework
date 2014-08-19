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

import static com.vaadin.tests.components.window.ComboboxScrollableWindow.COMBOBOX_ID;
import static com.vaadin.tests.components.window.ComboboxScrollableWindow.WINDOW_ID;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class ComboboxScrollableWindowTest extends MultiBrowserTest {

    @Test
    public void testWindowScrollbars() throws Exception {
        openTestURL();

        WebElement window = driver.findElement(By.id(WINDOW_ID));
        WebElement scrollableElement = window.findElement(By
                .className("v-scrollable"));
        TestBenchElementCommands scrollable = testBenchElement(scrollableElement);
        scrollable.scroll(1000);
        WebElement comboBox = driver.findElement(By.id(COMBOBOX_ID));
        WebElement selectButton = driver.findElement(By
                .className("v-filterselect-button"));
        selectButton.click();

        // Wait for the browser before taking a screenshot
        Thread.sleep(1000);
        compareScreen(getScreenshotBaseName());

    }

}
