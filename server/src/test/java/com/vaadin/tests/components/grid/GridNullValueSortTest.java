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
package com.vaadin.tests.components.grid;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.MockUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;

public class GridNullValueSortTest {

    private Grid<TestClass> grid;
    private Column<TestClass, String> stringColumn;
    private Column<TestClass, Object> nonComparableColumn;

    @Test
    public void sortWithNullValues() {
        this.grid.sort(this.stringColumn);
        this.grid.sort(this.nonComparableColumn);
        this.grid.getDataCommunicator().beforeClientResponse(true);
    }

    @Before
    public void setup() {
        VaadinSession.setCurrent(null);
        this.grid = new Grid<TestClass>();
        this.stringColumn = this.grid.addColumn(bean -> bean.stringField);
        this.nonComparableColumn = this.grid.addColumn(bean -> bean.nonComparableField);

        this.grid.setItems(new TestClass(null, new Object()), new TestClass("something", null));
        new MockUI().setContent(grid);
    }

    private static class TestClass {

        private final String stringField;
        private final Object nonComparableField;

        TestClass(final String stringField, final Object nonComparableField) {
            this.stringField = stringField;
            this.nonComparableField = nonComparableField;
        }
    }
}
