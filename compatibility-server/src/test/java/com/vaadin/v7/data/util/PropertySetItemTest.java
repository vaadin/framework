/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.v7.data.util;

import java.util.Iterator;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.Item.PropertySetChangeEvent;
import com.vaadin.v7.data.Item.PropertySetChangeListener;

public class PropertySetItemTest {

    private static final String ID1 = "id1";
    private static final String ID2 = "id2";
    private static final String ID3 = "id3";

    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final String VALUE3 = "value3";

    private ObjectProperty<String> prop1;
    private ObjectProperty<String> prop2;
    private ObjectProperty<String> prop3;

    private PropertySetChangeListener propertySetListenerMock;
    private PropertySetChangeListener propertySetListenerMock2;

    @Before
    public void setUp() {
        prop1 = new ObjectProperty<String>(VALUE1, String.class);
        prop2 = new ObjectProperty<String>(VALUE2, String.class);
        prop3 = new ObjectProperty<String>(VALUE3, String.class);

        propertySetListenerMock = EasyMock
                .createStrictMock(PropertySetChangeListener.class);
        propertySetListenerMock2 = EasyMock
                .createMock(PropertySetChangeListener.class);
    }

    @After
    public void tearDown() {
        prop1 = null;
        prop2 = null;
        prop3 = null;

        propertySetListenerMock = null;
        propertySetListenerMock2 = null;
    }

    private PropertysetItem createPropertySetItem() {
        return new PropertysetItem();
    }

    @Test
    public void testEmptyItem() {
        PropertysetItem item = createPropertySetItem();
        Assert.assertNotNull(item.getItemPropertyIds());
        Assert.assertEquals(0, item.getItemPropertyIds().size());
    }

    @Test
    public void testGetProperty() {
        PropertysetItem item = createPropertySetItem();

        Assert.assertNull(item.getItemProperty(ID1));

        item.addItemProperty(ID1, prop1);

        Assert.assertEquals(prop1, item.getItemProperty(ID1));
        Assert.assertNull(item.getItemProperty(ID2));
    }

    @Test
    public void testAddSingleProperty() {
        PropertysetItem item = createPropertySetItem();

        item.addItemProperty(ID1, prop1);
        Assert.assertEquals(1, item.getItemPropertyIds().size());
        Object firstValue = item.getItemPropertyIds().iterator().next();
        Assert.assertEquals(ID1, firstValue);
        Assert.assertEquals(prop1, item.getItemProperty(ID1));
    }

    @Test
    public void testAddMultipleProperties() {
        PropertysetItem item = createPropertySetItem();

        item.addItemProperty(ID1, prop1);
        Assert.assertEquals(1, item.getItemPropertyIds().size());
        Assert.assertEquals(prop1, item.getItemProperty(ID1));

        item.addItemProperty(ID2, prop2);
        Assert.assertEquals(2, item.getItemPropertyIds().size());
        Assert.assertEquals(prop1, item.getItemProperty(ID1));
        Assert.assertEquals(prop2, item.getItemProperty(ID2));

        item.addItemProperty(ID3, prop3);
        Assert.assertEquals(3, item.getItemPropertyIds().size());
    }

    @Test
    public void testAddedPropertyOrder() {
        PropertysetItem item = createPropertySetItem();
        item.addItemProperty(ID1, prop1);
        item.addItemProperty(ID2, prop2);
        item.addItemProperty(ID3, prop3);

        Iterator<?> it = item.getItemPropertyIds().iterator();
        Assert.assertEquals(ID1, it.next());
        Assert.assertEquals(ID2, it.next());
        Assert.assertEquals(ID3, it.next());
    }

    @Test
    public void testAddPropertyTwice() {
        PropertysetItem item = createPropertySetItem();
        Assert.assertTrue(item.addItemProperty(ID1, prop1));
        Assert.assertFalse(item.addItemProperty(ID1, prop1));

        Assert.assertEquals(1, item.getItemPropertyIds().size());
        Assert.assertEquals(prop1, item.getItemProperty(ID1));
    }

    @Test
    public void testCannotChangeProperty() {
        PropertysetItem item = createPropertySetItem();
        Assert.assertTrue(item.addItemProperty(ID1, prop1));

        Assert.assertEquals(prop1, item.getItemProperty(ID1));

        Assert.assertFalse(item.addItemProperty(ID1, prop2));

        Assert.assertEquals(1, item.getItemPropertyIds().size());
        Assert.assertEquals(prop1, item.getItemProperty(ID1));
    }

    @Test
    public void testRemoveProperty() {
        PropertysetItem item = createPropertySetItem();
        item.addItemProperty(ID1, prop1);
        item.removeItemProperty(ID1);

        Assert.assertEquals(0, item.getItemPropertyIds().size());
        Assert.assertNull(item.getItemProperty(ID1));
    }

    @Test
    public void testRemovePropertyOrder() {
        PropertysetItem item = createPropertySetItem();
        item.addItemProperty(ID1, prop1);
        item.addItemProperty(ID2, prop2);
        item.addItemProperty(ID3, prop3);

        item.removeItemProperty(ID2);

        Iterator<?> it = item.getItemPropertyIds().iterator();
        Assert.assertEquals(ID1, it.next());
        Assert.assertEquals(ID3, it.next());
    }

    @Test
    public void testRemoveNonExistentListener() {
        PropertysetItem item = createPropertySetItem();
        item.removeListener(propertySetListenerMock);
    }

    @Test
    public void testRemoveListenerTwice() {
        PropertysetItem item = createPropertySetItem();
        item.addListener(propertySetListenerMock);
        item.removeListener(propertySetListenerMock);
        item.removeListener(propertySetListenerMock);
    }

    @Test
    public void testAddPropertyNotification() {
        // exactly one notification each time
        PropertysetItem item = createPropertySetItem();

        // Expectations and start test
        propertySetListenerMock.itemPropertySetChange(
                EasyMock.isA(PropertySetChangeEvent.class));
        EasyMock.replay(propertySetListenerMock);

        // Add listener and add a property -> should end up in listener once
        item.addListener(propertySetListenerMock);
        item.addItemProperty(ID1, prop1);

        // Ensure listener was called once
        EasyMock.verify(propertySetListenerMock);

        // Remove the listener -> should not end up in listener when adding a
        // property
        item.removeListener(propertySetListenerMock);
        item.addItemProperty(ID2, prop2);

        // Ensure listener still has been called only once
        EasyMock.verify(propertySetListenerMock);
    }

    @Test
    public void testRemovePropertyNotification() {
        // exactly one notification each time
        PropertysetItem item = createPropertySetItem();
        item.addItemProperty(ID1, prop1);
        item.addItemProperty(ID2, prop2);

        // Expectations and start test
        propertySetListenerMock.itemPropertySetChange(
                EasyMock.isA(PropertySetChangeEvent.class));
        EasyMock.replay(propertySetListenerMock);

        // Add listener and add a property -> should end up in listener once
        item.addListener(propertySetListenerMock);
        item.removeItemProperty(ID1);

        // Ensure listener was called once
        EasyMock.verify(propertySetListenerMock);

        // Remove the listener -> should not end up in listener
        item.removeListener(propertySetListenerMock);
        item.removeItemProperty(ID2);

        // Ensure listener still has been called only once
        EasyMock.verify(propertySetListenerMock);
    }

    @Test
    public void testItemEqualsNull() {
        PropertysetItem item = createPropertySetItem();

        Assert.assertFalse(item.equals(null));
    }

    @Test
    public void testEmptyItemEquals() {
        PropertysetItem item1 = createPropertySetItem();
        PropertysetItem item2 = createPropertySetItem();

        Assert.assertTrue(item1.equals(item2));
    }

    @Test
    public void testItemEqualsSingleProperty() {
        PropertysetItem item1 = createPropertySetItem();
        PropertysetItem item2 = createPropertySetItem();
        item2.addItemProperty(ID1, prop1);
        PropertysetItem item3 = createPropertySetItem();
        item3.addItemProperty(ID1, prop1);
        PropertysetItem item4 = createPropertySetItem();
        item4.addItemProperty(ID1, prop2);
        PropertysetItem item5 = createPropertySetItem();
        item5.addItemProperty(ID2, prop2);

        Assert.assertFalse(item1.equals(item2));
        Assert.assertFalse(item1.equals(item3));
        Assert.assertFalse(item1.equals(item4));
        Assert.assertFalse(item1.equals(item5));

        Assert.assertTrue(item2.equals(item3));
        Assert.assertFalse(item2.equals(item4));
        Assert.assertFalse(item2.equals(item5));

        Assert.assertFalse(item3.equals(item4));
        Assert.assertFalse(item3.equals(item5));

        Assert.assertFalse(item4.equals(item5));

        Assert.assertFalse(item2.equals(item1));
    }

    @Test
    public void testItemEqualsMultipleProperties() {
        PropertysetItem item1 = createPropertySetItem();
        item1.addItemProperty(ID1, prop1);

        PropertysetItem item2 = createPropertySetItem();
        item2.addItemProperty(ID1, prop1);
        item2.addItemProperty(ID2, prop2);

        PropertysetItem item3 = createPropertySetItem();
        item3.addItemProperty(ID1, prop1);
        item3.addItemProperty(ID2, prop2);

        Assert.assertFalse(item1.equals(item2));

        Assert.assertTrue(item2.equals(item3));
    }

    @Test
    public void testItemEqualsPropertyOrder() {
        PropertysetItem item1 = createPropertySetItem();
        item1.addItemProperty(ID1, prop1);
        item1.addItemProperty(ID2, prop2);

        PropertysetItem item2 = createPropertySetItem();
        item2.addItemProperty(ID2, prop2);
        item2.addItemProperty(ID1, prop1);

        Assert.assertFalse(item1.equals(item2));
    }

    @Test
    public void testEqualsSingleListener() {
        PropertysetItem item1 = createPropertySetItem();
        PropertysetItem item2 = createPropertySetItem();

        item1.addListener(propertySetListenerMock);

        Assert.assertFalse(item1.equals(item2));
        Assert.assertFalse(item2.equals(item1));

        item2.addListener(propertySetListenerMock);

        Assert.assertTrue(item1.equals(item2));
        Assert.assertTrue(item2.equals(item1));
    }

    @Test
    public void testEqualsMultipleListeners() {
        PropertysetItem item1 = createPropertySetItem();
        PropertysetItem item2 = createPropertySetItem();

        item1.addListener(propertySetListenerMock);
        item1.addListener(propertySetListenerMock2);

        item2.addListener(propertySetListenerMock);

        Assert.assertFalse(item1.equals(item2));
        Assert.assertFalse(item2.equals(item1));

        item2.addListener(propertySetListenerMock2);

        Assert.assertTrue(item1.equals(item2));
        Assert.assertTrue(item2.equals(item1));
    }

    @Test
    public void testEqualsAddRemoveListener() {
        PropertysetItem item1 = createPropertySetItem();
        PropertysetItem item2 = createPropertySetItem();

        item1.addListener(propertySetListenerMock);
        item1.removeListener(propertySetListenerMock);

        Assert.assertTrue(item1.equals(item2));
        Assert.assertTrue(item2.equals(item1));
    }

    @Test
    public void testItemHashCodeEmpty() {
        PropertysetItem item1 = createPropertySetItem();
        PropertysetItem item2 = createPropertySetItem();

        Assert.assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    public void testItemHashCodeAddProperties() {
        PropertysetItem item1 = createPropertySetItem();
        PropertysetItem item2 = createPropertySetItem();

        Assert.assertEquals(item1.hashCode(), item2.hashCode());

        item1.addItemProperty(ID1, prop1);
        item1.addItemProperty(ID2, prop2);
        // hashCodes can be equal even if items are different

        item2.addItemProperty(ID1, prop1);
        item2.addItemProperty(ID2, prop2);
        // but here hashCodes must be equal
        Assert.assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    public void testItemHashCodeAddListeners() {
        PropertysetItem item1 = createPropertySetItem();
        PropertysetItem item2 = createPropertySetItem();

        Assert.assertEquals(item1.hashCode(), item2.hashCode());

        item1.addListener(propertySetListenerMock);
        // hashCodes can be equal even if items are different

        item2.addListener(propertySetListenerMock);
        // but here hashCodes must be equal
        Assert.assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    public void testItemHashCodeAddRemoveProperty() {
        PropertysetItem item1 = createPropertySetItem();
        PropertysetItem item2 = createPropertySetItem();

        item1.addItemProperty(ID1, prop1);
        item1.removeItemProperty(ID1);

        Assert.assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    public void testItemHashCodeAddRemoveListener() {
        PropertysetItem item1 = createPropertySetItem();
        PropertysetItem item2 = createPropertySetItem();

        item1.addListener(propertySetListenerMock);
        item1.removeListener(propertySetListenerMock);

        Assert.assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    public void testToString() {
        // toString() behavior is specified in the class javadoc
        PropertysetItem item = createPropertySetItem();

        Assert.assertEquals("", item.toString());

        item.addItemProperty(ID1, prop1);

        Assert.assertEquals(String.valueOf(prop1.getValue()), item.toString());

        item.addItemProperty(ID2, prop2);

        Assert.assertEquals(String.valueOf(prop1.getValue()) + " "
                + String.valueOf(prop2.getValue()), item.toString());
    }

}
