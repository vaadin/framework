package com.vaadin.tests.server;

import org.junit.Test;

import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Container.PropertySetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;

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
