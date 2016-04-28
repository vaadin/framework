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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for Window attached to the UI with not content.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class WindowInUiWithNoContentTest extends MultiBrowserTest {

    @Test
    public void testWindowInEmptyUI() {
        openTestURL();

        WebElement window = driver.findElement(By.className("v-window"));
        String position = window.getCssValue("position");

        Assert.assertEquals("Window element has non-absolute position and "
                + "is broken in the UI", "absolute", position);
    }

}
