package com.vaadin.tests.server;

import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Container.PropertySetChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;

public class AbstractBeanContainerListenersTest extends
        AbstractListenerMethodsTestBase {
    public void testPropertySetChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(BeanItemContainer.class,
                PropertySetChangeEvent.class, PropertySetChangeListener.class,
                new BeanItemContainer<PropertySetChangeListener>(
                        PropertySetChangeListener.class));
    }
}
