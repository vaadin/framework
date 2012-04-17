package com.vaadin.tests.server;

import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.server.component.AbstractListenerMethodsTest;

public class TestAbstractInMemoryContainerListeners extends
        AbstractListenerMethodsTest {
    public void testItemSetChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(IndexedContainer.class,
                ItemSetChangeEvent.class, ItemSetChangeListener.class);
    }
}
