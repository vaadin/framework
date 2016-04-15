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
package com.vaadin.tests.components.uitest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests UI scrolling.
 * 
 * @author Vaadin Ltd
 */
public class UIScrollingTest extends MultiBrowserTest {

    @Override
    protected boolean usePersistentHoverForIE() {
        return false;
    }

    @Test
    public void testScrolling() throws IOException, InterruptedException {
        openTestURL();

        List<ButtonElement> buttons = $(ButtonElement.class).all();
        buttons.get(0).click();
        sleep(100);

        buttons.get(1).click();

        waitForElementPresent(By.className("v-Notification"));

        NotificationElement notification = $(NotificationElement.class).first();
        assertEquals("Scrolled to 1000 px",
                notification.findElement(By.tagName("h1")).getText());

        // attempt to close the notification
        notification.close();

        WebElement ui = findElement(By.className("v-ui"));
        testBenchElement(ui).scroll(1020);

        buttons.get(1).click();

        waitForElementPresent(By.className("v-Notification"));

        notification = $(NotificationElement.class).first();
        assertEquals("Scrolled to 1020 px",
                notification.findElement(By.tagName("h1")).getText());

        notification.close();

    }
}
