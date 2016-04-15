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

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowZIndexTest extends MultiBrowserTest {

    @Test
    public void removingUpdatesZIndices() throws IOException {
        openTestURL();

        WebElement addButton = driver.findElement(By
                .xpath("//span[contains(text(),'Add window')]"));
        WebElement closeButton = driver.findElement(By
                .xpath("//span[contains(text(),'Close window')]"));

        addButton.click();
        addButton.click();
        addButton.click();
        addButton.click();
        addButton.click();

        closeButton.click();
        closeButton.click();
        closeButton.click();

        addButton.click();
        addButton.click();
        addButton.click();
        addButton.click();

        compareScreen("stacked");

        WebElement window4 = driver.findElement(By
                .xpath("//*[contains(text(), 'Window 4')]"));
        new Actions(driver).moveToElement(window4, 1, 9).click().perform();

        compareScreen("win4-on-top");
    }
}
