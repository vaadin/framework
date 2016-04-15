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
package com.vaadin.tests.components.splitpanel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.shared.ui.splitpanel.HorizontalSplitPanelState;
import com.vaadin.testbench.elements.HorizontalSplitPanelElement;
import com.vaadin.testbench.elements.VerticalSplitPanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for duplicate primary style name in SplitPanel.
 * 
 * @author Vaadin Ltd
 */
public class SplitPanelDuplicateStyleNameTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-splitpanel-horizontal"));
    }

    @Test
    public void testHorizontalNoDuplicateStyleName() {
        HorizontalSplitPanelElement split = $(HorizontalSplitPanelElement.class)
                .first();
        String classNames = split.getAttribute("class");
        String primaryStyleName = new HorizontalSplitPanelState().primaryStyleName;
        assertEquals("Duplicate primary style name should not exist",
                classNames.indexOf(primaryStyleName),
                classNames.lastIndexOf(primaryStyleName));
    }

    @Test
    public void testVerticalNoDuplicateStyleName() {
        VerticalSplitPanelElement split = $(VerticalSplitPanelElement.class)
                .first();
        String classNames = split.getAttribute("class");
        String primaryStyleName = new HorizontalSplitPanelState().primaryStyleName;
        assertEquals("Duplicate primary style name should not exist",
                classNames.indexOf(primaryStyleName),
                classNames.lastIndexOf(primaryStyleName));
    }
}
