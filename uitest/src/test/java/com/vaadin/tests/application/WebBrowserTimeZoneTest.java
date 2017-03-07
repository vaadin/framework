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
package com.vaadin.tests.application;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class WebBrowserTimeZoneTest extends MultiBrowserTest {
    @Test
    public void testBrowserTimeZoneInfo() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertLabelText("Browser raw offset", "7200000");
        assertLabelText("Browser to Europe/Helsinki offset difference", "0");
        assertLabelText("Browser could be in Helsinki", "Yes");
    }

    private void assertLabelText(String caption, String expected) {
        String actual = $(LabelElement.class).caption(caption).first()
                .getText();
        Assert.assertEquals(
                String.format("Unexpected text in label '%s',", caption),
                expected, actual);
    }
}
