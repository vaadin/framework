package com.vaadin.tests.server.component.datefield;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTest;
import com.vaadin.ui.DateField;

public class DateFieldListeners extends AbstractListenerMethodsTest {
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(DateField.class, FocusEvent.class,
                FocusListener.class);
    }

    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(DateField.class, BlurEvent.class,
                BlurListener.class);
    }
}
