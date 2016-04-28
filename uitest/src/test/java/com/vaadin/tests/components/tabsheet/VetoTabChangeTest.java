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
package com.vaadin.tests.components.tabsheet;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class VetoTabChangeTest extends SingleBrowserTest {
    @Test
    public void testReselectTabAfterVeto() {
        openTestURL();

        TabSheetElement tabSheet = $(TabSheetElement.class).first();
        Assert.assertEquals("Tab 1 should be there by default", "Tab 1",
                getTabContent(tabSheet));

        tabSheet.openTab(1);

        Assert.assertEquals("Tab should not have changed", "Tab 1",
                getTabContent(tabSheet));

        tabSheet.openTab(0);
        Assert.assertEquals("Tab should still be there", "Tab 1",
                getTabContent(tabSheet));
    }

    private String getTabContent(TabSheetElement tabSheet) {
        return tabSheet.getContent(LabelElement.class).getText();
    }
}
