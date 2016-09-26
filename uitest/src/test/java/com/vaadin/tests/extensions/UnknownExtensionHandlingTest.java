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
package com.vaadin.tests.extensions;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.extensions.UnknownExtensionHandling.MyExtension;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class UnknownExtensionHandlingTest extends SingleBrowserTest {

    @Test
    public void testUnknownExtensionHandling() {
        setDebug(true);
        openTestURL();

        openDebugLogTab();

        Assert.assertTrue(
                hasMessageContaining(MyExtension.class.getCanonicalName()));

        Assert.assertFalse(hasMessageContaining("Hierachy claims"));
    }

    private boolean hasMessageContaining(String needle) {
        List<WebElement> elements = findElements(
                By.className("v-debugwindow-message"));
        for (WebElement messageElement : elements) {
            // Can't use getText() since element isn't scrolled into view
            String text = (String) executeScript(
                    "return arguments[0].textContent", messageElement);
            if (text.contains(needle)) {
                return true;
            }
        }

        return false;
    }

}
