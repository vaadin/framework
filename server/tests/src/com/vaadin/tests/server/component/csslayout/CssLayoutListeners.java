package com.vaadin.tests.server.component.csslayout;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTest;
import com.vaadin.ui.CssLayout;

public class CssLayoutListeners extends AbstractListenerMethodsTest {
    public void testLayoutClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(CssLayout.class, LayoutClickEvent.class,
                LayoutClickListener.class);
    }
}
