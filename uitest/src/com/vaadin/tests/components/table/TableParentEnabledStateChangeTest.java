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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TableParentEnabledStateChangeTest extends SingleBrowserTest {

    @Test
    public void tableEnabledShouldFollowParent() {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        TableRowElement row = table.getRow(0);

        ButtonElement button = $(ButtonElement.class).first();

        row.click();
        Assert.assertTrue(isSelected(row));

        // Disable
        button.click();
        Assert.assertTrue(isSelected(row));
        row.click(); // Should have no effect
        Assert.assertTrue(isSelected(row));

        // Enable
        button.click();
        Assert.assertTrue(isSelected(row));
        row.click(); // Should deselect
        Assert.assertFalse(isSelected(row));
    }

    private boolean isSelected(TableRowElement row) {
        return hasCssClass(row, "v-selected");
    }
}
