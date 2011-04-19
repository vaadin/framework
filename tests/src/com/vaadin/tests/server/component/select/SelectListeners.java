package com.vaadin.tests.server.component.select;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.server.component.ListenerMethods;
import com.vaadin.ui.Select;

public class SelectListeners extends ListenerMethods {
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Select.class, FocusEvent.class,
                FocusListener.class);
    }

    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Select.class, BlurEvent.class,
                BlurListener.class);
    }
}