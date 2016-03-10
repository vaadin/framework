package com.vaadin.data.util.filter;

import org.junit.Assert;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;

public class AndOrFilterTest extends
        AbstractFilterTestBase<AbstractJunctionFilter> {

    protected Item item1 = new BeanItem<Integer>(1);
    protected Item item2 = new BeanItem<Integer>(2);

    public void testNoFilterAnd() {
        Filter filter = new And();

        Assert.assertTrue(filter.passesFilter(null, item1));
    }

    public void testSingleFilterAnd() {
        Filter filter = new And(new SameItemFilter(item1));

        Assert.assertTrue(filter.passesFilter(null, item1));
        Assert.assertFalse(filter.passesFilter(null, item2));
    }

    public void testTwoFilterAnd() {
        Filter filter1 = new And(new SameItemFilter(item1), new SameItemFilter(
                item1));
        Filter filter2 = new And(new SameItemFilter(item1), new SameItemFilter(
                item2));

        Assert.assertTrue(filter1.passesFilter(null, item1));
        Assert.assertFalse(filter1.passesFilter(null, item2));

        Assert.assertFalse(filter2.passesFilter(null, item1));
        Assert.assertFalse(filter2.passesFilter(null, item2));
    }

    public void testThreeFilterAnd() {
        Filter filter1 = new And(new SameItemFilter(item1), new SameItemFilter(
                item1), new SameItemFilter(item1));
        Filter filter2 = new And(new SameItemFilter(item1), new SameItemFilter(
                item1), new SameItemFilter(item2));

        Assert.assertTrue(filter1.passesFilter(null, item1));
        Assert.assertFalse(filter1.passesFilter(null, item2));

        Assert.assertFalse(filter2.passesFilter(null, item1));
        Assert.assertFalse(filter2.passesFilter(null, item2));
    }

    public void testNoFilterOr() {
        Filter filter = new Or();

        Assert.assertFalse(filter.passesFilter(null, item1));
    }

    public void testSingleFilterOr() {
        Filter filter = new Or(new SameItemFilter(item1));

        Assert.assertTrue(filter.passesFilter(null, item1));
        Assert.assertFalse(filter.passesFilter(null, item2));
    }

    public void testTwoFilterOr() {
        Filter filter1 = new Or(new SameItemFilter(item1), new SameItemFilter(
                item1));
        Filter filter2 = new Or(new SameItemFilter(item1), new SameItemFilter(
                item2));

        Assert.assertTrue(filter1.passesFilter(null, item1));
        Assert.assertFalse(filter1.passesFilter(null, item2));

        Assert.assertTrue(filter2.passesFilter(null, item1));
        Assert.assertTrue(filter2.passesFilter(null, item2));
    }

    public void testThreeFilterOr() {
        Filter filter1 = new Or(new SameItemFilter(item1), new SameItemFilter(
                item1), new SameItemFilter(item1));
        Filter filter2 = new Or(new SameItemFilter(item1), new SameItemFilter(
                item1), new SameItemFilter(item2));

        Assert.assertTrue(filter1.passesFilter(null, item1));
        Assert.assertFalse(filter1.passesFilter(null, item2));

        Assert.assertTrue(filter2.passesFilter(null, item1));
        Assert.assertTrue(filter2.passesFilter(null, item2));
    }

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

        Assert.assertEquals(filter0, filter0);
        Assert.assertEquals(filter0, filter0b);
        Assert.assertFalse(filter0.equals(filter1a));
        Assert.assertFalse(filter0.equals(other0));
        Assert.assertFalse(filter0.equals(other1));

        Assert.assertFalse(filter1a.equals(filter1b));
        Assert.assertFalse(filter1a.equals(other1));

        Assert.assertFalse(filter1a.equals(filter2a));
        Assert.assertFalse(filter2a.equals(filter1a));

        Assert.assertFalse(filter2a.equals(filter2b));
        Assert.assertEquals(filter2b, filter2b2);

        // hashCode()
        Assert.assertEquals(filter0.hashCode(), filter0.hashCode());
        Assert.assertEquals(filter0.hashCode(), filter0b.hashCode());
        Assert.assertEquals(filter1a.hashCode(), filter1a.hashCode());
        Assert.assertEquals(filter1a.hashCode(), filter1a2.hashCode());
        Assert.assertEquals(filter2a.hashCode(), filter2a.hashCode());
        Assert.assertEquals(filter2b.hashCode(), filter2b2.hashCode());
    }

    public void testOrEqualsHashCode() {
        Filter filter0 = new Or();
        Filter filter0b = new Or();
        Filter filter1a = new Or(new SameItemFilter(item1));
        Filter filter1a2 = new Or(new SameItemFilter(item1));
        Filter filter1b = new Or(new SameItemFilter(item2));
        Filter filter2a = new Or(new SameItemFilter(item1), new SameItemFilter(
                item1));
        Filter filter2b = new Or(new SameItemFilter(item1), new SameItemFilter(
                item2));
        Filter filter2b2 = new Or(new SameItemFilter(item1),
                new SameItemFilter(item2));
        Filter other0 = new And();
        Filter other1 = new And(new SameItemFilter(item1));

        Assert.assertEquals(filter0, filter0);
        Assert.assertEquals(filter0, filter0b);
        Assert.assertFalse(filter0.equals(filter1a));
        Assert.assertFalse(filter0.equals(other0));
        Assert.assertFalse(filter0.equals(other1));

        Assert.assertFalse(filter1a.equals(filter1b));
        Assert.assertFalse(filter1a.equals(other1));

        Assert.assertFalse(filter1a.equals(filter2a));
        Assert.assertFalse(filter2a.equals(filter1a));

        Assert.assertFalse(filter2a.equals(filter2b));
        Assert.assertEquals(filter2b, filter2b2);

        // hashCode()
        Assert.assertEquals(filter0.hashCode(), filter0.hashCode());
        Assert.assertEquals(filter0.hashCode(), filter0b.hashCode());
        Assert.assertEquals(filter1a.hashCode(), filter1a.hashCode());
        Assert.assertEquals(filter1a.hashCode(), filter1a2.hashCode());
        Assert.assertEquals(filter2a.hashCode(), filter2a.hashCode());
        Assert.assertEquals(filter2b.hashCode(), filter2b2.hashCode());
    }

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
        Assert.assertFalse(filter0.appliesToProperty("a"));
        Assert.assertFalse(filter0.appliesToProperty("d"));

        Assert.assertTrue(filter1a.appliesToProperty("a"));
        Assert.assertFalse(filter1a.appliesToProperty("b"));
        Assert.assertFalse(filter1b.appliesToProperty("a"));
        Assert.assertTrue(filter1b.appliesToProperty("b"));

        Assert.assertTrue(filter2aa.appliesToProperty("a"));
        Assert.assertFalse(filter2aa.appliesToProperty("b"));
        Assert.assertTrue(filter2ab.appliesToProperty("a"));
        Assert.assertTrue(filter2ab.appliesToProperty("b"));

        Assert.assertTrue(filter3abc.appliesToProperty("a"));
        Assert.assertTrue(filter3abc.appliesToProperty("b"));
        Assert.assertTrue(filter3abc.appliesToProperty("c"));
        Assert.assertFalse(filter3abc.appliesToProperty("d"));
    }

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
        Assert.assertTrue(filter0.appliesToProperty("a"));
        Assert.assertTrue(filter0.appliesToProperty("d"));

        Assert.assertTrue(filter1a.appliesToProperty("a"));
        Assert.assertFalse(filter1a.appliesToProperty("b"));
        Assert.assertFalse(filter1b.appliesToProperty("a"));
        Assert.assertTrue(filter1b.appliesToProperty("b"));

        Assert.assertTrue(filter2aa.appliesToProperty("a"));
        Assert.assertFalse(filter2aa.appliesToProperty("b"));
        Assert.assertTrue(filter2ab.appliesToProperty("a"));
        Assert.assertTrue(filter2ab.appliesToProperty("b"));

        Assert.assertTrue(filter3abc.appliesToProperty("a"));
        Assert.assertTrue(filter3abc.appliesToProperty("b"));
        Assert.assertTrue(filter3abc.appliesToProperty("c"));
        Assert.assertFalse(filter3abc.appliesToProperty("d"));
    }

}
