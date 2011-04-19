package com.vaadin.tests.server.component.embedded;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.tests.server.component.ListenerMethods;
import com.vaadin.ui.Embedded;

public class EmbeddedListeners extends ListenerMethods {
    public void testClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Embedded.class, ClickEvent.class,
                ClickListener.class);
    }
}
