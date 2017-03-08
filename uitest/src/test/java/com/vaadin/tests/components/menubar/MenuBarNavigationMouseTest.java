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
package com.vaadin.tests.components.menubar;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MenuBarNavigationMouseTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return MenuBarNavigation.class;
    }

    @Test
    public void testMenuBarMouseNavigation() throws Exception {
        openTestURL();
        MenuBarElement menuBar = $(MenuBarElement.class).first();
        menuBar.clickItem("File", "Export..", "As PDF...");
        Assert.assertEquals("1. MenuItem File/Export../As PDF... selected",
                getLogRow(0));
        menuBar.clickItem("Edit", "Copy");
        Assert.assertEquals("2. MenuItem Edit/Copy selected", getLogRow(0));
        menuBar.clickItem("Help");
        Assert.assertEquals("3. MenuItem Help selected", getLogRow(0));
        menuBar.clickItem("File", "Exit");
        Assert.assertEquals("4. MenuItem File/Exit selected", getLogRow(0));
    }
}
