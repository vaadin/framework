package com.vaadin.tests.server.components;

import com.vaadin.shared.Registration;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.ResizeEvent;
import com.vaadin.ui.Window.ResizeListener;
import com.vaadin.ui.Window.WindowBeforeCloseListener;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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

        // Ensure listener was called once
        EasyMock.verify(cl);

        // Remove the listener and send close event -> should not end up in
        // listener
        windowCloseListenerRegistration.remove();
        sendClose(window);

        // Ensure listener still has been called only once
        EasyMock.verify(cl);

    }

    @Test
    public void testPreCloseListener() {
        WindowBeforeCloseListener pcl = EasyMock.createMock(WindowBeforeCloseListener.class);

        // Expectations
        pcl.beforeWindowClose(EasyMock.isA(Window.WindowBeforeCloseEvent.class));

        // Start actual test
        EasyMock.replay(pcl);

        // Add listener and send a close event -> should end up in listener once
        Registration windowPreCloseListenerRegistration = window
                .addWindowBeforeCloseListener(pcl);
        sendClose(window);

        // Ensure listener was called once
        EasyMock.verify(pcl);

        // Remove the listener and send close event -> should not end up in
        // listener
        windowPreCloseListenerRegistration.remove();
        sendClose(window);

        // Ensure listener still has been called only once
        EasyMock.verify(pcl);
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
        Map<String, Object> variables = new HashMap<>();
        variables.put("close", true);
        window.changeVariables(window, variables);
    }
}
