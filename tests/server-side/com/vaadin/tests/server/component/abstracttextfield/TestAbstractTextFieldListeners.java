package com.vaadin.tests.server.component.abstracttextfield;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTest;
import com.vaadin.ui.TextField;

public class TestAbstractTextFieldListeners extends AbstractListenerMethodsTest {
    public void testTextChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(TextField.class, TextChangeEvent.class,
                TextChangeListener.class);
    }

    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(TextField.class, FocusEvent.class,
                FocusListener.class);
    }

    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(TextField.class, BlurEvent.class,
                BlurListener.class);
    }
}
