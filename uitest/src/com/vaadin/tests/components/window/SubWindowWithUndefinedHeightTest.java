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

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SubWindowWithUndefinedHeightTest extends MultiBrowserTest {

    @Test
    public void testUndefinedWindowSizeUpdate() throws IOException {
        openTestURL();

        $(ButtonElement.class).caption("click me").first().click();
        compareScreen("initial-tab1");

        $(TabSheetElement.class).first().openTab("tab 2");
        compareScreen("select-tab2");

        $(TabSheetElement.class).first().openTab("Tab 1");
        compareScreen("select-tab1");
    }
}
