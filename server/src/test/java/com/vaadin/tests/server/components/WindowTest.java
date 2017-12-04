package com.vaadin.tests.server.components;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.shared.Registration;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.ResizeEvent;
import com.vaadin.ui.Window.ResizeListener;

public class WindowTest {

    private Window window;

    @Before
    public void setUp() {
        window = new Window();
        new LegacyWindow().addWindow(window);
    }

    @Test
    public void testCloseListener() {
        CloseListener cl = EasyMock.createMock(Window.CloseListener.class);

        // Expectations
        cl.windowClose(EasyMock.isA(CloseEvent.class));

        // Start actual test
        EasyMock.replay(cl);

        // Add listener and send a close event -> should end up in listener once
        Registration windowCloseListenerRegistration = window
                .addCloseListener(cl);
        sendClose(window);
        System.out.println("window: " + window);
        // Ensure listener was called once
        EasyMock.verify(cl);

        System.out.println("window: " + window);
        // Remove the listener and send close event -> should not end up in
        // listener
        windowCloseListenerRegistration.remove();
        System.out.println("window: " + window);
        sendClose(window);
        System.out.println("end.");
        // Ensure listener still has been called only once
        EasyMock.verify(cl);

    }

    @Test
    public void testResizeListener() {
        ResizeListener rl = EasyMock.createMock(Window.ResizeListener.class);

        // Expectations
        rl.windowResized(EasyMock.isA(ResizeEvent.class));

        // Start actual test
        EasyMock.replay(rl);

        // Add listener and send a resize event -> should end up in listener
        // once
        Registration windowResizeListenerRegistration = window
                .addResizeListener(rl);
        sendResize(window);

        // Ensure listener was called once
        EasyMock.verify(rl);

        // Remove the listener and send close event -> should not end up in
        // listener
        windowResizeListenerRegistration.remove();
        sendResize(window);

        // Ensure listener still has been called only once
        EasyMock.verify(rl);

    }

    private void sendResize(Window window2) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("height", 1234);
        window.changeVariables(window, variables);

    }

    private static void sendClose(Window window) {
        System.out.println("sendClose: w: "+window.getClass());
        Map<String, Object> variables = new HashMap<>();
        variables.put("close", true);
        window.changeVariables(window, variables);
    }
}
