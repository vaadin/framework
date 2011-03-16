package com.vaadin.tests.server.container.filter;

import junit.framework.Assert;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.filter.Compare.LessOrEqual;

public class CompareFilterTest extends AbstractFilterTest {

    protected Item itemNull;
    protected Item itemEmpty;
    protected Item itemA;
    protected Item itemB;
    protected Item itemC;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        itemNull = new PropertysetItem();
        itemNull.addItemProperty(PROPERTY1, new ObjectProperty<String>(null,
                String.class));
        itemEmpty = new PropertysetItem();
        itemEmpty.addItemProperty(PROPERTY1, new ObjectProperty<String>("",
                String.class));
        itemA = new PropertysetItem();
        itemA.addItemProperty(PROPERTY1, new ObjectProperty<String>("a",
                String.class));
        itemB = new PropertysetItem();
        itemB.addItemProperty(PROPERTY1, new ObjectProperty<String>("b",
                String.class));
        itemC = new PropertysetItem();
        itemC.addItemProperty(PROPERTY1, new ObjectProperty<String>("c",
                String.class));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        itemNull = null;
        itemEmpty = null;
        itemA = null;
        itemB = null;
    }

    public void testCompareString() {
        Filter equalB = new Equal(PROPERTY1, "b");
        Filter greaterB = new Greater(PROPERTY1, "b");
        Filter lessB = new Less(PROPERTY1, "b");
        Filter greaterEqualB = new GreaterOrEqual(PROPERTY1, "b");
        Filter lessEqualB = new LessOrEqual(PROPERTY1, "b");

        Assert.assertFalse(equalB.passesFilter(null, itemA));
        Assert.assertTrue(equalB.passesFilter(null, itemB));
        Assert.assertFalse(equalB.passesFilter(null, itemC));

        Assert.assertFalse(greaterB.passesFilter(null, itemA));
        Assert.assertFalse(greaterB.passesFilter(null, itemB));
        Assert.assertTrue(greaterB.passesFilter(null, itemC));

        Assert.assertTrue(lessB.passesFilter(null, itemA));
        Assert.assertFalse(lessB.passesFilter(null, itemB));
        Assert.assertFalse(lessB.passesFilter(null, itemC));

        Assert.assertFalse(greaterEqualB.passesFilter(null, itemA));
        Assert.assertTrue(greaterEqualB.passesFilter(null, itemB));
        Assert.assertTrue(greaterEqualB.passesFilter(null, itemC));

        Assert.assertTrue(lessEqualB.passesFilter(null, itemA));
        Assert.assertTrue(lessEqualB.passesFilter(null, itemB));
        Assert.assertFalse(lessEqualB.passesFilter(null, itemC));
    }

    // TODO more tests: null comparisons, different datatypes...

    public void testCompareEqualsHashCode() {
        // most checks with Equal filter, then only some with others
        Filter equalNull = new Equal(PROPERTY1, null);
        Filter equalNull2 = new Equal(PROPERTY1, null);
        Filter equalNullProperty2 = new Equal(PROPERTY2, null);
        Filter equalEmpty = new Equal(PROPERTY1, "");
        Filter equalEmpty2 = new Equal(PROPERTY1, "");
        Filter equalEmptyProperty2 = new Equal(PROPERTY2, "");
        Filter equalA = new Equal(PROPERTY1, "a");
        Filter equalA2 = new Equal(PROPERTY1, "a");
        Filter equalAProperty2 = new Equal(PROPERTY2, "a");
        Filter equalB = new Equal(PROPERTY1, "b");

        Filter greaterNull = new Greater(PROPERTY1, null);
        Filter greaterEmpty = new Greater(PROPERTY1, "");

        Filter greaterA = new Greater(PROPERTY1, "a");
        Filter lessA = new Less(PROPERTY1, "a");
        Filter greaterEqualA = new GreaterOrEqual(PROPERTY1, "a");
        Filter lessEqualA = new LessOrEqual(PROPERTY1, "a");

        // equals()
        Assert.assertEquals(equalNull, equalNull);
        Assert.assertEquals(equalNull, equalNull2);
        Assert.assertFalse(equalNull.equals(equalNullProperty2));
        Assert.assertFalse(equalNull.equals(equalEmpty));
        Assert.assertFalse(equalNull.equals(equalA));

        Assert.assertEquals(equalEmpty, equalEmpty);
        Assert.assertFalse(equalEmpty.equals(equalNull));
        Assert.assertEquals(equalEmpty, equalEmpty2);
        Assert.assertFalse(equalEmpty.equals(equalEmptyProperty2));
        Assert.assertFalse(equalEmpty.equals(equalA));

        Assert.assertEquals(equalA, equalA);
        Assert.assertFalse(equalA.equals(equalNull));
        Assert.assertFalse(equalA.equals(equalEmpty));
        Assert.assertEquals(equalA, equalA2);
        Assert.assertFalse(equalA.equals(equalAProperty2));
        Assert.assertFalse(equalA.equals(equalB));

        Assert.assertEquals(greaterA, greaterA);
        Assert.assertFalse(greaterA.equals(lessA));
        Assert.assertFalse(greaterA.equals(greaterEqualA));
        Assert.assertFalse(greaterA.equals(lessEqualA));

        Assert.assertFalse(greaterNull.equals(greaterEmpty));
        Assert.assertFalse(greaterNull.equals(greaterA));
        Assert.assertFalse(greaterEmpty.equals(greaterNull));
        Assert.assertFalse(greaterEmpty.equals(greaterA));
        Assert.assertFalse(greaterA.equals(greaterNull));
        Assert.assertFalse(greaterA.equals(greaterEmpty));

        // hashCode()
        Assert.assertEquals(equalNull.hashCode(), equalNull2.hashCode());
        Assert.assertEquals(equalEmpty.hashCode(), equalEmpty2.hashCode());
        Assert.assertEquals(equalA.hashCode(), equalA2.hashCode());
    }
}
