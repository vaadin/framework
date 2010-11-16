package com.vaadin.tests.server.component.window;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.Application;
import com.vaadin.ui.Window;

public class AddRemoveSubWindow {

    public class TestApp extends Application {

        @Override
        public void init() {
            Window w = new Window("Main window");
            setMainWindow(w);
        }
    }

    @Test
    public void addSubWindow() {
        TestApp app = new TestApp();
        app.init();
        Window subWindow = new Window("Sub window");
        Window mainWindow = app.getMainWindow();

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
            Window w = new Window();
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
        Window mainWindow = app.getMainWindow();
        mainWindow.addWindow(subWindow);

        // Added to main window so the parent of the sub window should be the
        // main window
        assertEquals(subWindow.getParent(), mainWindow);

        // Remove from the wrong window, should result in an exception
        boolean removed = subWindow.removeWindow(subWindow);
        assertFalse("Window was removed even though it should not have been",
                removed);

        // Parent should still be set
        assertEquals(subWindow.getParent(), mainWindow);

        // Remove from the main window and assert it has been removed
        removed = mainWindow.removeWindow(subWindow);
        assertTrue("Window was not removed correctly", removed);
        assertNull(subWindow.getParent());
    }
}
