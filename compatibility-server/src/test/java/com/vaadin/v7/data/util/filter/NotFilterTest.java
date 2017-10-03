package com.vaadin.v7.data.util.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItem;

public class NotFilterTest extends AbstractFilterTestBase<Not> {

    protected Item item1 = new BeanItem<Integer>(1);
    protected Item item2 = new BeanItem<Integer>(2);

    @Test
    public void testNot() {
        Filter origFilter = new SameItemFilter(item1);
        Filter filter = new Not(origFilter);

        assertTrue(origFilter.passesFilter(null, item1));
        assertFalse(origFilter.passesFilter(null, item2));
        assertFalse(filter.passesFilter(null, item1));
        assertTrue(filter.passesFilter(null, item2));
    }

    @Test
    public void testANotAppliesToProperty() {
        Filter filterA = new Not(new SameItemFilter(item1, "a"));
        Filter filterB = new Not(new SameItemFilter(item1, "b"));

        assertTrue(filterA.appliesToProperty("a"));
        assertFalse(filterA.appliesToProperty("b"));
        assertFalse(filterB.appliesToProperty("a"));
        assertTrue(filterB.appliesToProperty("b"));
    }

    @Test
    public void testNotEqualsHashCode() {
        Filter origFilter = new SameItemFilter(item1);
        Filter filter1 = new Not(origFilter);
        Filter filter1b = new Not(new SameItemFilter(item1));
        Filter filter2 = new Not(new SameItemFilter(item2));

        // equals()
        assertEquals(filter1, filter1b);
        assertFalse(filter1.equals(filter2));
        assertFalse(filter1.equals(origFilter));
        assertFalse(filter1.equals(new And()));

        // hashCode()
        assertEquals(filter1.hashCode(), filter1b.hashCode());
    }

}
