package com.vaadin.tests.server.component.datefield;

import org.junit.Test;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.AbstractDateField;

public class DateFieldListenersTest extends AbstractListenerMethodsTestBase {

    public static class TestDateField extends AbstractDateField {

    }

    @Test
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(TestDateField.class, FocusEvent.class,
                FocusListener.class);
    }

    @Test
    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(TestDateField.class, BlurEvent.class,
                BlurListener.class);
    }
}
