/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.contextclick;

import com.vaadin.testbench.elements.TreeElement;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class TreeV8ContextClickTest extends AbstractContextClickTest {

    @Test
    public void testBodyContextClickWithTypedListener() {
        addOrRemoveTypedListener();

        TreeElement tree = $(TreeElement.class).first();
        contextClick(tree.getItem(0));

        assertEquals(
                "1. ContextClickEvent value: Granddad 0",
                getLogRow(0));

        tree.expand(0);
        tree.expand(2);
        contextClick(tree.getItem(6));

        assertEquals(
                "2. ContextClickEvent value: Son 0/1/3",
                getLogRow(0));
    }

    /**
     * Performs a context click on given element at coordinates 20, 10 followed
     * by a regular click. This prevents browser context menu from blocking
     * future operations.
     *
     * A smaller X offset might hit the resize handle of the previous cell that
     * overlaps with the next header cell.
     *
     * @param e
     *            web element
     */
    @Override
    protected void contextClick(WebElement e) {
        contextClick(e, 20, 10);
    }

}
