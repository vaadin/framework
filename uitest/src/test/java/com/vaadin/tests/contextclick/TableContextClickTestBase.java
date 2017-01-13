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

import com.vaadin.testbench.elements.TableElement;

public abstract class TableContextClickTestBase
        extends AbstractContextClickTest {

    @Override
    protected Class<?> getUIClass() {
        return TableContextClick.class;
    }

    protected void assertTypedContextClickListener(int startIndex) {
        contextClick($(TableElement.class).first().getCell(0, 0));

        assertEquals(
                (startIndex++)
                        + ". ContextClickEvent value: Lisa Schneider, propertyId: address, section: BODY",
                getLogRow(0));

        contextClick($(TableElement.class).first().getCell(0, 3));

        assertEquals(
                startIndex
                        + ". ContextClickEvent value: Lisa Schneider, propertyId: lastName, section: BODY",
                getLogRow(0));
    }
}
