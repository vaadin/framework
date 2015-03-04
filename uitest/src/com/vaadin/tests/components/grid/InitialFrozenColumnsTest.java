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

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class InitialFrozenColumnsTest extends MultiBrowserTest {
    @Test
    public void testInitialFrozenColumns() {
        setDebug(true);
        openTestURL();

        Assert.assertFalse("Notification was present",
                isElementPresent(NotificationElement.class));

        WebElement cell = $(GridElement.class).first().getCell(0, 0);
        assertTrue(cell.getAttribute("class").contains("frozen"));
    }
}
