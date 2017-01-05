package com.vaadin.v7.tests.server;

import org.junit.Test;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.data.Container.ItemSetChangeEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Container.PropertySetChangeEvent;
import com.vaadin.v7.data.Container.PropertySetChangeListener;
import com.vaadin.v7.data.util.IndexedContainer;

public class AbstractContainerListenersTest
        extends AbstractListenerMethodsTestBase {

    @Test
    public void testItemSetChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(IndexedContainer.class,
                ItemSetChangeEvent.class, ItemSetChangeListener.class);
    }

    @Test
    public void testPropertySetChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(IndexedContainer.class,
                PropertySetChangeEvent.class, PropertySetChangeListener.class);
    }
}
