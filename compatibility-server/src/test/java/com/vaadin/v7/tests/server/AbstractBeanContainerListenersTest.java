package com.vaadin.v7.tests.server;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.data.Container.PropertySetChangeEvent;
import com.vaadin.v7.data.Container.PropertySetChangeListener;
import com.vaadin.v7.data.util.BeanItemContainer;

public class AbstractBeanContainerListenersTest
        extends AbstractListenerMethodsTestBase {
    public void testPropertySetChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(BeanItemContainer.class,
                PropertySetChangeEvent.class, PropertySetChangeListener.class,
                new BeanItemContainer<PropertySetChangeListener>(
                        PropertySetChangeListener.class));
    }
}
