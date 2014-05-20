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
package com.vaadin.ui;

import java.net.MalformedURLException;
import java.net.URL;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.BorderStyle;

/**
 * Helper class to emulate the main window from Vaadin 6 using UIs. This class
 * should be used in the same way as Window used as a browser level window in
 * Vaadin 6 with {@link com.vaadin.server.LegacyApplication}
 */
@Deprecated
public class LegacyWindow extends UI {
    private String name;
    private LegacyApplication application;

    /**
     * Create a new legacy window
     */
    public LegacyWindow() {
        super(new VerticalLayout());
        ((VerticalLayout) getContent()).setMargin(true);
    }

    /**
     * Creates a new legacy window with the given caption
     * 
     * @param caption
     *            the caption of the window
     */
    public LegacyWindow(String caption) {
        super(new VerticalLayout());
        ((VerticalLayout) getContent()).setMargin(true);
        setCaption(caption);
    }

    /**
     * Creates a legacy window with the given caption and content layout
     * 
     * @param caption
     * @param content
     */
    public LegacyWindow(String caption, ComponentContainer content) {
        super(content);
        setCaption(caption);
    }

    @Override
    protected void init(VaadinRequest request) {
        // Just empty
    }

    public void setApplication(LegacyApplication application) {
        this.application = application;
    }

    public LegacyApplication getApplication() {
        return application;
    }

    /**
     * Gets the unique name of the window. The name of the window is used to
     * uniquely identify it.
     * <p>
     * The name also determines the URL that can be used for direct access to a
     * window. All windows can be accessed through
     * {@code http://host:port/app/win} where {@code http://host:port/app} is
     * the application URL (as returned by {@link LegacyApplication#getURL()}
     * and {@code win} is the window name.
     * </p>
     * <p>
     * Note! Portlets do not support direct window access through URLs.
     * </p>
     * 
     * @return the Name of the Window.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the unique name of the window. The name of the window is used to
     * uniquely identify it inside the application.
     * <p>
     * The name also determines the URL that can be used for direct access to a
     * window. All windows can be accessed through
     * {@code http://host:port/app/win} where {@code http://host:port/app} is
     * the application URL (as returned by {@link LegacyApplication#getURL()}
     * and {@code win} is the window name.
     * </p>
     * <p>
     * This method can only be called before the window is added to an
     * application.
     * <p>
     * Note! Portlets do not support direct window access through URLs.
     * </p>
     * 
     * @param name
     *            the new name for the window or null if the application should
     *            automatically assign a name to it
     * @throws IllegalStateException
     *             if the window is attached to an application
     */
    public void setName(String name) {
        this.name = name;
        // The name can not be changed in application
        if (isAttached()) {
            throw new IllegalStateException(
                    "Window name can not be changed while "
                            + "the window is in application");
        }

    }

    /**
     * Gets the full URL of the window. The returned URL is window specific and
     * can be used to directly refer to the window.
     * <p>
     * Note! This method can not be used for portlets.
     * </p>
     * 
     * @return the URL of the window or null if the window is not attached to an
     *         application
     */
    public URL getURL() {
        LegacyApplication application = getApplication();
        if (application == null) {
            return null;
        }

        try {
            return new URL(application.getURL(), getName() + "/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(
                    "Internal problem getting window URL, please report");
        }
    }

    /**
     * Opens the given resource in this UI. The contents of this UI is replaced
     * by the {@code Resource}.
     * 
     * @param resource
     *            the resource to show in this UI
     * 
     * @deprecated As of 7.0, use getPage().setLocation instead
     */
    @Deprecated
    public void open(Resource resource) {
        open(resource, null, false);
    }

    /* ********************************************************************* */

    /**
     * Opens the given resource in a window with the given name.
     * <p>
     * The supplied {@code windowName} is used as the target name in a
     * window.open call in the client. This means that special values such as
     * "_blank", "_self", "_top", "_parent" have special meaning. An empty or
     * <code>null</code> window name is also a special case.
     * </p>
     * <p>
     * "", null and "_self" as {@code windowName} all causes the resource to be
     * opened in the current window, replacing any old contents. For
     * downloadable content you should avoid "_self" as "_self" causes the
     * client to skip rendering of any other changes as it considers them
     * irrelevant (the page will be replaced by the resource). This can speed up
     * the opening of a resource, but it might also put the client side into an
     * inconsistent state if the window content is not completely replaced e.g.,
     * if the resource is downloaded instead of displayed in the browser.
     * </p>
     * <p>
     * "_blank" as {@code windowName} causes the resource to always be opened in
     * a new window or tab (depends on the browser and browser settings).
     * </p>
     * <p>
     * "_top" and "_parent" as {@code windowName} works as specified by the HTML
     * standard.
     * </p>
     * <p>
     * Any other {@code windowName} will open the resource in a window with that
     * name, either by opening a new window/tab in the browser or by replacing
     * the contents of an existing window with that name.
     * </p>
     * <p>
     * As of Vaadin 7.0.0, the functionality for opening a Resource in a Page
     * has been replaced with similar methods based on a String URL. This is
     * because the usage of Resource is problematic with memory management and
     * with security features in some browsers. Is is recommended to instead use
     * {@link Link} for starting downloads.
     * </p>
     * 
     * @param resource
     *            the resource.
     * @param windowName
     *            the name of the window.
     * @deprecated As of 7.0, use getPage().open instead
     */
    @Deprecated
    public void open(Resource resource, String windowName) {
        open(resource, windowName, true);
    }

    /**
     * Opens the given resource in a window with the given name and optionally
     * tries to force the resource to open in a new window instead of a new tab.
     * <p>
     * The supplied {@code windowName} is used as the target name in a
     * window.open call in the client. This means that special values such as
     * "_blank", "_self", "_top", "_parent" have special meaning. An empty or
     * <code>null</code> window name is also a special case.
     * </p>
     * <p>
     * "", null and "_self" as {@code windowName} all causes the resource to be
     * opened in the current window, replacing any old contents. For
     * downloadable content you should avoid "_self" as "_self" causes the
     * client to skip rendering of any other changes as it considers them
     * irrelevant (the page will be replaced by the resource). This can speed up
     * the opening of a resource, but it might also put the client side into an
     * inconsistent state if the window content is not completely replaced e.g.,
     * if the resource is downloaded instead of displayed in the browser.
     * </p>
     * <p>
     * "_blank" as {@code windowName} causes the resource to always be opened in
     * a new window or tab (depends on the browser and browser settings).
     * </p>
     * <p>
     * "_top" and "_parent" as {@code windowName} works as specified by the HTML
     * standard.
     * </p>
     * <p>
     * Any other {@code windowName} will open the resource in a window with that
     * name, either by opening a new window/tab in the browser or by replacing
     * the contents of an existing window with that name.
     * </p>
     * <p>
     * If {@code windowName} is set to open the resource in a new window or tab
     * and {@code tryToOpenAsPopup} is true, this method attempts to force the
     * browser to open a new window instead of a tab. NOTE: This is a
     * best-effort attempt and may not work reliably with all browsers and
     * different pop-up preferences. With most browsers using default settings,
     * {@code tryToOpenAsPopup} works properly.
     * </p>
     * <p>
     * As of Vaadin 7.0.0, the functionality for opening a Resource in a Page
     * has been replaced with similar methods based on a String URL. This is
     * because the usage of Resource is problematic with memory management and
     * with security features in some browsers. Is is recommended to instead use
     * {@link Link} for starting downloads.
     * </p>
     * 
     * @param resource
     *            the resource.
     * @param windowName
     *            the name of the window.
     * @param tryToOpenAsPopup
     *            Whether to try to force the resource to be opened in a new
     *            window
     * */
    public void open(Resource resource, String windowName,
            boolean tryToOpenAsPopup) {
        getPage().open(resource, windowName, tryToOpenAsPopup);
    }

    /**
     * Opens the given resource in a window with the given size, border and
     * name. For more information on the meaning of {@code windowName}, see
     * {@link #open(Resource, String)}.
     * <p>
     * As of Vaadin 7.0.0, the functionality for opening a Resource in a Page
     * has been replaced with similar methods based on a String URL. This is
     * because the usage of Resource is problematic with memory management and
     * with security features in some browsers. Is is recommended to instead use
     * {@link Link} for starting downloads.
     * </p>
     * 
     * @param resource
     *            the resource.
     * @param windowName
     *            the name of the window.
     * @param width
     *            the width of the window in pixels
     * @param height
     *            the height of the window in pixels
     * @param border
     *            the border style of the window.
     * @deprecated As of 7.0, use getPage().open instead
     */
    @Deprecated
    public void open(Resource resource, String windowName, int width,
            int height, BorderStyle border) {
        getPage().open(resource, windowName, width, height, border);
    }

    /**
     * Adds a new {@link BrowserWindowResizeListener} to this UI. The listener
     * will be notified whenever the browser window within which this UI resides
     * is resized.
     * 
     * @param resizeListener
     *            the listener to add
     * 
     * @see BrowserWindowResizeListener#browserWindowResized(BrowserWindowResizeEvent)
     * @see #setResizeLazy(boolean)
     * 
     * @deprecated As of 7.0, use the similarly named api in Page instead
     */
    @Deprecated
    public void addListener(BrowserWindowResizeListener resizeListener) {
        getPage().addListener(resizeListener);
    }

    /**
     * Removes a {@link BrowserWindowResizeListener} from this UI. The listener
     * will no longer be notified when the browser window is resized.
     * 
     * @param resizeListener
     *            the listener to remove
     * @deprecated As of 7.0, use the similarly named api in Page instead
     */
    @Deprecated
    public void removeListener(BrowserWindowResizeListener resizeListener) {
        getPage().removeListener(resizeListener);
    }

    /**
     * Gets the last known height of the browser window in which this UI
     * resides.
     * 
     * @return the browser window height in pixels
     * @deprecated As of 7.0, use the similarly named api in Page instead
     */
    @Deprecated
    public int getBrowserWindowHeight() {
        return getPage().getBrowserWindowHeight();
    }

    /**
     * Gets the last known width of the browser window in which this UI resides.
     * 
     * @return the browser window width in pixels
     * 
     * @deprecated As of 7.0, use the similarly named api in Page instead
     */
    @Deprecated
    public int getBrowserWindowWidth() {
        return getPage().getBrowserWindowWidth();
    }

    /**
     * Executes JavaScript in this window.
     * 
     * <p>
     * This method allows one to inject javascript from the server to client. A
     * client implementation is not required to implement this functionality,
     * but currently all web-based clients do implement this.
     * </p>
     * 
     * <p>
     * Executing javascript this way often leads to cross-browser compatibility
     * issues and regressions that are hard to resolve. Use of this method
     * should be avoided and instead it is recommended to create new widgets
     * with GWT. For more info on creating own, reusable client-side widgets in
     * Java, read the corresponding chapter in Book of Vaadin.
     * </p>
     * 
     * @param script
     *            JavaScript snippet that will be executed.
     * 
     * @deprecated As of 7.0, use JavaScript.getCurrent().execute(String)
     *             instead
     */
    @Deprecated
    public void executeJavaScript(String script) {
        getPage().getJavaScript().execute(script);
    }

    @Override
    public void setCaption(String caption) {
        // Override to provide backwards compatibility
        getState().caption = caption;
        getPage().setTitle(caption);
    }

    @Override
    public ComponentContainer getContent() {
        return (ComponentContainer) super.getContent();
    }

    /**
     * Set the content of the window. For a {@link LegacyWindow}, the content
     * must be a {@link ComponentContainer}.
     * 
     * @param content
     */
    @Override
    public void setContent(Component content) {
        if (!(content instanceof ComponentContainer)) {
            throw new IllegalArgumentException(
                    "The content of a LegacyWindow must be a ComponentContainer");
        }
        super.setContent(content);
    }

    /**
     * This implementation replaces a component in the content container (
     * {@link #getContent()}) instead of in the actual UI.
     * 
     * This method should only be called when the content is a
     * {@link ComponentContainer} (default {@link VerticalLayout} or explicitly
     * set).
     */
    public void replaceComponent(Component oldComponent, Component newComponent) {
        getContent().replaceComponent(oldComponent, newComponent);
    }

    /**
     * Adds a component to this UI. The component is not added directly to the
     * UI, but instead to the content container ({@link #getContent()}).
     * 
     * This method should only be called when the content is a
     * {@link ComponentContainer} (default {@link VerticalLayout} or explicitly
     * set).
     * 
     * @param component
     *            the component to add to this UI
     * 
     * @see #getContent()
     */
    public void addComponent(Component component) {
        getContent().addComponent(component);
    }

    /**
     * This implementation removes the component from the content container (
     * {@link #getContent()}) instead of from the actual UI.
     * 
     * This method should only be called when the content is a
     * {@link ComponentContainer} (default {@link VerticalLayout} or explicitly
     * set).
     */
    public void removeComponent(Component component) {
        getContent().removeComponent(component);
    }

    /**
     * This implementation removes the components from the content container (
     * {@link #getContent()}) instead of from the actual UI.
     * 
     * This method should only be called when the content is a
     * {@link ComponentContainer} (default {@link VerticalLayout} or explicitly
     * set).
     */
    public void removeAllComponents() {
        getContent().removeAllComponents();
    }

}
