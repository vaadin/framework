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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class UncloseableWindowCloseShortcutTest extends SingleBrowserTest {

    @Test
    public void testEscShortcut() {
        openTestURL();

        // Hit esc and verify that the Window was not closed.
        driver.findElement(By.cssSelector(".v-window-contents .v-scrollable"))
                .sendKeys(Keys.ESCAPE);
        assertTrue(
                "Uncloseable Window should remain open after esc is pressed.",
                isWindowOpen());
    }

    private boolean isWindowOpen() {
        return $(WindowElement.class).exists();
    }

}
