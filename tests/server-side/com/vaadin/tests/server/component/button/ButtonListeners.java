package com.vaadin.tests.server.component.button;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ButtonListeners extends AbstractListenerMethodsTest {
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Button.class, FocusEvent.class,
                FocusListener.class);
    }

    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Button.class, BlurEvent.class,
                BlurListener.class);
    }

    public void testClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Button.class, ClickEvent.class,
                ClickListener.class);
    }
}
