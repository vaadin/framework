package com.vaadin.v7.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.data.bean.Person;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.MethodProperty.MethodException;
import com.vaadin.v7.ui.Grid;

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

        assertEquals(Integer.valueOf(1), itemId);

        assertEquals("There should be one item in the container", 1,
                container.size());

        assertEquals("Hello", container.getItem(itemId)
                .getItemProperty("myColumn").getValue());
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

        assertEquals(null, container.getItem(itemId).getItemProperty("myColumn")
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
        assertEquals("Hello", item.getItemProperty("myColumn").getValue());
        assertEquals(Integer.valueOf(3),
                item.getItemProperty("myOther").getValue());
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
        assertEquals("Default value should be used for removed column", "",
                item.getItemProperty("myColumn").getValue());
        assertEquals(Integer.valueOf(3),
                item.getItemProperty("myOther").getValue());
    }

    @Test
    public void testMultiplePropertiesAfterReorder() {
        grid.addColumn("myOther", Integer.class);

        grid.setColumnOrder("myOther", "myColumn");

        grid.addRow(Integer.valueOf(3), "Hello");

        Item item = container.getItem(Integer.valueOf(1));
        assertEquals("Hello", item.getItemProperty("myColumn").getValue());
        assertEquals(Integer.valueOf(3),
                item.getItemProperty("myOther").getValue());
    }

    @Test
    public void testInvalidType_NothingAdded() {
        try {
            grid.addRow(Integer.valueOf(5));

            // Can't use @Test(expect = Foo.class) since we also want to verify
            // state after exception was thrown
            fail("Adding wrong type should throw ClassCastException");
        } catch (IllegalArgumentException e) {
            assertEquals("No row should have been added", 0, container.size());
        }
    }

    @Test
    public void testUnsupportingContainer() {
        setContainerRemoveColumns(new BeanItemContainer<Person>(Person.class));
        try {

            grid.addRow("name");

            // Can't use @Test(expect = Foo.class) since we also want to verify
            // state after exception was thrown
            fail("Adding to BeanItemContainer container should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            assertEquals("No row should have been added", 0, container.size());
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

        assertEquals(1, container.size());

        assertEquals("name", container.getIdByIndex(0).getFirstName());
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
            fail("Adding row should throw MethodException");
        } catch (MethodException e) {
            assertEquals("Got the wrong exception", "name",
                    e.getCause().getMessage());

            assertEquals("There should be no rows in the container", 0,
                    container.size());
        }
    }

    private void setContainerRemoveColumns(
            BeanItemContainer<Person> container) {
        // Remove predefined column so we can change container
        grid.removeAllColumns();
        grid.setContainerDataSource(container);
        grid.removeAllColumns();
        grid.addColumn("firstName");
    }

}
