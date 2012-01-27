package com.vaadin.tests.server.component.embedded;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTest;
import com.vaadin.ui.Embedded;

public class EmbeddedListeners extends AbstractListenerMethodsTest {
    public void testClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Embedded.class, ClickEvent.class,
                ClickListener.class);
    }
}
