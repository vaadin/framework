package com.vaadin.tests.server;

import com.vaadin.data.Item.PropertySetChangeEvent;
import com.vaadin.data.Item.PropertySetChangeListener;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;

public class PropertysetItemListenersTest extends
        AbstractListenerMethodsTestBase {
    public void testPropertySetChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(PropertysetItem.class,
                PropertySetChangeEvent.class, PropertySetChangeListener.class);
    }
}
