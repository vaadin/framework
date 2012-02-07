package com.vaadin.tests.server.component.optiongroup;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTest;
import com.vaadin.ui.OptionGroup;

public class OptionGroupListeners extends AbstractListenerMethodsTest {
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(OptionGroup.class, FocusEvent.class,
                FocusListener.class);
    }

    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(OptionGroup.class, BlurEvent.class,
                BlurListener.class);
    }
}
