package com.vaadin.tests.server.container;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.data.Container;
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

    private Map<String, ClassName> nameToBean = new HashMap<String, ClassName>();

    private BeanItemContainer<ClassName> getContainer() {
        return new BeanItemContainer<ClassName>(ClassName.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initializeContainer(Container container) {
        BeanItemContainer<ClassName> beanItemContainer = (BeanItemContainer<ClassName>) container;

        beanItemContainer.removeAllItems();
        nameToBean.clear();

        for (int i = 0; i < sampleData.length; i++) {
            ClassName className = new ClassName(sampleData[i], i);
            nameToBean.put(sampleData[i], className);
            beanItemContainer.addBean(className);
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

}
