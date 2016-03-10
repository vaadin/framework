package com.vaadin.data.util.filter;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.filter.Compare.LessOrEqual;

public class CompareFilterTest extends AbstractFilterTestBase<Compare> {

    protected Item itemNull;
    protected Item itemEmpty;
    protected Item itemA;
    protected Item itemB;
    protected Item itemC;

    protected final Filter equalB = new Equal(PROPERTY1, "b");
    protected final Filter greaterB = new Greater(PROPERTY1, "b");
    protected final Filter lessB = new Less(PROPERTY1, "b");
    protected final Filter greaterEqualB = new GreaterOrEqual(PROPERTY1, "b");
    protected final Filter lessEqualB = new LessOrEqual(PROPERTY1, "b");

    protected final Filter equalNull = new Equal(PROPERTY1, null);
    protected final Filter greaterNull = new Greater(PROPERTY1, null);
    protected final Filter lessNull = new Less(PROPERTY1, null);
    protected final Filter greaterEqualNull = new GreaterOrEqual(PROPERTY1,
            null);
    protected final Filter lessEqualNull = new LessOrEqual(PROPERTY1, null);

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
        Assert.assertFalse(equalB.passesFilter(null, itemEmpty));
        Assert.assertFalse(equalB.passesFilter(null, itemA));
        Assert.assertTrue(equalB.passesFilter(null, itemB));
        Assert.assertFalse(equalB.passesFilter(null, itemC));

        Assert.assertFalse(greaterB.passesFilter(null, itemEmpty));
        Assert.assertFalse(greaterB.passesFilter(null, itemA));
        Assert.assertFalse(greaterB.passesFilter(null, itemB));
        Assert.assertTrue(greaterB.passesFilter(null, itemC));

        Assert.assertTrue(lessB.passesFilter(null, itemEmpty));
        Assert.assertTrue(lessB.passesFilter(null, itemA));
        Assert.assertFalse(lessB.passesFilter(null, itemB));
        Assert.assertFalse(lessB.passesFilter(null, itemC));

        Assert.assertFalse(greaterEqualB.passesFilter(null, itemEmpty));
        Assert.assertFalse(greaterEqualB.passesFilter(null, itemA));
        Assert.assertTrue(greaterEqualB.passesFilter(null, itemB));
        Assert.assertTrue(greaterEqualB.passesFilter(null, itemC));

        Assert.assertTrue(lessEqualB.passesFilter(null, itemEmpty));
        Assert.assertTrue(lessEqualB.passesFilter(null, itemA));
        Assert.assertTrue(lessEqualB.passesFilter(null, itemB));
        Assert.assertFalse(lessEqualB.passesFilter(null, itemC));
    }

    public void testCompareWithNull() {
        // null comparisons: null is less than any other value
        Assert.assertFalse(equalB.passesFilter(null, itemNull));
        Assert.assertTrue(greaterB.passesFilter(null, itemNull));
        Assert.assertFalse(lessB.passesFilter(null, itemNull));
        Assert.assertTrue(greaterEqualB.passesFilter(null, itemNull));
        Assert.assertFalse(lessEqualB.passesFilter(null, itemNull));

        Assert.assertTrue(equalNull.passesFilter(null, itemNull));
        Assert.assertFalse(greaterNull.passesFilter(null, itemNull));
        Assert.assertFalse(lessNull.passesFilter(null, itemNull));
        Assert.assertTrue(greaterEqualNull.passesFilter(null, itemNull));
        Assert.assertTrue(lessEqualNull.passesFilter(null, itemNull));

        Assert.assertFalse(equalNull.passesFilter(null, itemA));
        Assert.assertFalse(greaterNull.passesFilter(null, itemA));
        Assert.assertTrue(lessNull.passesFilter(null, itemA));
        Assert.assertFalse(greaterEqualNull.passesFilter(null, itemA));
        Assert.assertTrue(lessEqualNull.passesFilter(null, itemA));
    }

    public void testCompareInteger() {
        int negative = -1;
        int zero = 0;
        int positive = 1;

        Item itemNegative = new PropertysetItem();
        itemNegative.addItemProperty(PROPERTY1, new ObjectProperty<Integer>(
                negative, Integer.class));
        Item itemZero = new PropertysetItem();
        itemZero.addItemProperty(PROPERTY1, new ObjectProperty<Integer>(zero,
                Integer.class));
        Item itemPositive = new PropertysetItem();
        itemPositive.addItemProperty(PROPERTY1, new ObjectProperty<Integer>(
                positive, Integer.class));

        Filter equalZero = new Equal(PROPERTY1, zero);
        Assert.assertFalse(equalZero.passesFilter(null, itemNegative));
        Assert.assertTrue(equalZero.passesFilter(null, itemZero));
        Assert.assertFalse(equalZero.passesFilter(null, itemPositive));

        Filter isPositive = new Greater(PROPERTY1, zero);
        Assert.assertFalse(isPositive.passesFilter(null, itemNegative));
        Assert.assertFalse(isPositive.passesFilter(null, itemZero));
        Assert.assertTrue(isPositive.passesFilter(null, itemPositive));

        Filter isNegative = new Less(PROPERTY1, zero);
        Assert.assertTrue(isNegative.passesFilter(null, itemNegative));
        Assert.assertFalse(isNegative.passesFilter(null, itemZero));
        Assert.assertFalse(isNegative.passesFilter(null, itemPositive));

        Filter isNonNegative = new GreaterOrEqual(PROPERTY1, zero);
        Assert.assertFalse(isNonNegative.passesFilter(null, itemNegative));
        Assert.assertTrue(isNonNegative.passesFilter(null, itemZero));
        Assert.assertTrue(isNonNegative.passesFilter(null, itemPositive));

        Filter isNonPositive = new LessOrEqual(PROPERTY1, zero);
        Assert.assertTrue(isNonPositive.passesFilter(null, itemNegative));
        Assert.assertTrue(isNonPositive.passesFilter(null, itemZero));
        Assert.assertFalse(isNonPositive.passesFilter(null, itemPositive));
    }

    public void testCompareBigDecimal() {
        BigDecimal negative = new BigDecimal(-1);
        BigDecimal zero = new BigDecimal(0);
        BigDecimal positive = new BigDecimal(1);
        positive.setScale(1);
        BigDecimal positiveScaleTwo = new BigDecimal(1).setScale(2);

        Item itemNegative = new PropertysetItem();
        itemNegative.addItemProperty(PROPERTY1, new ObjectProperty<BigDecimal>(
                negative, BigDecimal.class));
        Item itemZero = new PropertysetItem();
        itemZero.addItemProperty(PROPERTY1, new ObjectProperty<BigDecimal>(
                zero, BigDecimal.class));
        Item itemPositive = new PropertysetItem();
        itemPositive.addItemProperty(PROPERTY1, new ObjectProperty<BigDecimal>(
                positive, BigDecimal.class));
        Item itemPositiveScaleTwo = new PropertysetItem();
        itemPositiveScaleTwo.addItemProperty(PROPERTY1,
                new ObjectProperty<BigDecimal>(positiveScaleTwo,
                        BigDecimal.class));

        Filter equalZero = new Equal(PROPERTY1, zero);
        Assert.assertFalse(equalZero.passesFilter(null, itemNegative));
        Assert.assertTrue(equalZero.passesFilter(null, itemZero));
        Assert.assertFalse(equalZero.passesFilter(null, itemPositive));

        Filter isPositive = new Greater(PROPERTY1, zero);
        Assert.assertFalse(isPositive.passesFilter(null, itemNegative));
        Assert.assertFalse(isPositive.passesFilter(null, itemZero));
        Assert.assertTrue(isPositive.passesFilter(null, itemPositive));

        Filter isNegative = new Less(PROPERTY1, zero);
        Assert.assertTrue(isNegative.passesFilter(null, itemNegative));
        Assert.assertFalse(isNegative.passesFilter(null, itemZero));
        Assert.assertFalse(isNegative.passesFilter(null, itemPositive));

        Filter isNonNegative = new GreaterOrEqual(PROPERTY1, zero);
        Assert.assertFalse(isNonNegative.passesFilter(null, itemNegative));
        Assert.assertTrue(isNonNegative.passesFilter(null, itemZero));
        Assert.assertTrue(isNonNegative.passesFilter(null, itemPositive));

        Filter isNonPositive = new LessOrEqual(PROPERTY1, zero);
        Assert.assertTrue(isNonPositive.passesFilter(null, itemNegative));
        Assert.assertTrue(isNonPositive.passesFilter(null, itemZero));
        Assert.assertFalse(isNonPositive.passesFilter(null, itemPositive));

        Filter isPositiveScaleTwo = new Equal(PROPERTY1, positiveScaleTwo);
        Assert.assertTrue(isPositiveScaleTwo.passesFilter(null,
                itemPositiveScaleTwo));
        Assert.assertTrue(isPositiveScaleTwo.passesFilter(null, itemPositive));

    }

    public void testCompareDate() {
        Date now = new Date();
        // new Date() is only accurate to the millisecond, so repeating it gives
        // the same date
        Date earlier = new Date(now.getTime() - 1);
        Date later = new Date(now.getTime() + 1);

        Item itemEarlier = new PropertysetItem();
        itemEarlier.addItemProperty(PROPERTY1, new ObjectProperty<Date>(
                earlier, Date.class));
        Item itemNow = new PropertysetItem();
        itemNow.addItemProperty(PROPERTY1, new ObjectProperty<Date>(now,
                Date.class));
        Item itemLater = new PropertysetItem();
        itemLater.addItemProperty(PROPERTY1, new ObjectProperty<Date>(later,
                Date.class));

        Filter equalNow = new Equal(PROPERTY1, now);
        Assert.assertFalse(equalNow.passesFilter(null, itemEarlier));
        Assert.assertTrue(equalNow.passesFilter(null, itemNow));
        Assert.assertFalse(equalNow.passesFilter(null, itemLater));

        Filter after = new Greater(PROPERTY1, now);
        Assert.assertFalse(after.passesFilter(null, itemEarlier));
        Assert.assertFalse(after.passesFilter(null, itemNow));
        Assert.assertTrue(after.passesFilter(null, itemLater));

        Filter before = new Less(PROPERTY1, now);
        Assert.assertTrue(before.passesFilter(null, itemEarlier));
        Assert.assertFalse(before.passesFilter(null, itemNow));
        Assert.assertFalse(before.passesFilter(null, itemLater));

        Filter afterOrNow = new GreaterOrEqual(PROPERTY1, now);
        Assert.assertFalse(afterOrNow.passesFilter(null, itemEarlier));
        Assert.assertTrue(afterOrNow.passesFilter(null, itemNow));
        Assert.assertTrue(afterOrNow.passesFilter(null, itemLater));

        Filter beforeOrNow = new LessOrEqual(PROPERTY1, now);
        Assert.assertTrue(beforeOrNow.passesFilter(null, itemEarlier));
        Assert.assertTrue(beforeOrNow.passesFilter(null, itemNow));
        Assert.assertFalse(beforeOrNow.passesFilter(null, itemLater));
    }

    public void testCompareAppliesToProperty() {
        Filter filterA = new Equal("a", 1);
        Filter filterB = new Equal("b", 1);

        Assert.assertTrue(filterA.appliesToProperty("a"));
        Assert.assertFalse(filterA.appliesToProperty("b"));
        Assert.assertFalse(filterB.appliesToProperty("a"));
        Assert.assertTrue(filterB.appliesToProperty("b"));
    }

    public void testCompareEqualsHashCode() {
        // most checks with Equal filter, then only some with others
        Filter equalNull2 = new Equal(PROPERTY1, null);
        Filter equalNullProperty2 = new Equal(PROPERTY2, null);
        Filter equalEmpty = new Equal(PROPERTY1, "");
        Filter equalEmpty2 = new Equal(PROPERTY1, "");
        Filter equalEmptyProperty2 = new Equal(PROPERTY2, "");
        Filter equalA = new Equal(PROPERTY1, "a");
        Filter equalB2 = new Equal(PROPERTY1, "b");
        Filter equalBProperty2 = new Equal(PROPERTY2, "b");

        Filter greaterEmpty = new Greater(PROPERTY1, "");

        // equals()
        Assert.assertEquals(equalNull, equalNull);
        Assert.assertEquals(equalNull, equalNull2);
        Assert.assertFalse(equalNull.equals(equalNullProperty2));
        Assert.assertFalse(equalNull.equals(equalEmpty));
        Assert.assertFalse(equalNull.equals(equalB));

        Assert.assertEquals(equalEmpty, equalEmpty);
        Assert.assertFalse(equalEmpty.equals(equalNull));
        Assert.assertEquals(equalEmpty, equalEmpty2);
        Assert.assertFalse(equalEmpty.equals(equalEmptyProperty2));
        Assert.assertFalse(equalEmpty.equals(equalB));

        Assert.assertEquals(equalB, equalB);
        Assert.assertFalse(equalB.equals(equalNull));
        Assert.assertFalse(equalB.equals(equalEmpty));
        Assert.assertEquals(equalB, equalB2);
        Assert.assertFalse(equalB.equals(equalBProperty2));
        Assert.assertFalse(equalB.equals(equalA));

        Assert.assertEquals(greaterB, greaterB);
        Assert.assertFalse(greaterB.equals(lessB));
        Assert.assertFalse(greaterB.equals(greaterEqualB));
        Assert.assertFalse(greaterB.equals(lessEqualB));

        Assert.assertFalse(greaterNull.equals(greaterEmpty));
        Assert.assertFalse(greaterNull.equals(greaterB));
        Assert.assertFalse(greaterEmpty.equals(greaterNull));
        Assert.assertFalse(greaterEmpty.equals(greaterB));
        Assert.assertFalse(greaterB.equals(greaterNull));
        Assert.assertFalse(greaterB.equals(greaterEmpty));

        // hashCode()
        Assert.assertEquals(equalNull.hashCode(), equalNull2.hashCode());
        Assert.assertEquals(equalEmpty.hashCode(), equalEmpty2.hashCode());
        Assert.assertEquals(equalB.hashCode(), equalB2.hashCode());
    }

}
