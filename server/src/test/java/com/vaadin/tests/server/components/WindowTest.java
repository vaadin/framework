/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
