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
package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableColumnAddAndResizeTest extends MultiBrowserTest {

    @Test
    public void testAddAndResizeColumn() {
        setDebug(true);
        openTestURL();

        $(ButtonElement.class).caption("Add and Resize").first().click();
        assertFalse("Error notification present.", $(NotificationElement.class)
                .exists());
        assertEquals("Unexpected column width. ", 200, $(TableElement.class)
                .first().getCell(0, 1).getSize().getWidth());
    }
}
