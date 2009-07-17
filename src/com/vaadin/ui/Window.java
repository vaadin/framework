/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.URIHandler;

/**
 * Application window component.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Window extends Panel implements URIHandler, ParameterHandler {

    /**
     * Window with no border.
     */
    public static final int BORDER_NONE = 0;

    /**
     * Window with only minimal border.
     */
    public static final int BORDER_MINIMAL = 1;

    /**
     * Window with default borders.
     */
    public static final int BORDER_DEFAULT = 2;

    /**
     * The terminal this window is attached to.
     */
    private Terminal terminal = null;

    /**
     * The application this window is attached to.
     */
    private Application application = null;

    /**
     * List of URI handlers for this window.
     */
    private LinkedList uriHandlerList = null;

    /**
     * List of parameter handlers for this window.
     */
    private LinkedList parameterHandlerList = null;

    /** Set of subwindows */
    private final HashSet subwindows = new HashSet();

    /**
     * Explicitly specified theme of this window. If null, application theme is
     * used.
     */
    private String theme = null;

    /**
     * Resources to be opened automatically on next repaint.
     */
    private final LinkedList openList = new LinkedList();

    /**
     * The name of the window.
     */
    private String name = null;

    /**
     * Window border mode.
     */
    private int border = BORDER_DEFAULT;

    /**
     * Distance of Window top border in pixels from top border of the containing
     * (main window) or -1 if unspecified.
     */
    private int positionY = -1;

    /**
     * Distance of Window left border in pixels from left border of the
     * containing (main window) or -1 if unspecified .
     */
    private int positionX = -1;

    private LinkedList notifications;

    private boolean modal = false;

    private boolean resizable = true;

    private boolean centerRequested = false;

    private Focusable pendingFocus;

    /* ********************************************************************* */

    /**
     * Creates a new empty unnamed window with default layout.
     * 
     * <p>
     * To show the window in application, it must be added to application with
     * <code>Application.addWindow</code> method.
     * </p>
     * 
     * <p>
     * The windows are scrollable by default.
     * </p>
     * 
     * @param caption
     *            the Title of the window.
     */
    public Window() {
        this("", null);
    }

    /**
     * Creates a new empty window with default layout.
     * 
     * <p>
     * To show the window in application, it must be added to application with
     * <code>Application.addWindow</code> method.
     * </p>
     * 
     * <p>
     * The windows are scrollable by default.
     * </p>
     * 
     * @param caption
     *            the Title of the window.
     */
    public Window(String caption) {
        this(caption, null);
    }

    /**
     * Creates a new window.
     * 
     * <p>
     * To show the window in application, it must be added to application with
     * <code>Application.addWindow</code> method.
     * </p>
     * 
     * <p>
     * The windows are scrollable by default.
     * </p>
     * 
     * @param caption
     *            the Title of the window.
     * @param layout
     *            the Layout of the window.
     */
    public Window(String caption, ComponentContainer content) {
        super(caption, content);
        setScrollable(true);
        setSizeUndefined();
    }

    /**
     * Gets the terminal type.
     * 
     * @return the Value of property terminal.
     */
    public Terminal getTerminal() {
        return terminal;
    }

    /* ********************************************************************* */

    /**
     * Gets the window of the component. Returns the window where this component
     * belongs to. If the component does not yet belong to a window the returns
     * null.
     * 
     * @return the parent window of the component.
     */
    @Override
    public final Window getWindow() {
        return this;
    }

    /**
     * Gets the application instance of the component. Returns the application
     * where this component belongs to. If the component does not yet belong to
     * a application the returns null.
     * 
     * @return the parent application of the component.
     */
    @Override
    public final Application getApplication() {
        if (getParent() == null) {
            return application;
        }
        return ((Window) getParent()).getApplication();
    }

    /**
     * Getter for property parent.
     * 
     * <p>
     * Parent is the visual parent of a component. Each component can belong to
     * only one ComponentContainer at time.
     * </p>
     * 
     * <p>
     * For windows attached directly to the application, parent is
     * <code>null</code>. For windows inside other windows, parent is the window
     * containing this window.
     * </p>
     * 
     * @return the Value of property parent.
     */
    @Override
    public final Component getParent() {
        return super.getParent();
    }

    /**
     * Setter for property parent.
     * 
     * <p>
     * Parent is the visual parent of a component. This is mostly called by
     * containers add method and should not be called directly
     * </p>
     * 
     * @param parent
     *            the New value of property parent.
     */
    @Override
    public void setParent(Component parent) {
        super.setParent(parent);
    }

    /**
     * Gets the component UIDL tag.
     * 
     * @return the Component UIDL tag as string.
     */
    @Override
    public String getTag() {
        return "window";
    }

    /* ********************************************************************* */

    /**
     * Adds the new URI handler to this window. For sub-windows, URI handlers
     * are attached to root level window.
     * 
     * @param handler
     *            the URI handler to add.
     */
    public void addURIHandler(URIHandler handler) {
        if (getParent() != null) {
            // this is subwindow, attach to main level instead
            // TODO hold internal list also and remove on detach
            Window mainWindow = (Window) getParent();
            mainWindow.addURIHandler(handler);
        } else {
            if (uriHandlerList == null) {
                uriHandlerList = new LinkedList();
            }
            synchronized (uriHandlerList) {
                if (!uriHandlerList.contains(handler)) {
                    uriHandlerList.addLast(handler);
                }
            }
        }
    }

    /**
     * Removes the given URI handler from this window.
     * 
     * @param handler
     *            the URI handler to remove.
     */
    public void removeURIHandler(URIHandler handler) {
        if (getParent() != null) {
            // this is subwindow
            Window mainWindow = (Window) getParent();
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
     * Handles uri recursively. Windows uri handler passes uri to all
     * {@link URIHandler}s added to it.
     * <p>
     * Note, that instead of overriding this method developer should consider
     * using {@link Window#addURIHandler(URIHandler)} to add uri handler to
     * Window.
     * 
     * @param context
     * @param relativeUri
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
     * Adds the new parameter handler to this window. For sub windows, parameter
     * handlers are attached to parent windows.
     * 
     * @param handler
     *            the parameter handler to add.
     */
    public void addParameterHandler(ParameterHandler handler) {
        if (getParent() != null) {
            // this is subwindow
            // TODO hold internal list also and remove on detach
            Window mainWindow = (Window) getParent();
            mainWindow.addParameterHandler(handler);
        } else {
            if (parameterHandlerList == null) {
                parameterHandlerList = new LinkedList();
            }
            synchronized (parameterHandlerList) {
                if (!parameterHandlerList.contains(handler)) {
                    parameterHandlerList.addLast(handler);
                }
            }
        }

    }

    /**
     * Removes the given URI handler from this window.
     * 
     * @param handler
     *            the parameter handler to remove.
     */
    public void removeParameterHandler(ParameterHandler handler) {
        if (getParent() != null) {
            // this is subwindow
            Window mainWindow = (Window) getParent();
            mainWindow.addParameterHandler(handler);
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

    /* Documented by the interface */
    public void handleParameters(Map parameters) {
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
     * Gets the theme for this window.
     * 
     * <p>
     * Subwindows do not support themes and thus return theme used by the parent
     * </p>
     * 
     * @return the Name of the theme used in window. If the theme for this
     *         individual window is not explicitly set, the application theme is
     *         used instead. If application is not assigned the
     *         terminal.getDefaultTheme is used. If terminal is not set, null is
     *         returned
     */
    public String getTheme() {
        if (getParent() != null) {
            return ((Window) getParent()).getTheme();
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
     * Sets the theme for this window.
     * 
     * Setting theme for subwindows is not supported.
     * 
     * In Toolkit 5 terminal will reload its host page on theme changes.
     * 
     * @param theme
     *            the New theme for this window. Null implies the default theme.
     */
    public void setTheme(String theme) {
        if (getParent() != null) {
            throw new UnsupportedOperationException(
                    "Setting theme for sub-windows is not supported.");
        }
        this.theme = theme;
        requestRepaint();
    }

    /**
     * Paints the content of this component.
     * 
     * @param event
     *            the Paint Event.
     * @throws PaintException
     *             if the paint operation failed.
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

        if (centerRequested) {
            target.addAttribute("center", true);
            centerRequested = false;
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
                for (final Iterator i = openList.iterator(); i.hasNext();) {
                    ((OpenResource) i.next()).paintContent(target);
                }
                openList.clear();
            }
        }

        // Contents of the window panel is painted
        super.paintContent(target);

        // Window position
        target.addVariable(this, "positionx", getPositionX());
        target.addVariable(this, "positiony", getPositionY());

        // Window closing
        target.addVariable(this, "close", false);

        // Paint subwindows
        for (final Iterator i = subwindows.iterator(); i.hasNext();) {
            final Window w = (Window) i.next();
            w.paint(target);
        }

        // Paint notifications
        if (notifications != null) {
            target.startTag("notifications");
            for (final Iterator it = notifications.iterator(); it.hasNext();) {
                final Notification n = (Notification) it.next();
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
                target.paintReference(pendingFocus, "focused");
            }
            pendingFocus = null;
        }

    }

    /* ********************************************************************* */

    /**
     * Opens the given resource in this window.
     * 
     * @param resource
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
     * Opens the given resource in named terminal window. Empty or
     * <code>null</code> window name results the resource to be opened in this
     * window.
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

    /* ********************************************************************* */

    /**
     * Opens the given resource in named terminal window with given size and
     * border properties. Empty or <code>null</code> window name results the
     * resource to be opened in this window.
     * 
     * @param resource
     * @param windowName
     * @param width
     * @param height
     * @param border
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
     * Returns the full url of the window, this returns window specific url even
     * for the main window.
     * 
     * @return the URL of the window.
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
     * Gets the unique name of the window that indentifies it on the terminal.
     * 
     * <p>
     * Name identifies the URL used to access application-level windows, but is
     * not used for windows inside other windows. all application-level windows
     * can be accessed by their names in url
     * <code>http://host:port/foo/bar/</code> where
     * <code>http://host:port/foo/</code> is the application url as returned by
     * getURL() and <code>bar</code> is the name of the window. Also note that
     * not all windows should be added to application - one can also add windows
     * inside other windows - these windows show as smaller windows inside those
     * windows.
     * </p>
     * 
     * @return the Name of the Window.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the border.
     * 
     * @return the border.
     */
    public int getBorder() {
        return border;
    }

    /**
     * Sets the border.
     * 
     * @param border
     *            the border to set.
     */
    public void setBorder(int border) {
        this.border = border;
    }

    /**
     * Sets the application this window is connected to.
     * 
     * <p>
     * This method should not be invoked directly. Instead the
     * {@link com.vaadin.Application#addWindow(Window)} method should be used to
     * add the window to an application and
     * {@link com.vaadin.Application#removeWindow(Window)} method for removing
     * the window from the applicion. These methods call this method implicitly.
     * </p>
     * 
     * <p>
     * The method invokes {@link Component#attach()} and
     * {@link Component#detach()} methods when necessary.
     * <p>
     * 
     * @param application
     *            the application to set.
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
     * Sets the name.
     * <p>
     * The name of the window must be unique inside the application.
     * </p>
     * 
     * <p>
     * If the name is null, the the window is given name automatically when it
     * is added to an application.
     * </p>
     * 
     * @param name
     *            the name to set.
     */
    public void setName(String name) {

        // The name can not be changed in application
        if (getApplication() != null) {
            throw new IllegalStateException(
                    "Window name can not be changed while "
                            + "the window is in application");
        }

        this.name = name;
    }

    /**
     * Sets the terminal type. The terminal type is set by the the terminal
     * adapter and may change from time to time.
     * 
     * @param type
     *            the terminal type to set.
     */
    public void setTerminal(Terminal type) {
        terminal = type;
    }

    /**
     * Private data structure for storing opening window properties.
     */
    private class OpenResource implements Serializable {

        private final Resource resource;

        private final String name;

        private final int width;

        private final int height;

        private final int border;

        /**
         * Creates a new open resource.
         * 
         * @param resource
         * @param name
         * @param width
         * @param height
         * @param border
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
         * Paints the open-tag inside the window.
         * 
         * @param target
         *            the Paint Event.
         * @throws PaintException
         *             if the Paint Operation fails.
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

    /**
     * Called when one or more variables handled by the implementing class are
     * changed.
     * 
     * @see com.vaadin.terminal.VariableOwner#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map variables) {

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
            setPositionX(x < 0 ? -1 : x);
        }
        final Integer positiony = (Integer) variables.get("positiony");
        if (positiony != null) {
            final int y = positiony.intValue();
            setPositionY(y < 0 ? -1 : y);
        }

        if (!isReadOnly()) {
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
     * If one wants change the default behavior, register a window close
     * listenter and do something else. For example, you could re-open the
     * browser-level window with mainWindow.open(), re-add the removed
     * sub-window back to its parent or remove browser-level window
     * automatically from the application.
     * </p>
     */
    protected void close() {
        Window parent = (Window) getParent();
        if (parent == null) {
            fireClose();
        } else {
            // subwindow is removed from parent
            parent.removeWindow(this);
            fireClose();
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
        this.positionX = positionX;
        centerRequested = false;
        requestRepaint();
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
        this.positionY = positionY;
        centerRequested = false;
        requestRepaint();
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

    public interface CloseListener extends Serializable {
        public void windowClose(CloseEvent e);
    }

    /**
     * Adds the listener.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addListener(CloseListener listener) {
        addListener(CloseEvent.class, listener, WINDOW_CLOSE_METHOD);
    }

    /**
     * Removes the listener.
     * 
     * @param listener
     *            the listener to remove.
     */
    public void removeListener(CloseListener listener) {
        addListener(CloseEvent.class, listener, WINDOW_CLOSE_METHOD);
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
     * @param window
     *            Window to be removed.
     */
    public void removeWindow(Window window) {
        subwindows.remove(window);
        window.setParent(null);
        requestRepaint();

    }

    /**
     * Get the set of all child windows.
     * 
     * @return Set of child windows.
     */
    public Set getChildWindows() {
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
            notifications = new LinkedList();
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
            ((Window) getParent()).setFocusedComponent(focusable);
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

}
