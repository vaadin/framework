package com.vaadin.v7.tests.server.component.abstracttextfield;

import org.junit.Test;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.event.FieldEvents.TextChangeEvent;
import com.vaadin.v7.event.FieldEvents.TextChangeListener;
import com.vaadin.v7.ui.TextField;

public class AbstractTextFieldListenersTest
        extends AbstractListenerMethodsTestBase {

    @Test
    public void testTextChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(TextField.class, TextChangeEvent.class,
                TextChangeListener.class);
    }

    @Test
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(TextField.class, FocusEvent.class,
                FocusListener.class);
    }

    @Test
    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(TextField.class, BlurEvent.class,
                BlurListener.class);
    }
}
