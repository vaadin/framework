package com.vaadin.tests.server.component.window;

import org.junit.Test;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.ResizeEvent;
import com.vaadin.ui.Window.ResizeListener;

public class WindowListenersTest extends AbstractListenerMethodsTestBase {

    @Test
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Window.class, FocusEvent.class,
                FocusListener.class);
    }

    @Test
    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Window.class, BlurEvent.class,
                BlurListener.class);
    }

    @Test
    public void testResizeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Window.class, ResizeEvent.class,
                ResizeListener.class);
    }

    @Test
    public void testCloseListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Window.class, CloseEvent.class,
                CloseListener.class);
    }
}
