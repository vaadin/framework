package com.vaadin.v7.tests.server;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.data.Container.ItemSetChangeEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.util.IndexedContainer;

public class AbstractInMemoryContainerListenersTest
        extends AbstractListenerMethodsTestBase {
    public void testItemSetChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(IndexedContainer.class,
                ItemSetChangeEvent.class, ItemSetChangeListener.class);
    }
}
