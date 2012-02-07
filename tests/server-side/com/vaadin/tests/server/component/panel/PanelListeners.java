package com.vaadin.tests.server.component.panel;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTest;
import com.vaadin.ui.Panel;

public class PanelListeners extends AbstractListenerMethodsTest {
    public void testClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Panel.class, ClickEvent.class,
                ClickListener.class);
    }
}
