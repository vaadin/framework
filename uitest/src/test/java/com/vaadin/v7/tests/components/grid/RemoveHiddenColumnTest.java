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
package com.vaadin.v7.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class RemoveHiddenColumnTest extends SingleBrowserTest {

    @Test
    public void removeHiddenColumnInEmptyGrid() {
        openTestURL("debug");
        removeColumns();
    }

    @Test
    public void removeHiddenColumnInPopulatedGrid() {
        openTestURL("debug");
        ButtonElement add = $(ButtonElement.class).id("add");
        add.click();
        removeColumns();

    }

    private void removeColumns() {
        ButtonElement remove = $(ButtonElement.class).id("remove");
        remove.click();
        Assert.assertEquals("1. Removed column 'First Name' (hidden)",
                getLogRow(0));
        assertNoErrorNotifications();

        remove.click();
        Assert.assertEquals("2. Removed column 'Last Name'", getLogRow(0));
        assertNoErrorNotifications();
        remove.click();
        Assert.assertEquals("3. Removed column 'Email' (hidden)", getLogRow(0));
        assertNoErrorNotifications();
        remove.click();
        Assert.assertEquals("4. Removed column 'Age'", getLogRow(0));
        assertNoErrorNotifications();

    }

}
