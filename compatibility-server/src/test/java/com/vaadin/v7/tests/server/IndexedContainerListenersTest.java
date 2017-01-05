package com.vaadin.v7.tests.server;

import org.junit.Test;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.data.Container.PropertySetChangeEvent;
import com.vaadin.v7.data.Container.PropertySetChangeListener;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.IndexedContainer;

public class IndexedContainerListenersTest
        extends AbstractListenerMethodsTestBase {

    @Test
    public void testValueChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(IndexedContainer.class, ValueChangeEvent.class,
                ValueChangeListener.class);
    }

    @Test
    public void testPropertySetChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(IndexedContainer.class,
                PropertySetChangeEvent.class, PropertySetChangeListener.class);
    }
}
