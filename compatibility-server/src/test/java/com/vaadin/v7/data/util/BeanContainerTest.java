package com.vaadin.v7.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.AbstractBeanContainer.BeanIdResolver;

public class BeanContainerTest extends AbstractBeanContainerTestBase {

    protected static class PersonNameResolver
            implements BeanIdResolver<String, Person> {

        @Override
        public String getIdForBean(Person bean) {
            return bean != null ? bean.getName() : null;
        }

    }

    protected static class NullResolver
            implements BeanIdResolver<String, Person> {

        @Override
        public String getIdForBean(Person bean) {
            return null;
        }

    }

    private Map<String, ClassName> nameToBean = new LinkedHashMap<String, ClassName>();

    private BeanContainer<String, ClassName> getContainer() {
        return new BeanContainer<String, ClassName>(ClassName.class);
    }

    @Before
    public void setUp() {
        nameToBean.clear();

        for (int i = 0; i < sampleData.length; i++) {
            ClassName className = new ClassName(sampleData[i], i);
            nameToBean.put(sampleData[i], className);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initializeContainer(Container container) {
        BeanContainer<String, ClassName> beanItemContainer = (BeanContainer<String, ClassName>) container;

        beanItemContainer.removeAllItems();

        for (Entry<String, ClassName> entry : nameToBean.entrySet()) {
            beanItemContainer.addItem(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected boolean isFilteredOutItemNull() {
        return false;
    }

    @Test
    public void testGetType_existingProperty_typeReturned() {
        BeanContainer<String, ClassName> container = getContainer();
        assertEquals(
                "Unexpected type is returned for property 'simpleName'",
                String.class, container.getType("simpleName"));
    }

    @Test
    public void testGetType_notExistingProperty_nullReturned() {
        BeanContainer<String, ClassName> container = getContainer();
        assertNull("Not null type is returned for property ''",
                container.getType(""));
    }

    @Test
    public void testBasicOperations() {
        testBasicContainerOperations(getContainer());
    }

    @Test
    public void testFiltering() {
        testContainerFiltering(getContainer());
    }

    @Test
    public void testSorting() {
        testContainerSorting(getContainer());
    }

    @Test
    public void testSortingAndFiltering() {
        testContainerSortingAndFiltering(getContainer());
    }

    // duplicated from parent class and modified - adding items to
    // BeanContainer differs from other containers
    @Test
    public void testContainerOrdered() {
        BeanContainer<String, String> container = new BeanContainer<String, String>(
                String.class);

        String id = "test1";

        Item item = container.addItem(id, "value");
        assertNotNull(item);

        assertEquals(id, container.firstItemId());
        assertEquals(id, container.lastItemId());

        // isFirstId
        assertTrue(container.isFirstId(id));
        assertTrue(container.isFirstId(container.firstItemId()));
        // isLastId
        assertTrue(container.isLastId(id));
        assertTrue(container.isLastId(container.lastItemId()));

        // Add a new item before the first
        // addItemAfter
        String newFirstId = "newFirst";
        item = container.addItemAfter(null, newFirstId, "newFirstValue");
        assertNotNull(item);
        assertNotNull(container.getItem(newFirstId));

        // isFirstId
        assertTrue(container.isFirstId(newFirstId));
        assertTrue(container.isFirstId(container.firstItemId()));
        // isLastId
        assertTrue(container.isLastId(id));
        assertTrue(container.isLastId(container.lastItemId()));

        // nextItemId
        assertEquals(id, container.nextItemId(newFirstId));
        assertNull(container.nextItemId(id));
        assertNull(container.nextItemId("not-in-container"));

        // prevItemId
        assertEquals(newFirstId, container.prevItemId(id));
        assertNull(container.prevItemId(newFirstId));
        assertNull(container.prevItemId("not-in-container"));

        // addItemAfter(IDTYPE, IDTYPE, BT)
        String newSecondItemId = "newSecond";
        item = container.addItemAfter(newFirstId, newSecondItemId,
                "newSecondValue");
        // order is now: newFirstId, newSecondItemId, id
        assertNotNull(item);
        assertNotNull(container.getItem(newSecondItemId));
        assertEquals(id, container.nextItemId(newSecondItemId));
        assertEquals(newFirstId, container.prevItemId(newSecondItemId));

        // addItemAfter(IDTYPE, IDTYPE, BT)
        String fourthId = "id of the fourth item";
        Item fourth = container.addItemAfter(newFirstId, fourthId,
                "fourthValue");
        // order is now: newFirstId, fourthId, newSecondItemId, id
        assertNotNull(fourth);
        assertEquals(fourth, container.getItem(fourthId));
        assertEquals(newSecondItemId, container.nextItemId(fourthId));
        assertEquals(newFirstId, container.prevItemId(fourthId));

        // addItemAfter(IDTYPE, IDTYPE, BT)
        String fifthId = "fifth";
        Item fifth = container.addItemAfter(null, fifthId, "fifthValue");
        // order is now: fifthId, newFirstId, fourthId, newSecondItemId, id
        assertNotNull(fifth);
        assertEquals(fifth, container.getItem(fifthId));
        assertEquals(newFirstId, container.nextItemId(fifthId));
        assertNull(container.prevItemId(fifthId));

    }

    // TODO test Container.Indexed interface operation - testContainerIndexed()?

    @Test
    public void testAddItemAt() {
        BeanContainer<String, String> container = new BeanContainer<String, String>(
                String.class);

        container.addItem("id1", "value1");
        // id1
        container.addItemAt(0, "id2", "value2");
        // id2, id1
        container.addItemAt(1, "id3", "value3");
        // id2, id3, id1
        container.addItemAt(container.size(), "id4", "value4");
        // id2, id3, id1, id4

        assertNull(container.addItemAt(-1, "id5", "value5"));
        assertNull(container.addItemAt(container.size() + 1, "id6", "value6"));

        assertEquals(4, container.size());
        assertEquals("id2", container.getIdByIndex(0));
        assertEquals("id3", container.getIdByIndex(1));
        assertEquals("id1", container.getIdByIndex(2));
        assertEquals("id4", container.getIdByIndex(3));
    }

    @Test
    public void testUnsupportedMethods() {
        BeanContainer<String, Person> container = new BeanContainer<String, Person>(
                Person.class);
        container.addItem("John", new Person("John"));

        try {
            container.addItem();
            fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }

        try {
            container.addItem(null);
            fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }

        try {
            container.addItemAfter(null, null);
            fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }

        try {
            container.addItemAfter(new Person("Jane"));
            fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }

        try {
            container.addItemAt(0);
            fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }

        try {
            container.addItemAt(0, new Person("Jane"));
            fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }

        try {
            container.addContainerProperty("lastName", String.class, "");
            fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }

        assertEquals(1, container.size());
    }

    @Test
    public void testRemoveContainerProperty() {
        BeanContainer<String, Person> container = new BeanContainer<String, Person>(
                Person.class);
        container.setBeanIdResolver(new PersonNameResolver());
        container.addBean(new Person("John"));

        assertEquals("John",
                container.getContainerProperty("John", "name").getValue());
        assertTrue(container.removeContainerProperty("name"));
        assertNull(container.getContainerProperty("John", "name"));

        assertNotNull(container.getItem("John"));
        // property removed also from item
        assertNull(container.getItem("John").getItemProperty("name"));
    }

    @Test
    public void testAddNullBeans() {
        BeanContainer<String, Person> container = new BeanContainer<String, Person>(
                Person.class);

        assertNull(container.addItem("id1", null));
        assertNull(container.addItemAfter(null, "id2", null));
        assertNull(container.addItemAt(0, "id3", null));

        assertEquals(0, container.size());
    }

    @Test
    public void testAddNullId() {
        BeanContainer<String, Person> container = new BeanContainer<String, Person>(
                Person.class);

        Person john = new Person("John");

        assertNull(container.addItem(null, john));
        assertNull(container.addItemAfter(null, null, john));
        assertNull(container.addItemAt(0, null, john));

        assertEquals(0, container.size());
    }

    @Test
    public void testEmptyContainer() {
        BeanContainer<String, Person> container = new BeanContainer<String, Person>(
                Person.class);

        assertNull(container.firstItemId());
        assertNull(container.lastItemId());

        assertEquals(0, container.size());

        // could test more about empty container
    }

    @Test
    public void testAddBeanWithoutResolver() {
        BeanContainer<String, Person> container = new BeanContainer<String, Person>(
                Person.class);

        try {
            container.addBean(new Person("John"));
            fail();
        } catch (IllegalStateException e) {
            // should get exception
        }
        try {
            container.addBeanAfter(null, new Person("Jane"));
            fail();
        } catch (IllegalStateException e) {
            // should get exception
        }
        try {
            container.addBeanAt(0, new Person("Jack"));
            fail();
        } catch (IllegalStateException e) {
            // should get exception
        }
        try {
            container
                    .addAll(Arrays.asList(new Person[] { new Person("Jack") }));
            fail();
        } catch (IllegalStateException e) {
            // should get exception
        }

        assertEquals(0, container.size());
    }

    @Test
    public void testAddAllWithNullItemId() {
        BeanContainer<String, Person> container = new BeanContainer<String, Person>(
                Person.class);
        // resolver that returns null as item id
        container.setBeanIdResolver(
                new BeanIdResolver<String, AbstractBeanContainerTestBase.Person>() {

                    @Override
                    public String getIdForBean(Person bean) {
                        return bean.getName();
                    }
                });

        List<Person> persons = new ArrayList<Person>();
        persons.add(new Person("John"));
        persons.add(new Person("Marc"));
        persons.add(new Person(null));
        persons.add(new Person("foo"));

        try {
            container.addAll(persons);
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }

        container.removeAllItems();
        persons.remove(2);
        container.addAll(persons);
        assertEquals(3, container.size());
    }

    @Test
    public void testAddBeanWithNullResolver() {
        BeanContainer<String, Person> container = new BeanContainer<String, Person>(
                Person.class);
        // resolver that returns null as item id
        container.setBeanIdResolver(new NullResolver());

        try {
            container.addBean(new Person("John"));
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            container.addBeanAfter(null, new Person("Jane"));
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            container.addBeanAt(0, new Person("Jack"));
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }

        assertEquals(0, container.size());
    }

    @Test
    public void testAddBeanWithResolver() {
        BeanContainer<String, Person> container = new BeanContainer<String, Person>(
                Person.class);
        container.setBeanIdResolver(new PersonNameResolver());

        assertNotNull(container.addBean(new Person("John")));
        assertNotNull(container.addBeanAfter(null, new Person("Jane")));
        assertNotNull(container.addBeanAt(0, new Person("Jack")));

        container.addAll(Arrays.asList(
                new Person[] { new Person("Jill"), new Person("Joe") }));

        assertTrue(container.containsId("John"));
        assertTrue(container.containsId("Jane"));
        assertTrue(container.containsId("Jack"));
        assertTrue(container.containsId("Jill"));
        assertTrue(container.containsId("Joe"));
        assertEquals(3, container.indexOfId("Jill"));
        assertEquals(4, container.indexOfId("Joe"));
        assertEquals(5, container.size());
    }

    @Test
    public void testAddNullBeansWithResolver() {
        BeanContainer<String, Person> container = new BeanContainer<String, Person>(
                Person.class);
        container.setBeanIdResolver(new PersonNameResolver());

        assertNull(container.addBean(null));
        assertNull(container.addBeanAfter(null, null));
        assertNull(container.addBeanAt(0, null));

        assertEquals(0, container.size());
    }

    @Test
    public void testAddBeanWithPropertyResolver() {
        BeanContainer<String, Person> container = new BeanContainer<String, Person>(
                Person.class);
        container.setBeanIdProperty("name");

        assertNotNull(container.addBean(new Person("John")));
        assertNotNull(container.addBeanAfter(null, new Person("Jane")));
        assertNotNull(container.addBeanAt(0, new Person("Jack")));

        container.addAll(Arrays.asList(
                new Person[] { new Person("Jill"), new Person("Joe") }));

        assertTrue(container.containsId("John"));
        assertTrue(container.containsId("Jane"));
        assertTrue(container.containsId("Jack"));
        assertTrue(container.containsId("Jill"));
        assertTrue(container.containsId("Joe"));
        assertEquals(3, container.indexOfId("Jill"));
        assertEquals(4, container.indexOfId("Joe"));
        assertEquals(5, container.size());
    }

    @Test
    public void testAddNestedContainerProperty() {
        BeanContainer<String, NestedMethodPropertyTest.Person> container = new BeanContainer<String, NestedMethodPropertyTest.Person>(
                NestedMethodPropertyTest.Person.class);
        container.setBeanIdProperty("name");

        container.addBean(new NestedMethodPropertyTest.Person("John",
                new NestedMethodPropertyTest.Address("Ruukinkatu 2-4", 20540)));

        assertTrue(container.addNestedContainerProperty("address.street"));
        assertEquals("Ruukinkatu 2-4", container
                .getContainerProperty("John", "address.street").getValue());
    }

    @Test
    public void testNestedContainerPropertyWithNullBean() {
        BeanContainer<String, NestedMethodPropertyTest.Person> container = new BeanContainer<String, NestedMethodPropertyTest.Person>(
                NestedMethodPropertyTest.Person.class);
        container.setBeanIdProperty("name");

        container.addBean(new NestedMethodPropertyTest.Person("John", null));
        assertTrue(container
                .addNestedContainerProperty("address.postalCodeObject"));
        assertTrue(container.addNestedContainerProperty("address.street"));
        // the nested properties added with allowNullBean setting should return
        // null
        assertNull(container.getContainerProperty("John", "address.street")
                .getValue());
    }

}
