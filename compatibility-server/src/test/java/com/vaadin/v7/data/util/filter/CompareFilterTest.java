package com.vaadin.v7.data.util.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertysetItem;
import com.vaadin.v7.data.util.filter.Compare.Equal;
import com.vaadin.v7.data.util.filter.Compare.Greater;
import com.vaadin.v7.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.v7.data.util.filter.Compare.Less;
import com.vaadin.v7.data.util.filter.Compare.LessOrEqual;

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

    @Before
    public void setUp() throws Exception {
        itemNull = new PropertysetItem();
        itemNull.addItemProperty(PROPERTY1,
                new ObjectProperty<String>(null, String.class));
        itemEmpty = new PropertysetItem();
        itemEmpty.addItemProperty(PROPERTY1,
                new ObjectProperty<String>("", String.class));
        itemA = new PropertysetItem();
        itemA.addItemProperty(PROPERTY1,
                new ObjectProperty<String>("a", String.class));
        itemB = new PropertysetItem();
        itemB.addItemProperty(PROPERTY1,
                new ObjectProperty<String>("b", String.class));
        itemC = new PropertysetItem();
        itemC.addItemProperty(PROPERTY1,
                new ObjectProperty<String>("c", String.class));
    }

    @After
    public void tearDown() throws Exception {
        itemNull = null;
        itemEmpty = null;
        itemA = null;
        itemB = null;
    }

    @Test
    public void testCompareString() {
        assertFalse(equalB.passesFilter(null, itemEmpty));
        assertFalse(equalB.passesFilter(null, itemA));
        assertTrue(equalB.passesFilter(null, itemB));
        assertFalse(equalB.passesFilter(null, itemC));

        assertFalse(greaterB.passesFilter(null, itemEmpty));
        assertFalse(greaterB.passesFilter(null, itemA));
        assertFalse(greaterB.passesFilter(null, itemB));
        assertTrue(greaterB.passesFilter(null, itemC));

        assertTrue(lessB.passesFilter(null, itemEmpty));
        assertTrue(lessB.passesFilter(null, itemA));
        assertFalse(lessB.passesFilter(null, itemB));
        assertFalse(lessB.passesFilter(null, itemC));

        assertFalse(greaterEqualB.passesFilter(null, itemEmpty));
        assertFalse(greaterEqualB.passesFilter(null, itemA));
        assertTrue(greaterEqualB.passesFilter(null, itemB));
        assertTrue(greaterEqualB.passesFilter(null, itemC));

        assertTrue(lessEqualB.passesFilter(null, itemEmpty));
        assertTrue(lessEqualB.passesFilter(null, itemA));
        assertTrue(lessEqualB.passesFilter(null, itemB));
        assertFalse(lessEqualB.passesFilter(null, itemC));
    }

    @Test
    public void testCompareWithNull() {
        // null comparisons: null is less than any other value
        assertFalse(equalB.passesFilter(null, itemNull));
        assertTrue(greaterB.passesFilter(null, itemNull));
        assertFalse(lessB.passesFilter(null, itemNull));
        assertTrue(greaterEqualB.passesFilter(null, itemNull));
        assertFalse(lessEqualB.passesFilter(null, itemNull));

        assertTrue(equalNull.passesFilter(null, itemNull));
        assertFalse(greaterNull.passesFilter(null, itemNull));
        assertFalse(lessNull.passesFilter(null, itemNull));
        assertTrue(greaterEqualNull.passesFilter(null, itemNull));
        assertTrue(lessEqualNull.passesFilter(null, itemNull));

        assertFalse(equalNull.passesFilter(null, itemA));
        assertFalse(greaterNull.passesFilter(null, itemA));
        assertTrue(lessNull.passesFilter(null, itemA));
        assertFalse(greaterEqualNull.passesFilter(null, itemA));
        assertTrue(lessEqualNull.passesFilter(null, itemA));
    }

    @Test
    public void testCompareInteger() {
        int negative = -1;
        int zero = 0;
        int positive = 1;

        Item itemNegative = new PropertysetItem();
        itemNegative.addItemProperty(PROPERTY1,
                new ObjectProperty<Integer>(negative, Integer.class));
        Item itemZero = new PropertysetItem();
        itemZero.addItemProperty(PROPERTY1,
                new ObjectProperty<Integer>(zero, Integer.class));
        Item itemPositive = new PropertysetItem();
        itemPositive.addItemProperty(PROPERTY1,
                new ObjectProperty<Integer>(positive, Integer.class));

        Filter equalZero = new Equal(PROPERTY1, zero);
        assertFalse(equalZero.passesFilter(null, itemNegative));
        assertTrue(equalZero.passesFilter(null, itemZero));
        assertFalse(equalZero.passesFilter(null, itemPositive));

        Filter isPositive = new Greater(PROPERTY1, zero);
        assertFalse(isPositive.passesFilter(null, itemNegative));
        assertFalse(isPositive.passesFilter(null, itemZero));
        assertTrue(isPositive.passesFilter(null, itemPositive));

        Filter isNegative = new Less(PROPERTY1, zero);
        assertTrue(isNegative.passesFilter(null, itemNegative));
        assertFalse(isNegative.passesFilter(null, itemZero));
        assertFalse(isNegative.passesFilter(null, itemPositive));

        Filter isNonNegative = new GreaterOrEqual(PROPERTY1, zero);
        assertFalse(isNonNegative.passesFilter(null, itemNegative));
        assertTrue(isNonNegative.passesFilter(null, itemZero));
        assertTrue(isNonNegative.passesFilter(null, itemPositive));

        Filter isNonPositive = new LessOrEqual(PROPERTY1, zero);
        assertTrue(isNonPositive.passesFilter(null, itemNegative));
        assertTrue(isNonPositive.passesFilter(null, itemZero));
        assertFalse(isNonPositive.passesFilter(null, itemPositive));
    }

    @Test
    public void testCompareBigDecimal() {
        BigDecimal negative = new BigDecimal(-1);
        BigDecimal zero = new BigDecimal(0);
        BigDecimal positive = new BigDecimal(1);
        positive.setScale(1);
        BigDecimal positiveScaleTwo = new BigDecimal(1).setScale(2);

        Item itemNegative = new PropertysetItem();
        itemNegative.addItemProperty(PROPERTY1,
                new ObjectProperty<BigDecimal>(negative, BigDecimal.class));
        Item itemZero = new PropertysetItem();
        itemZero.addItemProperty(PROPERTY1,
                new ObjectProperty<BigDecimal>(zero, BigDecimal.class));
        Item itemPositive = new PropertysetItem();
        itemPositive.addItemProperty(PROPERTY1,
                new ObjectProperty<BigDecimal>(positive, BigDecimal.class));
        Item itemPositiveScaleTwo = new PropertysetItem();
        itemPositiveScaleTwo.addItemProperty(PROPERTY1,
                new ObjectProperty<BigDecimal>(positiveScaleTwo,
                        BigDecimal.class));

        Filter equalZero = new Equal(PROPERTY1, zero);
        assertFalse(equalZero.passesFilter(null, itemNegative));
        assertTrue(equalZero.passesFilter(null, itemZero));
        assertFalse(equalZero.passesFilter(null, itemPositive));

        Filter isPositive = new Greater(PROPERTY1, zero);
        assertFalse(isPositive.passesFilter(null, itemNegative));
        assertFalse(isPositive.passesFilter(null, itemZero));
        assertTrue(isPositive.passesFilter(null, itemPositive));

        Filter isNegative = new Less(PROPERTY1, zero);
        assertTrue(isNegative.passesFilter(null, itemNegative));
        assertFalse(isNegative.passesFilter(null, itemZero));
        assertFalse(isNegative.passesFilter(null, itemPositive));

        Filter isNonNegative = new GreaterOrEqual(PROPERTY1, zero);
        assertFalse(isNonNegative.passesFilter(null, itemNegative));
        assertTrue(isNonNegative.passesFilter(null, itemZero));
        assertTrue(isNonNegative.passesFilter(null, itemPositive));

        Filter isNonPositive = new LessOrEqual(PROPERTY1, zero);
        assertTrue(isNonPositive.passesFilter(null, itemNegative));
        assertTrue(isNonPositive.passesFilter(null, itemZero));
        assertFalse(isNonPositive.passesFilter(null, itemPositive));

        Filter isPositiveScaleTwo = new Equal(PROPERTY1, positiveScaleTwo);
        assertTrue(isPositiveScaleTwo.passesFilter(null, itemPositiveScaleTwo));
        assertTrue(isPositiveScaleTwo.passesFilter(null, itemPositive));
    }

    @Test
    public void testCompareDate() {
        Date now = new Date();
        // new Date() is only accurate to the millisecond, so repeating it gives
        // the same date
        Date earlier = new Date(now.getTime() - 1);
        Date later = new Date(now.getTime() + 1);

        Item itemEarlier = new PropertysetItem();
        itemEarlier.addItemProperty(PROPERTY1,
                new ObjectProperty<Date>(earlier, Date.class));
        Item itemNow = new PropertysetItem();
        itemNow.addItemProperty(PROPERTY1,
                new ObjectProperty<Date>(now, Date.class));
        Item itemLater = new PropertysetItem();
        itemLater.addItemProperty(PROPERTY1,
                new ObjectProperty<Date>(later, Date.class));

        Filter equalNow = new Equal(PROPERTY1, now);
        assertFalse(equalNow.passesFilter(null, itemEarlier));
        assertTrue(equalNow.passesFilter(null, itemNow));
        assertFalse(equalNow.passesFilter(null, itemLater));

        Filter after = new Greater(PROPERTY1, now);
        assertFalse(after.passesFilter(null, itemEarlier));
        assertFalse(after.passesFilter(null, itemNow));
        assertTrue(after.passesFilter(null, itemLater));

        Filter before = new Less(PROPERTY1, now);
        assertTrue(before.passesFilter(null, itemEarlier));
        assertFalse(before.passesFilter(null, itemNow));
        assertFalse(before.passesFilter(null, itemLater));

        Filter afterOrNow = new GreaterOrEqual(PROPERTY1, now);
        assertFalse(afterOrNow.passesFilter(null, itemEarlier));
        assertTrue(afterOrNow.passesFilter(null, itemNow));
        assertTrue(afterOrNow.passesFilter(null, itemLater));

        Filter beforeOrNow = new LessOrEqual(PROPERTY1, now);
        assertTrue(beforeOrNow.passesFilter(null, itemEarlier));
        assertTrue(beforeOrNow.passesFilter(null, itemNow));
        assertFalse(beforeOrNow.passesFilter(null, itemLater));
    }

    @Test
    public void testCompareAppliesToProperty() {
        Filter filterA = new Equal("a", 1);
        Filter filterB = new Equal("b", 1);

        assertTrue(filterA.appliesToProperty("a"));
        assertFalse(filterA.appliesToProperty("b"));
        assertFalse(filterB.appliesToProperty("a"));
        assertTrue(filterB.appliesToProperty("b"));
    }

    @Test
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
        assertEquals(equalNull, equalNull);
        assertEquals(equalNull, equalNull2);
        assertFalse(equalNull.equals(equalNullProperty2));
        assertFalse(equalNull.equals(equalEmpty));
        assertFalse(equalNull.equals(equalB));

        assertEquals(equalEmpty, equalEmpty);
        assertFalse(equalEmpty.equals(equalNull));
        assertEquals(equalEmpty, equalEmpty2);
        assertFalse(equalEmpty.equals(equalEmptyProperty2));
        assertFalse(equalEmpty.equals(equalB));

        assertEquals(equalB, equalB);
        assertFalse(equalB.equals(equalNull));
        assertFalse(equalB.equals(equalEmpty));
        assertEquals(equalB, equalB2);
        assertFalse(equalB.equals(equalBProperty2));
        assertFalse(equalB.equals(equalA));

        assertEquals(greaterB, greaterB);
        assertFalse(greaterB.equals(lessB));
        assertFalse(greaterB.equals(greaterEqualB));
        assertFalse(greaterB.equals(lessEqualB));

        assertFalse(greaterNull.equals(greaterEmpty));
        assertFalse(greaterNull.equals(greaterB));
        assertFalse(greaterEmpty.equals(greaterNull));
        assertFalse(greaterEmpty.equals(greaterB));
        assertFalse(greaterB.equals(greaterNull));
        assertFalse(greaterB.equals(greaterEmpty));

        // hashCode()
        assertEquals(equalNull.hashCode(), equalNull2.hashCode());
        assertEquals(equalEmpty.hashCode(), equalEmpty2.hashCode());
        assertEquals(equalB.hashCode(), equalB2.hashCode());
    }

}
