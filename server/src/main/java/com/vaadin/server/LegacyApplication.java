/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.server;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.UI;

/**
 * A special application designed to help migrating applications from Vaadin 6
 * to Vaadin 7. The legacy application supports setting a main window, adding
 * additional browser level windows and defining the theme for the entire
 * application.
 * 
 * @deprecated As of 7.0. This class is only intended to ease migration and
 *             should not be used for new projects.
 * 
 * @since 7.0
 */
@Deprecated
public abstract class LegacyApplication implements ErrorHandler {
    private LegacyWindow mainWindow;
    private String theme;

    private Map<String, LegacyWindow> legacyUINames = new HashMap<String, LegacyWindow>();

    private boolean isRunning = true;

    /**
     * URL where the user is redirected to on application close, or null if
     * application is just closed without redirection.
     */
    private String logoutURL = null;
    private URL url;

    /**
     * Sets the main window of this application. Setting window as a main window
     * of this application also adds the window to this application.
     * 
     * @param mainWindow
     *            the UI to set as the default window
     */
    public void setMainWindow(LegacyWindow mainWindow) {
        if (this.mainWindow != null) {
            throw new IllegalStateException("mainWindow has already been set");
        }
        if (mainWindow.isAttached()) {
            throw new IllegalStateException(
                    "mainWindow is attached to another application");
        }
        if (UI.getCurrent() == null) {
            // Assume setting a main window from Application.init if there's
            // no current UI -> set the main window as the current UI
            UI.setCurrent(mainWindow);
        }
        addWindow(mainWindow);
        this.mainWindow = mainWindow;
    }

    public void doInit(URL url) {
        this.url = url;
        VaadinSession.getCurrent().setErrorHandler(this);
        init();
    }

    protected abstract void init();

    /**
     * Gets the mainWindow of the application.
     * 
     * <p>
     * The main window is the window attached to the application URL (
     * {@link #getURL()}) and thus which is show by default to the user.
     * </p>
     * <p>
     * Note that each application must have at least one main window.
     * </p>
     * 
     * @return the UI used as the default window
     */
    public LegacyWindow getMainWindow() {
        return mainWindow;
    }

    /**
     * Sets the application's theme.
     * <p>
     * Note that this theme can be overridden for a specific UI with
     * {@link VaadinSession#getThemeForUI(UI)}. Setting theme to be
     * <code>null</code> selects the default theme. For the available theme
     * names, see the contents of the VAADIN/themes directory.
     * </p>
     * 
     * @param theme
     *            the new theme for this application.
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }

    /**
     * Gets the application's theme. The application's theme is the default
     * theme used by all the uIs for which a theme is not explicitly defined. If
     * the application theme is not explicitly set, <code>null</code> is
     * returned.
     * 
     * @return the name of the application's theme.
     */
    public String getTheme() {
        return theme;
    }

    /**
     * <p>
     * Gets a UI by name. Returns <code>null</code> if the application is not
     * running or it does not contain a window corresponding to the name.
     * </p>
     * 
     * @param name
     *            the name of the requested window
     * @return a UI corresponding to the name, or <code>null</code> to use the
     *         default window
     */
    public LegacyWindow getWindow(String name) {
        return legacyUINames.get(name);
    }

    /**
     * Counter to get unique names for windows with no explicit name
     */
    private int namelessUIIndex = 0;

    /**
     * Adds a new browser level window to this application. Please note that UI
     * doesn't have a name that is used in the URL - to add a named window you
     * should instead use {@link #addWindow(UI, String)}
     * 
     * @param uI
     *            the UI window to add to the application
     * @return returns the name that has been assigned to the window
     * 
     * @see #addWindow(UI, String)
     */
    public void addWindow(LegacyWindow uI) {
        if (uI.getName() == null) {
            String name = Integer.toString(namelessUIIndex++);
            uI.setName(name);
        }

        uI.setApplication(this);

        legacyUINames.put(uI.getName(), uI);
        uI.setSession(VaadinSession.getCurrent());
    }

    /**
     * Removes the specified window from the application. This also removes all
     * name mappings for the window (see {@link #addWindow(UI, String) and
     * #getWindowName(UI)}.
     * 
     * <p>
     * Note that removing window from the application does not close the browser
     * window - the window is only removed from the server-side.
     * </p>
     * 
     * @param uI
     *            the UI to remove
     */
    public void removeWindow(LegacyWindow uI) {
        for (Entry<String, LegacyWindow> entry : legacyUINames.entrySet()) {
            if (entry.getValue() == uI) {
                legacyUINames.remove(entry.getKey());
            }
        }
    }

    /**
     * Gets the set of windows contained by the application.
     * 
     * <p>
     * Note that the returned set of windows can not be modified.
     * </p>
     * 
     * @return the unmodifiable collection of windows.
     */
    public Collection<LegacyWindow> getWindows() {
        return Collections.unmodifiableCollection(legacyUINames.values());
    }

    @Override
    public void error(ErrorEvent event) {
        DefaultErrorHandler.doDefault(event);
    }

    public VaadinSession getContext() {
        return VaadinSession.getCurrent();
    }

    public void close() {
        isRunning = false;
        Collection<LegacyWindow> windows = getWindows();
        for (LegacyWindow legacyWindow : windows) {
            String logoutUrl = getLogoutURL();
            if (logoutUrl == null) {
                URL url = getURL();
                if (url != null) {
                    logoutUrl = url.toString();
                }
            }
            if (logoutUrl != null) {
                legacyWindow.getPage().setLocation(logoutUrl);
            }
            legacyWindow.close();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public URL getURL() {
        return url;
    }

    /**
     * Returns the URL user is redirected to on application close. If the URL is
     * <code>null</code>, the application is closed normally as defined by the
     * application running environment.
     * <p>
     * Desktop application just closes the application window and
     * web-application redirects the browser to application main URL.
     * </p>
     * 
     * @return the URL.
     */
    public String getLogoutURL() {
        return logoutURL;
    }

    /**
     * Sets the URL user is redirected to on application close. If the URL is
     * <code>null</code>, the application is closed normally as defined by the
     * application running environment: Desktop application just closes the
     * application window and web-application redirects the browser to
     * application main URL.
     * 
     * @param logoutURL
     *            the logoutURL to set.
     */
    public void setLogoutURL(String logoutURL) {
        this.logoutURL = logoutURL;
    }
}
