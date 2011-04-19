package com.vaadin.tests.server.component.csslayout;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.tests.server.component.ListenerMethods;
import com.vaadin.ui.CssLayout;

public class CssLayoutListeners extends ListenerMethods {
    public void testLayoutClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(CssLayout.class, LayoutClickEvent.class,
                LayoutClickListener.class);
    }
}