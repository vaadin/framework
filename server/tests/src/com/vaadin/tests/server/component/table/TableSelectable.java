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
package com.vaadin.tests.server.component.table;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Table;

/**
 * Tests for 'selectable' property of {@link Table} class.
 * 
 * @author Vaadin Ltd
 */
public class TableSelectable {

    @Test
    public void setSelectable_explicitSelectable_tableIsSelectable() {
        Table table = new Table();
        table.setSelectable(true);

        Assert.assertTrue(table.isSelectable());
    }

    @Test
    public void addValueChangeListener_explicitSelectable_tableIsSelectable() {
        TestTable table = new TestTable();
        table.addValueChangeListener(EasyMock
                .createMock(ValueChangeListener.class));

        Assert.assertTrue(table.isSelectable());
        Assert.assertTrue(table.markAsDirtyCalled);
    }

    @Test
    public void tableIsNotSelectableByDefult() {
        Table table = new Table();

        Assert.assertFalse(table.isSelectable());
    }

    @Test
    public void setSelectable_explicitNotSelectable_tableIsNotSelectable() {
        Table table = new Table();
        table.setSelectable(false);
        table.addValueChangeListener(EasyMock
                .createMock(ValueChangeListener.class));

        Assert.assertFalse(table.isSelectable());
    }

    private static final class TestTable extends Table {
        @Override
        public void markAsDirty() {
            markAsDirtyCalled = true;
        }

        private boolean markAsDirtyCalled;
    }
}
