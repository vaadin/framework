package com.vaadin.tests.server.component.csslayout;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.CssLayout;

public class CssLayoutListenersTest extends AbstractListenerMethodsTestBase {
    public void testLayoutClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(CssLayout.class, LayoutClickEvent.class,
                LayoutClickListener.class);
    }
}
