package com.vaadin.v7.data.util.filter;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItem;

public class AndOrFilterTest
        extends AbstractFilterTestBase<AbstractJunctionFilter> {

    protected Item item1 = new BeanItem<Integer>(1);
    protected Item item2 = new BeanItem<Integer>(2);

    @Test
    public void testNoFilterAnd() {
        Filter filter = new And();

        assertTrue(filter.passesFilter(null, item1));
    }

    @Test
    public void testSingleFilterAnd() {
        Filter filter = new And(new SameItemFilter(item1));

        assertTrue(filter.passesFilter(null, item1));
        assertFalse(filter.passesFilter(null, item2));
    }

    @Test
    public void testTwoFilterAnd() {
        Filter filter1 = new And(new SameItemFilter(item1),
                new SameItemFilter(item1));
        Filter filter2 = new And(new SameItemFilter(item1),
                new SameItemFilter(item2));

        assertTrue(filter1.passesFilter(null, item1));
        assertFalse(filter1.passesFilter(null, item2));

        assertFalse(filter2.passesFilter(null, item1));
        assertFalse(filter2.passesFilter(null, item2));
    }

    @Test
    public void testThreeFilterAnd() {
        Filter filter1 = new And(new SameItemFilter(item1),
                new SameItemFilter(item1), new SameItemFilter(item1));
        Filter filter2 = new And(new SameItemFilter(item1),
                new SameItemFilter(item1), new SameItemFilter(item2));

        assertTrue(filter1.passesFilter(null, item1));
        assertFalse(filter1.passesFilter(null, item2));

        assertFalse(filter2.passesFilter(null, item1));
        assertFalse(filter2.passesFilter(null, item2));
    }

    @Test
    public void testNoFilterOr() {
        Filter filter = new Or();

        assertFalse(filter.passesFilter(null, item1));
    }

    @Test
    public void testSingleFilterOr() {
        Filter filter = new Or(new SameItemFilter(item1));

        assertTrue(filter.passesFilter(null, item1));
        assertFalse(filter.passesFilter(null, item2));
    }

    @Test
    public void testTwoFilterOr() {
        Filter filter1 = new Or(new SameItemFilter(item1),
                new SameItemFilter(item1));
        Filter filter2 = new Or(new SameItemFilter(item1),
                new SameItemFilter(item2));

        assertTrue(filter1.passesFilter(null, item1));
        assertFalse(filter1.passesFilter(null, item2));

        assertTrue(filter2.passesFilter(null, item1));
        assertTrue(filter2.passesFilter(null, item2));
    }

    @Test
    public void testThreeFilterOr() {
        Filter filter1 = new Or(new SameItemFilter(item1),
                new SameItemFilter(item1), new SameItemFilter(item1));
        Filter filter2 = new Or(new SameItemFilter(item1),
                new SameItemFilter(item1), new SameItemFilter(item2));

        assertTrue(filter1.passesFilter(null, item1));
        assertFalse(filter1.passesFilter(null, item2));

        assertTrue(filter2.passesFilter(null, item1));
        assertTrue(filter2.passesFilter(null, item2));
    }

    @Test
    public void testAndEqualsHashCode() {
        Filter filter0 = new And();
        Filter filter0b = new And();
        Filter filter1a = new And(new SameItemFilter(item1));
        Filter filter1a2 = new And(new SameItemFilter(item1));
        Filter filter1b = new And(new SameItemFilter(item2));
        Filter filter2a = new And(new SameItemFilter(item1),
                new SameItemFilter(item1));
        Filter filter2b = new And(new SameItemFilter(item1),
                new SameItemFilter(item2));
        Filter filter2b2 = new And(new SameItemFilter(item1),
                new SameItemFilter(item2));
        Filter other0 = new Or();
        Filter other1 = new Or(new SameItemFilter(item1));

        assertEquals(filter0, filter0);
        assertEquals(filter0, filter0b);
        assertFalse(filter0.equals(filter1a));
        assertFalse(filter0.equals(other0));
        assertFalse(filter0.equals(other1));

        assertFalse(filter1a.equals(filter1b));
        assertFalse(filter1a.equals(other1));

        assertFalse(filter1a.equals(filter2a));
        assertFalse(filter2a.equals(filter1a));

        assertFalse(filter2a.equals(filter2b));
        assertEquals(filter2b, filter2b2);

        // hashCode()
        assertEquals(filter0.hashCode(), filter0.hashCode());
        assertEquals(filter0.hashCode(), filter0b.hashCode());
        assertEquals(filter1a.hashCode(), filter1a.hashCode());
        assertEquals(filter1a.hashCode(), filter1a2.hashCode());
        assertEquals(filter2a.hashCode(), filter2a.hashCode());
        assertEquals(filter2b.hashCode(), filter2b2.hashCode());
    }

    @Test
    public void testOrEqualsHashCode() {
        Filter filter0 = new Or();
        Filter filter0b = new Or();
        Filter filter1a = new Or(new SameItemFilter(item1));
        Filter filter1a2 = new Or(new SameItemFilter(item1));
        Filter filter1b = new Or(new SameItemFilter(item2));
        Filter filter2a = new Or(new SameItemFilter(item1),
                new SameItemFilter(item1));
        Filter filter2b = new Or(new SameItemFilter(item1),
                new SameItemFilter(item2));
        Filter filter2b2 = new Or(new SameItemFilter(item1),
                new SameItemFilter(item2));
        Filter other0 = new And();
        Filter other1 = new And(new SameItemFilter(item1));

        assertEquals(filter0, filter0);
        assertEquals(filter0, filter0b);
        assertFalse(filter0.equals(filter1a));
        assertFalse(filter0.equals(other0));
        assertFalse(filter0.equals(other1));

        assertFalse(filter1a.equals(filter1b));
        assertFalse(filter1a.equals(other1));

        assertFalse(filter1a.equals(filter2a));
        assertFalse(filter2a.equals(filter1a));

        assertFalse(filter2a.equals(filter2b));
        assertEquals(filter2b, filter2b2);

        // hashCode()
        assertEquals(filter0.hashCode(), filter0.hashCode());
        assertEquals(filter0.hashCode(), filter0b.hashCode());
        assertEquals(filter1a.hashCode(), filter1a.hashCode());
        assertEquals(filter1a.hashCode(), filter1a2.hashCode());
        assertEquals(filter2a.hashCode(), filter2a.hashCode());
        assertEquals(filter2b.hashCode(), filter2b2.hashCode());
    }

    @Test
    public void testAndAppliesToProperty() {
        Filter filter0 = new And();
        Filter filter1a = new And(new SameItemFilter(item1, "a"));
        Filter filter1b = new And(new SameItemFilter(item1, "b"));
        Filter filter2aa = new And(new SameItemFilter(item1, "a"),
                new SameItemFilter(item1, "a"));
        Filter filter2ab = new And(new SameItemFilter(item1, "a"),
                new SameItemFilter(item1, "b"));
        Filter filter3abc = new And(new SameItemFilter(item1, "a"),
                new SameItemFilter(item1, "b"), new SameItemFilter(item1, "c"));

        // empty And does not filter out anything
        assertFalse(filter0.appliesToProperty("a"));
        assertFalse(filter0.appliesToProperty("d"));

        assertTrue(filter1a.appliesToProperty("a"));
        assertFalse(filter1a.appliesToProperty("b"));
        assertFalse(filter1b.appliesToProperty("a"));
        assertTrue(filter1b.appliesToProperty("b"));

        assertTrue(filter2aa.appliesToProperty("a"));
        assertFalse(filter2aa.appliesToProperty("b"));
        assertTrue(filter2ab.appliesToProperty("a"));
        assertTrue(filter2ab.appliesToProperty("b"));

        assertTrue(filter3abc.appliesToProperty("a"));
        assertTrue(filter3abc.appliesToProperty("b"));
        assertTrue(filter3abc.appliesToProperty("c"));
        assertFalse(filter3abc.appliesToProperty("d"));
    }

    @Test
    public void testOrAppliesToProperty() {
        Filter filter0 = new Or();
        Filter filter1a = new Or(new SameItemFilter(item1, "a"));
        Filter filter1b = new Or(new SameItemFilter(item1, "b"));
        Filter filter2aa = new Or(new SameItemFilter(item1, "a"),
                new SameItemFilter(item1, "a"));
        Filter filter2ab = new Or(new SameItemFilter(item1, "a"),
                new SameItemFilter(item1, "b"));
        Filter filter3abc = new Or(new SameItemFilter(item1, "a"),
                new SameItemFilter(item1, "b"), new SameItemFilter(item1, "c"));

        // empty Or filters out everything
        assertTrue(filter0.appliesToProperty("a"));
        assertTrue(filter0.appliesToProperty("d"));

        assertTrue(filter1a.appliesToProperty("a"));
        assertFalse(filter1a.appliesToProperty("b"));
        assertFalse(filter1b.appliesToProperty("a"));
        assertTrue(filter1b.appliesToProperty("b"));

        assertTrue(filter2aa.appliesToProperty("a"));
        assertFalse(filter2aa.appliesToProperty("b"));
        assertTrue(filter2ab.appliesToProperty("a"));
        assertTrue(filter2ab.appliesToProperty("b"));

        assertTrue(filter3abc.appliesToProperty("a"));
        assertTrue(filter3abc.appliesToProperty("b"));
        assertTrue(filter3abc.appliesToProperty("c"));
        assertFalse(filter3abc.appliesToProperty("d"));
    }

}
