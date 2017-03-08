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
package com.vaadin.tests.elements.table;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableElementContextMenuTest extends MultiBrowserTest {

    private TableElement tableElement;

    @Before
    public void init() {
        openTestURL();
        tableElement = $(TableElement.class).first();
    }

    @Test
    public void tableContextMenu_menuOpenFetchMenu_contextMenuFetchedCorrectly() {
        tableElement.contextClick();
        TableElement.ContextMenuElement contextMenu = tableElement
                .getContextMenu();
        Assert.assertNotNull(
                "There is no context menu open by tableElement.contextClick()",
                contextMenu);
    }

    @Test(expected = NoSuchElementException.class)
    public void tableContextMenu_menuClosedfetchContextMenu_exceptionThrown() {
        tableElement.getContextMenu();
    }
}
