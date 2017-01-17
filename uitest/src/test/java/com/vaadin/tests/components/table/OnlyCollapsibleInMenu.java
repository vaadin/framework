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
package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import com.vaadin.v7.shared.ui.table.CollapseMenuContent;

public class OnlyCollapsibleInMenu extends SingleBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return Tables.class;
    }

    @Test
    public void testOnlyCollapsibleInMenu() {
        openTestURL();
        TableElement table = $(TableElement.class).first();

        selectMenuPath("Component", "Columns", "Property 3", "Collapsible");
        table.getCollapseMenuToggle().click();
        Assert.assertEquals("Property 3 should still be in the context menu",
                "Property 3", table.getContextMenu().getItem(2).getText());

        selectMenuPath("Component", "Features", "Collapsible menu content",
                CollapseMenuContent.COLLAPSIBLE_COLUMNS.toString());
        table.getCollapseMenuToggle().click();
        Assert.assertEquals("Property 3 should not be in the context menu",
                "Property 4", table.getContextMenu().getItem(2).getText());

        selectMenuPath("Component", "Features", "Collapsible menu content",
                CollapseMenuContent.ALL_COLUMNS.toString());
        table.getCollapseMenuToggle().click();
        Assert.assertEquals("Property 3 should again  be in the context menu",
                "Property 3", table.getContextMenu().getItem(2).getText());
    }

}
