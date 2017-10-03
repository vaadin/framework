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
package com.vaadin.tests.push;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class PushToggleComponentVisibilityTest extends SingleBrowserTest {

    private static final String HIDE = "hide";

    @Test
    public void ensureComponentVisible() {
        openTestURL();

        $(ButtonElement.class).id(HIDE).click();
        assertEquals("Please wait", $(LabelElement.class).first().getText());

        waitUntil(driver -> isElementPresent(ButtonElement.class));
        $(ButtonElement.class).id(HIDE).click();
        assertEquals("Please wait", $(LabelElement.class).first().getText());
    }
}
