package com.vaadin.tests.server;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.data.Item.PropertySetChangeEvent;
import com.vaadin.v7.data.Item.PropertySetChangeListener;
import com.vaadin.v7.data.util.PropertysetItem;

public class PropertysetItemListenersTest
        extends AbstractListenerMethodsTestBase {
    public void testPropertySetChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(PropertysetItem.class,
                PropertySetChangeEvent.class, PropertySetChangeListener.class);
    }
}
