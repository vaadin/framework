/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.vaadin.Application;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.URIHandler;
import com.vaadin.terminal.gwt.client.ui.VWindow;

/**
 * A component that represents an application (browser native) window or a sub
 * window.
 * <p>
 * If the window is a application window or a sub window depends on how it is
 * added to the application. Adding a {@code Window} to a {@code Window} using
 * {@link Window#addWindow(Window)} makes it a sub window and adding a
 * {@code Window} to the {@code Application} using
 * {@link Application#addWindow(Window)} makes it an application window.
 * </p>
 * <p>
 * An application window is the base of any view in a Vaadin application. All
 * applications contain a main application window (set using
 * {@link Application#setMainWindow(Window)} which is what is initially shown to
 * the user. The contents of a window is set using
 * {@link #setContent(ComponentContainer)}. The contents can in turn contain
 * other components. For multi-tab applications there is one window instance per
 * opened tab.
 * </p>
 * <p>
 * A sub window is floating popup style window that can be added to an
 * application window. Like the application window its content is set using
 * {@link #setContent(ComponentContainer)}. A sub window can be positioned on
 * the screen using absolute coordinates (pixels). The default content of the
 * Window is set to be suitable for application windows. For sub windows it
 * might be necessary to set the size of the content to work as expected.
 * </p>
 * <p>
 * Window caption is displayed in the browser title bar for application level
 * windows and in the window header for sub windows.
 * </p>
 * <p>
 * Certain methods in this class are only meaningful for sub windows and other
 * parts only for application windows. These are marked using <b>Sub window
 * only</b> and <b>Application window only</b> respectively in the javadoc.
 * </p>
 * <p>
 * Sub window is to be split into a separate component in Vaadin 7.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
@ClientWidget(VWindow.class)
public class Window extends Panel implements URIHandler, ParameterHandler {

    /**
     * <b>Application window only</b>. A border style used for opening resources
     * in a window without a border.
     */
    public static final int BORDER_NONE = 0;

    /**
     * <b>Application window only</b>. A border style used for opening resources
     * in a window with a minimal border.
     */
    public static final int BORDER_MINIMAL = 1;

    /**
     * <b>Application window only</b>. A border style that indicates that the
     * default border style should be used when opening resources.
     */
    public static final int BORDER_DEFAULT = 2;

    /**
     * <b>Application window only</b>. The user terminal for this window.
     */
    private Terminal terminal = null;

    /**
     * <b>Application window only</b>. The application this window is attached
     * to or null.
     */
    private Application application = null;

    /**
     * <b>Application window only</b>. List of URI handlers for this window.
     */
    private LinkedList<URIHandler> uriHandlerList = null;

    /**
     * <b>Application window only</b>. List of parameter handlers for this
     * window.
     */
    private LinkedList<ParameterHandler> parameterHandlerList = null;

    /**
     * <b>Application window only</b>. List of sub windows in this window. A sub
     * window cannot have other sub windows.
     */
    private final LinkedHashSet<Window> subwindows = new LinkedHashSet<Window>();

    /**
     * <b>Application window only</b>. Explicitly specified theme of this window
     * or null if the application theme should be used.
     */
    private String theme = null;

    /**
     * <b>Application window only</b>. Resources to be opened automatically on
     * next repaint. The list is automatically cleared when it has been sent to
     * the client.
     */
    private final LinkedList<OpenResource> openList = new LinkedList<OpenResource>();

    /**
     * <b>Application window only</b>. Unique name of the window used to
     * identify it.
     */
    private String name = null;

    /**
     * <b>Application window only.</b> Border mode of the Window.
     */
    private int border = BORDER_DEFAULT;

    /**
     * <b>Sub window only</b>. Top offset in pixels for the sub window (relative
     * to the parent application window) or -1 if unspecified.
     */
    private int positionY = -1;

    /**
     * <b>Sub window only</b>. Left offset in pixels for the sub window
     * (relative to the parent application window) or -1 if unspecified.
     */
    private int positionX = -1;

    /**
     * <b>Application window only</b>. A list of notifications that are waiting
     * to be sent to the client. Cleared (set to null) when the notifications
     * have been sent.
     */
    private LinkedList<Notification> notifications;

    /**
     * <b>Sub window only</b>. Modality flag for sub window.
     */
    private boolean modal = false;

    /**
     * <b>Sub window only</b>. Controls if the end user can resize the window.
     */
    private boolean resizable = true;

    /**
     * <b>Sub window only</b>. Controls if the end user can move the window by
     * dragging.
     */
    private boolean draggable = true;

    /**
     * <b>Sub window only</b>. Flag which is true if the window is centered on
     * the screen.
     */
    private boolean centerRequested = false;

    /**
     * Component that should be focused after the next repaint. Null if no focus
     * change should take place.
     */
    private Focusable pendingFocus;

    /**
     * <b>Application window only</b>. A list of javascript commands that are
     * waiting to be sent to the client. Cleared (set to null) when the commands
     * have been sent.
     */
    private ArrayList<String> jsExecQueue = null;

    /**
     * The component that should be scrolled into view after the next repaint.
     * Null if nothing should be scrolled into view.
     */
    private Component scrollIntoView;

    /**
     * Creates a new unnamed window with a default layout.
     */
    public Window() {
        this("", null);
    }

    /**
     * Creates a new unnamed window with a default layout and given title.
     * 
     * @param caption
     *            the title of the window.
     */
    public Window(String caption) {
        this(caption, null);
    }

    /**
     * Creates a new unnamed window with the given content and title.
     * 
     * @param caption
     *            the title of the window.
     * @param content
     *            the contents of the window
     */
    public Window(String caption, ComponentContainer content) {
        super(caption, content);
        setScrollable(true);
        setSizeUndefined();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Panel#addComponent(com.vaadin.ui.Component)
     */
    @Override
    public void addComponent(Component c) {
        if (c instanceof Window) {
            throw new IllegalArgumentException(
                    "Window cannot be added to another via addComponent. "
                            + "Use addWindow(Window) instead.");
        }
        super.addComponent(c);
    }

    /**
     * <b>Application window only</b>. Gets the user terminal.
     * 
     * @return the user terminal
     */
    public Terminal getTerminal() {
        return terminal;
    }

    /* ********************************************************************* */

    /**
     * Gets the parent window of the component.
     * <p>
     * This is always the window itself.
     * </p>
     * 
     * @see Component#getWindow()
     * @return the window itself
     */
    @Override
    public final Window getWindow() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractComponent#getApplication()
     */
    @Override
    public final Application getApplication() {
        if (getParent() == null) {
            return application;
        }
        return (getParent()).getApplication();
    }

    /**
     * Gets the parent component of the window.
     * 
     * <p>
     * The parent of an application window is always null. The parent of a sub
     * window is the application window the sub window is attached to.
     * </p>
     * 
     * @return the parent window
     * @see Component#getParent()
     */
    @Override
    public final Window getParent() {
        return (Window) super.getParent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractComponent#setParent(com.vaadin.ui.Component)
     */
    @Override
    public void setParent(Component parent) {
        super.setParent(parent);
    }

    /* ********************************************************************* */

    /**
     * <b>Application window only</b>. Adds a new URI handler to this window. If
     * this is a sub window the URI handler is attached to the parent
     * application window.
     * 
     * @param handler
     *            the URI handler to add.
     */
    public void addURIHandler(URIHandler handler) {
        if (getParent() != null) {
            // this is subwindow, attach to main level instead
            // TODO hold internal list also and remove on detach
            Window mainWindow = getParent();
            mainWindow.addURIHandler(handler);
        } else {
            if (uriHandlerList == null) {
                uriHandlerList = new LinkedList<URIHandler>();
            }
            synchronized (uriHandlerList) {
                if (!uriHandlerList.contains(handler)) {
                    uriHandlerList.addLast(handler);
                }
            }
        }
    }

    /**
     * <b>Application window only</b>. Removes the URI handler from this window.
     * If this is a sub window the URI handler is removed from the parent
     * application window.
     * 
     * @param handler
     *            the URI handler to remove.
     */
    public void removeURIHandler(URIHandler handler) {
        if (getParent() != null) {
            // this is subwindow
            Window mainWindow = getParent();
            mainWindow.removeURIHandler(handler);
        } else {
            if (handler == null || uriHandlerList == null) {
                return;
            }
            synchronized (uriHandlerList) {
                uriHandlerList.remove(handler);
                if (uriHandlerList.isEmpty()) {
                    uriHandlerList = null;
                }
            }
        }
    }

    /**
     * <b>Application window only</b>. Handles an URI by passing the URI to all
     * URI handlers defined using {@link #addURIHandler(URIHandler)}. All URI
     * handlers are called for each URI but no more than one handler may return
     * a {@link DownloadStream}. If more than one stream is returned a
     * {@code RuntimeException} is thrown.
     * 
     * @param context
     *            The URL of the application
     * @param relativeUri
     *            The URI relative to {@code context}
     * @return A {@code DownloadStream} that one of the URI handlers returned,
     *         null if no {@code DownloadStream} was returned.
     */
    public DownloadStream handleURI(URL context, String relativeUri) {

        DownloadStream result = null;
        if (uriHandlerList != null) {
            Object[] handlers;
            synchronized (uriHandlerList) {
                handlers = uriHandlerList.toArray();
            }
            for (int i = 0; i < handlers.length; i++) {
                final DownloadStream ds = ((URIHandler) handlers[i]).handleURI(
                        context, relativeUri);
                if (ds != null) {
                    if (result != null) {
                        throw new RuntimeException("handleURI for " + context
                                + " uri: '" + relativeUri
                                + "' returns ambigious result.");
                    }
                    result = ds;
                }
            }
        }
        return result;
    }

    /* ********************************************************************* */

    /**
     * <b>Application window only</b>. Adds a new parameter handler to this
     * window. If this is a sub window the parameter handler is attached to the
     * parent application window.
     * 
     * @param handler
     *            the parameter handler to add.
     */
    public void addParameterHandler(ParameterHandler handler) {
        if (getParent() != null) {
            // this is subwindow
            // TODO hold internal list also and remove on detach
            Window mainWindow = getParent();
            mainWindow.addParameterHandler(handler);
        } else {
            if (parameterHandlerList == null) {
                parameterHandlerList = new LinkedList<ParameterHandler>();
            }
            synchronized (parameterHandlerList) {
                if (!parameterHandlerList.contains(handler)) {
                    parameterHandlerList.addLast(handler);
                }
            }
        }

    }

    /**
     * <b>Application window only</b>. Removes the parameter handler from this
     * window. If this is a sub window the parameter handler is removed from the
     * parent application window.
     * 
     * @param handler
     *            the parameter handler to remove.
     */
    public void removeParameterHandler(ParameterHandler handler) {
        if (getParent() != null) {
            // this is subwindow
            Window mainWindow = getParent();
            mainWindow.removeParameterHandler(handler);
        } else {
            if (handler == null || parameterHandlerList == null) {
                return;
            }
            synchronized (parameterHandlerList) {
                parameterHandlerList.remove(handler);
                if (parameterHandlerList.isEmpty()) {
                    parameterHandlerList = null;
                }
            }
        }
    }

    /**
     * <b>Application window only</b>. Handles parameters by passing the
     * parameters to all {@code ParameterHandler}s defined using
     * {@link #addParameterHandler(ParameterHandler)}. All
     * {@code ParameterHandler}s are called for each set of parameters.
     * 
     * @param parameters
     *            a map containing the parameter names and values
     * @see ParameterHandler#handleParameters(Map)
     */
    public void handleParameters(Map<String, String[]> parameters) {
        if (parameterHandlerList != null) {
            Object[] handlers;
            synchronized (parameterHandlerList) {
                handlers = parameterHandlerList.toArray();
            }
            for (int i = 0; i < handlers.length; i++) {
                ((ParameterHandler) handlers[i]).handleParameters(parameters);
            }
        }
    }

    /* ********************************************************************* */

    /**
     * <b>Application window only</b>. Gets the theme for this window.
     * <p>
     * If the theme for this window is not explicitly set, the application theme
     * name is returned. If the window is not attached to an application, the
     * terminal default theme name is returned. If the theme name cannot be
     * determined, null is returned
     * </p>
     * <p>
     * Subwindows do not support themes and return the theme used by the parent
     * window
     * </p>
     * 
     * @return the name of the theme used for the window
     */
    public String getTheme() {
        if (getParent() != null) {
            return (getParent()).getTheme();
        }
        if (theme != null) {
            return theme;
        }
        if ((application != null) && (application.getTheme() != null)) {
            return application.getTheme();
        }
        if (terminal != null) {
            return terminal.getDefaultTheme();
        }
        return null;
    }

    /**
     * <b>Application window only</b>. Sets the name of the theme to use for
     * this window. Changing the theme will cause the page to be reloaded.
     * 
     * @param theme
     *            the name of the new theme for this window or null to use the
     *            application theme.
     */
    public void setTheme(String theme) {
        if (getParent() != null) {
            throw new UnsupportedOperationException(
                    "Setting theme for sub-windows is not supported.");
        }
        this.theme = theme;
        requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Panel#paintContent(com.vaadin.terminal.PaintTarget)
     */
    @Override
    public synchronized void paintContent(PaintTarget target)
            throws PaintException {

        // Sets the window name
        final String name = getName();
        target.addAttribute("name", name == null ? "" : name);

        // Sets the window theme
        final String theme = getTheme();
        target.addAttribute("theme", theme == null ? "" : theme);

        if (modal) {
            target.addAttribute("modal", true);
        }

        if (resizable) {
            target.addAttribute("resizable", true);
        }

        if (!draggable) {
            // Inverted to prevent an extra attribute for almost all sub windows
            target.addAttribute("fixedposition", true);
        }

        if (centerRequested) {
            target.addAttribute("center", true);
            centerRequested = false;
        }

        if (scrollIntoView != null) {
            target.addAttribute("scrollTo", scrollIntoView);
            scrollIntoView = null;
        }

        // Marks the main window
        if (getApplication() != null
                && this == getApplication().getMainWindow()) {
            target.addAttribute("main", true);
        }

        if (getContent() != null) {
            if (getContent().getHeightUnits() == Sizeable.UNITS_PERCENTAGE) {
                target.addAttribute("layoutRelativeHeight", true);
            }
            if (getContent().getWidthUnits() == Sizeable.UNITS_PERCENTAGE) {
                target.addAttribute("layoutRelativeWidth", true);
            }
        }

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

        // Contents of the window panel is painted
        super.paintContent(target);

        // Add executable javascripts if needed
        if (jsExecQueue != null) {
            for (String script : jsExecQueue) {
                target.startTag("execJS");
                target.addAttribute("script", script);
                target.endTag("execJS");
            }
            jsExecQueue = null;
        }

        // Window position
        target.addVariable(this, "positionx", getPositionX());
        target.addVariable(this, "positiony", getPositionY());

        // Window closing
        target.addVariable(this, "close", false);

        if (getParent() == null) {
            // Paint subwindows
            for (final Iterator<Window> i = subwindows.iterator(); i.hasNext();) {
                final Window w = i.next();
                w.paint(target);
            }
        } else {
            // mark subwindows
            target.addAttribute("sub", true);
        }

        // Paint notifications
        if (notifications != null) {
            target.startTag("notifications");
            for (final Iterator<Notification> it = notifications.iterator(); it
                    .hasNext();) {
                final Notification n = it.next();
                target.startTag("notification");
                if (n.getCaption() != null) {
                    target.addAttribute("caption", n.getCaption());
                }
                if (n.getMessage() != null) {
                    target.addAttribute("message", n.getMessage());
                }
                if (n.getIcon() != null) {
                    target.addAttribute("icon", n.getIcon());
                }
                target.addAttribute("position", n.getPosition());
                target.addAttribute("delay", n.getDelayMsec());
                if (n.getStyleName() != null) {
                    target.addAttribute("style", n.getStyleName());
                }
                target.endTag("notification");
            }
            target.endTag("notifications");
            notifications = null;
        }

        if (pendingFocus != null) {
            // ensure focused component is still attached to this main window
            if (pendingFocus.getWindow() == this
                    || (pendingFocus.getWindow() != null && pendingFocus
                            .getWindow().getParent() == this)) {
                target.addAttribute("focused", pendingFocus);
            }
            pendingFocus = null;
        }

    }

    /* ********************************************************************* */

    /**
     * Scrolls any component between the component and window to a suitable
     * position so the component is visible to the user. The given component
     * must be inside this window.
     * 
     * @param component
     *            the component to be scrolled into view
     * @throws IllegalArgumentException
     *             if {@code component} is not inside this window
     */
    public void scrollIntoView(Component component)
            throws IllegalArgumentException {
        if (component.getWindow() != this) {
            throw new IllegalArgumentException(
                    "The component where to scroll must be inside this window.");
        }
        scrollIntoView = component;
        requestRepaint();
    }

    /**
     * Opens the given resource in this window. The contents of this Window is
     * replaced by the {@code Resource}.
     * 
     * @param resource
     *            the resource to show in this window
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

    /* ********************************************************************* */

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

        if (application == null) {
            return null;
        }

        try {
            return new URL(application.getURL(), getName() + "/");
        } catch (final MalformedURLException e) {
            throw new RuntimeException(
                    "Internal problem getting window URL, please report");
        }
    }

    /**
     * <b>Application window only</b>. Gets the unique name of the window. The
     * name of the window is used to uniquely identify it.
     * <p>
     * The name also determines the URL that can be used for direct access to a
     * window. All windows can be accessed through
     * {@code http://host:port/app/win} where {@code http://host:port/app} is
     * the application URL (as returned by {@link Application#getURL()} and
     * {@code win} is the window name.
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
     * Returns the border style of the window.
     * 
     * @see #setBorder(int)
     * @return the border style for the window
     */
    public int getBorder() {
        return border;
    }

    /**
     * Sets the border style for this window. Valid values are
     * {@link Window#BORDER_NONE}, {@link Window#BORDER_MINIMAL},
     * {@link Window#BORDER_DEFAULT}.
     * <p>
     * <b>Note!</b> Setting this seems to currently have no effect whatsoever on
     * the window.
     * </p>
     * 
     * @param border
     *            the border style to set
     */
    public void setBorder(int border) {
        this.border = border;
    }

    /**
     * Sets the application this window is attached to.
     * 
     * <p>
     * This method is called by the framework and should not be called directly
     * from application code. {@link com.vaadin.Application#addWindow(Window)}
     * should be used to add the window to an application and
     * {@link com.vaadin.Application#removeWindow(Window)} to remove the window
     * from the application.
     * </p>
     * <p>
     * This method invokes {@link Component#attach()} and
     * {@link Component#detach()} methods when necessary.
     * <p>
     * 
     * @param application
     *            the application the window is attached to
     */
    public void setApplication(Application application) {

        // If the application is not changed, dont do nothing
        if (application == this.application) {
            return;
        }

        // Sends detach event if the window is connected to application
        if (this.application != null) {
            detach();
        }

        // Connects to new parent
        this.application = application;

        // Sends the attach event if connected to a window
        if (application != null) {
            attach();
        }
    }

    /**
     * <b>Application window only</b>. Sets the unique name of the window. The
     * name of the window is used to uniquely identify it inside the
     * application.
     * <p>
     * The name also determines the URL that can be used for direct access to a
     * window. All windows can be accessed through
     * {@code http://host:port/app/win} where {@code http://host:port/app} is
     * the application URL (as returned by {@link Application#getURL()} and
     * {@code win} is the window name.
     * </p>
     * <p>
     * This method can only be called before the window is added to an
     * application.
     * </p>
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
    public void setName(String name) throws IllegalStateException {

        // The name can not be changed in application
        if (getApplication() != null) {
            throw new IllegalStateException(
                    "Window name can not be changed while "
                            + "the window is in application");
        }

        this.name = name;
    }

    /**
     * Sets the user terminal. Used by the terminal adapter, should never be
     * called from application code.
     * 
     * @param type
     *            the terminal to set.
     */
    public void setTerminal(Terminal type) {
        terminal = type;
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
            case Window.BORDER_MINIMAL:
                target.addAttribute("border", "minimal");
                break;
            case Window.BORDER_NONE:
                target.addAttribute("border", "none");
                break;
            }

            target.endTag("open");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Panel#changeVariables(java.lang.Object, java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {

        boolean sizeHasChanged = false;
        // size is handled in super class, but resize events only in windows ->
        // so detect if size change occurs before super.changeVariables()
        if (variables.containsKey("height")
                && (getHeightUnits() != UNITS_PIXELS || (Integer) variables
                        .get("height") != getHeight())) {
            sizeHasChanged = true;
        }
        if (variables.containsKey("width")
                && (getWidthUnits() != UNITS_PIXELS || (Integer) variables
                        .get("width") != getWidth())) {
            sizeHasChanged = true;
        }

        super.changeVariables(source, variables);

        // Positioning
        final Integer positionx = (Integer) variables.get("positionx");
        if (positionx != null) {
            final int x = positionx.intValue();
            // This is information from the client so it is already using the
            // position. No need to repaint.
            setPositionX(x < 0 ? -1 : x, false);
        }
        final Integer positiony = (Integer) variables.get("positiony");
        if (positiony != null) {
            final int y = positiony.intValue();
            // This is information from the client so it is already using the
            // position. No need to repaint.
            setPositionY(y < 0 ? -1 : y, false);
        }

        if (isClosable()) {
            // Closing
            final Boolean close = (Boolean) variables.get("close");
            if (close != null && close.booleanValue()) {
                close();
            }
        }

        // fire event if size has really changed
        if (sizeHasChanged) {
            fireResize();
        }

    }

    /**
     * Method that handles window closing (from UI).
     * 
     * <p>
     * By default, sub-windows are removed from their respective parent windows
     * and thus visually closed on browser-side. Browser-level windows also
     * closed on the client-side, but they are not implicitly removed from the
     * application.
     * </p>
     * 
     * <p>
     * To explicitly close a sub-window, use {@link #removeWindow(Window)}. To
     * react to a window being closed (after it is closed), register a
     * {@link CloseListener}.
     * </p>
     */
    protected void close() {
        Window parent = getParent();
        if (parent == null) {
            fireClose();
        } else {
            // subwindow is removed from parent
            parent.removeWindow(this);
        }
    }

    /**
     * Gets the distance of Window left border in pixels from left border of the
     * containing (main window).
     * 
     * @return the Distance of Window left border in pixels from left border of
     *         the containing (main window). or -1 if unspecified.
     * @since 4.0.0
     */
    public int getPositionX() {
        return positionX;
    }

    /**
     * Sets the distance of Window left border in pixels from left border of the
     * containing (main window).
     * 
     * @param positionX
     *            the Distance of Window left border in pixels from left border
     *            of the containing (main window). or -1 if unspecified.
     * @since 4.0.0
     */
    public void setPositionX(int positionX) {
        setPositionX(positionX, true);
    }

    /**
     * Sets the distance of Window left border in pixels from left border of the
     * containing (main window).
     * 
     * @param positionX
     *            the Distance of Window left border in pixels from left border
     *            of the containing (main window). or -1 if unspecified.
     * @param repaintRequired
     *            true if the window needs to be repainted, false otherwise
     * @since 6.3.4
     */
    private void setPositionX(int positionX, boolean repaintRequired) {
        this.positionX = positionX;
        centerRequested = false;
        if (repaintRequired) {
            requestRepaint();
        }
    }

    /**
     * Gets the distance of Window top border in pixels from top border of the
     * containing (main window).
     * 
     * @return Distance of Window top border in pixels from top border of the
     *         containing (main window). or -1 if unspecified .
     * 
     * @since 4.0.0
     */
    public int getPositionY() {
        return positionY;
    }

    /**
     * Sets the distance of Window top border in pixels from top border of the
     * containing (main window).
     * 
     * @param positionY
     *            the Distance of Window top border in pixels from top border of
     *            the containing (main window). or -1 if unspecified
     * 
     * @since 4.0.0
     */
    public void setPositionY(int positionY) {
        setPositionY(positionY, true);
    }

    /**
     * Sets the distance of Window top border in pixels from top border of the
     * containing (main window).
     * 
     * @param positionY
     *            the Distance of Window top border in pixels from top border of
     *            the containing (main window). or -1 if unspecified
     * @param repaintRequired
     *            true if the window needs to be repainted, false otherwise
     * 
     * @since 6.3.4
     */
    private void setPositionY(int positionY, boolean repaintRequired) {
        this.positionY = positionY;
        centerRequested = false;
        if (repaintRequired) {
            requestRepaint();
        }
    }

    private static final Method WINDOW_CLOSE_METHOD;
    static {
        try {
            WINDOW_CLOSE_METHOD = CloseListener.class.getDeclaredMethod(
                    "windowClose", new Class[] { CloseEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error, window close method not found");
        }
    }

    public class CloseEvent extends Component.Event {

        /**
         * 
         * @param source
         */
        public CloseEvent(Component source) {
            super(source);
        }

        /**
         * Gets the Window.
         * 
         * @return the window.
         */
        public Window getWindow() {
            return (Window) getSource();
        }
    }

    /**
     * An interface used for listening to Window close events. Add the
     * CloseListener to a browser level window or a sub window and
     * {@link CloseListener#windowClose(CloseEvent)} will be called whenever the
     * user closes the window.
     * 
     * <p>
     * Since Vaadin 6.5, removing windows using {@link #removeWindow(Window)}
     * does fire the CloseListener.
     * </p>
     */
    public interface CloseListener extends Serializable {
        /**
         * Called when the user closes a window. Use
         * {@link CloseEvent#getWindow()} to get a reference to the
         * {@link Window} that was closed.
         * 
         * @param e
         *            Event containing
         */
        public void windowClose(CloseEvent e);
    }

    /**
     * Adds a CloseListener to the window.
     * 
     * For a sub window the CloseListener is fired when the user closes it
     * (clicks on the close button).
     * 
     * For a browser level window the CloseListener is fired when the browser
     * level window is closed. Note that closing a browser level window does not
     * mean it will be destroyed.
     * 
     * <p>
     * Since Vaadin 6.5, removing windows using {@link #removeWindow(Window)}
     * does fire the CloseListener.
     * </p>
     * 
     * @param listener
     *            the CloseListener to add.
     */
    public void addListener(CloseListener listener) {
        addListener(CloseEvent.class, listener, WINDOW_CLOSE_METHOD);
    }

    /**
     * Removes the CloseListener from the window.
     * 
     * <p>
     * For more information on CloseListeners see {@link CloseListener}.
     * </p>
     * 
     * @param listener
     *            the CloseListener to remove.
     */
    public void removeListener(CloseListener listener) {
        removeListener(CloseEvent.class, listener, WINDOW_CLOSE_METHOD);
    }

    protected void fireClose() {
        fireEvent(new Window.CloseEvent(this));
    }

    /**
     * Method for the resize event.
     */
    private static final Method WINDOW_RESIZE_METHOD;
    static {
        try {
            WINDOW_RESIZE_METHOD = ResizeListener.class.getDeclaredMethod(
                    "windowResized", new Class[] { ResizeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error, window resized method not found");
        }
    }

    /**
     * Resize events are fired whenever the client-side fires a resize-event
     * (e.g. the browser window is resized). The frequency may vary across
     * browsers.
     */
    public class ResizeEvent extends Component.Event {

        /**
         * 
         * @param source
         */
        public ResizeEvent(Component source) {
            super(source);
        }

        /**
         * Get the window form which this event originated
         * 
         * @return the window
         */
        public Window getWindow() {
            return (Window) getSource();
        }
    }

    /**
     * Listener for window resize events.
     * 
     * @see com.vaadin.ui.Window.ResizeEvent
     */
    public interface ResizeListener extends Serializable {
        public void windowResized(ResizeEvent e);
    }

    /**
     * Add a resize listener.
     * 
     * @param listener
     */
    public void addListener(ResizeListener listener) {
        addListener(ResizeEvent.class, listener, WINDOW_RESIZE_METHOD);
    }

    /**
     * Remove a resize listener.
     * 
     * @param listener
     */
    public void removeListener(ResizeListener listener) {
        removeListener(ResizeEvent.class, this);
    }

    /**
     * Fire the resize event.
     */
    protected void fireResize() {
        fireEvent(new ResizeEvent(this));
    }

    private void attachWindow(Window w) {
        subwindows.add(w);
        w.setParent(this);
        requestRepaint();
    }

    /**
     * Adds a window inside another window.
     * 
     * <p>
     * Adding windows inside another window creates "subwindows". These windows
     * should not be added to application directly and are not accessible
     * directly with any url. Addding windows implicitly sets their parents.
     * </p>
     * 
     * <p>
     * Only one level of subwindows are supported. Thus you can add windows
     * inside such windows whose parent is <code>null</code>.
     * </p>
     * 
     * @param window
     * @throws IllegalArgumentException
     *             if a window is added inside non-application level window.
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
                    "Window was already added to application"
                            + " - it can not be added to another window also.");
        } else if (getParent() != null) {
            throw new IllegalArgumentException(
                    "You can only add windows inside application-level windows.");
        } else if (window.subwindows.size() > 0) {
            throw new IllegalArgumentException(
                    "Only one level of subwindows are supported.");
        }

        attachWindow(window);
    }

    /**
     * Remove the given subwindow from this window.
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
    public boolean removeWindow(Window window) throws IllegalArgumentException {
        if (!subwindows.remove(window)) {
            // Window window is not a subwindow of this window.
            return false;
        }
        window.setParent(null);
        window.fireClose();
        requestRepaint();

        return true;
    }

    /**
     * Get the set of all child windows.
     * 
     * @return Set of child windows.
     */
    public Set<Window> getChildWindows() {
        return Collections.unmodifiableSet(subwindows);
    }

    /**
     * Sets sub-window modal, so that widgets behind it cannot be accessed.
     * <b>Note:</b> affects sub-windows only.
     * 
     * @param modality
     *            true if modality is to be turned on
     */
    public void setModal(boolean modality) {
        modal = modality;
        center();
        requestRepaint();
    }

    /**
     * @return true if this window is modal.
     */
    public boolean isModal() {
        return modal;
    }

    /**
     * Sets sub-window resizable. <b>Note:</b> affects sub-windows only.
     * 
     * @param resizable
     *            true if resizability is to be turned on
     */
    public void setResizable(boolean resizeability) {
        resizable = resizeability;
        requestRepaint();
    }

    /**
     * 
     * @return true if window is resizable by the end-user, otherwise false.
     */
    public boolean isResizable() {
        return resizable;
    }

    /**
     * Request to center this window on the screen. <b>Note:</b> affects
     * sub-windows only.
     */
    public void center() {
        centerRequested = true;
        requestRepaint();
    }

    /**
     * Shows a notification message on the middle of the window. The message
     * automatically disappears ("humanized message").
     * 
     * @see #showNotification(com.vaadin.ui.Window.Notification)
     * @see Notification
     * 
     * @param caption
     *            The message
     */
    public void showNotification(String caption) {
        addNotification(new Notification(caption));
    }

    /**
     * Shows a notification message the window. The position and behavior of the
     * message depends on the type, which is one of the basic types defined in
     * {@link Notification}, for instance Notification.TYPE_WARNING_MESSAGE.
     * 
     * @see #showNotification(com.vaadin.ui.Window.Notification)
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
     * description on the middle of the window. The message automatically
     * disappears ("humanized message").
     * 
     * @see #showNotification(com.vaadin.ui.Window.Notification)
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
     * @see #showNotification(com.vaadin.ui.Window.Notification)
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

    private void addNotification(Notification notification) {
        if (notifications == null) {
            notifications = new LinkedList<Notification>();
        }
        notifications.add(notification);
        requestRepaint();
    }

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
    void setFocusedComponent(Focusable focusable) {
        if (getParent() != null) {
            // focus is handled by main windows
            (getParent()).setFocusedComponent(focusable);
        } else {
            pendingFocus = focusable;
            requestRepaint();
        }
    }

    /**
     * A notification message, used to display temporary messages to the user -
     * for example "Document saved", or "Save failed".
     * <p>
     * The notification message can consist of several parts: caption,
     * description and icon. It is usually used with only caption - one should
     * be wary of filling the notification with too much information.
     * </p>
     * <p>
     * The notification message tries to be as unobtrusive as possible, while
     * still drawing needed attention. There are several basic types of messages
     * that can be used in different situations:
     * <ul>
     * <li>TYPE_HUMANIZED_MESSAGE fades away quickly as soon as the user uses
     * the mouse or types something. It can be used to show fairly unimportant
     * messages, such as feedback that an operation succeeded ("Document Saved")
     * - the kind of messages the user ignores once the application is familiar.
     * </li>
     * <li>TYPE_WARNING_MESSAGE is shown for a short while after the user uses
     * the mouse or types something. It's default style is also more noticeable
     * than the humanized message. It can be used for messages that do not
     * contain a lot of important information, but should be noticed by the
     * user. Despite the name, it does not have to be a warning, but can be used
     * instead of the humanized message whenever you want to make the message a
     * little more noticeable.</li>
     * <li>TYPE_ERROR_MESSAGE requires to user to click it before disappearing,
     * and can be used for critical messages.</li>
     * <li>TYPE_TRAY_NOTIFICATION is shown for a while in the lower left corner
     * of the window, and can be used for "convenience notifications" that do
     * not have to be noticed immediately, and should not interfere with the
     * current task - for instance to show "You have a new message in your
     * inbox" while the user is working in some other area of the application.</li>
     * </ul>
     * </p>
     * <p>
     * In addition to the basic pre-configured types, a Notification can also be
     * configured to show up in a custom position, for a specified time (or
     * until clicked), and with a custom stylename. An icon can also be added.
     * </p>
     * 
     */
    public static class Notification implements Serializable {
        public static final int TYPE_HUMANIZED_MESSAGE = 1;
        public static final int TYPE_WARNING_MESSAGE = 2;
        public static final int TYPE_ERROR_MESSAGE = 3;
        public static final int TYPE_TRAY_NOTIFICATION = 4;

        public static final int POSITION_CENTERED = 1;
        public static final int POSITION_CENTERED_TOP = 2;
        public static final int POSITION_CENTERED_BOTTOM = 3;
        public static final int POSITION_TOP_LEFT = 4;
        public static final int POSITION_TOP_RIGHT = 5;
        public static final int POSITION_BOTTOM_LEFT = 6;
        public static final int POSITION_BOTTOM_RIGHT = 7;

        public static final int DELAY_FOREVER = -1;
        public static final int DELAY_NONE = 0;

        private String caption;
        private String description;
        private Resource icon;
        private int position = POSITION_CENTERED;
        private int delayMsec = 0;
        private String styleName;

        /**
         * Creates a "humanized" notification message.
         * 
         * @param caption
         *            The message to show
         */
        public Notification(String caption) {
            this(caption, null, TYPE_HUMANIZED_MESSAGE);
        }

        /**
         * Creates a notification message of the specified type.
         * 
         * @param caption
         *            The message to show
         * @param type
         *            The type of message
         */
        public Notification(String caption, int type) {
            this(caption, null, type);
        }

        /**
         * Creates a "humanized" notification message with a bigger caption and
         * smaller description.
         * 
         * @param caption
         *            The message caption
         * @param description
         *            The message description
         */
        public Notification(String caption, String description) {
            this(caption, description, TYPE_HUMANIZED_MESSAGE);
        }

        /**
         * Creates a notification message of the specified type, with a bigger
         * caption and smaller description.
         * 
         * @param caption
         *            The message caption
         * @param description
         *            The message description
         * @param type
         *            The type of message
         */
        public Notification(String caption, String description, int type) {
            this.caption = caption;
            this.description = description;
            setType(type);
        }

        private void setType(int type) {
            switch (type) {
            case TYPE_WARNING_MESSAGE:
                delayMsec = 1500;
                styleName = "warning";
                break;
            case TYPE_ERROR_MESSAGE:
                delayMsec = -1;
                styleName = "error";
                break;
            case TYPE_TRAY_NOTIFICATION:
                delayMsec = 3000;
                position = POSITION_BOTTOM_RIGHT;
                styleName = "tray";

            case TYPE_HUMANIZED_MESSAGE:
            default:
                break;
            }

        }

        /**
         * Gets the caption part of the notification message.
         * 
         * @return The message caption
         */
        public String getCaption() {
            return caption;
        }

        /**
         * Sets the caption part of the notification message
         * 
         * @param caption
         *            The message caption
         */
        public void setCaption(String caption) {
            this.caption = caption;
        }

        /**
         * @deprecated Use {@link #getDescription()} instead.
         * @return
         */
        @Deprecated
        public String getMessage() {
            return description;
        }

        /**
         * @deprecated Use {@link #setDescription(String)} instead.
         * @param description
         */
        @Deprecated
        public void setMessage(String description) {
            this.description = description;
        }

        /**
         * Gets the description part of the notification message.
         * 
         * @return The message description.
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the description part of the notification message.
         * 
         * @param description
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Gets the position of the notification message.
         * 
         * @return The position
         */
        public int getPosition() {
            return position;
        }

        /**
         * Sets the position of the notification message.
         * 
         * @param position
         *            The desired notification position
         */
        public void setPosition(int position) {
            this.position = position;
        }

        /**
         * Gets the icon part of the notification message.
         * 
         * @return The message icon
         */
        public Resource getIcon() {
            return icon;
        }

        /**
         * Sets the icon part of the notification message.
         * 
         * @param icon
         *            The desired message icon
         */
        public void setIcon(Resource icon) {
            this.icon = icon;
        }

        /**
         * Gets the delay before the notification disappears.
         * 
         * @return the delay in msec, -1 indicates the message has to be
         *         clicked.
         */
        public int getDelayMsec() {
            return delayMsec;
        }

        /**
         * Sets the delay before the notification disappears.
         * 
         * @param delayMsec
         *            the desired delay in msec, -1 to require the user to click
         *            the message
         */
        public void setDelayMsec(int delayMsec) {
            this.delayMsec = delayMsec;
        }

        /**
         * Sets the style name for the notification message.
         * 
         * @param styleName
         *            The desired style name.
         */
        public void setStyleName(String styleName) {
            this.styleName = styleName;
        }

        /**
         * Gets the style name for the notification message.
         * 
         * @return
         */
        public String getStyleName() {
            return styleName;
        }
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
     */
    public void executeJavaScript(String script) {

        if (getParent() != null) {
            throw new UnsupportedOperationException(
                    "Only application level windows can execute javascript.");
        }

        if (jsExecQueue == null) {
            jsExecQueue = new ArrayList<String>();
        }

        jsExecQueue.add(script);

        requestRepaint();
    }

    /**
     * Returns the closable status of the sub window. If a sub window is
     * closable it typically shows an X in the upper right corner. Clicking on
     * the X sends a close event to the server. Setting closable to false will
     * remove the X from the sub window and prevent the user from closing the
     * window.
     * 
     * Note! For historical reasons readonly controls the closability of the sub
     * window and therefore readonly and closable affect each other. Setting
     * readonly to true will set closable to false and vice versa.
     * <p/>
     * Closable only applies to sub windows, not to browser level windows.
     * 
     * @return true if the sub window can be closed by the user.
     */
    public boolean isClosable() {
        return !isReadOnly();
    }

    /**
     * Sets the closable status for the sub window. If a sub window is closable
     * it typically shows an X in the upper right corner. Clicking on the X
     * sends a close event to the server. Setting closable to false will remove
     * the X from the sub window and prevent the user from closing the window.
     * 
     * Note! For historical reasons readonly controls the closability of the sub
     * window and therefore readonly and closable affect each other. Setting
     * readonly to true will set closable to false and vice versa.
     * <p/>
     * Closable only applies to sub windows, not to browser level windows.
     * 
     * @param closable
     *            determines if the sub window can be closed by the user.
     */
    public void setClosable(boolean closable) {
        setReadOnly(!closable);
    }

    /**
     * Indicates whether a sub window can be dragged or not. By default a sub
     * window is draggable.
     * <p/>
     * Draggable only applies to sub windows, not to browser level windows.
     * 
     * @param draggable
     *            true if the sub window can be dragged by the user
     */
    public boolean isDraggable() {
        return draggable;
    }

    /**
     * Enables or disables that a sub window can be dragged (moved) by the user.
     * By default a sub window is draggable.
     * <p/>
     * Draggable only applies to sub windows, not to browser level windows.
     * 
     * @param draggable
     *            true if the sub window can be dragged by the user
     */
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
        requestRepaint();
    }

    /*
     * Actions
     */
    protected CloseShortcut closeShortcut;

    /**
     * Makes is possible to close the window by pressing the given
     * {@link KeyCode} and (optional) {@link ModifierKey}s.<br/>
     * Note that this shortcut only reacts while the window has focus, closing
     * itself - if you want to close a subwindow from a parent window, use
     * {@link #addAction(com.vaadin.event.Action)} of the parent window instead.
     * 
     * @param keyCode
     *            the keycode for invoking the shortcut
     * @param modifiers
     *            the (optional) modifiers for invoking the shortcut, null for
     *            none
     */
    public void setCloseShortcut(int keyCode, int... modifiers) {
        if (closeShortcut != null) {
            removeAction(closeShortcut);
        }
        closeShortcut = new CloseShortcut(this, keyCode, modifiers);
        addAction(closeShortcut);
    }

    /**
     * Removes the keyboard shortcut previously set with
     * {@link #setCloseShortcut(int, int...)}.
     */
    public void removeCloseShortcut() {
        if (closeShortcut != null) {
            removeAction(closeShortcut);
            closeShortcut = null;
        }
    }

    /**
     * A {@link ShortcutListener} specifically made to define a keyboard
     * shortcut that closes the window.
     * 
     * <pre>
     * <code>
     *  // within the window using helper
     *  subWindow.setCloseShortcut(KeyCode.ESCAPE, null);
     *  
     *  // or globally
     *  getWindow().addAction(new Window.CloseShortcut(subWindow, KeyCode.ESCAPE));
     * </code>
     * </pre>
     * 
     */
    public static class CloseShortcut extends ShortcutListener {
        protected Window window;

        /**
         * Creates a keyboard shortcut for closing the given window using the
         * shorthand notation defined in {@link ShortcutAction}.
         * 
         * @param window
         *            to be closed when the shortcut is invoked
         * @param shorthandCaption
         *            the caption with shortcut keycode and modifiers indicated
         */
        public CloseShortcut(Window window, String shorthandCaption) {
            super(shorthandCaption);
            this.window = window;
        }

        /**
         * Creates a keyboard shortcut for closing the given window using the
         * given {@link KeyCode} and {@link ModifierKey}s.
         * 
         * @param window
         *            to be closed when the shortcut is invoked
         * @param keyCode
         *            KeyCode to react to
         * @param modifiers
         *            optional modifiers for shortcut
         */
        public CloseShortcut(Window window, int keyCode, int... modifiers) {
            super(null, keyCode, modifiers);
            this.window = window;
        }

        /**
         * Creates a keyboard shortcut for closing the given window using the
         * given {@link KeyCode}.
         * 
         * @param window
         *            to be closed when the shortcut is invoked
         * @param keyCode
         *            KeyCode to react to
         */
        public CloseShortcut(Window window, int keyCode) {
            this(window, keyCode, null);
        }

        @Override
        public void handleAction(Object sender, Object target) {
            window.close();
        }
    }
}
