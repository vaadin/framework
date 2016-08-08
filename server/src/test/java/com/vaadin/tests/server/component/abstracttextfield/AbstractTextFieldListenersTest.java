package com.vaadin.tests.server.component.abstracttextfield;

import org.junit.Test;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;

public class AbstractTextFieldListenersTest extends
        AbstractListenerMethodsTestBase {

    @Test
    public void testTextChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(LegacyTextField.class, TextChangeEvent.class,
                TextChangeListener.class);
    }

    @Test
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(LegacyTextField.class, FocusEvent.class,
                FocusListener.class);
    }

    @Test
    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(LegacyTextField.class, BlurEvent.class,
                BlurListener.class);
    }
}
