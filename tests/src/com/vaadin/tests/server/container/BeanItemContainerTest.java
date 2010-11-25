package com.vaadin.tests.server.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.EasyMock;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;

/**
 * Test basic functionality of BeanItemContainer.
 * 
 * Most sorting related tests are in {@link BeanItemContainerSortTest}.
 */
public class BeanItemContainerTest extends AbstractContainerTest {

    // basics from the common container test

    public class ClassName {
        // field names match constants in parent test class
        private String fullyQualifiedName;
        private String simpleName;
        private String reverseFullyQualifiedName;
        private Integer idNumber;

        public ClassName(String fullyQualifiedName, Integer idNumber) {
            this.fullyQualifiedName = fullyQualifiedName;
            simpleName = AbstractContainerTest
                    .getSimpleName(fullyQualifiedName);
            reverseFullyQualifiedName = reverse(fullyQualifiedName);
            this.idNumber = idNumber;
        }

        public String getFullyQualifiedName() {
            return fullyQualifiedName;
        }

        public void setFullyQualifiedName(String fullyQualifiedName) {
            this.fullyQualifiedName = fullyQualifiedName;
        }

        public String getSimpleName() {
            return simpleName;
        }

        public void setSimpleName(String simpleName) {
            this.simpleName = simpleName;
        }

        public String getReverseFullyQualifiedName() {
            return reverseFullyQualifiedName;
        }

        public void setReverseFullyQualifiedName(
                String reverseFullyQualifiedName) {
            this.reverseFullyQualifiedName = reverseFullyQualifiedName;
        }

        public Integer getIdNumber() {
            return idNumber;
        }

        public void setIdNumber(Integer idNumber) {
            this.idNumber = idNumber;
        }
    }

    private Map<String, ClassName> nameToBean = new LinkedHashMap<String, ClassName>();

    private BeanItemContainer<ClassName> getContainer() {
        return new BeanItemContainer<ClassName>(ClassName.class);
    }

    @Override
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
        BeanItemContainer<ClassName> beanItemContainer = (BeanItemContainer<ClassName>) container;

        beanItemContainer.removeAllItems();

        Iterator<ClassName> it = nameToBean.values().iterator();
        while (it.hasNext()) {
            beanItemContainer.addBean(it.next());
        }
    }

    @Override
    protected void validateContainer(Container container,
            Object expectedFirstItemId, Object expectedLastItemId,
            Object itemIdInSet, Object itemIdNotInSet,
            boolean checkGetItemNull, int expectedSize) {
        Object notInSet = nameToBean.get(itemIdNotInSet);
        if (notInSet == null && itemIdNotInSet != null) {
            notInSet = new ClassName(String.valueOf(itemIdNotInSet), 9999);
        }
        super.validateContainer(container, nameToBean.get(expectedFirstItemId),
                nameToBean.get(expectedLastItemId),
                nameToBean.get(itemIdInSet), notInSet, checkGetItemNull,
                expectedSize);
    }

    @Override
    protected boolean isFilteredOutItemNull() {
        return false;
    }

    public void testBasicOperations() {
        testBasicContainerOperations(getContainer());
    }

    public void testFiltering() {
        testContainerFiltering(getContainer());
    }

    public void testSorting() {
        testContainerSorting(getContainer());
    }

    public void testSortingAndFiltering() {
        testContainerSortingAndFiltering(getContainer());
    }

    // duplicated from parent class and modified - adding items to
    // BeanItemContainer differs from other containers
    public void testContainerOrdered() {
        BeanItemContainer<String> container = new BeanItemContainer<String>(
                String.class);

        String id = "test1";

        Item item = container.addBean(id);
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
        item = container.addItemAfter(null, newFirstId);
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

        // addItemAfter(Object)
        String newSecondItemId = "newSecond";
        item = container.addItemAfter(newFirstId, newSecondItemId);
        // order is now: newFirstId, newSecondItemId, id
        assertNotNull(item);
        assertNotNull(container.getItem(newSecondItemId));
        assertEquals(id, container.nextItemId(newSecondItemId));
        assertEquals(newFirstId, container.prevItemId(newSecondItemId));

        // addItemAfter(Object,Object)
        String fourthId = "id of the fourth item";
        Item fourth = container.addItemAfter(newFirstId, fourthId);
        // order is now: newFirstId, fourthId, newSecondItemId, id
        assertNotNull(fourth);
        assertEquals(fourth, container.getItem(fourthId));
        assertEquals(newSecondItemId, container.nextItemId(fourthId));
        assertEquals(newFirstId, container.prevItemId(fourthId));

        // addItemAfter(Object,Object)
        Object fifthId = "fifth";
        Item fifth = container.addItemAfter(null, fifthId);
        // order is now: fifthId, newFirstId, fourthId, newSecondItemId, id
        assertNotNull(fifth);
        assertEquals(fifth, container.getItem(fifthId));
        assertEquals(newFirstId, container.nextItemId(fifthId));
        assertNull(container.prevItemId(fifthId));

    }

    public void testContainerIndexed() {
        testContainerIndexed(getContainer(), nameToBean.get(sampleData[2]), 2,
                false, new ClassName("org.vaadin.test.Test", 8888), true);
    }

    // note that the constructor tested here is problematic, and should also
    // take the bean class as a parameter
    public void testCollectionConstructor() {
        List<ClassName> classNames = new ArrayList<ClassName>();
        classNames.add(new ClassName("a.b.c.Def", 1));
        classNames.add(new ClassName("a.b.c.Fed", 2));
        classNames.add(new ClassName("b.c.d.Def", 3));

        BeanItemContainer<ClassName> container = new BeanItemContainer<ClassName>(
                classNames);

        Assert.assertEquals(3, container.size());
        Assert.assertEquals(classNames.get(0), container.firstItemId());
        Assert.assertEquals(classNames.get(1), container.getIdByIndex(1));
        Assert.assertEquals(classNames.get(2), container.lastItemId());
    }

    // this only applies to the collection constructor with no type parameter
    public void testEmptyCollectionConstructor() {
        try {
            BeanItemContainer<ClassName> container = new BeanItemContainer<ClassName>(
                    (Collection<ClassName>) null);
            Assert.fail("Initializing BeanItemContainer from a null collection should not work!");
        } catch (IllegalArgumentException e) {
            // success
        }
        try {
            BeanItemContainer<ClassName> container = new BeanItemContainer<ClassName>(
                    new ArrayList<ClassName>());
            Assert.fail("Initializing BeanItemContainer from an empty collection should not work!");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    protected abstract class ItemSetChangeListenerTester {
        public void listenerTest() {
            listenerTest(true);
        }

        public void listenerTest(boolean expectChangeEvent) {
            BeanItemContainer<ClassName> container = prepareContainer();

            ItemSetChangeListener listener = EasyMock
                    .createStrictMock(ItemSetChangeListener.class);

            // Expectations and start test
            if (expectChangeEvent) {
                listener.containerItemSetChange(EasyMock
                        .isA(ItemSetChangeEvent.class));
            }
            EasyMock.replay(listener);

            // Add listener and add a property -> should end up in listener
            // once
            container.addListener(listener);
            performModification(container);

            // Ensure listener was called once
            EasyMock.verify(listener);

            // Remove the listener
            container.removeListener(listener);
            performModification(container);

            // Ensure listener has not been called again
            EasyMock.verify(listener);
        }

        protected BeanItemContainer<ClassName> prepareContainer() {
            BeanItemContainer<ClassName> container = getContainer();
            initializeContainer(container);
            return container;
        }

        protected abstract void performModification(
                BeanItemContainer<ClassName> container);
    }

    public void testItemSetChangeListeners() {
        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(
                    BeanItemContainer<ClassName> container) {
                container.addBean(new ClassName("com.example.Test", 1111));
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(
                    BeanItemContainer<ClassName> container) {
                container.removeItem(nameToBean.get(sampleData[0]));
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(
                    BeanItemContainer<ClassName> container) {
                container.removeItem(new ClassName("com.example.Test", 1111));
            }
        }.listenerTest(false);

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(
                    BeanItemContainer<ClassName> container) {
                // this test does not check that there would be no second
                // notification because the collection is already empty
                container.removeAllItems();
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            private int propertyIndex = 0;

            @Override
            protected void performModification(
                    BeanItemContainer<ClassName> container) {
                Collection<String> containerPropertyIds = container
                        .getContainerPropertyIds();
                container.addContainerFilter(new ArrayList<String>(
                        containerPropertyIds).get(propertyIndex++), "a", true,
                        false);
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(
                    BeanItemContainer<ClassName> container) {
                container.removeContainerFilters(SIMPLE_NAME);
            }

            @Override
            protected BeanItemContainer<ClassName> prepareContainer() {
                BeanItemContainer<ClassName> container = super
                        .prepareContainer();
                container.addContainerFilter(SIMPLE_NAME, "a", true, false);
                return container;
            };
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(
                    BeanItemContainer<ClassName> container) {
                container.removeAllContainerFilters();
            }

            @Override
            protected BeanItemContainer<ClassName> prepareContainer() {
                BeanItemContainer<ClassName> container = super
                        .prepareContainer();
                container.addContainerFilter(SIMPLE_NAME, "a", true, false);
                return container;
            };
        }.listenerTest();
    }

    protected static class Person {
        private String name;

        public Person(String name) {
            setName(name);
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public void testAddRemoveWhileFiltering() {
        BeanItemContainer<Person> container = new BeanItemContainer<Person>(
                Person.class);

        Person john = new Person("John");
        Person jane = new Person("Jane");
        Person matthew = new Person("Matthew");

        Person jack = new Person("Jack");
        Person michael = new Person("Michael");
        Person william = new Person("William");
        Person julia = new Person("Julia");
        Person george = new Person("George");
        Person mark = new Person("Mark");

        container.addBean(john);
        container.addBean(jane);
        container.addBean(matthew);

        assertEquals(3, container.size());
        // john, jane, matthew

        container.addContainerFilter("name", "j", true, true);

        assertEquals(2, container.size());
        // john, jane, (matthew)

        // add a bean that passes the filter
        container.addBean(jack);
        assertEquals(3, container.size());
        assertEquals(jack, container.lastItemId());
        // john, jane, (matthew), jack

        // add beans that do not pass the filter
        container.addBean(michael);
        // john, jane, (matthew), jack, (michael)
        container.addItemAfter(null, william);
        // (william), john, jane, (matthew), jack, (michael)

        // add after an item that is shown
        container.addItemAfter(john, george);
        // (william), john, (george), jane, (matthew), jack, (michael)
        assertEquals(3, container.size());
        assertEquals(john, container.firstItemId());

        // add after an item that is not shown does nothing
        container.addItemAfter(william, julia);
        // (william), john, (george), jane, (matthew), jack, (michael)
        assertEquals(3, container.size());
        assertEquals(john, container.firstItemId());

        container.addItemAt(1, julia);
        // (william), john, julia, (george), jane, (matthew), jack, (michael)

        container.addItemAt(2, mark);
        // (william), john, julia, (mark), (george), jane, (matthew), jack,
        // (michael)

        container.removeItem(matthew);
        // (william), john, julia, (mark), (george), jane, jack, (michael)

        assertEquals(4, container.size());
        assertEquals(jack, container.lastItemId());

        container.removeContainerFilters("name");

        assertEquals(8, container.size());
        assertEquals(william, container.firstItemId());
        assertEquals(john, container.nextItemId(william));
        assertEquals(julia, container.nextItemId(john));
        assertEquals(mark, container.nextItemId(julia));
        assertEquals(george, container.nextItemId(mark));
        assertEquals(jane, container.nextItemId(george));
        assertEquals(jack, container.nextItemId(jane));
        assertEquals(michael, container.lastItemId());
    }

    public void testRefilterOnPropertyModification() {
        BeanItemContainer<Person> container = new BeanItemContainer<Person>(
                Person.class);

        Person john = new Person("John");
        Person jane = new Person("Jane");
        Person matthew = new Person("Matthew");

        container.addBean(john);
        container.addBean(jane);
        container.addBean(matthew);

        assertEquals(3, container.size());
        // john, jane, matthew

        container.addContainerFilter("name", "j", true, true);

        assertEquals(2, container.size());
        // john, jane, (matthew)

        // #6053 currently, modification of an item that is not visible does not
        // trigger refiltering - should it?
        // matthew.setName("Julia");
        // assertEquals(3, container.size());
        // john, jane, julia

        john.setName("Mark");
        assertEquals(2, container.size());
        // (mark), jane, julia

        container.removeAllContainerFilters();

        assertEquals(3, container.size());
    }

}
