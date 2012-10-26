/*
 * Copyright 2011 Vaadin Ltd.
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

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import com.vaadin.LegacyApplication;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ActionManager;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.LegacyComponent;
import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.Resource;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServiceSession;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.shared.ui.ui.UIServerRpc;
import com.vaadin.shared.ui.ui.UIState;
import com.vaadin.util.CurrentInstance;
import com.vaadin.util.ReflectTools;

/**
 * The topmost component in any component hierarchy. There is one UI for every
 * Vaadin instance in a browser window. A UI may either represent an entire
 * browser window (or tab) or some part of a html page where a Vaadin
 * application is embedded.
 * <p>
 * The UI is the server side entry point for various client side features that
 * are not represented as components added to a layout, e.g notifications, sub
 * windows, and executing javascript in the browser.
 * </p>
 * <p>
 * When a new UI instance is needed, typically because the user opens a URL in a
 * browser window which points to e.g. {@link VaadinServlet}, all
 * {@link UIProvider}s registered to the current {@link VaadinServiceSession}
 * are queried for the UI class that should be used. The selection is by defaylt
 * based on the {@value VaadinServiceSession#UI_PARAMETER} parameter from
 * web.xml.
 * </p>
 * <p>
 * After a UI has been created by the application, it is initialized using
 * {@link #init(VaadinRequest)}. This method is intended to be overridden by the
 * developer to add components to the user interface and initialize
 * non-component functionality. The component hierarchy is initialized by
 * passing a {@link ComponentContainer} with the main layout of the view to
 * {@link #setContent(ComponentContainer)}.
 * </p>
 * 
 * @see #init(VaadinRequest)
 * @see UIProvider
 * 
 * @since 7.0
 */
public abstract class UI extends AbstractComponentContainer implements
        Action.Container, Action.Notifier, LegacyComponent {

    /**
     * Helper class to emulate the main window from Vaadin 6 using UIs. This
     * class should be used in the same way as Window used as a browser level
     * window in Vaadin 6 with {@link com.vaadin.LegacyApplication}
     */
    @Deprecated
    public static class LegacyWindow extends UI {
        private String name;
        private LegacyApplication application;

        /**
         * Create a new legacy window
         */
        public LegacyWindow() {
            super();
        }

        /**
         * Creates a new legacy window with the given caption
         * 
         * @param caption
         *            the caption of the window
         */
        public LegacyWindow(String caption) {
            super();
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
         * The name also determines the URL that can be used for direct access
         * to a window. All windows can be accessed through
         * {@code http://host:port/app/win} where {@code http://host:port/app}
         * is the application URL (as returned by
         * {@link LegacyApplication#getURL()} and {@code win} is the window
         * name.
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
         * The name also determines the URL that can be used for direct access
         * to a window. All windows can be accessed through
         * {@code http://host:port/app/win} where {@code http://host:port/app}
         * is the application URL (as returned by
         * {@link LegacyApplication#getURL()} and {@code win} is the window
         * name.
         * </p>
         * <p>
         * This method can only be called before the window is added to an
         * application.
         * <p>
         * Note! Portlets do not support direct window access through URLs.
         * </p>
         * 
         * @param name
         *            the new name for the window or null if the application
         *            should automatically assign a name to it
         * @throws IllegalStateException
         *             if the window is attached to an application
         */
        public void setName(String name) {
            this.name = name;
            // The name can not be changed in application
            if (getSession() != null) {
                throw new IllegalStateException(
                        "Window name can not be changed while "
                                + "the window is in application");
            }

        }

        /**
         * Gets the full URL of the window. The returned URL is window specific
         * and can be used to directly refer to the window.
         * <p>
         * Note! This method can not be used for portlets.
         * </p>
         * 
         * @return the URL of the window or null if the window is not attached
         *         to an application
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
         * Opens the given resource in this UI. The contents of this UI is
         * replaced by the {@code Resource}.
         * 
         * @param resource
         *            the resource to show in this UI
         * 
         * @deprecated As of 7.0, use getPage().setLocation instead
         */
        @Deprecated
        public void open(Resource resource) {
            open(resource, null);
        }

        /* ********************************************************************* */

        /**
         * Opens the given resource in a window with the given name.
         * <p>
         * The supplied {@code windowName} is used as the target name in a
         * window.open call in the client. This means that special values such
         * as "_blank", "_self", "_top", "_parent" have special meaning. An
         * empty or <code>null</code> window name is also a special case.
         * </p>
         * <p>
         * "", null and "_self" as {@code windowName} all causes the resource to
         * be opened in the current window, replacing any old contents. For
         * downloadable content you should avoid "_self" as "_self" causes the
         * client to skip rendering of any other changes as it considers them
         * irrelevant (the page will be replaced by the resource). This can
         * speed up the opening of a resource, but it might also put the client
         * side into an inconsistent state if the window content is not
         * completely replaced e.g., if the resource is downloaded instead of
         * displayed in the browser.
         * </p>
         * <p>
         * "_blank" as {@code windowName} causes the resource to always be
         * opened in a new window or tab (depends on the browser and browser
         * settings).
         * </p>
         * <p>
         * "_top" and "_parent" as {@code windowName} works as specified by the
         * HTML standard.
         * </p>
         * <p>
         * Any other {@code windowName} will open the resource in a window with
         * that name, either by opening a new window/tab in the browser or by
         * replacing the contents of an existing window with that name.
         * </p>
         * <p>
         * As of Vaadin 7.0.0, the functionality for opening a Resource in a
         * Page has been replaced with similar methods based on a String URL.
         * This is because the usage of Resource is problematic with memory
         * management and with security features in some browsers. Is is
         * recommended to instead use {@link Link} for starting downloads.
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
            open(resource, windowName, -1, -1, Page.BORDER_DEFAULT);
        }

        /**
         * Opens the given resource in a window with the given size, border and
         * name. For more information on the meaning of {@code windowName}, see
         * {@link #open(Resource, String)}.
         * <p>
         * As of Vaadin 7.0.0, the functionality for opening a Resource in a
         * Page has been replaced with similar methods based on a String URL.
         * This is because the usage of Resource is problematic with memory
         * management and with security features in some browsers. Is is
         * recommended to instead use {@link Link} for starting downloads.
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
         * Adds a new {@link BrowserWindowResizeListener} to this UI. The
         * listener will be notified whenever the browser window within which
         * this UI resides is resized.
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
         * Removes a {@link BrowserWindowResizeListener} from this UI. The
         * listener will no longer be notified when the browser window is
         * resized.
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
         * Gets the last known width of the browser window in which this UI
         * resides.
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
         * This method allows one to inject javascript from the server to
         * client. A client implementation is not required to implement this
         * functionality, but currently all web-based clients do implement this.
         * </p>
         * 
         * <p>
         * Executing javascript this way often leads to cross-browser
         * compatibility issues and regressions that are hard to resolve. Use of
         * this method should be avoided and instead it is recommended to create
         * new widgets with GWT. For more info on creating own, reusable
         * client-side widgets in Java, read the corresponding chapter in Book
         * of Vaadin.
         * </p>
         * 
         * @param script
         *            JavaScript snippet that will be executed.
         * 
         * @deprecated as of 7.0, use JavaScript.getCurrent().execute(String)
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

    }

    /**
     * Event fired when a UI is removed from the application.
     */
    public static class CleanupEvent extends Event {

        private static final String CLEANUP_EVENT_IDENTIFIER = "uiCleanup";

        public CleanupEvent(UI source) {
            super(source);
        }

        public UI getUI() {
            return (UI) getSource();
        }
    }

    /**
     * Interface for listening {@link UI.CleanupEvent UI cleanup events}.
     * 
     */
    public interface CleanupListener extends EventListener {

        public static final Method cleanupMethod = ReflectTools.findMethod(
                CleanupListener.class, "cleanup", CleanupEvent.class);

        /**
         * Called when a CleanupListener is notified of a CleanupEvent.
         * {@link UI#getCurrent()} returns <code>event.getUI()</code> within
         * this method.
         * 
         * @param event
         *            The close event that was fired.
         */
        public void cleanup(CleanupEvent event);
    }

    /**
     * The application to which this UI belongs
     */
    private VaadinServiceSession session;

    /**
     * List of windows in this UI.
     */
    private final LinkedHashSet<Window> windows = new LinkedHashSet<Window>();

    /**
     * The component that should be scrolled into view after the next repaint.
     * Null if nothing should be scrolled into view.
     */
    private Component scrollIntoView;

    /**
     * The id of this UI, used to find the server side instance of the UI form
     * which a request originates. A negative value indicates that the UI id has
     * not yet been assigned by the Application.
     * 
     * @see VaadinServiceSession#getNextUIid()
     */
    private int uiId = -1;

    /**
     * Keeps track of the Actions added to this component, and manages the
     * painting and handling as well.
     */
    protected ActionManager actionManager;

    /** Identifies the click event */
    private ConnectorTracker connectorTracker = new ConnectorTracker(this);

    private Page page = new Page(this);

    private UIServerRpc rpc = new UIServerRpc() {
        @Override
        public void click(MouseEventDetails mouseDetails) {
            fireEvent(new ClickEvent(UI.this, mouseDetails));
        }

        @Override
        public void resize(int viewWidth, int viewHeight, int windowWidth,
                int windowHeight) {
            // TODO We're not doing anything with the view dimensions
            getPage().setBrowserWindowSize(windowWidth, windowHeight);
        }
    };

    /**
     * Timestamp keeping track of the last heartbeat of this UI. Updated to the
     * current time whenever the application receives a heartbeat or UIDL
     * request from the client for this UI.
     */
    private long lastHeartbeat = System.currentTimeMillis();

    private long lastUidlRequest = System.currentTimeMillis();

    /**
     * Creates a new empty UI without a caption. This UI will have a
     * {@link VerticalLayout} with margins enabled as its content.
     */
    public UI() {
        this((ComponentContainer) null);
    }

    /**
     * Creates a new UI with the given component container as its content.
     * 
     * @param content
     *            the content container to use as this UIs content.
     * 
     * @see #setContent(ComponentContainer)
     */
    public UI(ComponentContainer content) {
        registerRpc(rpc);
        setSizeFull();
        setContent(content);
    }

    @Override
    protected UIState getState() {
        return (UIState) super.getState();
    }

    @Override
    public Class<? extends UIState> getStateType() {
        // This is a workaround for a problem with creating the correct state
        // object during build
        return UIState.class;
    }

    /**
     * Overridden to return a value instead of referring to the parent.
     * 
     * @return this UI
     * 
     * @see com.vaadin.ui.AbstractComponent#getUI()
     */
    @Override
    public UI getUI() {
        return this;
    }

    /**
     * This implementation replaces a component in the content container (
     * {@link #getContent()}) instead of in the actual UI.
     */
    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {
        getContent().replaceComponent(oldComponent, newComponent);
    }

    /**
     * Gets the application object to which the component is attached.
     * 
     * <p>
     * The method will return {@code null} if the component is not currently
     * attached to an application.
     * </p>
     * 
     * <p>
     * Getting a null value is often a problem in constructors of regular
     * components and in the initializers of custom composite components. A
     * standard workaround is to use {@link VaadinServiceSession#getCurrent()}
     * to retrieve the application instance that the current request relates to.
     * Another way is to move the problematic initialization to
     * {@link #attach()}, as described in the documentation of the method.
     * </p>
     * 
     * @return the parent application of the component or <code>null</code>.
     * @see #attach()
     */
    @Override
    public VaadinServiceSession getSession() {
        return session;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        page.paintContent(target);

        if (scrollIntoView != null) {
            target.addAttribute("scrollTo", scrollIntoView);
            scrollIntoView = null;
        }

        if (pendingFocus != null) {
            // ensure focused component is still attached to this main window
            if (pendingFocus.getUI() == this
                    || (pendingFocus.getUI() != null && pendingFocus.getUI()
                            .getParent() == this)) {
                target.addAttribute("focused", pendingFocus);
            }
            pendingFocus = null;
        }

        if (actionManager != null) {
            actionManager.paintActions(null, target);
        }

        if (isResizeLazy()) {
            target.addAttribute(UIConstants.RESIZE_LAZY, true);
        }
    }

    /**
     * Fire a click event to all click listeners.
     * 
     * @param object
     *            The raw "value" of the variable change from the client side.
     */
    private void fireClick(Map<String, Object> parameters) {
        MouseEventDetails mouseDetails = MouseEventDetails
                .deSerialize((String) parameters.get("mouseDetails"));
        fireEvent(new ClickEvent(this, mouseDetails));
    }

    /**
     * For internal use only.
     */
    public void fireCleanupEvent() {
        fireEvent(new CleanupEvent(this));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void changeVariables(Object source, Map<String, Object> variables) {
        if (variables.containsKey(EventId.CLICK_EVENT_IDENTIFIER)) {
            fireClick((Map<String, Object>) variables
                    .get(EventId.CLICK_EVENT_IDENTIFIER));
        }

        // Actions
        if (actionManager != null) {
            actionManager.handleActions(variables, this);
        }

        if (variables.containsKey(UIConstants.LOCATION_VARIABLE)) {
            String location = (String) variables
                    .get(UIConstants.LOCATION_VARIABLE);
            getPage().updateLocation(location);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.ComponentContainer#getComponentIterator()
     */
    @Override
    public Iterator<Component> getComponentIterator() {
        // TODO could directly create some kind of combined iterator instead of
        // creating a new ArrayList
        ArrayList<Component> components = new ArrayList<Component>();

        if (getContent() != null) {
            components.add(getContent());
        }

        components.addAll(windows);

        return components.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.ComponentContainer#getComponentCount()
     */
    @Override
    public int getComponentCount() {
        return windows.size() + (getContent() == null ? 0 : 1);
    }

    /**
     * Sets the application to which this UI is assigned. It is not legal to
     * change the application once it has been set nor to set a
     * <code>null</code> application.
     * <p>
     * This method is mainly intended for internal use by the framework.
     * </p>
     * 
     * @param session
     *            the application to set
     * 
     * @throws IllegalStateException
     *             if the application has already been set
     * 
     * @see #getSession()
     */
    public void setSession(VaadinServiceSession session) {
        if ((session == null) == (this.session == null)) {
            throw new IllegalStateException(
                    "VaadinServiceSession has already been set. Old session: "
                            + getSessionDetails(this.session)
                            + ". New session: " + getSessionDetails(session)
                            + ".");
        } else {
            if (session == null) {
                detach();
            }
            this.session = session;
        }

        if (session != null) {
            attach();
        }
    }

    private static String getSessionDetails(VaadinServiceSession session) {
        if (session == null) {
            return null;
        } else {
            return session.toString() + " for "
                    + session.getService().getServiceName();
        }
    }

    /**
     * Gets the id of the UI, used to identify this UI within its application
     * when processing requests. The UI id should be present in every request to
     * the server that originates from this UI.
     * {@link VaadinService#findUI(VaadinRequest)} uses this id to find the
     * route to which the request belongs.
     * 
     * @return
     */
    public int getUIId() {
        return uiId;
    }

    /**
     * Adds a window as a subwindow inside this UI. To open a new browser window
     * or tab, you should instead use a {@link UIProvider}.
     * 
     * @param window
     * @throws IllegalArgumentException
     *             if the window is already added to an application
     * @throws NullPointerException
     *             if the given <code>Window</code> is <code>null</code>.
     */
    public void addWindow(Window window) throws IllegalArgumentException,
            NullPointerException {

        if (window == null) {
            throw new NullPointerException("Argument must not be null");
        }

        if (window.getUI() != null && window.getUI().getSession() != null) {
            throw new IllegalArgumentException(
                    "Window is already attached to an application.");
        }

        attachWindow(window);
    }

    /**
     * Helper method to attach a window.
     * 
     * @param w
     *            the window to add
     */
    private void attachWindow(Window w) {
        windows.add(w);
        w.setParent(this);
        markAsDirty();
    }

    /**
     * Remove the given subwindow from this UI.
     * 
     * Since Vaadin 6.5, {@link Window.CloseListener}s are called also when
     * explicitly removing a window by calling this method.
     * 
     * Since Vaadin 6.5, returns a boolean indicating if the window was removed
     * or not.
     * 
     * @param window
     *            Window to be removed.
     * @return true if the subwindow was removed, false otherwise
     */
    public boolean removeWindow(Window window) {
        if (!windows.remove(window)) {
            // Window window is not a subwindow of this UI.
            return false;
        }
        window.setParent(null);
        window.fireClose();
        markAsDirty();

        return true;
    }

    /**
     * Gets all the windows added to this UI.
     * 
     * @return an unmodifiable collection of windows
     */
    public Collection<Window> getWindows() {
        return Collections.unmodifiableCollection(windows);
    }

    @Override
    public void focus() {
        super.focus();
    }

    /**
     * Component that should be focused after the next repaint. Null if no focus
     * change should take place.
     */
    private Focusable pendingFocus;

    private boolean resizeLazy = false;

    private String theme;

    private Navigator navigator;

    /**
     * This method is used by Component.Focusable objects to request focus to
     * themselves. Focus renders must be handled at window level (instead of
     * Component.Focusable) due we want the last focused component to be focused
     * in client too. Not the one that is rendered last (the case we'd get if
     * implemented in Focusable only).
     * 
     * To focus component from Vaadin application, use Focusable.focus(). See
     * {@link Focusable}.
     * 
     * @param focusable
     *            to be focused on next paint
     */
    public void setFocusedComponent(Focusable focusable) {
        pendingFocus = focusable;
        markAsDirty();
    }

    /**
     * Scrolls any component between the component and UI to a suitable position
     * so the component is visible to the user. The given component must belong
     * to this UI.
     * 
     * @param component
     *            the component to be scrolled into view
     * @throws IllegalArgumentException
     *             if {@code component} does not belong to this UI
     */
    public void scrollIntoView(Component component)
            throws IllegalArgumentException {
        if (component.getUI() != this) {
            throw new IllegalArgumentException(
                    "The component where to scroll must belong to this UI.");
        }
        scrollIntoView = component;
        markAsDirty();
    }

    /**
     * Gets the content of this UI. The content is a component container that
     * serves as the outermost item of the visual contents of this UI.
     * 
     * @return a component container to use as content
     * 
     * @see #setContent(ComponentContainer)
     * @see #createDefaultLayout()
     */
    public ComponentContainer getContent() {
        return (ComponentContainer) getState().content;
    }

    /**
     * Helper method to create the default content layout that is used if no
     * content has not been explicitly defined.
     * 
     * @return a newly created layout
     */
    private static VerticalLayout createDefaultLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        return layout;
    }

    /**
     * Sets the content of this UI. The content is a component container that
     * serves as the outermost item of the visual contents of this UI. If no
     * content has been set, a {@link VerticalLayout} with margins enabled will
     * be used by default - see {@link #createDefaultLayout()}. The content can
     * also be set in a constructor.
     * 
     * @return a component container to use as content
     * 
     * @see #UI(ComponentContainer)
     * @see #createDefaultLayout()
     */
    public void setContent(ComponentContainer content) {
        if (content == null) {
            content = createDefaultLayout();
        }

        if (getState().content != null) {
            super.removeComponent((Component) getState().content);
        }
        getState().content = content;
        if (content != null) {
            super.addComponent(content);
        }
    }

    /**
     * Adds a component to this UI. The component is not added directly to the
     * UI, but instead to the content container ({@link #getContent()}).
     * 
     * @param component
     *            the component to add to this UI
     * 
     * @see #getContent()
     */
    @Override
    public void addComponent(Component component) {
        getContent().addComponent(component);
    }

    /**
     * This implementation removes the component from the content container (
     * {@link #getContent()}) instead of from the actual UI.
     */
    @Override
    public void removeComponent(Component component) {
        getContent().removeComponent(component);
    }

    /**
     * This implementation removes the components from the content container (
     * {@link #getContent()}) instead of from the actual UI.
     */
    @Override
    public void removeAllComponents() {
        getContent().removeAllComponents();
    }

    /**
     * Internal initialization method, should not be overridden. This method is
     * not declared as final because that would break compatibility with e.g.
     * CDI.
     * 
     * @param request
     *            the initialization request
     * @param uiId
     *            the id of the new ui
     */
    public void doInit(VaadinRequest request, int uiId) {
        if (this.uiId != -1) {
            throw new IllegalStateException("UI id has already been defined");
        }
        this.uiId = uiId;

        // Actual theme - used for finding CustomLayout templates
        theme = request.getParameter("theme");

        getPage().init(request);

        // Call the init overridden by the application developer
        init(request);
    }

    /**
     * Initializes this UI. This method is intended to be overridden by
     * subclasses to build the view and configure non-component functionality.
     * Performing the initialization in a constructor is not suggested as the
     * state of the UI is not properly set up when the constructor is invoked.
     * <p>
     * The {@link VaadinRequest} can be used to get information about the
     * request that caused this UI to be created.
     * </p>
     * 
     * @param request
     *            the Vaadin request that caused this UI to be created
     */
    protected abstract void init(VaadinRequest request);

    /**
     * Sets the thread local for the current UI. This method is used by the
     * framework to set the current application whenever a new request is
     * processed and it is cleared when the request has been processed.
     * <p>
     * The application developer can also use this method to define the current
     * UI outside the normal request handling, e.g. when initiating custom
     * background threads.
     * </p>
     * 
     * @param uI
     *            the UI to register as the current UI
     * 
     * @see #getCurrent()
     * @see ThreadLocal
     */
    public static void setCurrent(UI ui) {
        CurrentInstance.setInheritable(UI.class, ui);
    }

    /**
     * Gets the currently used UI. The current UI is automatically defined when
     * processing requests to the server. In other cases, (e.g. from background
     * threads), the current UI is not automatically defined.
     * 
     * @return the current UI instance if available, otherwise <code>null</code>
     * 
     * @see #setCurrent(UI)
     */
    public static UI getCurrent() {
        return CurrentInstance.get(UI.class);
    }

    public void setScrollTop(int scrollTop) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    protected ActionManager getActionManager() {
        if (actionManager == null) {
            actionManager = new ActionManager(this);
        }
        return actionManager;
    }

    @Override
    public <T extends Action & com.vaadin.event.Action.Listener> void addAction(
            T action) {
        getActionManager().addAction(action);
    }

    @Override
    public <T extends Action & com.vaadin.event.Action.Listener> void removeAction(
            T action) {
        if (actionManager != null) {
            actionManager.removeAction(action);
        }
    }

    @Override
    public void addActionHandler(Handler actionHandler) {
        getActionManager().addActionHandler(actionHandler);
    }

    @Override
    public void removeActionHandler(Handler actionHandler) {
        if (actionManager != null) {
            actionManager.removeActionHandler(actionHandler);
        }
    }

    /**
     * Should resize operations be lazy, i.e. should there be a delay before
     * layout sizes are recalculated. Speeds up resize operations in slow UIs
     * with the penalty of slightly decreased usability.
     * <p>
     * Default value: <code>false</code>
     * 
     * @param resizeLazy
     *            true to use a delay before recalculating sizes, false to
     *            calculate immediately.
     */
    public void setResizeLazy(boolean resizeLazy) {
        this.resizeLazy = resizeLazy;
        markAsDirty();
    }

    /**
     * Checks whether lazy resize is enabled.
     * 
     * @return <code>true</code> if lazy resize is enabled, <code>false</code>
     *         if lazy resize is not enabled
     */
    public boolean isResizeLazy() {
        return resizeLazy;
    }

    /**
     * Add a click listener to the UI. The listener is called whenever the user
     * clicks inside the UI. Also when the click targets a component inside the
     * UI, provided the targeted component does not prevent the click event from
     * propagating.
     * 
     * Use {@link #removeListener(ClickListener)} to remove the listener.
     * 
     * @param listener
     *            The listener to add
     */
    public void addClickListener(ClickListener listener) {
        addListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class, listener,
                ClickListener.clickMethod);
    }

    /**
     * Adds a cleanup listener to the UI. Cleanup listeners are invoked when the
     * UI is removed from the session due to UI or session expiration.
     * 
     * @param listener
     *            The CleanupListener that should be added.
     */
    public void addCleanupListener(CleanupListener listener) {
        addListener(CleanupEvent.CLEANUP_EVENT_IDENTIFIER, CleanupEvent.class,
                listener, CleanupListener.cleanupMethod);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #addClickListener(ClickListener)}
     **/
    @Deprecated
    public void addListener(ClickListener listener) {
        addClickListener(listener);
    }

    /**
     * Remove a click listener from the UI. The listener should earlier have
     * been added using {@link #addListener(ClickListener)}.
     * 
     * @param listener
     *            The listener to remove
     */
    public void removeClickListener(ClickListener listener) {
        removeListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class,
                listener);
    }

    /**
     * Removes a cleanup listener from the UI, if previously added with
     * {@link #addCleanupListener(CleanupListener)}.
     * 
     * @param listener
     *            The CleanupListener that should be removed
     */
    public void removeCleanupListener(CleanupListener listener) {
        removeListener(CleanupEvent.CLEANUP_EVENT_IDENTIFIER,
                CleanupEvent.class, listener);
    }

    /**
     * @deprecated Since 7.0, replaced by
     *             {@link #removeClickListener(ClickListener)}
     **/
    @Deprecated
    public void removeListener(ClickListener listener) {
        removeClickListener(listener);
    }

    @Override
    public boolean isConnectorEnabled() {
        // TODO How can a UI be invisible? What does it mean?
        return isVisible() && isEnabled();
    }

    public ConnectorTracker getConnectorTracker() {
        return connectorTracker;
    }

    public Page getPage() {
        return page;
    }

    /**
     * Returns the navigator attached to this UI or null if there is no
     * navigator.
     * 
     * @return
     */
    public Navigator getNavigator() {
        return navigator;
    }

    /**
     * For internal use only.
     * 
     * @param navigator
     */
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    /**
     * Setting the caption of a UI is not supported. To set the title of the
     * HTML page, use Page.setTitle
     * 
     * @deprecated as of 7.0.0, use {@link Page#setTitle(String)}
     */
    @Override
    @Deprecated
    public void setCaption(String caption) {
        throw new UnsupportedOperationException(
                "You can not set the title of a UI. To set the title of the HTML page, use Page.setTitle");
    }

    /**
     * Shows a notification message on the middle of the UI. The message
     * automatically disappears ("humanized message").
     * 
     * Care should be taken to to avoid XSS vulnerabilities as the caption is
     * rendered as html.
     * 
     * @see #showNotification(Notification)
     * @see Notification
     * 
     * @param caption
     *            The message
     * 
     * @deprecated As of 7.0, use Notification.show instead but be aware that
     *             Notification.show does not allow HTML.
     */
    @Deprecated
    public void showNotification(String caption) {
        Notification notification = new Notification(caption);
        notification.setHtmlContentAllowed(true);// Backwards compatibility
        getPage().showNotification(notification);
    }

    /**
     * Shows a notification message the UI. The position and behavior of the
     * message depends on the type, which is one of the basic types defined in
     * {@link Notification}, for instance Notification.TYPE_WARNING_MESSAGE.
     * 
     * Care should be taken to to avoid XSS vulnerabilities as the caption is
     * rendered as html.
     * 
     * @see #showNotification(Notification)
     * @see Notification
     * 
     * @param caption
     *            The message
     * @param type
     *            The message type
     * 
     * @deprecated As of 7.0, use Notification.show instead but be aware that
     *             Notification.show does not allow HTML.
     */
    @Deprecated
    public void showNotification(String caption, Notification.Type type) {
        Notification notification = new Notification(caption, type);
        notification.setHtmlContentAllowed(true);// Backwards compatibility
        getPage().showNotification(notification);
    }

    /**
     * Shows a notification consisting of a bigger caption and a smaller
     * description on the middle of the UI. The message automatically disappears
     * ("humanized message").
     * 
     * Care should be taken to to avoid XSS vulnerabilities as the caption and
     * description are rendered as html.
     * 
     * @see #showNotification(Notification)
     * @see Notification
     * 
     * @param caption
     *            The caption of the message
     * @param description
     *            The message description
     * 
     * @deprecated As of 7.0, use new Notification(...).show(Page) instead but
     *             be aware that HTML by default not allowed.
     */
    @Deprecated
    public void showNotification(String caption, String description) {
        Notification notification = new Notification(caption, description);
        notification.setHtmlContentAllowed(true);// Backwards compatibility
        getPage().showNotification(notification);
    }

    /**
     * Shows a notification consisting of a bigger caption and a smaller
     * description. The position and behavior of the message depends on the
     * type, which is one of the basic types defined in {@link Notification} ,
     * for instance Notification.TYPE_WARNING_MESSAGE.
     * 
     * Care should be taken to to avoid XSS vulnerabilities as the caption and
     * description are rendered as html.
     * 
     * @see #showNotification(Notification)
     * @see Notification
     * 
     * @param caption
     *            The caption of the message
     * @param description
     *            The message description
     * @param type
     *            The message type
     * 
     * @deprecated As of 7.0, use new Notification(...).show(Page) instead but
     *             be aware that HTML by default not allowed.
     */
    @Deprecated
    public void showNotification(String caption, String description,
            Notification.Type type) {
        Notification notification = new Notification(caption, description, type);
        notification.setHtmlContentAllowed(true);// Backwards compatibility
        getPage().showNotification(notification);
    }

    /**
     * Shows a notification consisting of a bigger caption and a smaller
     * description. The position and behavior of the message depends on the
     * type, which is one of the basic types defined in {@link Notification} ,
     * for instance Notification.TYPE_WARNING_MESSAGE.
     * 
     * Care should be taken to avoid XSS vulnerabilities if html content is
     * allowed.
     * 
     * @see #showNotification(Notification)
     * @see Notification
     * 
     * @param caption
     *            The message caption
     * @param description
     *            The message description
     * @param type
     *            The type of message
     * @param htmlContentAllowed
     *            Whether html in the caption and description should be
     *            displayed as html or as plain text
     * 
     * @deprecated As of 7.0, use new Notification(...).show(Page).
     */
    @Deprecated
    public void showNotification(String caption, String description,
            Notification.Type type, boolean htmlContentAllowed) {
        getPage()
                .showNotification(
                        new Notification(caption, description, type,
                                htmlContentAllowed));
    }

    /**
     * Shows a notification message.
     * 
     * @see Notification
     * @see #showNotification(String)
     * @see #showNotification(String, int)
     * @see #showNotification(String, String)
     * @see #showNotification(String, String, int)
     * 
     * @param notification
     *            The notification message to show
     * 
     * @deprecated As of 7.0, use Notification.show instead
     */
    @Deprecated
    public void showNotification(Notification notification) {
        getPage().showNotification(notification);
    }

    /**
     * Returns the timestamp (milliseconds since the epoch) of the last received
     * heartbeat for this UI.
     * 
     * @see #heartbeat()
     * @see VaadinServiceSession#cleanupInactiveUIs()
     * 
     * @return The time the last heartbeat request occurred.
     */
    public long getLastHeartbeatTime() {
        return lastHeartbeat;
    }

    /**
     * Returns the timestamp (milliseconds since the epoch) of the last received
     * UIDL request for this UI.
     * 
     * @return
     */
    public long getLastUidlRequestTime() {
        return lastUidlRequest;
    }

    /**
     * Sets the last heartbeat request timestamp for this UI. Called by the
     * framework whenever the application receives a valid heartbeat request for
     * this UI.
     */
    public void setLastHeartbeatTime(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    /**
     * Sets the last UIDL request timestamp for this UI. Called by the framework
     * whenever the application receives a valid UIDL request for this UI.
     */
    public void setLastUidlRequestTime(long lastUidlRequest) {
        this.lastUidlRequest = lastUidlRequest;
    }

    /**
     * Gets the theme that was used when the UI was initialized.
     * 
     * @return the theme name
     */
    public String getTheme() {
        return theme;
    }
}
