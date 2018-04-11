/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.annotations.HtmlImport;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.event.EventRouter;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.shared.ui.ui.PageClientRpc;
import com.vaadin.shared.ui.ui.PageState;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.shared.ui.ui.UIState;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.Dependency;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.util.ReflectTools;

public class Page implements Serializable {

    /**
     * Listener that gets notified when the size of the browser window
     * containing the uI has changed.
     *
     * @see #addBrowserWindowResizeListener(BrowserWindowResizeListener)
     */
    @FunctionalInterface
    public interface BrowserWindowResizeListener extends Serializable {
        /**
         * Invoked when the browser window containing a UI has been resized.
         *
         * @param event
         *            a browser window resize event
         */
        public void browserWindowResized(BrowserWindowResizeEvent event);
    }

    /**
     * Event that is fired when a browser window containing a uI is resized.
     */
    public static class BrowserWindowResizeEvent extends EventObject {

        private final int width;
        private final int height;

        /**
         * Creates a new event.
         *
         * @param source
         *            the uI for which the browser window has been resized
         * @param width
         *            the new width of the browser window
         * @param height
         *            the new height of the browser window
         */
        public BrowserWindowResizeEvent(Page source, int width, int height) {
            super(source);
            this.width = width;
            this.height = height;
        }

        @Override
        public Page getSource() {
            return (Page) super.getSource();
        }

        /**
         * Gets the new browser window height.
         *
         * @return an integer with the new pixel height of the browser window
         */
        public int getHeight() {
            return height;
        }

        /**
         * Gets the new browser window width.
         *
         * @return an integer with the new pixel width of the browser window
         */
        public int getWidth() {
            return width;
        }
    }

    /**
     * Private class for storing properties related to opening resources.
     */
    private class OpenResource implements Serializable {

        /**
         * The resource to open
         */
        private final Resource resource;

        /**
         * The name of the target window
         */
        private final String name;

        /**
         * The width of the target window
         */
        private final int width;

        /**
         * The height of the target window
         */
        private final int height;

        /**
         * The border style of the target window
         */
        private final BorderStyle border;

        private final boolean tryToOpenAsPopup;

        /**
         * Creates a new open resource.
         *
         * @param url
         *            The URL to open
         * @param name
         *            The name of the target window
         * @param width
         *            The width of the target window
         * @param height
         *            The height of the target window
         * @param border
         *            The border style of the target window
         * @param tryToOpenAsPopup
         *            Should try to open as a pop-up
         */
        private OpenResource(String url, String name, int width, int height,
                BorderStyle border, boolean tryToOpenAsPopup) {
            this(new ExternalResource(url), name, width, height, border,
                    tryToOpenAsPopup);
        }

        /**
         * Creates a new open resource.
         *
         * @param resource
         *            The resource to open
         * @param name
         *            The name of the target window
         * @param width
         *            The width of the target window
         * @param height
         *            The height of the target window
         * @param border
         *            The border style of the target window
         * @param tryToOpenAsPopup
         *            Should try to open as a pop-up
         */
        private OpenResource(Resource resource, String name, int width,
                int height, BorderStyle border, boolean tryToOpenAsPopup) {
            this.resource = resource;
            this.name = name;
            this.width = width;
            this.height = height;
            this.border = border;
            this.tryToOpenAsPopup = tryToOpenAsPopup;
        }

        /**
         * Paints the open request. Should be painted inside the window.
         *
         * @param target
         *            the paint target
         * @throws PaintException
         *             if the paint operation fails
         */
        private void paintContent(PaintTarget target) throws PaintException {
            target.startTag("open");
            target.addAttribute("src", resource);
            if (name != null && !name.isEmpty()) {
                target.addAttribute("name", name);
            }
            if (!tryToOpenAsPopup) {
                target.addAttribute("popup", tryToOpenAsPopup);
            }
            if (width >= 0) {
                target.addAttribute("width", width);
            }
            if (height >= 0) {
                target.addAttribute("height", height);
            }
            switch (border) {
            case MINIMAL:
                target.addAttribute("border", "minimal");
                break;
            case NONE:
                target.addAttribute("border", "none");
                break;
            }

            target.endTag("open");
        }
    }

    private static final Method BROWSER_RESIZE_METHOD = ReflectTools.findMethod(
            BrowserWindowResizeListener.class, "browserWindowResized",
            BrowserWindowResizeEvent.class);

    /**
     * @deprecated As of 7.0, use {@link BorderStyle#NONE} instead.
     */
    @Deprecated
    public static final BorderStyle BORDER_NONE = BorderStyle.NONE;

    /**
     * @deprecated As of 7.0, use {@link BorderStyle#MINIMAL} instead.
     */
    @Deprecated
    public static final BorderStyle BORDER_MINIMAL = BorderStyle.MINIMAL;

    /**
     * @deprecated As of 7.0, use {@link BorderStyle#DEFAULT} instead.
     */
    @Deprecated
    public static final BorderStyle BORDER_DEFAULT = BorderStyle.DEFAULT;

    /**
     * Listener that that gets notified when the URI fragment of the page
     * changes.
     *
     * @see Page#addUriFragmentChangedListener(UriFragmentChangedListener)
     * @deprecated Use {@link PopStateListener} instead
     */
    @Deprecated
    @FunctionalInterface
    public interface UriFragmentChangedListener extends Serializable {
        /**
         * Event handler method invoked when the URI fragment of the page
         * changes. Please note that the initial URI fragment has already been
         * set when a new UI is initialized, so there will not be any initial
         * event for listeners added during {@link UI#init(VaadinRequest)}.
         *
         * @see Page#addUriFragmentChangedListener(UriFragmentChangedListener)
         *
         * @param event
         *            the URI fragment changed event
         */
        public void uriFragmentChanged(UriFragmentChangedEvent event);
    }

    private static final Method URI_FRAGMENT_CHANGED_METHOD = ReflectTools
            .findMethod(Page.UriFragmentChangedListener.class,
                    "uriFragmentChanged", UriFragmentChangedEvent.class);

    /**
     * Listener that that gets notified when the URI of the page changes due to
     * back/forward functionality of the browser.
     *
     * @see Page#addPopStateListener(PopStateListener)
     * @since 8.0
     */
    @FunctionalInterface
    public interface PopStateListener extends Serializable {
        /**
         * Event handler method invoked when the URI fragment of the page
         * changes. Please note that the initial URI fragment has already been
         * set when a new UI is initialized, so there will not be any initial
         * event for listeners added during {@link UI#init(VaadinRequest)}.
         *
         * @see Page#addUriFragmentChangedListener(UriFragmentChangedListener)
         *
         * @param event
         *            the URI fragment changed event
         */
        public void uriChanged(PopStateEvent event);
    }

    private static final Method URI_CHANGED_METHOD = ReflectTools.findMethod(
            Page.PopStateListener.class, "uriChanged", PopStateEvent.class);

    /**
     * Resources to be opened automatically on next repaint. The list is
     * automatically cleared when it has been sent to the client.
     */
    private final LinkedList<OpenResource> openList = new LinkedList<>();

    /**
     * Event fired when the URI fragment of a <code>Page</code> changes.
     *
     * @see Page#addUriFragmentChangedListener(UriFragmentChangedListener)
     */
    public static class UriFragmentChangedEvent extends EventObject {

        /**
         * The new URI fragment
         */
        private final String uriFragment;

        /**
         * Creates a new instance of UriFragmentReader change event.
         *
         * @param source
         *            the Source of the event.
         * @param uriFragment
         *            the new uriFragment
         */
        public UriFragmentChangedEvent(Page source, String uriFragment) {
            super(source);
            this.uriFragment = uriFragment;
        }

        /**
         * Gets the page in which the fragment has changed.
         *
         * @return the page in which the fragment has changed
         */
        public Page getPage() {
            return (Page) getSource();
        }

        /**
         * Get the new URI fragment.
         *
         * @return the new fragment
         */
        public String getUriFragment() {
            return uriFragment;
        }
    }

    /**
     * Event fired when the URI of a <code>Page</code> changes (aka HTML 5
     * popstate event) on the client side due to browsers back/forward
     * functionality.
     *
     * @see Page#addPopStateListener(PopStateListener)
     * @since 8.0
     */
    public static class PopStateEvent extends EventObject {

        /**
         * The new URI as String
         */
        private final String uri;

        /**
         * Creates a new instance of PopstateEvent.
         *
         * @param source
         *            the Source of the event.
         * @param uri
         *            the new uri
         */
        public PopStateEvent(Page source, String uri) {
            super(source);
            this.uri = uri;
        }

        /**
         * Gets the page in which the uri has changed.
         *
         * @return the page in which the uri has changed
         */
        public Page getPage() {
            return (Page) getSource();
        }

        /**
         * Get the new URI.
         *
         * @return the new uri
         */
        public String getUri() {
            return uri;
        }
    }

    @FunctionalInterface
    private static interface InjectedStyle extends Serializable {
        public void paint(int id, PaintTarget target) throws PaintException;
    }

    private static class InjectedStyleString implements InjectedStyle {

        private final String css;

        public InjectedStyleString(String css) {
            this.css = css;
        }

        @Override
        public void paint(int id, PaintTarget target) throws PaintException {
            target.startTag("css-string");
            target.addAttribute("id", id);
            target.addText(css);
            target.endTag("css-string");
        }
    }

    private static class InjectedStyleResource implements InjectedStyle {

        private final Resource resource;

        public InjectedStyleResource(Resource resource) {
            this.resource = resource;
        }

        @Override
        public void paint(int id, PaintTarget target) throws PaintException {
            target.startTag("css-resource");
            target.addAttribute("id", id);
            target.addAttribute("url", resource);
            target.endTag("css-resource");
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof InjectedStyleResource) {
                InjectedStyleResource that = (InjectedStyleResource) obj;
                return resource.equals(that.resource);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return resource.hashCode();
        }
    }

    /**
     * Contains dynamically injected styles injected in the HTML document at
     * runtime.
     *
     * @since 7.1
     */
    public static class Styles implements Serializable {

        private LinkedHashSet<InjectedStyle> injectedStyles = new LinkedHashSet<>();

        private LinkedHashSet<InjectedStyle> pendingInjections = new LinkedHashSet<>();

        private final UI ui;

        private Styles(UI ui) {
            this.ui = ui;
        }

        /**
         * Injects a raw CSS string into the page.
         *
         * @param css
         *            The CSS to inject
         */
        public void add(String css) {
            if (css == null) {
                throw new IllegalArgumentException(
                        "Cannot inject null CSS string");
            }

            pendingInjections.add(new InjectedStyleString(css));
            ui.markAsDirty();
        }

        /**
         * Injects a CSS resource into the page.
         *
         * @param resource
         *            The resource to inject.
         */
        public void add(Resource resource) {
            if (resource == null) {
                throw new IllegalArgumentException(
                        "Cannot inject null resource");
            }

            InjectedStyleResource injection = new InjectedStyleResource(
                    resource);
            if (!injectedStyles.contains(injection)
                    && pendingInjections.add(injection)) {
                ui.markAsDirty();
            }
        }

        private void paint(PaintTarget target) throws PaintException {

            // If full repaint repaint all injections
            if (target.isFullRepaint()) {
                injectedStyles.addAll(pendingInjections);
                pendingInjections = injectedStyles;
                injectedStyles = new LinkedHashSet<>();
            }

            if (!pendingInjections.isEmpty()) {

                target.startTag("css-injections");

                for (InjectedStyle pending : pendingInjections) {
                    int id = injectedStyles.size();
                    pending.paint(id, target);
                    injectedStyles.add(pending);
                }
                pendingInjections.clear();

                target.endTag("css-injections");
            }
        }
    }

    private EventRouter eventRouter;

    private final UI uI;

    private int browserWindowWidth = -1;
    private int browserWindowHeight = -1;

    private JavaScript javaScript;

    private Styles styles;

    /**
     * The current browser location.
     */
    private URI location;

    private final PageState state;

    private String windowName;

    private String newPushState;
    private String newReplaceState;

    private List<Dependency> pendingDependencies;

    public Page(UI uI, PageState state) {
        this.uI = uI;
        this.state = state;
    }

    private Registration addListener(Class<?> eventType, Object target,
            Method method) {
        if (!hasEventRouter()) {
            eventRouter = new EventRouter();
        }
        return eventRouter.addListener(eventType, target, method);
    }

    private void removeListener(Class<?> eventType, Object target,
            Method method) {
        if (hasEventRouter()) {
            eventRouter.removeListener(eventType, target, method);
        }
    }

    /**
     * Adds a listener that gets notified every time the URI fragment of this
     * page is changed. Please note that the initial URI fragment has already
     * been set when a new UI is initialized, so there will not be any initial
     * event for listeners added during {@link UI#init(VaadinRequest)}.
     *
     * @see #getUriFragment()
     * @see #setUriFragment(String)
     * @see Registration
     *
     * @param listener
     *            the URI fragment listener to add
     * @return a registration object for removing the listener
     * @deprecated Use {@link Page#addPopStateListener(PopStateListener)}
     *             instead
     * @since 8.0
     */
    @Deprecated
    public Registration addUriFragmentChangedListener(
            Page.UriFragmentChangedListener listener) {
        return addListener(UriFragmentChangedEvent.class, listener,
                URI_FRAGMENT_CHANGED_METHOD);
    }

    /**
     * Adds a listener that gets notified every time the URI of this page is
     * changed due to back/forward functionality of the browser.
     * <p>
     * Note that one only gets notified when the back/forward button affects
     * history changes with-in same UI, created by
     * {@link Page#pushState(String)} or {@link Page#replaceState(String)}
     * functions.
     *
     * @see #getLocation()
     * @see Registration
     *
     * @param listener
     *            the Popstate listener to add
     * @return a registration object for removing the listener
     * @since 8.0
     */
    public Registration addPopStateListener(Page.PopStateListener listener) {
        return addListener(PopStateEvent.class, listener, URI_CHANGED_METHOD);
    }

    /**
     * Removes a URI fragment listener that was previously added to this page.
     *
     * @param listener
     *            the URI fragment listener to remove
     *
     * @see Page#addUriFragmentChangedListener(UriFragmentChangedListener)
     *
     * @deprecated As of 8.0, replaced by {@link Registration#remove()} in the
     *             registration object returned from
     *             {@link #addUriFragmentChangedListener(UriFragmentChangedListener)}.
     */
    @Deprecated
    public void removeUriFragmentChangedListener(
            Page.UriFragmentChangedListener listener) {
        removeListener(UriFragmentChangedEvent.class, listener,
                URI_FRAGMENT_CHANGED_METHOD);
    }

    /**
     * Sets the fragment part in the current location URI. Optionally fires a
     * {@link UriFragmentChangedEvent}.
     * <p>
     * The fragment is the optional last component of a URI, prefixed with a
     * hash sign ("#").
     * <p>
     * Passing an empty string as <code>newFragment</code> sets an empty
     * fragment (a trailing "#" in the URI.) Passing <code>null</code> if there
     * is already a non-null fragment will leave a trailing # in the URI since
     * removing it would cause the browser to reload the page. This is not fully
     * consistent with the semantics of {@link java.net.URI}.
     *
     * @param newUriFragment
     *            The new fragment.
     * @param fireEvents
     *            true to fire event
     *
     * @see #getUriFragment()
     * @see #setLocation(URI)
     * @see UriFragmentChangedEvent
     * @see Page.UriFragmentChangedListener
     *
     */
    public void setUriFragment(String newUriFragment, boolean fireEvents) {
        String oldUriFragment = location.getFragment();
        if (newUriFragment == null && getUriFragment() != null) {
            // Can't completely remove the fragment once it has been set, will
            // instead set it to the empty string
            newUriFragment = "";
        }
        if (newUriFragment == oldUriFragment || (newUriFragment != null
                && newUriFragment.equals(oldUriFragment))) {
            return;
        }
        try {
            location = new URI(location.getScheme(),
                    location.getSchemeSpecificPart(), newUriFragment);
            pushState(location);
        } catch (URISyntaxException e) {
            // This should not actually happen as the fragment syntax is not
            // constrained
            throw new RuntimeException(e);
        }
        if (fireEvents) {
            fireEvent(new UriFragmentChangedEvent(this, newUriFragment));
        }
    }

    private void fireEvent(EventObject event) {
        if (hasEventRouter()) {
            eventRouter.fireEvent(event);
        }
    }

    /**
     * Sets URI fragment. This method fires a {@link UriFragmentChangedEvent}
     *
     * @param newUriFragment
     *            id of the new fragment
     * @see UriFragmentChangedEvent
     * @see Page.UriFragmentChangedListener
     */
    public void setUriFragment(String newUriFragment) {
        setUriFragment(newUriFragment, true);
    }

    /**
     * Gets the currently set URI fragment.
     * <p>
     * Returns <code>null</code> if there is no fragment and an empty string if
     * there is an empty fragment.
     * <p>
     * To listen to changes in fragment, hook a
     * {@link Page.UriFragmentChangedListener}.
     *
     * @return the current fragment in browser location URI.
     *
     * @see #getLocation()
     * @see #setUriFragment(String)
     * @see #addUriFragmentChangedListener(UriFragmentChangedListener)
     */
    public String getUriFragment() {
        return location.getFragment();
    }

    public void init(VaadinRequest request) {
        // NOTE: UI.refresh makes assumptions about the semantics of this
        // method.
        // It should be kept in sync if this method is changed.

        // Extract special parameter sent by vaadinBootstrap.js
        String location = request.getParameter("v-loc");
        String clientWidth = request.getParameter("v-cw");
        String clientHeight = request.getParameter("v-ch");
        windowName = request.getParameter("v-wn");

        if (location != null) {
            try {
                this.location = new URI(location);
            } catch (URISyntaxException e) {
                throw new RuntimeException(
                        "Invalid location URI received from client", e);
            }
        }
        if (clientWidth != null && clientHeight != null) {
            try {
                browserWindowWidth = Integer.parseInt(clientWidth);
                browserWindowHeight = Integer.parseInt(clientHeight);
            } catch (NumberFormatException e) {
                throw new RuntimeException(
                        "Invalid window size received from client", e);
            }
        }
    }

    public WebBrowser getWebBrowser() {
        return uI.getSession().getBrowser();
    }

    /**
     * Gets the window.name value of the browser window of this page.
     *
     * @since 7.2
     *
     * @return the window name, <code>null</code> if the name is not known
     */
    public String getWindowName() {
        return windowName;
    }

    /**
     * For internal use only. Updates the internal state with the given values.
     * Does not resize the Page or browser window.
     *
     * @deprecated As of 7.2, use
     *             {@link #updateBrowserWindowSize(int, int, boolean)} instead.
     *
     * @param width
     *            the new browser window width
     * @param height
     *            the new browse window height
     */
    @Deprecated
    public void updateBrowserWindowSize(int width, int height) {
        updateBrowserWindowSize(width, height, true);
    }

    /**
     * For internal use only. Updates the internal state with the given values.
     * Does not resize the Page or browser window.
     *
     * @since 7.2
     *
     * @param width
     *            the new browser window width
     * @param height
     *            the new browser window height
     * @param fireEvents
     *            whether to fire {@link BrowserWindowResizeEvent} if the size
     *            changes
     */
    public void updateBrowserWindowSize(int width, int height,
            boolean fireEvents) {
        boolean sizeChanged = false;

        if (width != browserWindowWidth) {
            browserWindowWidth = width;
            sizeChanged = true;
        }

        if (height != browserWindowHeight) {
            browserWindowHeight = height;
            sizeChanged = true;
        }

        if (fireEvents && sizeChanged) {
            fireEvent(new BrowserWindowResizeEvent(this, browserWindowWidth,
                    browserWindowHeight));
        }

    }

    /**
     * Adds a new {@link BrowserWindowResizeListener} to this UI. The listener
     * will be notified whenever the browser window within which this UI resides
     * is resized.
     * <p>
     * In most cases, the UI should be in lazy resize mode when using browser
     * window resize listeners. Otherwise, a large number of events can be
     * received while a resize is being performed. Use
     * {@link UI#setResizeLazy(boolean)}.
     * </p>
     *
     * @param resizeListener
     *            the listener to add
     * @return a registration object for removing the listener
     *
     * @see BrowserWindowResizeListener#browserWindowResized(BrowserWindowResizeEvent)
     * @see UI#setResizeLazy(boolean)
     * @see Registration
     * @since 8.0
     */
    public Registration addBrowserWindowResizeListener(
            BrowserWindowResizeListener resizeListener) {
        Registration registration = addListener(BrowserWindowResizeEvent.class,
                resizeListener, BROWSER_RESIZE_METHOD);
        getState(true).hasResizeListeners = true;
        return () -> {
            registration.remove();
            getState(true).hasResizeListeners = hasEventRouter()
                    && eventRouter.hasListeners(BrowserWindowResizeEvent.class);
        };
    }

    /**
     * Removes a {@link BrowserWindowResizeListener} from this UI. The listener
     * will no longer be notified when the browser window is resized.
     *
     * @param resizeListener
     *            the listener to remove
     *
     * @deprecated As of 8.0, replaced by {@link Registration#remove()} in the
     *             registration object returned from
     *             {@link #addBrowserWindowResizeListener(BrowserWindowResizeListener)}
     *             .
     */
    @Deprecated
    public void removeBrowserWindowResizeListener(
            BrowserWindowResizeListener resizeListener) {
        removeListener(BrowserWindowResizeEvent.class, resizeListener,
                BROWSER_RESIZE_METHOD);
        getState(true).hasResizeListeners = hasEventRouter()
                && eventRouter.hasListeners(BrowserWindowResizeEvent.class);
    }

    /**
     * Gets the last known height of the browser window in which this UI
     * resides.
     *
     * @return the browser window height in pixels
     */
    public int getBrowserWindowHeight() {
        return browserWindowHeight;
    }

    /**
     * Gets the last known width of the browser window in which this uI resides.
     *
     * @return the browser window width in pixels
     */
    public int getBrowserWindowWidth() {
        return browserWindowWidth;
    }

    public JavaScript getJavaScript() {
        if (javaScript == null) {
            // Create and attach on first use
            javaScript = new JavaScript();
            javaScript.extend(uI);
        }
        return javaScript;
    }

    /**
     * Returns that stylesheet associated with this Page. The stylesheet
     * contains additional styles injected at runtime into the HTML document.
     *
     * @since 7.1
     */
    public Styles getStyles() {

        if (styles == null) {
            styles = new Styles(uI);
        }
        return styles;
    }

    public void paintContent(PaintTarget target) throws PaintException {
        if (!openList.isEmpty()) {
            for (OpenResource anOpenList : openList) {
                (anOpenList).paintContent(target);
            }
            openList.clear();
        }

        if (newPushState != null) {
            target.addAttribute(UIConstants.ATTRIBUTE_PUSH_STATE, newPushState);
            newPushState = null;
        }
        if (newReplaceState != null) {
            target.addAttribute(UIConstants.ATTRIBUTE_REPLACE_STATE,
                    newReplaceState);
            newReplaceState = null;
        }

        if (styles != null) {
            styles.paint(target);
        }
    }

    /**
     * Navigates this page to the given URI. The contents of this page in the
     * browser is replaced with whatever is returned for the given URI.
     * <p>
     * This method should not be used to start downloads, as the client side
     * will assume the browser will navigate away when opening the URI. Use one
     * of the {@code Page.open} methods or {@code FileDownloader} instead.
     *
     * @see #open(String, String)
     * @see FileDownloader
     *
     * @param uri
     *            the URI to show
     */
    public void setLocation(String uri) {
        openList.add(
                new OpenResource(uri, "_self", -1, -1, BORDER_DEFAULT, false));
        uI.markAsDirty();
    }

    /**
     * Navigates this page to the given URI. The contents of this page in the
     * browser is replaced with whatever is returned for the given URI.
     * <p>
     * This method should not be used to start downloads, as the client side
     * will assume the browser will navigate away when opening the URI. Use one
     * of the {@code Page.open} methods or {@code FileDownloader} instead.
     *
     * @see #open(String, String)
     * @see FileDownloader
     *
     * @param uri
     *            the URI to show
     */
    public void setLocation(URI uri) {
        setLocation(uri.toString());
    }

    /**
     * Returns the location URI of this page, as reported by the browser. Note
     * that this may not be consistent with the server URI the application is
     * deployed in due to potential proxies, redirections and similar.
     *
     * @return The browser location URI.
     * @throws IllegalStateException
     *             if the
     *             {@link DeploymentConfiguration#isSendUrlsAsParameters()} is
     *             set to {@code false}
     */
    public URI getLocation() throws IllegalStateException {
        if (location == null && !uI.getSession().getConfiguration()
                .isSendUrlsAsParameters()) {
            throw new IllegalStateException("Location is not available as the "
                    + Constants.SERVLET_PARAMETER_SENDURLSASPARAMETERS
                    + " parameter is configured as false");
        }
        return location;
    }

    /**
     * Updates the browsers URI without causing actual page change. This method
     * is useful if you wish implement "deep linking" to your application.
     * Calling the method also adds a new entry to clients browser history and
     * you can further use {@link PopStateListener} to track the usage of
     * back/forward feature in browser.
     * <p>
     * Note, the current implementation supports setting only one new uri in one
     * user interaction.
     *
     * @param uri
     *            to be used for pushState operation. The URI is resolved over
     *            the current location. If the given URI is absolute, it must be
     *            of same origin as the current URI or the browser will not
     *            accept the new value.
     * @since 8.0
     */
    public void pushState(String uri) {
        newPushState = uri;
        uI.markAsDirty();
        location = location.resolve(uri);
    }

    /**
     * Updates the browsers URI without causing actual page change. This method
     * is useful if you wish implement "deep linking" to your application.
     * Calling the method also adds a new entry to clients browser history and
     * you can further use {@link PopStateListener} to track the usage of
     * back/forward feature in browser.
     * <p>
     * Note, the current implementation supports setting only one new uri in one
     * user interaction.
     *
     * @param uri
     *            the URI to be used for pushState operation. The URI is
     *            resolved over the current location. If the given URI is
     *            absolute, it must be of same origin as the current URI or the
     *            browser will not accept the new value.
     * @since 8.0
     */
    public void pushState(URI uri) {
        pushState(uri.toString());
    }

    /**
     * Updates the browsers URI without causing actual page change in the same
     * way as {@link #pushState(String)}, but does not add new entry to browsers
     * history.
     *
     * @param uri
     *            the URI to be used for replaceState operation. The URI is
     *            resolved over the current location. If the given URI is
     *            absolute, it must be of same origin as the current URI or the
     *            browser will not accept the new value.
     * @since 8.0
     */
    public void replaceState(String uri) {
        newReplaceState = uri;
        uI.markAsDirty();
        location = location.resolve(uri);
    }

    /**
     * Updates the browsers URI without causing actual page change in the same
     * way as {@link #pushState(URI)}, but does not add new entry to browsers
     * history.
     *
     * @param uri
     *            the URI to be used for replaceState operation. The URI is
     *            resolved over the current location. If the given URI is
     *            absolute, it must be of same origin as the current URI or the
     *            browser will not accept the new value.
     * @since 8.0
     */
    public void replaceState(URI uri) {
        replaceState(uri.toString());
    }

    /**
     * For internal use only. Used to update the server-side location when the
     * client-side location changes.
     *
     * @deprecated As of 7.2, use {@link #updateLocation(String, boolean)}
     *             instead.
     *
     * @param location
     *            the new location URI
     */
    @Deprecated
    public void updateLocation(String location) {
        updateLocation(location, true, false);
    }

    /**
     * For internal use only. Used to update the server-side location when the
     * client-side location changes.
     *
     * @since 8.0
     *
     * @param location
     *            the new location URI
     * @param fireEvents
     *            whether to fire {@link UriFragmentChangedEvent} if the URI
     *            fragment changes
     * @param firePopstate
     *            whether to fire {@link PopStateEvent}
     */
    public void updateLocation(String location, boolean fireEvents,
            boolean firePopstate) {
        try {
            String oldUriFragment = this.location.getFragment();
            this.location = new URI(location);
            String newUriFragment = this.location.getFragment();
            if (fireEvents
                    && !SharedUtil.equals(oldUriFragment, newUriFragment)) {
                fireEvent(new UriFragmentChangedEvent(this, newUriFragment));
            }
            if (firePopstate) {
                fireEvent(new PopStateEvent(this, location));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens the given url in a window with the given name. Equivalent to
     * {@link #open(String, String, boolean) open} (url, windowName, true) .
     * <p>
     * The supplied {@code windowName} is used as the target name in a
     * window.open call in the client. This means that special values such as
     * "_blank", "_self", "_top", "_parent" have special meaning. An empty or
     * <code>null</code> window name is also a special case.
     * </p>
     * <p>
     * "", null and "_self" as {@code windowName} all causes the URL to be
     * opened in the current window, replacing any old contents. For
     * downloadable content you should avoid "_self" as "_self" causes the
     * client to skip rendering of any other changes as it considers them
     * irrelevant (the page will be replaced by the response from the URL). This
     * can speed up the opening of a URL, but it might also put the client side
     * into an inconsistent state if the window content is not completely
     * replaced e.g., if the URL is downloaded instead of displayed in the
     * browser.
     * </p>
     * <p>
     * "_blank" as {@code windowName} causes the URL to always be opened in a
     * new window or tab (depends on the browser and browser settings).
     * </p>
     * <p>
     * "_top" and "_parent" as {@code windowName} works as specified by the HTML
     * standard.
     * </p>
     * <p>
     * Any other {@code windowName} will open the URL in a window with that
     * name, either by opening a new window/tab in the browser or by replacing
     * the contents of an existing window with that name.
     * </p>
     * <p>
     * Please note that opening a popup window in this way may be blocked by the
     * browser's popup-blocker because the new browser window is opened when
     * processing a response from the server. To avoid this, you should instead
     * use {@link Link} for opening the window because browsers are more
     * forgiving when the window is opened directly from a client-side click
     * event.
     * </p>
     *
     * @param url
     *            the URL to open.
     * @param windowName
     *            the name of the window.
     */
    public void open(String url, String windowName) {
        open(url, windowName, true);
    }

    /**
     * Opens the given url in a window with the given name. Equivalent to
     * {@link #open(String, String, boolean) open} (url, windowName, true) .
     * <p>
     * The supplied {@code windowName} is used as the target name in a
     * window.open call in the client. This means that special values such as
     * "_blank", "_self", "_top", "_parent" have special meaning. An empty or
     * <code>null</code> window name is also a special case.
     * </p>
     * <p>
     * "", null and "_self" as {@code windowName} all causes the URL to be
     * opened in the current window, replacing any old contents. For
     * downloadable content you should avoid "_self" as "_self" causes the
     * client to skip rendering of any other changes as it considers them
     * irrelevant (the page will be replaced by the response from the URL). This
     * can speed up the opening of a URL, but it might also put the client side
     * into an inconsistent state if the window content is not completely
     * replaced e.g., if the URL is downloaded instead of displayed in the
     * browser.
     * </p>
     * <p>
     * "_blank" as {@code windowName} causes the URL to always be opened in a
     * new window or tab (depends on the browser and browser settings).
     * </p>
     * <p>
     * "_top" and "_parent" as {@code windowName} works as specified by the HTML
     * standard.
     * </p>
     * <p>
     * Any other {@code windowName} will open the URL in a window with that
     * name, either by opening a new window/tab in the browser or by replacing
     * the contents of an existing window with that name.
     * </p>
     * <p>
     * Please note that opening a popup window in this way may be blocked by the
     * browser's popup-blocker because the new browser window is opened when
     * processing a response from the server. To avoid this, you should instead
     * use {@link Link} for opening the window because browsers are more
     * forgiving when the window is opened directly from a client-side click
     * event.
     * </p>
     *
     * @param url
     *            the URL to open.
     * @param windowName
     *            the name of the window.
     * @param tryToOpenAsPopup
     *            Whether to try to force the resource to be opened in a new
     *            window
     */
    public void open(String url, String windowName, boolean tryToOpenAsPopup) {
        openList.add(new OpenResource(url, windowName, -1, -1, BORDER_DEFAULT,
                tryToOpenAsPopup));
        uI.markAsDirty();
    }

    /**
     * Opens the given URL in a window with the given size, border and name. For
     * more information on the meaning of {@code windowName}, see
     * {@link #open(String, String)}.
     * <p>
     * Please note that opening a popup window in this way may be blocked by the
     * browser's popup-blocker because the new browser window is opened when
     * processing a response from the server. To avoid this, you should instead
     * use {@link Link} for opening the window because browsers are more
     * forgiving when the window is opened directly from a client-side click
     * event.
     * </p>
     *
     * @param url
     *            the URL to open.
     * @param windowName
     *            the name of the window.
     * @param width
     *            the width of the window in pixels
     * @param height
     *            the height of the window in pixels
     * @param border
     *            the border style of the window.
     */
    public void open(String url, String windowName, int width, int height,
            BorderStyle border) {
        openList.add(
                new OpenResource(url, windowName, width, height, border, true));
        uI.markAsDirty();
    }

    /**
     * @deprecated As of 7.0, only retained to maintain compatibility with
     *             LegacyWindow.open methods. See documentation for
     *             {@link LegacyWindow#open(Resource, String, int, int, BorderStyle)}
     *             for discussion about replacing API.
     */
    @Deprecated
    public void open(Resource resource, String windowName, int width,
            int height, BorderStyle border) {
        openList.add(new OpenResource(resource, windowName, width, height,
                border, true));
        uI.markAsDirty();
    }

    /**
     * @deprecated As of 7.0, only retained to maintain compatibility with
     *             LegacyWindow.open methods. See documentation for
     *             {@link LegacyWindow#open(Resource, String, boolean)} for
     *             discussion about replacing API.
     */
    @Deprecated
    public void open(Resource resource, String windowName,
            boolean tryToOpenAsPopup) {
        openList.add(new OpenResource(resource, windowName, -1, -1,
                BORDER_DEFAULT, tryToOpenAsPopup));
        uI.markAsDirty();
    }

    /**
     * Shows a notification message.
     *
     * @see Notification
     *
     * @param notification
     *            The notification message to show
     *
     * @deprecated As of 7.0, use Notification.show(Page) instead.
     */
    @Deprecated
    public void showNotification(Notification notification) {
        notification.show(this);
    }

    /**
     * Gets the Page to which the current uI belongs. This is automatically
     * defined when processing requests to the server. In other cases, (e.g.
     * from background threads), the current uI is not automatically defined.
     *
     * @see UI#getCurrent()
     *
     * @return the current page instance if available, otherwise
     *         <code>null</code>
     */
    public static Page getCurrent() {
        UI currentUI = UI.getCurrent();
        if (currentUI == null) {
            return null;
        }
        return currentUI.getPage();
    }

    /**
     * Sets the page title. The page title is displayed by the browser e.g. as
     * the title of the browser window or as the title of the tab.
     * <p>
     * If the title is set to null, it will not left as-is. Set to empty string
     * to clear the title.
     *
     * @param title
     *            the page title to set
     */
    public void setTitle(String title) {
        getState(true).title = title;
    }

    /**
     * Reloads the page in the browser.
     */
    public void reload() {
        uI.getRpcProxy(PageClientRpc.class).reload();
    }

    /**
     * Returns the page state.
     * <p>
     * The page state is transmitted to UIConnector together with
     * {@link UIState} rather than as an individual entity.
     * </p>
     * <p>
     * The state should be considered an internal detail of Page. Classes
     * outside of Page should not access it directly but only through public
     * APIs provided by Page.
     * </p>
     *
     * @since 7.1
     * @param markAsDirty
     *            true to mark the state as dirty
     * @return PageState object that can be read in any case and modified if
     *         markAsDirty is true
     */
    protected PageState getState(boolean markAsDirty) {
        if (markAsDirty) {
            uI.markAsDirty();
        }
        return state;
    }

    private boolean hasEventRouter() {
        return eventRouter != null;
    }

    /**
     * Add a dependency that should be added to the current page.
     * <p>
     * These dependencies are always added before the dependencies included by
     * using the annotations {@link HtmlImport}, {@link JavaScript} and
     * {@link StyleSheet} during the same request.
     * <p>
     * Please note that these dependencies are always sent to the client side
     * and not filtered out by any {@link DependencyFilter}.
     *
     * @param dependency
     *            the dependency to add
     * @since 8.1
     */
    public void addDependency(Dependency dependency) {
        if (pendingDependencies == null) {
            pendingDependencies = new ArrayList<>();
        }
        pendingDependencies.add(dependency);
    }

    /**
     * Returns all pending dependencies.
     * <p>
     * For internal use only, calling this method will clear the pending
     * dependencies.
     *
     * @return the pending dependencies to the current page
     * @since 8.1
     */
    public Collection<Dependency> getPendingDependencies() {
        List<Dependency> copy = new ArrayList<>();
        if (pendingDependencies != null) {
            copy.addAll(pendingDependencies);
        }
        pendingDependencies = null;
        return copy;
    }

    /**
     * Returns the {@link UI} of this {@link Page}.
     *
     * @return the {@link UI} of this {@link Page}.
     *
     * @since 8.2
     */
    public UI getUI() {
        return uI;
    }
}
