/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.components.customlayout;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class OverflowAutoFixTest extends MultiBrowserTest {
    @Test
    public void testRestoreOverflowHidden() throws InterruptedException {
        openTestURL();

        click("run-button-one");

        assertElementCssValueEquals("first-scrollbar", "overflow", "scroll");
        assertElementCssValueEquals("second-scrollbar", "overflow-x", "hidden");
        assertElementCssValueEquals("third-scrollbar", "overflow-y", "hidden");
    }

    @Test
    public void testRestoreOverflowOther() throws InterruptedException {
        openTestURL();

        click("run-button-two");

        assertElementCssValueEquals("first-scrollbar", "overflow", "visible");
        assertElementCssValueEquals("second-scrollbar", "overflow-x", "scroll");
        assertElementCssValueEquals("third-scrollbar", "overflow-y", "auto");
    }

    private void click(String className) {
        findElement(By.className(className)).click();
    }

    private void assertElementCssValueEquals(String className,
            String propertyName, String expected) {
        Assert.assertEquals(
                String.format(
                        "Unexpected value for property '%s' on element '%s',",
                        propertyName, className),
                expected,
                findElement(By.className(className)).getCssValue(propertyName));
    }
}
