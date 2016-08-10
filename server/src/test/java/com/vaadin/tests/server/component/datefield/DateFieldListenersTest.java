package com.vaadin.tests.server.component.datefield;

import org.junit.Test;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.legacy.ui.LegacyDateField;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;

public class DateFieldListenersTest extends AbstractListenerMethodsTestBase {

    @Test
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(LegacyDateField.class, FocusEvent.class,
                FocusListener.class);
    }

    @Test
    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(LegacyDateField.class, BlurEvent.class,
                BlurListener.class);
    }
}
