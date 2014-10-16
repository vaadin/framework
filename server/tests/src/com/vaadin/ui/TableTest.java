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
package com.vaadin.ui;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.BeanItemContainerGenerator;

public class TableTest {

    Table table;

    @Before
    public void init() {
        table = new Table();
    }

    @Test
    public void initiallyEmpty() {
        Assert.assertTrue(table.isEmpty());
    }

    @Test
    public void emptyAfterClearSingleSelect() {
        table.setContainerDataSource(BeanItemContainerGenerator
                .createContainer(100));
        Assert.assertTrue(table.isEmpty());
        Object first = table.getContainerDataSource().getItemIds().iterator()
                .next();
        table.setValue(first);
        Assert.assertEquals(first, table.getValue());
        Assert.assertFalse(table.isEmpty());
        table.clear();
        Assert.assertEquals(null, table.getValue());
        Assert.assertTrue(table.isEmpty());
    }

    @Test
    public void emptyAfterClearMultiSelect() {
        table.setMultiSelect(true);
        table.setContainerDataSource(BeanItemContainerGenerator
                .createContainer(100));

        Assert.assertTrue(table.isEmpty());
        Assert.assertArrayEquals(new Object[] {},
                ((Collection) table.getValue()).toArray());

        Object first = table.getContainerDataSource().getItemIds().iterator()
                .next();
        table.select(first);
        Assert.assertArrayEquals(new Object[] { first },
                ((Collection) table.getValue()).toArray());
        Assert.assertFalse(table.isEmpty());

        table.clear();
        Assert.assertArrayEquals(new Object[] {},
                ((Collection) table.getValue()).toArray());
        Assert.assertTrue(table.isEmpty());
    }

}
