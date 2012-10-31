package com.vaadin.tests.server.components;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.ResizeEvent;
import com.vaadin.ui.Window.ResizeListener;

public class TestWindow extends TestCase {

    private Window window;

    @Override
    protected void setUp() throws Exception {
        window = new Window();
        new LegacyWindow().addWindow(window);
    }

    public void testCloseListener() {
        CloseListener cl = EasyMock.createMock(Window.CloseListener.class);

        // Expectations
        cl.windowClose(EasyMock.isA(CloseEvent.class));

        // Start actual test
        EasyMock.replay(cl);

        // Add listener and send a close event -> should end up in listener once
        window.addListener(cl);
        sendClose(window);

        // Ensure listener was called once
        EasyMock.verify(cl);

        // Remove the listener and send close event -> should not end up in
        // listener
        window.removeListener(cl);
        sendClose(window);

        // Ensure listener still has been called only once
        EasyMock.verify(cl);

    }

    public void testResizeListener() {
        ResizeListener rl = EasyMock.createMock(Window.ResizeListener.class);

        // Expectations
        rl.windowResized(EasyMock.isA(ResizeEvent.class));

        // Start actual test
        EasyMock.replay(rl);

        // Add listener and send a resize event -> should end up in listener
        // once
        window.addListener(rl);
        sendResize(window);

        // Ensure listener was called once
        EasyMock.verify(rl);

        // Remove the listener and send close event -> should not end up in
        // listener
        window.removeListener(rl);
        sendResize(window);

        // Ensure listener still has been called only once
        EasyMock.verify(rl);

    }

    private void sendResize(Window window2) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("height", 1234);
        window.changeVariables(window, variables);

    }

    private static void sendClose(Window window) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("close", true);
        window.changeVariables(window, variables);
    }
}
