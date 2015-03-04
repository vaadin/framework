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
package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridSingleColumnTest extends MultiBrowserTest {

    @Test
    public void testHeaderIsVisible() {
        openTestURL();

        GridCellElement cell = $(GridElement.class).first().getHeaderCell(0, 0);
        Assert.assertTrue("No header available", cell.getText()
                .equalsIgnoreCase("header"));
    }

    @Test
    public void testScrollDidNotThrow() {
        setDebug(true);
        openTestURL();

        Assert.assertFalse("Exception when scrolling on init",
                isElementPresent(NotificationElement.class));
    }
}
