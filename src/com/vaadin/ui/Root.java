/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vaadin.Application;
import com.vaadin.annotations.EagerInit;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ActionManager;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.Vaadin6Component;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedRequest.BrowserDetails;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.VNotification;
import com.vaadin.terminal.gwt.client.ui.root.RootServerRPC;
import com.vaadin.terminal.gwt.client.ui.root.RootState;
import com.vaadin.terminal.gwt.client.ui.root.VRoot;
import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.Window.CloseListener;

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
     * Listener that gets notified when the size of the browser window
     * containing the root has changed.
     * 
     * @see Root#addListener(BrowserWindowResizeListener)
     */
    public interface BrowserWindowResizeListener extends Serializable {
        /**
         * Invoked when the browser window containing a Root has been resized.
         * 
         * @param event
         *            a browser window resize event
         */
        public void browserWindowResized(BrowserWindowResizeEvent event);
    }

    /**
     * Event that is fired when a browser window containing a root is resized.
     */
    public class BrowserWindowResizeEvent extends Component.Event {

        private final int width;
        private final int height;

        /**
         * Creates a new event
         * 
         * @param source
         *            the root for which the browser window has been resized
         * @param width
         *            the new width of the browser window
         * @param height
         *            the new height of the browser window
         */
        public BrowserWindowResizeEvent(Root source, int width, int height) {
            super(source);
            this.width = width;
            this.height = height;
        }

        @Override
        public Root getSource() {
            return (Root) super.getSource();
        }

        /**
         * Gets the new browser window height
         * 
         * @return an integer with the new pixel height of the browser window
         */
        public int getHeight() {
            return height;
        }

        /**
         * Gets the new browser window width
         * 
         * @return an integer with the new pixel width of the browser window
         */
        public int getWidth() {
            return width;
        }
    }

    private static final Method BROWSWER_RESIZE_METHOD = ReflectTools
            .findMethod(BrowserWindowResizeListener.class,
                    "browserWindowResized", BrowserWindowResizeEvent.class);

    /**
     * Listener that listens changes in URI fragment.
     */
    public interface FragmentChangedListener extends Serializable {
        public void fragmentChanged(FragmentChangedEvent event);
    }

    /**
     * Event fired when uri fragment changes.
     */
    public class FragmentChangedEvent extends Component.Event {

        /**
         * The new uri fragment
         */
        private final String fragment;

        /**
         * Creates a new instance of UriFragmentReader change event.
         * 
         * @param source
         *            the Source of the event.
         */
        public FragmentChangedEvent(Root source, String fragment) {
            super(source);
            this.fragment = fragment;
        }

        /**
         * Gets the root in which the fragment has changed.
         * 
         * @return the root in which the fragment has changed
         */
        public Root getRoot() {
            return (Root) getComponent();
        }

        /**
         * Get the new fragment
         * 
         * @return the new fragment
         */
        public String getFragment() {
            return fragment;
        }
    }

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
    }

    private static final Method FRAGMENT_CHANGED_METHOD;

    static {
        try {
            FRAGMENT_CHANGED_METHOD = FragmentChangedListener.class
                    .getDeclaredMethod("fragmentChanged",
                            new Class[] { FragmentChangedEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in FragmentChangedListener");
        }
    }

    /**
     * A border style used for opening resources in a window without a border.
     */
    public static final int BORDER_NONE = 0;

    /**
     * A border style used for opening resources in a window with a minimal
     * border.
     */
    public static final int BORDER_MINIMAL = 1;

    /**
     * A border style that indicates that the default border style should be
     * used when opening resources.
     */
    public static final int BORDER_DEFAULT = 2;

    /**
     * The application to which this root belongs
     */
    private Application application;

    /**
     * A list of notifications that are waiting to be sent to the client.
     * Cleared (set to null) when the notifications have been sent.
     */
    private List<Notification> notifications;

    /**
     * A list of javascript commands that are waiting to be sent to the client.
     * Cleared (set to null) when the commands have been sent.
     */
    private List<String> jsExecQueue = null;

    /**
     * List of windows in this root.
     */
    private final LinkedHashSet<Window> windows = new LinkedHashSet<Window>();

    /**
     * Resources to be opened automatically on next repaint. The list is
     * automatically cleared when it has been sent to the client.
     */
    private final LinkedList<OpenResource> openList = new LinkedList<OpenResource>();

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

    private int browserWindowWidth = -1;
    private int browserWindowHeight = -1;

    /** Identifies the click event */
    private static final String CLICK_EVENT_ID = VRoot.CLICK_EVENT_ID;

    private DirtyConnectorTracker dirtyConnectorTracker = new DirtyConnectorTracker(
            this);

    private RootServerRPC rpc = new RootServerRPC() {
        public void click(MouseEventDetails mouseDetails) {
            fireEvent(new ClickEvent(Root.this, mouseDetails));
        }
    };

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

    public void replaceComponent(Component oldComponent, Component newComponent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Application getApplication() {
        return application;
    }

    public void paintContent(PaintTarget target) throws PaintException {
        // Open requested resource
        synchronized (openList) {
            if (!openList.isEmpty()) {
                for (final Iterator<OpenResource> i = openList.iterator(); i
                        .hasNext();) {
                    (i.next()).paintContent(target);
                }
                openList.clear();
            }
        }

        // Paint notifications
        if (notifications != null) {
            target.startTag("notifications");
            for (final Iterator<Notification> it = notifications.iterator(); it
                    .hasNext();) {
                final Notification n = it.next();
                target.startTag("notification");
                if (n.getCaption() != null) {
                    target.addAttribute(
                            VNotification.ATTRIBUTE_NOTIFICATION_CAPTION,
                            n.getCaption());
                }
                if (n.getDescription() != null) {
                    target.addAttribute(
                            VNotification.ATTRIBUTE_NOTIFICATION_MESSAGE,
                            n.getDescription());
                }
                if (n.getIcon() != null) {
                    target.addAttribute(
                            VNotification.ATTRIBUTE_NOTIFICATION_ICON,
                            n.getIcon());
                }
                if (!n.isHtmlContentAllowed()) {
                    target.addAttribute(
                            VRoot.NOTIFICATION_HTML_CONTENT_NOT_ALLOWED, true);
                }
                target.addAttribute(
                        VNotification.ATTRIBUTE_NOTIFICATION_POSITION,
                        n.getPosition());
                target.addAttribute(VNotification.ATTRIBUTE_NOTIFICATION_DELAY,
                        n.getDelayMsec());
                if (n.getStyleName() != null) {
                    target.addAttribute(
                            VNotification.ATTRIBUTE_NOTIFICATION_STYLE,
                            n.getStyleName());
                }
                target.endTag("notification");
            }
            target.endTag("notifications");
            notifications = null;
        }

        // Add executable javascripts if needed
        if (jsExecQueue != null) {
            for (String script : jsExecQueue) {
                target.startTag("execJS");
                target.addAttribute("script", script);
                target.endTag("execJS");
            }
            jsExecQueue = null;
        }

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

        if (fragment != null) {
            target.addAttribute(VRoot.FRAGMENT_VARIABLE, fragment);
        }

        if (isResizeLazy()) {
            target.addAttribute(VRoot.RESIZE_LAZY, true);
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

    @SuppressWarnings("unchecked")
    public void changeVariables(Object source, Map<String, Object> variables) {
        if (variables.containsKey(CLICK_EVENT_ID)) {
            fireClick((Map<String, Object>) variables.get(CLICK_EVENT_ID));
        }

        // Actions
        if (actionManager != null) {
            actionManager.handleActions(variables, this);
        }

        if (variables.containsKey(VRoot.FRAGMENT_VARIABLE)) {
            String fragment = (String) variables.get(VRoot.FRAGMENT_VARIABLE);
            setFragment(fragment, true);
        }

        boolean sendResizeEvent = false;
        if (variables.containsKey("height")) {
            browserWindowHeight = ((Integer) variables.get("height"))
                    .intValue();
            sendResizeEvent = true;
        }
        if (variables.containsKey("width")) {
            browserWindowWidth = ((Integer) variables.get("width")).intValue();
            sendResizeEvent = true;
        }
        if (sendResizeEvent) {
            fireEvent(new BrowserWindowResizeEvent(this, browserWindowWidth,
                    browserWindowHeight));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.ComponentContainer#getComponentIterator()
     */
    public Iterator<Component> getComponentIterator() {
        return Collections.singleton((Component) getContent()).iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.ComponentContainer#getComponentCount()
     */
    public int getComponentCount() {
        return getContent() == null ? 0 : 1;
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

    /**
     * The current URI fragment.
     */
    private String fragment;

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
     */
    public void showNotification(String caption) {
        addNotification(new Notification(caption));
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
     */
    public void showNotification(String caption, int type) {
        addNotification(new Notification(caption, type));
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
     */
    public void showNotification(String caption, String description) {
        addNotification(new Notification(caption, description));
    }

    /**
     * Shows a notification consisting of a bigger caption and a smaller
     * description. The position and behavior of the message depends on the
     * type, which is one of the basic types defined in {@link Notification},
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
     */
    public void showNotification(String caption, String description, int type) {
        addNotification(new Notification(caption, description, type));
    }

    /**
     * Shows a notification consisting of a bigger caption and a smaller
     * description. The position and behavior of the message depends on the
     * type, which is one of the basic types defined in {@link Notification},
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
     */
    public void showNotification(String caption, String description, int type,
            boolean htmlContentAllowed) {
        addNotification(new Notification(caption, description, type,
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
     */
    public void showNotification(Notification notification) {
        addNotification(notification);
    }

    /**
     * Internal helper method to actually add a notification.
     * 
     * @param notification
     *            the notification to add
     */
    private void addNotification(Notification notification) {
        if (notifications == null) {
            notifications = new LinkedList<Notification>();
        }
        notifications.add(notification);
        requestRepaint();
    }

    /**
     * Executes JavaScript in this root.
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
     */
    public void executeJavaScript(String script) {
        if (jsExecQueue == null) {
            jsExecQueue = new ArrayList<String>();
        }

        jsExecQueue.add(script);

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
        BrowserDetails browserDetails = request.getBrowserDetails();
        if (browserDetails != null) {
            fragment = browserDetails.getUriFragment();
        }

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
     * @see #getCurrentRoot()
     * @see ThreadLocal
     */
    public static void setCurrentRoot(Root root) {
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
     * @see #setCurrentRoot(Root)
     */
    public static Root getCurrentRoot() {
        return currentRoot.get();
    }

    /**
     * Opens the given resource in this root. The contents of this Root is
     * replaced by the {@code Resource}.
     * 
     * @param resource
     *            the resource to show in this root
     */
    public void open(Resource resource) {
        synchronized (openList) {
            if (!openList.contains(resource)) {
                openList.add(new OpenResource(resource, null, -1, -1,
                        BORDER_DEFAULT));
            }
        }
        requestRepaint();
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
     * 
     * @param resource
     *            the resource.
     * @param windowName
     *            the name of the window.
     */
    public void open(Resource resource, String windowName) {
        synchronized (openList) {
            if (!openList.contains(resource)) {
                openList.add(new OpenResource(resource, windowName, -1, -1,
                        BORDER_DEFAULT));
            }
        }
        requestRepaint();
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
     */
    public void open(Resource resource, String windowName, int width,
            int height, int border) {
        synchronized (openList) {
            if (!openList.contains(resource)) {
                openList.add(new OpenResource(resource, windowName, width,
                        height, border));
            }
        }
        requestRepaint();
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
        private final int border;

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
         */
        private OpenResource(Resource resource, String name, int width,
                int height, int border) {
            this.resource = resource;
            this.name = name;
            this.width = width;
            this.height = height;
            this.border = border;
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
            if (name != null && name.length() > 0) {
                target.addAttribute("name", name);
            }
            if (width >= 0) {
                target.addAttribute("width", width);
            }
            if (height >= 0) {
                target.addAttribute("height", height);
            }
            switch (border) {
            case BORDER_MINIMAL:
                target.addAttribute("border", "minimal");
                break;
            case BORDER_NONE:
                target.addAttribute("border", "none");
                break;
            }

            target.endTag("open");
        }
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

    public <T extends Action & com.vaadin.event.Action.Listener> void addAction(
            T action) {
        getActionManager().addAction(action);
    }

    public <T extends Action & com.vaadin.event.Action.Listener> void removeAction(
            T action) {
        if (actionManager != null) {
            actionManager.removeAction(action);
        }
    }

    public void addActionHandler(Handler actionHandler) {
        getActionManager().addActionHandler(actionHandler);
    }

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
        addListener(CLICK_EVENT_ID, ClickEvent.class, listener,
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
        removeListener(CLICK_EVENT_ID, ClickEvent.class, listener);
    }

    public void addListener(FragmentChangedListener listener) {
        addListener(FragmentChangedEvent.class, listener,
                FRAGMENT_CHANGED_METHOD);
    }

    public void removeListener(FragmentChangedListener listener) {
        removeListener(FragmentChangedEvent.class, listener,
                FRAGMENT_CHANGED_METHOD);
    }

    /**
     * Sets URI fragment. Optionally fires a {@link FragmentChangedEvent}
     * 
     * @param newFragment
     *            id of the new fragment
     * @param fireEvent
     *            true to fire event
     * @see FragmentChangedEvent
     * @see FragmentChangedListener
     */
    public void setFragment(String newFragment, boolean fireEvents) {
        if (newFragment == null) {
            throw new NullPointerException("The fragment may not be null");
        }
        if (!newFragment.equals(fragment)) {
            fragment = newFragment;
            if (fireEvents) {
                fireEvent(new FragmentChangedEvent(this, newFragment));
            }
            requestRepaint();
        }
    }

    /**
     * Sets URI fragment. This method fires a {@link FragmentChangedEvent}
     * 
     * @param newFragment
     *            id of the new fragment
     * @see FragmentChangedEvent
     * @see FragmentChangedListener
     */
    public void setFragment(String newFragment) {
        setFragment(newFragment, true);
    }

    /**
     * Gets currently set URI fragment.
     * <p>
     * To listen changes in fragment, hook a {@link FragmentChangedListener}.
     * 
     * @return the current fragment in browser uri or null if not known
     */
    public String getFragment() {
        return fragment;
    }

    /**
     * Adds a new {@link BrowserWindowResizeListener} to this root. The listener
     * will be notified whenever the browser window within which this root
     * resides is resized.
     * 
     * @param resizeListener
     *            the listener to add
     * 
     * @see BrowserWindowResizeListener#browserWindowResized(BrowserWindowResizeEvent)
     * @see #setResizeLazy(boolean)
     */
    public void addListener(BrowserWindowResizeListener resizeListener) {
        addListener(BrowserWindowResizeEvent.class, resizeListener,
                BROWSWER_RESIZE_METHOD);
    }

    /**
     * Removes a {@link BrowserWindowResizeListener} from this root. The
     * listener will no longer be notified when the browser window is resized.
     * 
     * @param resizeListener
     *            the listener to remove
     */
    public void removeListener(BrowserWindowResizeListener resizeListener) {
        removeListener(BrowserWindowResizeEvent.class, resizeListener,
                BROWSWER_RESIZE_METHOD);
    }

    /**
     * Gets the last known height of the browser window in which this root
     * resides.
     * 
     * @return the browser window height in pixels
     */
    public int getBrowserWindowHeight() {
        return browserWindowHeight;
    }

    /**
     * Gets the last known width of the browser window in which this root
     * resides.
     * 
     * @return the browser window width in pixels
     */
    public int getBrowserWindowWidth() {
        return browserWindowWidth;
    }

    /**
     * Notifies the child components and windows that the root is attached to
     * the application.
     */
    @Override
    public void attach() {
        super.attach();
        for (Window w : windows) {
            w.attach();
        }
    }

    /**
     * Notifies the child components and windows that the root is detached from
     * the application.
     */
    @Override
    public void detach() {
        super.detach();
        for (Window w : windows) {
            w.detach();
        }
    }

    @Override
    public boolean isConnectorEnabled() {
        // TODO How can a Root be invisible? What does it mean?
        return isVisible() && isEnabled();
    }

    public DirtyConnectorTracker getDirtyConnectorTracker() {
        return dirtyConnectorTracker;
    }

    public void componentAttached(Component component) {
        getDirtyConnectorTracker().componentAttached(component);
    }

    public void componentDetached(Component component) {
        getDirtyConnectorTracker().componentDetached(component);
    }

}
