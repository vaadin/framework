package com.vaadin.data.util.filter;

import org.junit.Assert;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;

public class NotFilterTest extends AbstractFilterTestBase<Not> {

    protected Item item1 = new BeanItem<Integer>(1);
    protected Item item2 = new BeanItem<Integer>(2);

    public void testNot() {
        Filter origFilter = new SameItemFilter(item1);
        Filter filter = new Not(origFilter);

        Assert.assertTrue(origFilter.passesFilter(null, item1));
        Assert.assertFalse(origFilter.passesFilter(null, item2));
        Assert.assertFalse(filter.passesFilter(null, item1));
        Assert.assertTrue(filter.passesFilter(null, item2));
    }

    public void testANotAppliesToProperty() {
        Filter filterA = new Not(new SameItemFilter(item1, "a"));
        Filter filterB = new Not(new SameItemFilter(item1, "b"));

        Assert.assertTrue(filterA.appliesToProperty("a"));
        Assert.assertFalse(filterA.appliesToProperty("b"));
        Assert.assertFalse(filterB.appliesToProperty("a"));
        Assert.assertTrue(filterB.appliesToProperty("b"));
    }

    public void testNotEqualsHashCode() {
        Filter origFilter = new SameItemFilter(item1);
        Filter filter1 = new Not(origFilter);
        Filter filter1b = new Not(new SameItemFilter(item1));
        Filter filter2 = new Not(new SameItemFilter(item2));

        // equals()
        Assert.assertEquals(filter1, filter1b);
        Assert.assertFalse(filter1.equals(filter2));
        Assert.assertFalse(filter1.equals(origFilter));
        Assert.assertFalse(filter1.equals(new And()));

        // hashCode()
        Assert.assertEquals(filter1.hashCode(), filter1b.hashCode());
    }

}
