package com.vaadin.tests.server.component.optiongroup;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.OptionGroup;

public class OptionGroupListenersTest extends AbstractListenerMethodsTestBase {
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(OptionGroup.class, FocusEvent.class,
                FocusListener.class);
    }

    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(OptionGroup.class, BlurEvent.class,
                BlurListener.class);
    }
}
