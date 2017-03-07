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
package com.vaadin.tests.server.component.window;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class AddRemoveSubWindowTest {

    public class TestApp extends LegacyApplication {

        @Override
        public void init() {
            LegacyWindow w = new LegacyWindow("Main window");
            setMainWindow(w);
        }
    }

    @Test
    public void addSubWindow() {
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));
        TestApp app = new TestApp();
        app.init();
        Window subWindow = new Window("Sub window");
        UI mainWindow = app.getMainWindow();

        mainWindow.addWindow(subWindow);
        // Added to main window so the parent of the sub window should be the
        // main window
        assertEquals(subWindow.getParent(), mainWindow);

        try {
            mainWindow.addWindow(subWindow);
            assertTrue("Window.addWindow did not throw the expected exception",
                    false);
        } catch (IllegalArgumentException e) {
            // Should throw an exception as it has already been added to the
            // main window
        }

        // Try to add the same sub window to another window
        try {
            LegacyWindow w = new LegacyWindow();
            w.addWindow(subWindow);
            assertTrue("Window.addWindow did not throw the expected exception",
                    false);
        } catch (IllegalArgumentException e) {
            // Should throw an exception as it has already been added to the
            // main window
        }

    }

    @Test
    public void removeSubWindow() {
        TestApp app = new TestApp();
        app.init();
        Window subWindow = new Window("Sub window");
        UI mainWindow = app.getMainWindow();
        mainWindow.addWindow(subWindow);

        // Added to main window so the parent of the sub window should be the
        // main window
        assertEquals(subWindow.getParent(), mainWindow);

        // Parent should still be set
        assertEquals(subWindow.getParent(), mainWindow);

        // Remove from the main window and assert it has been removed
        boolean removed = mainWindow.removeWindow(subWindow);
        assertTrue("Window was not removed correctly", removed);
        assertNull(subWindow.getParent());
    }
}
