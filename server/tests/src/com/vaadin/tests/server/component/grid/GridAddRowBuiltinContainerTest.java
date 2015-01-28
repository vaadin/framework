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
package com.vaadin.tests.server.component.grid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty.MethodException;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;

public class GridAddRowBuiltinContainerTest {
    Grid grid = new Grid();
    Container.Indexed container;

    @Before
    public void setUp() {
        container = grid.getContainerDataSource();

        grid.addColumn("myColumn");
    }

    @Test
    public void testSimpleCase() {
        Object itemId = grid.addRow("Hello");

        Assert.assertEquals(Integer.valueOf(1), itemId);

        Assert.assertEquals("There should be one item in the container", 1,
                container.size());

        Assert.assertEquals("Hello",
                container.getItem(itemId).getItemProperty("myColumn")
                        .getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParameter() {
        // cast to Object[] to distinguish from one null varargs value
        grid.addRow((Object[]) null);
    }

    @Test
    public void testNullValue() {
        // cast to Object to distinguish from a null varargs array
        Object itemId = grid.addRow((Object) null);

        Assert.assertEquals(null,
                container.getItem(itemId).getItemProperty("myColumn")
                        .getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddInvalidType() {
        grid.addRow(Integer.valueOf(5));
    }

    @Test
    public void testMultipleProperties() {
        grid.addColumn("myOther", Integer.class);

        Object itemId = grid.addRow("Hello", Integer.valueOf(3));

        Item item = container.getItem(itemId);
        Assert.assertEquals("Hello", item.getItemProperty("myColumn")
                .getValue());
        Assert.assertEquals(Integer.valueOf(3), item.getItemProperty("myOther")
                .getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPropertyAmount() {
        grid.addRow("Hello", Integer.valueOf(3));
    }

    @Test
    public void testRemovedColumn() {
        grid.addColumn("myOther", Integer.class);
        grid.removeColumn("myColumn");

        grid.addRow(Integer.valueOf(3));

        Item item = container.getItem(Integer.valueOf(1));
        Assert.assertEquals("Default value should be used for removed column",
                "", item.getItemProperty("myColumn").getValue());
        Assert.assertEquals(Integer.valueOf(3), item.getItemProperty("myOther")
                .getValue());
    }

    @Test
    public void testMultiplePropertiesAfterReorder() {
        grid.addColumn("myOther", Integer.class);

        grid.setColumnOrder("myOther", "myColumn");

        grid.addRow(Integer.valueOf(3), "Hello");

        Item item = container.getItem(Integer.valueOf(1));
        Assert.assertEquals("Hello", item.getItemProperty("myColumn")
                .getValue());
        Assert.assertEquals(Integer.valueOf(3), item.getItemProperty("myOther")
                .getValue());
    }

    @Test
    public void testInvalidType_NothingAdded() {
        try {
            grid.addRow(Integer.valueOf(5));

            // Can't use @Test(expect = Foo.class) since we also want to verify
            // state after exception was thrown
            Assert.fail("Adding wrong type should throw ClassCastException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("No row should have been added", 0,
                    container.size());
        }
    }

    @Test
    public void testUnsupportingContainer() {
        setContainerRemoveColumns(new BeanItemContainer<Person>(Person.class));
        try {

            grid.addRow("name");

            // Can't use @Test(expect = Foo.class) since we also want to verify
            // state after exception was thrown
            Assert.fail("Adding to BeanItemContainer container should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            Assert.assertEquals("No row should have been added", 0,
                    container.size());
        }
    }

    @Test
    public void testCustomContainer() {
        BeanItemContainer<Person> container = new BeanItemContainer<Person>(
                Person.class) {
            @Override
            public Object addItem() {
                BeanItem<Person> item = addBean(new Person());
                return getBeanIdResolver().getIdForBean(item.getBean());
            }
        };

        setContainerRemoveColumns(container);

        grid.addRow("name");

        Assert.assertEquals(1, container.size());

        Assert.assertEquals("name", container.getIdByIndex(0).getFirstName());
    }

    @Test
    public void testSetterThrowing() {
        BeanItemContainer<Person> container = new BeanItemContainer<Person>(
                Person.class) {
            @Override
            public Object addItem() {
                BeanItem<Person> item = addBean(new Person() {
                    @Override
                    public void setFirstName(String firstName) {
                        if ("name".equals(firstName)) {
                            throw new RuntimeException(firstName);
                        } else {
                            super.setFirstName(firstName);
                        }
                    }
                });
                return getBeanIdResolver().getIdForBean(item.getBean());
            }
        };

        setContainerRemoveColumns(container);

        try {

            grid.addRow("name");

            // Can't use @Test(expect = Foo.class) since we also want to verify
            // state after exception was thrown
            Assert.fail("Adding row should throw MethodException");
        } catch (MethodException e) {
            Assert.assertEquals("Got the wrong exception", "name", e.getCause()
                    .getMessage());

            Assert.assertEquals("There should be no rows in the container", 0,
                    container.size());
        }
    }

    private void setContainerRemoveColumns(BeanItemContainer<Person> container) {
        // Remove predefined column so we can change container
        grid.removeAllColumns();
        grid.setContainerDataSource(container);
        grid.removeAllColumns();
        grid.addColumn("firstName");
    }

}
