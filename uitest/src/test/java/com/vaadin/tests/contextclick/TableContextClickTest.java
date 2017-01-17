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
package com.vaadin.tests.contextclick;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.ButtonElement;

public class TableContextClickTest extends TableContextClickTestBase {

    @Test
    public void testBodyContextClickWithTypedListener() {
        addOrRemoveTypedListener();

        assertTypedContextClickListener(1);
    }

    @Test
    public void testHeaderContextClickWithTypedListener() {
        addOrRemoveTypedListener();

        contextClick($(TableElement.class).first().getHeaderCell(0));

        assertEquals(
                "1. ContextClickEvent value: address, propertyId: address, section: HEADER",
                getLogRow(0));

        contextClick($(TableElement.class).first().getHeaderCell(3));

        assertEquals(
                "2. ContextClickEvent value: lastName, propertyId: lastName, section: HEADER",
                getLogRow(0));
    }

    @Test
    public void testFooterContextClickWithTypedListener() {
        addOrRemoveTypedListener();

        contextClick($(TableElement.class).first().getFooterCell(0));

        assertEquals(
                "1. ContextClickEvent value: null, propertyId: address, section: FOOTER",
                getLogRow(0));

        contextClick($(TableElement.class).first().getFooterCell(3));

        assertEquals(
                "2. ContextClickEvent value: null, propertyId: lastName, section: FOOTER",
                getLogRow(0));
    }

    @Test
    public void testContextClickInEmptyTable() {
        addOrRemoveTypedListener();

        $(ButtonElement.class).caption("Remove all content").first().click();

        contextClick($(TableElement.class).first(), 100, 100);

        assertEquals(
                "1. ContextClickEvent value: , propertyId: null, section: BODY",
                getLogRow(0));
    }

}
