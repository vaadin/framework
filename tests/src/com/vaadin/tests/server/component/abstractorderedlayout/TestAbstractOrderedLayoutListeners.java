package com.vaadin.tests.server.component.abstractorderedlayout;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.tests.server.component.ListenerMethods;
import com.vaadin.ui.VerticalLayout;

public class TestAbstractOrderedLayoutListeners extends ListenerMethods {
    public void testLayoutClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(VerticalLayout.class, LayoutClickEvent.class,
                LayoutClickListener.class);
    }
}
