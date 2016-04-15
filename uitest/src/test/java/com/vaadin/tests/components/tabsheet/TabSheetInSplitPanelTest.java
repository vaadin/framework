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

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TabSheetInSplitPanelTest extends MultiBrowserTest {

    @Test
    public void ensureNoScrollbars() {
        openTestURL();
        TabSheetElement ts = $(TabSheetElement.class).first();
        List<WebElement> scrollables = ts.findElements(By
                .xpath("//*[contains(@class,'v-scrollable')]"));
        for (WebElement scrollable : scrollables) {
            assertNoHorizontalScrollbar(scrollable,
                    "Element should not have a horizontal scrollbar");
            assertNoVerticalScrollbar(scrollable,
                    "Element should not have a vertical scrollbar");
        }
    }

}
