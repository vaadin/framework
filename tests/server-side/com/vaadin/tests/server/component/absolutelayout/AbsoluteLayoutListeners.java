package com.vaadin.tests.server.component.absolutelayout;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTest;
import com.vaadin.ui.AbsoluteLayout;

public class AbsoluteLayoutListeners extends AbstractListenerMethodsTest {
    public void testLayoutClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(AbsoluteLayout.class, LayoutClickEvent.class,
                LayoutClickListener.class);
    }
}
