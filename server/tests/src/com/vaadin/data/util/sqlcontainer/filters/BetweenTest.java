package com.vaadin.data.util.sqlcontainer.filters;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Between;

public class BetweenTest {

    private Item itemWithPropertyValue(Object propertyId, Object value) {
        Property<?> property = EasyMock.createMock(Property.class);
        property.getValue();
        EasyMock.expectLastCall().andReturn(value).anyTimes();
        EasyMock.replay(property);

        Item item = EasyMock.createMock(Item.class);
        item.getItemProperty(propertyId);
        EasyMock.expectLastCall().andReturn(property).anyTimes();
        EasyMock.replay(item);
        return item;
    }

    @Test
    public void passesFilter_valueIsInRange_shouldBeTrue() {
        Item item = itemWithPropertyValue("foo", 15);
        Between between = new Between("foo", 1, 30);
        Assert.assertTrue(between.passesFilter("foo", item));
    }

    @Test
    public void passesFilter_valueIsOutOfRange_shouldBeFalse() {
        Item item = itemWithPropertyValue("foo", 15);
        Between between = new Between("foo", 0, 2);
        Assert.assertFalse(between.passesFilter("foo", item));
    }

    @Test
    public void passesFilter_valueNotComparable_shouldBeFalse() {
        Item item = itemWithPropertyValue("foo", new Object());
        Between between = new Between("foo", 0, 2);
        Assert.assertFalse(between.passesFilter("foo", item));
    }

    @Test
    public void appliesToProperty_differentProperties_shoudlBeFalse() {
        Between between = new Between("foo", 0, 2);
        Assert.assertFalse(between.appliesToProperty("bar"));
    }

    @Test
    public void appliesToProperty_sameProperties_shouldBeTrue() {
        Between between = new Between("foo", 0, 2);
        Assert.assertTrue(between.appliesToProperty("foo"));
    }

    @Test
    public void hashCode_equalInstances_shouldBeEqual() {
        Between b1 = new Between("foo", 0, 2);
        Between b2 = new Between("foo", 0, 2);
        Assert.assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void equals_differentObjects_shouldBeFalse() {
        Between b1 = new Between("foo", 0, 2);
        Object obj = new Object();
        Assert.assertFalse(b1.equals(obj));
    }

    @Test
    public void equals_sameInstance_shouldBeTrue() {
        Between b1 = new Between("foo", 0, 2);
        Between b2 = b1;
        Assert.assertTrue(b1.equals(b2));
    }

    @Test
    public void equals_equalInstances_shouldBeTrue() {
        Between b1 = new Between("foo", 0, 2);
        Between b2 = new Between("foo", 0, 2);
        Assert.assertTrue(b1.equals(b2));
    }

    @Test
    public void equals_equalInstances2_shouldBeTrue() {
        Between b1 = new Between(null, null, null);
        Between b2 = new Between(null, null, null);
        Assert.assertTrue(b1.equals(b2));
    }

    @Test
    public void equals_secondValueDiffers_shouldBeFalse() {
        Between b1 = new Between("foo", 0, 1);
        Between b2 = new Between("foo", 0, 2);
        Assert.assertFalse(b1.equals(b2));
    }

    @Test
    public void equals_firstAndSecondValueDiffers_shouldBeFalse() {
        Between b1 = new Between("foo", 0, null);
        Between b2 = new Between("foo", 1, 2);
        Assert.assertFalse(b1.equals(b2));
    }

    @Test
    public void equals_propertyAndFirstAndSecondValueDiffers_shouldBeFalse() {
        Between b1 = new Between("foo", null, 1);
        Between b2 = new Between("bar", 1, 2);
        Assert.assertFalse(b1.equals(b2));
    }

    @Test
    public void equals_propertiesDiffer_shouldBeFalse() {
        Between b1 = new Between(null, 0, 1);
        Between b2 = new Between("bar", 0, 1);
        Assert.assertFalse(b1.equals(b2));
    }
}
