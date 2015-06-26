package com.vaadin.tests.server.component.absolutelayout;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.AbsoluteLayout;

public class AbsoluteLayoutListenersTest extends
        AbstractListenerMethodsTestBase {
    public void testLayoutClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(AbsoluteLayout.class, LayoutClickEvent.class,
                LayoutClickListener.class);
    }
}
