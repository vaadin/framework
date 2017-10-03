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
package com.vaadin.tests.serialization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class LegacySerializerUITest extends SingleBrowserTest {

    @Test
    public void testInfinity() {
        openTestURL();
        WebElement html = findElement(By.className("gwt-HTML"));
        assertEquals("doubleInfinity: Infinity", html.getText());
        // Can't send infinity back, never have been able to
        assertEquals("1. doubleInfinity: null", getLogRow(0));
    }

}
