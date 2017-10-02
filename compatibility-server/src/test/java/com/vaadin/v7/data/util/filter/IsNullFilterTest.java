package com.vaadin.v7.data.util.filter;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertysetItem;

public class IsNullFilterTest extends AbstractFilterTestBase<IsNull> {

    @Test
    public void testIsNull() {
        Item item1 = new PropertysetItem();
        item1.addItemProperty("a",
                new ObjectProperty<String>(null, String.class));
        item1.addItemProperty("b",
                new ObjectProperty<String>("b", String.class));
        Item item2 = new PropertysetItem();
        item2.addItemProperty("a",
                new ObjectProperty<String>("a", String.class));
        item2.addItemProperty("b",
                new ObjectProperty<String>(null, String.class));

        Filter filter1 = new IsNull("a");
        Filter filter2 = new IsNull("b");

        assertTrue(filter1.passesFilter(null, item1));
        assertFalse(filter1.passesFilter(null, item2));
        assertFalse(filter2.passesFilter(null, item1));
        assertTrue(filter2.passesFilter(null, item2));
    }

    @Test
    public void testIsNullAppliesToProperty() {
        Filter filterA = new IsNull("a");
        Filter filterB = new IsNull("b");

        assertTrue(filterA.appliesToProperty("a"));
        assertFalse(filterA.appliesToProperty("b"));
        assertFalse(filterB.appliesToProperty("a"));
        assertTrue(filterB.appliesToProperty("b"));
    }

    @Test
    public void testIsNullEqualsHashCode() {
        Filter filter1 = new IsNull("a");
        Filter filter1b = new IsNull("a");
        Filter filter2 = new IsNull("b");

        // equals()
        assertEquals(filter1, filter1b);
        assertFalse(filter1.equals(filter2));
        assertFalse(filter1.equals(new And()));

        // hashCode()
        assertEquals(filter1.hashCode(), filter1b.hashCode());
    }

}
