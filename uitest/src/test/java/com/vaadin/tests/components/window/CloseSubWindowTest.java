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
package com.vaadin.tests.components.window;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CloseSubWindowTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        openSubWindow();
    }

    @Test
    public void testClosingFromClickHandler() throws Exception {
        $(WindowElement.class).$(ButtonElement.class).first().click();
        assertLogText();
    }

    @Test
    public void testClosingFromTitleBar() throws Exception {
        $(WindowElement.class).first()
                .findElement(By.className("v-window-closebox")).click();
        assertLogText();
    }

    @Test
    public void testClosingByRemovingFromUI() throws Exception {
        $(WindowElement.class).$(ButtonElement.class).get(1).click();
        assertLogText();
    }

    private void openSubWindow() {
        $(ButtonElement.class).id("opensub").click();
    }

    private void assertLogText() {
        Assert.assertEquals("Unexpected log contents,",
                "1. Window 'Sub-window' closed", getLogRow(0));
    }
}
