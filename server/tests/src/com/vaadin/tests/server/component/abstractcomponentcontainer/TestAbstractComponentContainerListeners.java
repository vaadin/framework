package com.vaadin.tests.server.component.abstractcomponentcontainer;

import com.vaadin.tests.server.component.AbstractListenerMethodsTest;
import com.vaadin.ui.HasComponents.ComponentAttachEvent;
import com.vaadin.ui.HasComponents.ComponentAttachListener;
import com.vaadin.ui.HasComponents.ComponentDetachEvent;
import com.vaadin.ui.HasComponents.ComponentDetachListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class TestAbstractComponentContainerListeners extends
        AbstractListenerMethodsTest {
    public void testComponentDetachListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(HorizontalLayout.class,
                ComponentDetachEvent.class, ComponentDetachListener.class);
    }

    public void testComponentAttachListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(VerticalLayout.class,
                ComponentAttachEvent.class, ComponentAttachListener.class);
    }
}
