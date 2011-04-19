package com.vaadin.tests.server.component.gridlayout;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.tests.server.component.ListenerMethods;
import com.vaadin.ui.GridLayout;

public class GridLayoutListeners extends ListenerMethods {
    public void testLayoutClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(GridLayout.class, LayoutClickEvent.class,
                LayoutClickListener.class);
    }
}
