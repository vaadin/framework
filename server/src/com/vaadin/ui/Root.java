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

import com.vaadin.Application;
import com.vaadin.annotations.EagerInit;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ActionManager;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.root.RootConstants;
import com.vaadin.shared.ui.root.RootServerRpc;
import com.vaadin.shared.ui.root.RootState;
import com.vaadin.terminal.Page;
import com.vaadin.terminal.Page.BrowserWindowResizeEvent;
import com.vaadin.terminal.Page.BrowserWindowResizeListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.Vaadin6Component;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedRequest.BrowserDetails;
import com.vaadin.tools.ReflectTools;

/**
 * The topmost component in any component hierarchy. There is one root for every
 * Vaadin instance in a browser window. A root may either represent an entire
 * browser window (or tab) or some part of a html page where a Vaadin
 * application is embedded.
 * <p>
 * The root is the server side entry point for various client side features that
 * are not represented as components added to a layout, e.g notifications, sub
 * windows, and executing javascript in the browser.
 * </p>
 * <p>
 * When a new application instance is needed, typically because the user opens
 * the application in a browser window,
 * {@link Application#gerRoot(WrappedRequest)} is invoked to get a root. That
 * method does by default create a root according to the
 * {@value Application#ROOT_PARAMETER} parameter from web.xml.
 * </p>
 * <p>
 * After a root has been created by the application, it is initialized using
 * {@link #init(WrappedRequest)}. This method is intended to be overridden by
 * the developer to add components to the user interface and initialize
 * non-component functionality. The component hierarchy is initialized by
 * passing a {@link ComponentContainer} with the main layout of the view to
 * {@link #setContent(ComponentContainer)}.
 * </p>
 * <p>
 * If a {@link EagerInit} annotation is present on a class extending
 * <code>Root</code>, the framework will use a faster initialization method
 * which will not ensure that {@link BrowserDetails} are present in the
 * {@link WrappedRequest} passed to the init method.
 * </p>
 * 
 * @see #init(WrappedRequest)
 * @see Application#getRoot(WrappedRequest)
 * 
 * @since 7.0
 */
public abstract class Root extends AbstractComponentContainer implements
        Action.Container, Action.Notifier, Vaadin6Component {

    /**
     * Helper class to emulate the main window from Vaadin 6 using roots. This
     * class should be used in the same way as Window used as a browser level
     * window in Vaadin 6 with {@link com.vaadin.Application.LegacyApplication}
     */
    @Deprecated
    @EagerInit
    public static class LegacyWindow extends Root {
        private String name;

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
            super(caption);
        }

        /**
         * Creates a legacy window with the given caption and content layout
         * 
         * @param caption
         * @param content
         */
        public LegacyWindow(String caption, ComponentContainer content) {
            super(caption, content);
        }

        @Override
        protected void init(WrappedRequest request) {
            // Just empty
        }

        /**
         * Gets the unique name of the window. The name of the window is used to
         * uniquely identify it.
         * <p>
         * The name also determines the URL that can be used for direct access
         * to a window. All windows can be accessed through
         * {@code http://host:port/app/win} where {@code http://host:port/app}
         * is the application URL (as returned by {@link Application#getURL()}
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
         * The name also determines the URL that can be used for direct access
         * to a window. All windows can be accessed through
         * {@code http://host:port/app/win} where {@code http://host:port/app}
         * is the application URL (as returned by {@link Application#getURL()}
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
         *            the new name for the window or null if the application
         *            should automatically assign a name to it
         * @throws IllegalStateException
         *             if the window is attached to an application
         */
        public void setName(String name) {
            this.name = name;
            // The name can not be changed in application
            if (getApplication() != null) {
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
            Application application = getApplication();
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
         * Opens the given resource in this root. The contents of this Root is
         * replaced by the {@code Resource}.
         * 
         * @param resource
         *            the resource to show in this root
         * 
         * @deprecated As of 7.0, use getPage().open instead
         */
        @Deprecated
        public void open(Resource resource) {
            getPage().open(resource);
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
         * 
         * @param resource
         *            the resource.
         * @param windowName
         *            the name of the window.
         * @deprecated As of 7.0, use getPage().open instead
         */
        @Deprecated
        public void open(Resource resource, String windowName) {
            getPage().open(resource, windowName);
        }

        /**
         * Opens the given resource in a window with the given size, border and
         * name. For more information on the meaning of {@code windowName}, see
         * {@link #open(Resource, String)}.
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
         *            the border style of the window. See {@link #BORDER_NONE
         *            Window.BORDER_* constants}
         * @deprecated As of 7.0, use getPage().open instead
         */
        @Deprecated
        public void open(Resource resource, String windowName, int width,
                int height, int border) {
            getPage().open(resource, windowName, width, height, border);
        }

        /**
         * Adds a new {@link BrowserWindowResizeListener} to this root. The
         * listener will be notified whenever the browser window within which
         * this root resides is resized.
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
         * Removes a {@link BrowserWindowResizeListener} from this root. The
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
         * Gets the last known height of the browser window in which this root
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
         * Gets the last known width of the browser window in which this root
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
            getState().setCaption(caption);
            getPage().setTitle(caption);
        }

    }

    /**
     * Event fired when a Root is removed from the application.
     */
    public static class CloseEvent extends Event {

        private static final String CLOSE_EVENT_IDENTIFIER = "rootClose";

        public CloseEvent(Root source) {
            super(source);
        }

        public Root getRoot() {
            return (Root) getSource();
        }
    }

    /**
     * Interface for listening {@link Root.CloseEvent root close events}.
     * 
     */
    public interface CloseListener extends EventListener {

        public static final Method closeMethod = ReflectTools.findMethod(
                CloseListener.class, "click", CloseEvent.class);

        /**
         * Called when a CloseListener is notified of a CloseEvent.
         * {@link Root#getCurrent()} returns <code>event.getRoot()</code> within
         * this method.
         * 
         * @param event
         *            The close event that was fired.
         */
        public void close(CloseEvent event);
    }

    /**
     * The application to which this root belongs
     */
    private Application application;

    /**
     * List of windows in this root.
     */
    private final LinkedHashSet<Window> windows = new LinkedHashSet<Window>();

    /**
     * The component that should be scrolled into view after the next repaint.
     * Null if nothing should be scrolled into view.
     */
    private Component scrollIntoView;

    /**
     * The id of this root, used to find the server side instance of the root
     * form which a request originates. A negative value indicates that the root
     * id has not yet been assigned by the Application.
     * 
     * @see Application#nextRootId
     */
    private int rootId = -1;

    /**
     * Keeps track of the Actions added to this component, and manages the
     * painting and handling as well.
     */
    protected ActionManager actionManager;

    /**
     * Thread local for keeping track of the current root.
     */
    private static final ThreadLocal<Root> currentRoot = new ThreadLocal<Root>();

    /** Identifies the click event */
    private ConnectorTracker connectorTracker = new ConnectorTracker(this);

    private Page page = new Page(this);

    private RootServerRpc rpc = new RootServerRpc() {
        @Override
        public void click(MouseEventDetails mouseDetails) {
            fireEvent(new ClickEvent(Root.this, mouseDetails));
        }
    };

    /**
     * Timestamp keeping track of the last heartbeat of this Root. Updated to
     * the current time whenever the application receives a heartbeat or UIDL
     * request from the client for this Root.
     */
    private long lastHeartbeat = System.currentTimeMillis();

    /**
     * Creates a new empty root without a caption. This root will have a
     * {@link VerticalLayout} with margins enabled as its content.
     */
    public Root() {
        this((ComponentContainer) null);
    }

    /**
     * Creates a new root with the given component container as its content.
     * 
     * @param content
     *            the content container to use as this roots content.
     * 
     * @see #setContent(ComponentContainer)
     */
    public Root(ComponentContainer content) {
        registerRpc(rpc);
        setSizeFull();
        setContent(content);
    }

    /**
     * Creates a new empty root with the given caption. This root will have a
     * {@link VerticalLayout} with margins enabled as its content.
     * 
     * @param caption
     *            the caption of the root, used as the page title if there's
     *            nothing but the application on the web page
     * 
     * @see #setCaption(String)
     */
    public Root(String caption) {
        this((ComponentContainer) null);
        setCaption(caption);
    }

    /**
     * Creates a new root with the given caption and content.
     * 
     * @param caption
     *            the caption of the root, used as the page title if there's
     *            nothing but the application on the web page
     * @param content
     *            the content container to use as this roots content.
     * 
     * @see #setContent(ComponentContainer)
     * @see #setCaption(String)
     */
    public Root(String caption, ComponentContainer content) {
        this(content);
        setCaption(caption);
    }

    @Override
    public RootState getState() {
        return (RootState) super.getState();
    }

    @Override
    public Class<? extends RootState> getStateType() {
        // This is a workaround for a problem with creating the correct state
        // object during build
        return RootState.class;
    }

    /**
     * Overridden to return a value instead of referring to the parent.
     * 
     * @return this root
     * 
     * @see com.vaadin.ui.AbstractComponent#getRoot()
     */
    @Override
    public Root getRoot() {
        return this;
    }

    @Override
    public void replaceComponent(Component oldComponent, Component newComponent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Application getApplication() {
        return application;
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
            if (pendingFocus.getRoot() == this
                    || (pendingFocus.getRoot() != null && pendingFocus
                            .getRoot().getParent() == this)) {
                target.addAttribute("focused", pendingFocus);
            }
            pendingFocus = null;
        }

        if (actionManager != null) {
            actionManager.paintActions(null, target);
        }

        if (isResizeLazy()) {
            target.addAttribute(RootConstants.RESIZE_LAZY, true);
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
    public void fireCloseEvent() {
        Root current = Root.getCurrent();
        Root.setCurrent(this);
        fireEvent(new CloseEvent(this));
        Root.setCurrent(current);
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

        if (variables.containsKey(RootConstants.FRAGMENT_VARIABLE)) {
            String fragment = (String) variables
                    .get(RootConstants.FRAGMENT_VARIABLE);
            getPage().setFragment(fragment, true);
        }

        if (variables.containsKey("height") || variables.containsKey("width")) {
            getPage().setBrowserWindowSize((Integer) variables.get("width"),
                    (Integer) variables.get("height"));
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
     * Sets the application to which this root is assigned. It is not legal to
     * change the application once it has been set nor to set a
     * <code>null</code> application.
     * <p>
     * This method is mainly intended for internal use by the framework.
     * </p>
     * 
     * @param application
     *            the application to set
     * 
     * @throws IllegalStateException
     *             if the application has already been set
     * 
     * @see #getApplication()
     */
    public void setApplication(Application application) {
        if ((application == null) == (this.application == null)) {
            throw new IllegalStateException("Application has already been set");
        } else {
            this.application = application;
        }

        if (application != null) {
            attach();
        } else {
            detach();
        }
    }

    /**
     * Sets the id of this root within its application. The root id is used to
     * route requests to the right root.
     * <p>
     * This method is mainly intended for internal use by the framework.
     * </p>
     * 
     * @param rootId
     *            the id of this root
     * 
     * @throws IllegalStateException
     *             if the root id has already been set
     * 
     * @see #getRootId()
     */
    public void setRootId(int rootId) {
        if (this.rootId != -1) {
            throw new IllegalStateException("Root id has already been defined");
        }
        this.rootId = rootId;
    }

    /**
     * Gets the id of the root, used to identify this root within its
     * application when processing requests. The root id should be present in
     * every request to the server that originates from this root.
     * {@link Application#getRootForRequest(WrappedRequest)} uses this id to
     * find the route to which the request belongs.
     * 
     * @return
     */
    public int getRootId() {
        return rootId;
    }

    /**
     * Adds a window as a subwindow inside this root. To open a new browser
     * window or tab, you should instead use {@link open(Resource)} with an url
     * pointing to this application and ensure
     * {@link Application#getRoot(WrappedRequest)} returns an appropriate root
     * for the request.
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

        if (window.getApplication() != null) {
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
        requestRepaint();
    }

    /**
     * Remove the given subwindow from this root.
     * 
     * Since Vaadin 6.5, {@link CloseListener}s are called also when explicitly
     * removing a window by calling this method.
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
            // Window window is not a subwindow of this root.
            return false;
        }
        window.setParent(null);
        window.fireClose();
        requestRepaint();

        return true;
    }

    /**
     * Gets all the windows added to this root.
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
        requestRepaint();
    }

    /**
     * Scrolls any component between the component and root to a suitable
     * position so the component is visible to the user. The given component
     * must belong to this root.
     * 
     * @param component
     *            the component to be scrolled into view
     * @throws IllegalArgumentException
     *             if {@code component} does not belong to this root
     */
    public void scrollIntoView(Component component)
            throws IllegalArgumentException {
        if (component.getRoot() != this) {
            throw new IllegalArgumentException(
                    "The component where to scroll must belong to this root.");
        }
        scrollIntoView = component;
        requestRepaint();
    }

    /**
     * Gets the content of this root. The content is a component container that
     * serves as the outermost item of the visual contents of this root.
     * 
     * @return a component container to use as content
     * 
     * @see #setContent(ComponentContainer)
     * @see #createDefaultLayout()
     */
    public ComponentContainer getContent() {
        return (ComponentContainer) getState().getContent();
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
     * Sets the content of this root. The content is a component container that
     * serves as the outermost item of the visual contents of this root. If no
     * content has been set, a {@link VerticalLayout} with margins enabled will
     * be used by default - see {@link #createDefaultLayout()}. The content can
     * also be set in a constructor.
     * 
     * @return a component container to use as content
     * 
     * @see #Root(ComponentContainer)
     * @see #createDefaultLayout()
     */
    public void setContent(ComponentContainer content) {
        if (content == null) {
            content = createDefaultLayout();
        }

        if (getState().getContent() != null) {
            super.removeComponent((Component) getState().getContent());
        }
        getState().setContent(content);
        if (content != null) {
            super.addComponent(content);
        }

        requestRepaint();
    }

    /**
     * Adds a component to this root. The component is not added directly to the
     * root, but instead to the content container ({@link #getContent()}).
     * 
     * @param component
     *            the component to add to this root
     * 
     * @see #getContent()
     */
    @Override
    public void addComponent(Component component) {
        getContent().addComponent(component);
    }

    /**
     * This implementation removes the component from the content container (
     * {@link #getContent()}) instead of from the actual root.
     */
    @Override
    public void removeComponent(Component component) {
        getContent().removeComponent(component);
    }

    /**
     * This implementation removes the components from the content container (
     * {@link #getContent()}) instead of from the actual root.
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
     */
    public void doInit(WrappedRequest request) {
        getPage().init(request);

        // Call the init overridden by the application developer
        init(request);
    }

    /**
     * Initializes this root. This method is intended to be overridden by
     * subclasses to build the view and configure non-component functionality.
     * Performing the initialization in a constructor is not suggested as the
     * state of the root is not properly set up when the constructor is invoked.
     * <p>
     * The {@link WrappedRequest} can be used to get information about the
     * request that caused this root to be created. By default, the
     * {@link BrowserDetails} will be available in the request. If the browser
     * details are not required, loading the application in the browser can take
     * some shortcuts giving a faster initial rendering. This can be indicated
     * by adding the {@link EagerInit} annotation to the Root class.
     * </p>
     * 
     * @param request
     *            the wrapped request that caused this root to be created
     */
    protected abstract void init(WrappedRequest request);

    /**
     * Sets the thread local for the current root. This method is used by the
     * framework to set the current application whenever a new request is
     * processed and it is cleared when the request has been processed.
     * <p>
     * The application developer can also use this method to define the current
     * root outside the normal request handling, e.g. when initiating custom
     * background threads.
     * </p>
     * 
     * @param root
     *            the root to register as the current root
     * 
     * @see #getCurrent()
     * @see ThreadLocal
     */
    public static void setCurrent(Root root) {
        currentRoot.set(root);
    }

    /**
     * Gets the currently used root. The current root is automatically defined
     * when processing requests to the server. In other cases, (e.g. from
     * background threads), the current root is not automatically defined.
     * 
     * @return the current root instance if available, otherwise
     *         <code>null</code>
     * 
     * @see #setCurrent(Root)
     */
    public static Root getCurrent() {
        return currentRoot.get();
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
        requestRepaint();
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
     * Add a click listener to the Root. The listener is called whenever the
     * user clicks inside the Root. Also when the click targets a component
     * inside the Root, provided the targeted component does not prevent the
     * click event from propagating.
     * 
     * Use {@link #removeListener(ClickListener)} to remove the listener.
     * 
     * @param listener
     *            The listener to add
     */
    public void addListener(ClickListener listener) {
        addListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class, listener,
                ClickListener.clickMethod);
    }

    /**
     * Remove a click listener from the Root. The listener should earlier have
     * been added using {@link #addListener(ClickListener)}.
     * 
     * @param listener
     *            The listener to remove
     */
    public void removeListener(ClickListener listener) {
        removeListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class,
                listener);
    }

    /**
     * Adds a close listener to the Root. The listener is called when the Root
     * is removed from the application.
     * 
     * @param listener
     *            The listener to add.
     */
    public void addListener(CloseListener listener) {
        addListener(CloseEvent.CLOSE_EVENT_IDENTIFIER, CloseEvent.class,
                listener, CloseListener.closeMethod);
    }

    /**
     * Removes a close listener from the Root if it has previously been added
     * with {@link #addListener(ClickListener)}. Otherwise, has no effect.
     * 
     * @param listener
     *            The listener to remove.
     */
    public void removeListener(CloseListener listener) {
        removeListener(CloseEvent.CLOSE_EVENT_IDENTIFIER, CloseEvent.class,
                listener);
    }

    @Override
    public boolean isConnectorEnabled() {
        // TODO How can a Root be invisible? What does it mean?
        return isVisible() && isEnabled();
    }

    public ConnectorTracker getConnectorTracker() {
        return connectorTracker;
    }

    public Page getPage() {
        return page;
    }

    /**
     * Setting the caption of a Root is not supported. To set the title of the
     * HTML page, use Page.setTitle
     * 
     * @deprecated as of 7.0.0, use {@link Page#setTitle(String)}
     */
    @Override
    @Deprecated
    public void setCaption(String caption) {
        throw new IllegalStateException(
                "You can not set the title of a Root. To set the title of the HTML page, use Page.setTitle");
    }

    /**
     * Shows a notification message on the middle of the root. The message
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
     * Shows a notification message the root. The position and behavior of the
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
    public void showNotification(String caption, int type) {
        Notification notification = new Notification(caption, type);
        notification.setHtmlContentAllowed(true);// Backwards compatibility
        getPage().showNotification(notification);
    }

    /**
     * Shows a notification consisting of a bigger caption and a smaller
     * description on the middle of the root. The message automatically
     * disappears ("humanized message").
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
    public void showNotification(String caption, String description, int type) {
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
    public void showNotification(String caption, String description, int type,
            boolean htmlContentAllowed) {
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
     * Returns the timestamp (millisecond since the epoch) of the last received
     * heartbeat for this Root.
     * 
     * @see #heartbeat()
     * @see Application#closeInactiveRoots()
     * 
     * @return The time
     */
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    /**
     * Updates the heartbeat timestamp of this Root to the current time. Called
     * by the framework whenever the application receives a valid heartbeat or
     * UIDL request for this Root.
     * 
     * @see java.lang.System#currentTimeMillis()
     */
    public void heartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }
}
