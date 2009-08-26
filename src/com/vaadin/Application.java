/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin;

import java.io.Serializable;
import java.net.SocketException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.SystemError;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.URIHandler;
import com.vaadin.terminal.VariableOwner;
import com.vaadin.terminal.gwt.server.ChangeVariablesErrorEvent;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Window;

/**
 * <p>
 * Base class required for all Vaadin applications. This class provides all the
 * basic services required by Vaadin. These services allow external discovery
 * and manipulation of the user, {@link com.vaadin.ui.Window windows} and
 * themes, and starting and stopping the application.
 * </p>
 * 
 * <p>
 * As mentioned, all Vaadin applications must inherit this class. However, this
 * is almost all of what one needs to do to create a fully functional
 * application. The only thing a class inheriting the <code>Application</code>
 * needs to do is implement the <code>init</code> method where it creates the
 * windows it needs to perform its function. Note that all applications must
 * have at least one window: the main window. The first unnamed window
 * constructed by an application automatically becomes the main window which
 * behaves just like other windows with one exception: when accessing windows
 * using URLs the main window corresponds to the application URL whereas other
 * windows correspond to a URL gotten by catenating the window's name to the
 * application URL.
 * </p>
 * 
 * <p>
 * See the class <code>com.vaadin.demo.HelloWorld</code> for a simple example of
 * a fully working application.
 * </p>
 * 
 * <p>
 * <strong>Window access.</strong> <code>Application</code> provides methods to
 * list, add and remove the windows it contains.
 * </p>
 * 
 * <p>
 * <strong>Execution control.</strong> This class includes method to start and
 * finish the execution of the application. Being finished means basically that
 * no windows will be available from the application anymore.
 * </p>
 * 
 * <p>
 * <strong>Theme selection.</strong> The theme selection process allows a theme
 * to be specified at three different levels. When a window's theme needs to be
 * found out, the window itself is queried for a preferred theme. If the window
 * does not prefer a specific theme, the application containing the window is
 * queried. If neither the application prefers a theme, the default theme for
 * the {@link com.vaadin.terminal.Terminal terminal} is used. The terminal
 * always defines a default theme.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public abstract class Application implements URIHandler,
        Terminal.ErrorListener, Serializable {

    /**
     * Random window name generator.
     */
    private static Random nameGenerator = new Random();

    /**
     * Application context the application is running in.
     */
    private ApplicationContext context;

    /**
     * The current user or <code>null</code> if no user has logged in.
     */
    private Object user;

    /**
     * Mapping from window name to window instance.
     */
    private final Hashtable windows = new Hashtable();

    /**
     * Main window of the application.
     */
    private Window mainWindow = null;

    /**
     * The application's URL.
     */
    private URL applicationUrl;

    /**
     * Name of the theme currently used by the application.
     */
    private String theme = null;

    /**
     * Application status.
     */
    private boolean applicationIsRunning = false;

    /**
     * Application properties.
     */
    private Properties properties;

    /**
     * Default locale of the application.
     */
    private Locale locale;

    /**
     * List of listeners listening user changes.
     */
    private LinkedList userChangeListeners = null;

    /**
     * Window attach listeners.
     */
    private LinkedList windowAttachListeners = null;

    /**
     * Window detach listeners.
     */
    private LinkedList windowDetachListeners = null;

    /**
     * Application resource mapping: key <-> resource.
     */
    private final Hashtable resourceKeyMap = new Hashtable();

    private final Hashtable keyResourceMap = new Hashtable();

    private long lastResourceKeyNumber = 0;

    /**
     * URL where the user is redirected to on application close, or null if
     * application is just closed without redirection.
     */
    private String logoutURL = null;

    /**
     * The default SystemMessages (read-only). Change by overriding
     * getSystemMessages() and returning CustomizedSystemMessages
     */
    private static final SystemMessages DEFAULT_SYSTEM_MESSAGES = new SystemMessages();

    /**
     * Application wide error handler which is used by default if an error is
     * left unhandled.
     */
    private Terminal.ErrorListener errorHandler = this;

    /**
     * <p>
     * Gets a window by name. Returns <code>null</code> if the application is
     * not running or it does not contain a window corresponding to the name.
     * </p>
     * 
     * <p>
     * All windows can be referenced by their names in url
     * <code>http://host:port/foo/bar/</code> where
     * <code>http://host:port/foo/</code> is the application url as returned by
     * getURL() and <code>bar</code> is the name of the window.
     * </p>
     * 
     * <p>
     * One should note that this method can, as a side effect create new windows
     * if needed by the application. This can be achieved by overriding the
     * default implementation.
     * </p>
     * 
     * <p>
     * If for some reason user opens another window with same url that is
     * already open, name is modified by adding "_12345678" postfix to the name,
     * where 12345678 is a random number. One can decide to create another
     * window-object for those windows (recommended) or to discard the postfix.
     * If the user has two browser windows pointing to the same window-object on
     * server, synchronization errors are likely to occur.
     * </p>
     * 
     * <p>
     * If no browser-level windowing is used, all defaults are fine and this
     * method can be left as is. In case browser-level windows are needed, it is
     * recommended to create new window-objects on this method from their names
     * if the super.getWindow() does not find existing windows. See below for
     * implementation example: <code><pre>
        // If we already have the requested window, use it
        Window w = super.getWindow(name);
        if (w == null) {
            // If no window found, create it
            w = new Window(name);
            // set windows name to the one requested
            w.setName(name);
            // add it to this application
            addWindow(w);
            // ensure use of window specific url
            w.open(new ExternalResource(w.getURL().toString()));
            // add some content
            w.addComponent(new Label("Test window"));
        }
        return w;</pre></code>
     * </p>
     * 
     * <p>
     * <strong>Note</strong> that all returned Window objects must be added to
     * this application instance.
     * 
     * <p>
     * The method should return null if the window does not exists (and is not
     * created as a side-effect) or if the application is not running anymore.
     * </p>
     * 
     * @param name
     *            the name of the window.
     * @return the window associated with the given URI or <code>null</code>
     */
    public Window getWindow(String name) {

        // For closed app, do not give any windows
        if (!isRunning()) {
            return null;
        }

        // Gets the window by name
        final Window window = (Window) windows.get(name);

        return window;
    }

    /**
     * Adds a new window to the application.
     * 
     * <p>
     * This implicitly invokes the
     * {@link com.vaadin.ui.Window#setApplication(Application)} method.
     * </p>
     * 
     * <p>
     * Note that all application-level windows can be accessed by their names in
     * url <code>http://host:port/foo/bar/</code> where
     * <code>http://host:port/foo/</code> is the application url as returned by
     * getURL() and <code>bar</code> is the name of the window. Also note that
     * not all windows should be added to application - one can also add windows
     * inside other windows - these windows show as smaller windows inside those
     * windows.
     * </p>
     * 
     * @param window
     *            the new <code>Window</code> to add. If the name of the window
     *            is <code>null</code>, an unique name is automatically given
     *            for the window.
     * @throws IllegalArgumentException
     *             if a window with the same name as the new window already
     *             exists in the application.
     * @throws NullPointerException
     *             if the given <code>Window</code> is <code>null</code>.
     */
    public void addWindow(Window window) throws IllegalArgumentException,
            NullPointerException {

        // Nulls can not be added to application
        if (window == null) {
            return;
        }

        // Check that one is not adding a sub-window to application
        if (window.getParent() != null) {
            throw new IllegalArgumentException(
                    "Window was already added inside another window"
                            + " - it can not be added to application also.");
        }

        // Gets the naming proposal from window
        String name = window.getName();

        // Checks that the application does not already contain
        // window having the same name
        if (name != null && windows.containsKey(name)) {

            // If the window is already added
            if (window == windows.get(name)) {
                return;
            }

            // Otherwise complain
            throw new IllegalArgumentException("Window with name '"
                    + window.getName()
                    + "' is already present in the application");
        }

        // If the name of the window is null, the window is automatically named
        if (name == null) {
            boolean accepted = false;
            while (!accepted) {

                // Try another name
                name = String.valueOf(Math.abs(nameGenerator.nextInt()));
                if (!windows.containsKey(name)) {
                    accepted = true;
                }
            }
            window.setName(name);
        }

        // Adds the window to application
        windows.put(name, window);
        window.setApplication(this);

        fireWindowAttachEvent(window);

        // If no main window is set, declare the window to be main window
        if (getMainWindow() == null) {
            mainWindow = window;
        }
    }

    /**
     * Send information to all listeners about new Windows associated with this
     * application.
     * 
     * @param window
     */
    private void fireWindowAttachEvent(Window window) {
        // Fires the window attach event
        if (windowAttachListeners != null) {
            final Object[] listeners = windowAttachListeners.toArray();
            final WindowAttachEvent event = new WindowAttachEvent(window);
            for (int i = 0; i < listeners.length; i++) {
                ((WindowAttachListener) listeners[i]).windowAttached(event);
            }
        }
    }

    /**
     * Removes the specified window from the application.
     * 
     * @param window
     *            the window to be removed.
     */
    public void removeWindow(Window window) {
        if (window != null && windows.contains(window)) {

            // Removes the window from application
            windows.remove(window.getName());

            // If the window was main window, clear it
            if (getMainWindow() == window) {
                setMainWindow(null);
            }

            // Removes the application from window
            if (window.getApplication() == this) {
                window.setApplication(null);
            }

            fireWindowDetachEvent(window);
        }
    }

    private void fireWindowDetachEvent(Window window) {
        // Fires the window detach event
        if (windowDetachListeners != null) {
            final Object[] listeners = windowDetachListeners.toArray();
            final WindowDetachEvent event = new WindowDetachEvent(window);
            for (int i = 0; i < listeners.length; i++) {
                ((WindowDetachListener) listeners[i]).windowDetached(event);
            }
        }
    }

    /**
     * Gets the user of the application.
     * 
     * @return the User of the application.
     */
    public Object getUser() {
        return user;
    }

    /**
     * <p>
     * Sets the user of the application instance. An application instance may
     * have a user associated to it. This can be set in login procedure or
     * application initialization.
     * </p>
     * <p>
     * A component performing the user login procedure can assign the user
     * property of the application and make the user object available to other
     * components of the application.
     * </p>
     * 
     * @param user
     *            the new user.
     */
    public void setUser(Object user) {
        final Object prevUser = this.user;
        if (user == prevUser || (user != null && user.equals(prevUser))) {
            return;
        }

        this.user = user;
        if (userChangeListeners != null) {
            final Object[] listeners = userChangeListeners.toArray();
            final UserChangeEvent event = new UserChangeEvent(this, user,
                    prevUser);
            for (int i = 0; i < listeners.length; i++) {
                ((UserChangeListener) listeners[i])
                        .applicationUserChanged(event);
            }
        }
    }

    /**
     * Gets the URL of the application.
     * 
     * @return the application's URL.
     */
    public URL getURL() {
        return applicationUrl;
    }

    /**
     * Ends the Application. In effect this will cause the application stop
     * returning any windows when asked.
     */
    public void close() {
        applicationIsRunning = false;
    }

    /**
     * Starts the application on the given URL.After this call the application
     * corresponds to the given URL and it will return windows when asked for
     * them.
     * 
     * Application properties are defined by servlet configuration object
     * {@link javax.servlet.ServletConfig} and they are overridden by
     * context-wide initialization parameters
     * {@link javax.servlet.ServletContext}.
     * 
     * @param applicationUrl
     *            the URL the application should respond to.
     * @param applicationProperties
     *            the Application properties as specified by the servlet
     *            configuration.
     * @param context
     *            the context application will be running in.
     * 
     */
    public void start(URL applicationUrl, Properties applicationProperties,
            ApplicationContext context) {
        this.applicationUrl = applicationUrl;
        properties = applicationProperties;
        this.context = context;
        init();
        applicationIsRunning = true;
    }

    /**
     * Tests if the application is running or if it has been finished.
     * 
     * @return <code>true</code> if the application is running,
     *         <code>false</code> if not.
     */
    public boolean isRunning() {
        return applicationIsRunning;
    }

    /**
     * Gets the set of windows contained by the application.
     * 
     * @return the Unmodifiable collection of windows.
     */
    public Collection getWindows() {
        return Collections.unmodifiableCollection(windows.values());
    }

    /**
     * <p>
     * Main initializer of the application. The <code>init</code> method is
     * called by the framework when the application is started, and it should
     * perform whatever initialization operations the application needs, such as
     * creating windows and adding components to them.
     * </p>
     */
    public abstract void init();

    /**
     * Gets the application's theme. The application's theme is the default
     * theme used by all the windows in it that do not explicitly specify a
     * theme. If the application theme is not explicitly set, the
     * <code>null</code> is returned.
     * 
     * @return the name of the application's theme.
     */
    public String getTheme() {
        return theme;
    }

    /**
     * Sets the application's theme.
     * <p>
     * Note that this theme can be overridden by the windows. <code>null</code>
     * implies the default terminal theme.
     * </p>
     * 
     * @param theme
     *            the new theme for this application.
     */
    public void setTheme(String theme) {
        // Collect list of windows not having the current or future theme
        final LinkedList toBeUpdated = new LinkedList();
        final String oldAppTheme = getTheme();
        for (final Iterator i = getWindows().iterator(); i.hasNext();) {
            final Window w = (Window) i.next();
            final String windowTheme = w.getTheme();
            if ((windowTheme == null)
                    || (!windowTheme.equals(theme) && windowTheme
                            .equals(oldAppTheme))) {
                toBeUpdated.add(w);
            }
        }

        // Updates the theme
        this.theme = theme;

        // Ask windows to update themselves
        for (final Iterator i = toBeUpdated.iterator(); i.hasNext();) {
            ((Window) i.next()).requestRepaint();
        }
    }

    /**
     * Gets the mainWindow of the application.
     * 
     * @return the main window.
     */
    public Window getMainWindow() {
        return mainWindow;
    }

    /**
     * <p>
     * Sets the mainWindow. If the main window is not explicitly set, the main
     * window defaults to first created window. Setting window as a main window
     * of this application also adds the window to this application.
     * </p>
     * 
     * @param mainWindow
     *            the mainWindow to set.
     */
    public void setMainWindow(Window mainWindow) {

        addWindow(mainWindow);
        this.mainWindow = mainWindow;
    }

    /**
     * Returns an enumeration of all the names in this application.
     * 
     * See {@link #start(URL, Properties, ApplicationContext)} how properties
     * are defined.
     * 
     * @return an enumeration of all the keys in this property list, including
     *         the keys in the default property list.
     * 
     */
    public Enumeration getPropertyNames() {
        return properties.propertyNames();
    }

    /**
     * Searches for the property with the specified name in this application.
     * This method returns <code>null</code> if the property is not found.
     * 
     * See {@link #start(URL, Properties, ApplicationContext)} how properties
     * are defined.
     * 
     * @param name
     *            the name of the property.
     * @return the value in this property list with the specified key value.
     */
    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    /**
     * Adds new resource to the application. The resource can be accessed by the
     * user of the application.
     * 
     * @param resource
     *            the resource to add.
     */
    public void addResource(ApplicationResource resource) {

        // Check if the resource is already mapped
        if (resourceKeyMap.containsKey(resource)) {
            return;
        }

        // Generate key
        final String key = String.valueOf(++lastResourceKeyNumber);

        // Add the resource to mappings
        resourceKeyMap.put(resource, key);
        keyResourceMap.put(key, resource);
    }

    /**
     * Removes the resource from the application.
     * 
     * @param resource
     *            the resource to remove.
     */
    public void removeResource(ApplicationResource resource) {
        final Object key = resourceKeyMap.get(resource);
        if (key != null) {
            resourceKeyMap.remove(resource);
            keyResourceMap.remove(key);
        }
    }

    /**
     * Gets the relative uri of the resource.
     * 
     * @param resource
     *            the resource to get relative location.
     * @return the relative uri of the resource.
     */
    public String getRelativeLocation(ApplicationResource resource) {

        // Gets the key
        final String key = (String) resourceKeyMap.get(resource);

        // If the resource is not registered, return null
        if (key == null) {
            return null;
        }

        final String filename = resource.getFilename();
        if (filename == null) {
            return "APP/" + key + "/";
        } else {
            return "APP/" + key + "/" + filename;
        }
    }

    /**
     * This method gets called by terminal. It has lots of duties like to pass
     * uri handler to proper uri handlers registered to windows etc.
     * 
     * In most situations developers should NOT OVERRIDE this method. Instead
     * developers should implement and register uri handlers to windows.
     * 
     * @see com.vaadin.terminal.URIHandler#handleURI(URL, String)
     */
    public DownloadStream handleURI(URL context, String relativeUri) {

        // If the relative uri is null, we are ready
        if (relativeUri == null) {
            return null;
        }

        // Resolves the prefix
        String prefix = relativeUri;
        final int index = relativeUri.indexOf('/');
        if (index >= 0) {
            prefix = relativeUri.substring(0, index);
        }

        // Handles the resource requests
        if (prefix.equals("APP")) {

            // Handles the resource request
            final int next = relativeUri.indexOf('/', index + 1);
            if (next < 0) {
                return null;
            }
            final String key = relativeUri.substring(index + 1, next);
            final ApplicationResource resource = (ApplicationResource) keyResourceMap
                    .get(key);
            if (resource != null) {
                DownloadStream stream = resource.getStream();
                if (stream != null) {
                    stream.setCacheTime(resource.getCacheTime());
                    return stream;
                } else {
                    return null;
                }
            } else {
                // Resource requests override uri handling
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Gets the default locale for this application.
     * 
     * @return the locale of this application.
     */
    public Locale getLocale() {
        if (locale != null) {
            return locale;
        }
        return Locale.getDefault();
    }

    /**
     * Sets the default locale for this application.
     * 
     * @param locale
     *            the Locale object.
     * 
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * <p>
     * An event that characterizes a change in the current selection.
     * </p>
     * Application user change event sent when the setUser is called to change
     * the current user of the application.
     * 
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class UserChangeEvent extends java.util.EventObject {

        /**
         * New user of the application.
         */
        private final Object newUser;

        /**
         * Previous user of the application.
         */
        private final Object prevUser;

        /**
         * Constructor for user change event.
         * 
         * @param source
         *            the application source.
         * @param newUser
         *            the new User.
         * @param prevUser
         *            the previous User.
         */
        public UserChangeEvent(Application source, Object newUser,
                Object prevUser) {
            super(source);
            this.newUser = newUser;
            this.prevUser = prevUser;
        }

        /**
         * Gets the new user of the application.
         * 
         * @return the new User.
         */
        public Object getNewUser() {
            return newUser;
        }

        /**
         * Gets the previous user of the application.
         * 
         * @return the previous Vaadin user, if user has not changed ever on
         *         application it returns <code>null</code>
         */
        public Object getPreviousUser() {
            return prevUser;
        }

        /**
         * Gets the application where the user change occurred.
         * 
         * @return the Application.
         */
        public Application getApplication() {
            return (Application) getSource();
        }
    }

    /**
     * The <code>UserChangeListener</code> interface for listening application
     * user changes.
     * 
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface UserChangeListener extends EventListener, Serializable {

        /**
         * The <code>applicationUserChanged</code> method Invoked when the
         * application user has changed.
         * 
         * @param event
         *            the change event.
         */
        public void applicationUserChanged(Application.UserChangeEvent event);
    }

    /**
     * Adds the user change listener.
     * 
     * @param listener
     *            the user change listener to add.
     */
    public void addListener(UserChangeListener listener) {
        if (userChangeListeners == null) {
            userChangeListeners = new LinkedList();
        }
        userChangeListeners.add(listener);
    }

    /**
     * Removes the user change listener.
     * 
     * @param listener
     *            the user change listener to remove.
     */
    public void removeListener(UserChangeListener listener) {
        if (userChangeListeners == null) {
            return;
        }
        userChangeListeners.remove(listener);
        if (userChangeListeners.isEmpty()) {
            userChangeListeners = null;
        }
    }

    /**
     * Window detach event.
     */
    public class WindowDetachEvent extends EventObject {

        private final Window window;

        /**
         * Creates a event.
         * 
         * @param window
         *            the Detached window.
         */
        public WindowDetachEvent(Window window) {
            super(Application.this);
            this.window = window;
        }

        /**
         * Gets the detached window.
         * 
         * @return the detached window.
         */
        public Window getWindow() {
            return window;
        }

        /**
         * Gets the application from which the window was detached.
         * 
         * @return the Application.
         */
        public Application getApplication() {
            return (Application) getSource();
        }
    }

    /**
     * Window attach event.
     */
    public class WindowAttachEvent extends EventObject {

        private final Window window;

        /**
         * Creates a event.
         * 
         * @param window
         *            the Attached window.
         */
        public WindowAttachEvent(Window window) {
            super(Application.this);
            this.window = window;
        }

        /**
         * Gets the attached window.
         * 
         * @return the attached window.
         */
        public Window getWindow() {
            return window;
        }

        /**
         * Gets the application to which the window was attached.
         * 
         * @return the Application.
         */
        public Application getApplication() {
            return (Application) getSource();
        }
    }

    /**
     * Window attach listener interface.
     */
    public interface WindowAttachListener extends Serializable {

        /**
         * Window attached
         * 
         * @param event
         *            the window attach event.
         */
        public void windowAttached(WindowAttachEvent event);
    }

    /**
     * Window detach listener interface.
     */
    public interface WindowDetachListener extends Serializable {

        /**
         * Window detached.
         * 
         * @param event
         *            the window detach event.
         */
        public void windowDetached(WindowDetachEvent event);
    }

    /**
     * Adds the window attach listener.
     * 
     * @param listener
     *            the window attach listener to add.
     */
    public void addListener(WindowAttachListener listener) {
        if (windowAttachListeners == null) {
            windowAttachListeners = new LinkedList();
        }
        windowAttachListeners.add(listener);
    }

    /**
     * Adds the window detach listener.
     * 
     * @param listener
     *            the window detach listener to add.
     */
    public void addListener(WindowDetachListener listener) {
        if (windowDetachListeners == null) {
            windowDetachListeners = new LinkedList();
        }
        windowDetachListeners.add(listener);
    }

    /**
     * Removes the window attach listener.
     * 
     * @param listener
     *            the window attach listener to remove.
     */
    public void removeListener(WindowAttachListener listener) {
        if (windowAttachListeners != null) {
            windowAttachListeners.remove(listener);
            if (windowAttachListeners.isEmpty()) {
                windowAttachListeners = null;
            }
        }
    }

    /**
     * Removes the window detach listener.
     * 
     * @param listener
     *            the window detach listener to remove.
     */
    public void removeListener(WindowDetachListener listener) {
        if (windowDetachListeners != null) {
            windowDetachListeners.remove(listener);
            if (windowDetachListeners.isEmpty()) {
                windowDetachListeners = null;
            }
        }
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

    /**
     * Gets the SystemMessages for this application. SystemMessages are used to
     * notify the user of various critical situations that can occur, such as
     * session expiration, client/server out of sync, and internal server error.
     * 
     * You can customize the messages by overriding this method and returning
     * {@link CustomizedSystemMessages}
     * 
     * @return the SystemMessages for this application
     */
    public static SystemMessages getSystemMessages() {
        return DEFAULT_SYSTEM_MESSAGES;
    }

    /**
     * <p>
     * Invoked by the terminal on any exception that occurs in application and
     * is thrown by the <code>setVariable</code> to the terminal. The default
     * implementation sets the exceptions as <code>ComponentErrors</code> to the
     * component that initiated the exception and prints stack trace to standard
     * error stream.
     * </p>
     * <p>
     * You can safely override this method in your application in order to
     * direct the errors to some other destination (for example log).
     * </p>
     * 
     * @param event
     *            the change event.
     * @see com.vaadin.terminal.Terminal.ErrorListener#terminalError(com.vaadin.terminal.Terminal.ErrorEvent)
     */
    public void terminalError(Terminal.ErrorEvent event) {
        final Throwable t = event.getThrowable();
        if (t instanceof SocketException) {
            // Most likely client browser closed socket
            System.err
                    .println("Warning: SocketException in CommunicationManager."
                            + " Most likely client (browser) closed socket.");
            return;
        }

        // Finds the original source of the error/exception
        Object owner = null;
        if (event instanceof VariableOwner.ErrorEvent) {
            owner = ((VariableOwner.ErrorEvent) event).getVariableOwner();
        } else if (event instanceof URIHandler.ErrorEvent) {
            owner = ((URIHandler.ErrorEvent) event).getURIHandler();
        } else if (event instanceof ParameterHandler.ErrorEvent) {
            owner = ((ParameterHandler.ErrorEvent) event).getParameterHandler();
        } else if (event instanceof ChangeVariablesErrorEvent) {
            owner = ((ChangeVariablesErrorEvent) event).getComponent();
        }

        // Shows the error in AbstractComponent
        if (owner instanceof AbstractComponent) {
            if (t instanceof ErrorMessage) {
                ((AbstractComponent) owner).setComponentError((ErrorMessage) t);
            } else {
                ((AbstractComponent) owner)
                        .setComponentError(new SystemError(t));
            }
        }

        // also print the error on console
        t.printStackTrace();
    }

    /**
     * Gets the application context.
     * <p>
     * The application context is the environment where the application is
     * running in.
     * </p>
     * 
     * @return the application context.
     */
    public ApplicationContext getContext() {
        return context;
    }

    /**
     * Override this method to return correct version number of your
     * Application. Version information is delivered for example to Testing
     * Tools test results.
     * 
     * @return version string
     */
    public String getVersion() {
        return "NONVERSIONED";
    }

    /**
     * Gets the application error handler.
     * 
     * The default error handler is the application itself.
     * 
     * @return Application error handler
     */
    public Terminal.ErrorListener getErrorHandler() {
        return errorHandler;
    }

    /**
     * Sets the application error handler.
     * 
     * The default error handler is the application itself.
     * 
     * @param errorHandler
     */
    public void setErrorHandler(Terminal.ErrorListener errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Contains the system messages used to notify the user about various
     * critical situations that can occur.
     * <p>
     * Customize by overriding the static Application.getSystemMessages() and
     * returning {@link CustomizedSystemMessages}
     * </p>
     * 
     */
    public static class SystemMessages implements Serializable {
        protected String sessionExpiredURL = null;
        protected boolean sessionExpiredNotificationEnabled = true;
        protected String sessionExpiredCaption = "Session Expired";
        protected String sessionExpiredMessage = "Take note of any unsaved data, and <u>click here</u> to continue.";

        protected String communicationErrorURL = null;
        protected boolean communicationErrorNotificationEnabled = true;
        protected String communicationErrorCaption = "Communication problem";
        protected String communicationErrorMessage = "Take note of any unsaved data, and <u>click here</u> to continue.";

        protected String internalErrorURL = null;
        protected boolean internalErrorNotificationEnabled = true;
        protected String internalErrorCaption = "Internal Error";
        protected String internalErrorMessage = "Please notify the administrator.<br/>Take note of any unsaved data, and <u>click here</u> to continue.";

        protected String outOfSyncURL = null;
        protected boolean outOfSyncNotificationEnabled = true;
        protected String outOfSyncCaption = "Out of sync";
        protected String outOfSyncMessage = "Something has caused us to be out of sync with the server.<br/>Take note of any unsaved data, and <u>click here</u> to re-sync.";

        /**
         * Use {@link CustomizedSystemMessages} to customize
         */
        private SystemMessages() {

        }

        /**
         * @return the URL to load on null for restart
         */
        public String getSessionExpiredURL() {
            return sessionExpiredURL;
        }

        /**
         * @return true = notification enabled, false = notification disabled
         */
        public boolean isSessionExpiredNotificationEnabled() {
            return sessionExpiredNotificationEnabled;
        }

        /**
         * @return the notification caption, or null for no caption
         */
        public String getSessionExpiredCaption() {
            return (sessionExpiredNotificationEnabled ? sessionExpiredCaption
                    : null);
        }

        /**
         * @return the notification message, or null for no message
         */
        public String getSessionExpiredMessage() {
            return (sessionExpiredNotificationEnabled ? sessionExpiredMessage
                    : null);
        }

        /**
         * @return the URL to load on null for restart
         */
        public String getCommunicationErrorURL() {
            return communicationErrorURL;
        }

        /**
         * @return true = notification enabled, false = notification disabled
         */
        public boolean isCommunicationErrorNotificationEnabled() {
            return communicationErrorNotificationEnabled;
        }

        /**
         * @return the notification caption, or null for no caption
         */
        public String getCommunicationErrorCaption() {
            return (communicationErrorNotificationEnabled ? communicationErrorCaption
                    : null);
        }

        /**
         * @return the notification message, or null for no message
         */
        public String getCommunicationErrorMessage() {
            return (communicationErrorNotificationEnabled ? communicationErrorMessage
                    : null);
        }

        /**
         * @return the URL to load on null for restart
         */
        public String getInternalErrorURL() {
            return internalErrorURL;
        }

        /**
         * @return true = notification enabled, false = notification disabled
         */
        public boolean isInternalErrorNotificationEnabled() {
            return internalErrorNotificationEnabled;
        }

        /**
         * @return the notification caption, or null for no caption
         */
        public String getInternalErrorCaption() {
            return (internalErrorNotificationEnabled ? internalErrorCaption
                    : null);
        }

        /**
         * @return the notification message, or null for no message
         */
        public String getInternalErrorMessage() {
            return (internalErrorNotificationEnabled ? internalErrorMessage
                    : null);
        }

        /**
         * @return the URL to load on null for restart
         */
        public String getOutOfSyncURL() {
            return outOfSyncURL;
        }

        /**
         * @return true = notification enabled, false = notification disabled
         */
        public boolean isOutOfSyncNotificationEnabled() {
            return outOfSyncNotificationEnabled;
        }

        /**
         * @return the notification caption, or null for no caption
         */
        public String getOutOfSyncCaption() {
            return (outOfSyncNotificationEnabled ? outOfSyncCaption : null);
        }

        /**
         * @return the notification message, or null for no message
         */
        public String getOutOfSyncMessage() {
            return (outOfSyncNotificationEnabled ? outOfSyncMessage : null);
        }

    }

    /**
     * Contains the system messages used to notify the user about various
     * critical situations that can occur.
     * <p>
     * Customize by overriding the static Application.getSystemMessages() and
     * returning CustomizedSystemMessages. Note that getSystemMessages() is
     * static - changing the system messages will by default change the message
     * for all users of the application.
     * </p>
     * <p>
     * The default behavior is to show a notification, and restart the
     * application the the user clicks the message. <br/>
     * Instead of restarting the application, you can set a specific URL that
     * the user is taken to.<br/>
     * Setting both caption and message to null will restart the application (or
     * go to the specified URL) without displaying a notification.
     * set*NotificationEnabled(false) will achieve the same thing.
     * </p>
     * <p>
     * The situations are:
     * <li>Session expired: the user session has expired, usually due to
     * inactivity.</li>
     * <li>Communication error: the client failed to contact the server, or the
     * server returned and invalid response.</li>
     * <li>Internal error: unhandled critical server error (e.g out of memory,
     * database crash)
     * <li>Out of sync: the client is not in sync with the server. E.g the user
     * opens two windows showing the same application, but the application does
     * not support this and uses the same Window instance. When the user makes
     * changes in one of the windows - the other window is no longer in sync,
     * and (for instance) pressing a button that is no longer present in the UI
     * will cause a out-of-sync -situation.
     * </p>
     */

    public static class CustomizedSystemMessages extends SystemMessages
            implements Serializable {

        /**
         * Sets the URL to go to when the session has expired.
         * 
         * @param sessionExpiredURL
         *            the URL to go to, or null to reload current
         */
        public void setSessionExpiredURL(String sessionExpiredURL) {
            this.sessionExpiredURL = sessionExpiredURL;
        }

        /**
         * Enables or disables the notification. If disabled, the set URL (or
         * current) is loaded directly when next transaction between server and
         * client happens.
         * 
         * @param sessionExpiredNotificationEnabled
         *            true = enabled, false = disabled
         */
        public void setSessionExpiredNotificationEnabled(
                boolean sessionExpiredNotificationEnabled) {
            this.sessionExpiredNotificationEnabled = sessionExpiredNotificationEnabled;
        }

        /**
         * Sets the caption of the notification. Set to null for no caption. If
         * both caption and message are null, client automatically forwards to
         * sessionExpiredUrl after timeout timer expires. Timer uses value read
         * from HTTPSession.getMaxInactiveInterval()
         * 
         * @param sessionExpiredCaption
         *            the caption
         */
        public void setSessionExpiredCaption(String sessionExpiredCaption) {
            this.sessionExpiredCaption = sessionExpiredCaption;
        }

        /**
         * Sets the message of the notification. Set to null for no message. If
         * both caption and message are null, client automatically forwards to
         * sessionExpiredUrl after timeout timer expires. Timer uses value read
         * from HTTPSession.getMaxInactiveInterval()
         * 
         * @param sessionExpiredMessage
         *            the message
         */
        public void setSessionExpiredMessage(String sessionExpiredMessage) {
            this.sessionExpiredMessage = sessionExpiredMessage;
        }

        /**
         * Sets the URL to go to when there is a communication error.
         * 
         * @param communicationErrorURL
         *            the URL to go to, or null to reload current
         */
        public void setCommunicationErrorURL(String communicationErrorURL) {
            this.communicationErrorURL = communicationErrorURL;
        }

        /**
         * Enables or disables the notification. If disabled, the set URL (or
         * current) is loaded directly.
         * 
         * @param communicationErrorNotificationEnabled
         *            true = enabled, false = disabled
         */
        public void setCommunicationErrorNotificationEnabled(
                boolean communicationErrorNotificationEnabled) {
            this.communicationErrorNotificationEnabled = communicationErrorNotificationEnabled;
        }

        /**
         * Sets the caption of the notification. Set to null for no caption. If
         * both caption and message is null, the notification is disabled;
         * 
         * @param communicationErrorCaption
         *            the caption
         */
        public void setCommunicationErrorCaption(
                String communicationErrorCaption) {
            this.communicationErrorCaption = communicationErrorCaption;
        }

        /**
         * Sets the message of the notification. Set to null for no message. If
         * both caption and message is null, the notification is disabled;
         * 
         * @param communicationErrorMessage
         *            the message
         */
        public void setCommunicationErrorMessage(
                String communicationErrorMessage) {
            this.communicationErrorMessage = communicationErrorMessage;
        }

        /**
         * Sets the URL to go to when an internal error occurs.
         * 
         * @param internalErrorURL
         *            the URL to go to, or null to reload current
         */
        public void setInternalErrorURL(String internalErrorURL) {
            this.internalErrorURL = internalErrorURL;
        }

        /**
         * Enables or disables the notification. If disabled, the set URL (or
         * current) is loaded directly.
         * 
         * @param internalErrorNotificationEnabled
         *            true = enabled, false = disabled
         */
        public void setInternalErrorNotificationEnabled(
                boolean internalErrorNotificationEnabled) {
            this.internalErrorNotificationEnabled = internalErrorNotificationEnabled;
        }

        /**
         * Sets the caption of the notification. Set to null for no caption. If
         * both caption and message is null, the notification is disabled;
         * 
         * @param internalErrorCaption
         *            the caption
         */
        public void setInternalErrorCaption(String internalErrorCaption) {
            this.internalErrorCaption = internalErrorCaption;
        }

        /**
         * Sets the message of the notification. Set to null for no message. If
         * both caption and message is null, the notification is disabled;
         * 
         * @param internalErrorMessage
         *            the message
         */
        public void setInternalErrorMessage(String internalErrorMessage) {
            this.internalErrorMessage = internalErrorMessage;
        }

        /**
         * Sets the URL to go to when the client is out-of-sync.
         * 
         * @param outOfSyncURL
         *            the URL to go to, or null to reload current
         */
        public void setOutOfSyncURL(String outOfSyncURL) {
            this.outOfSyncURL = outOfSyncURL;
        }

        /**
         * Enables or disables the notification. If disabled, the set URL (or
         * current) is loaded directly.
         * 
         * @param outOfSyncNotificationEnabled
         *            true = enabled, false = disabled
         */
        public void setOutOfSyncNotificationEnabled(
                boolean outOfSyncNotificationEnabled) {
            this.outOfSyncNotificationEnabled = outOfSyncNotificationEnabled;
        }

        /**
         * Sets the caption of the notification. Set to null for no caption. If
         * both caption and message is null, the notification is disabled;
         * 
         * @param outOfSyncCaption
         *            the caption
         */
        public void setOutOfSyncCaption(String outOfSyncCaption) {
            this.outOfSyncCaption = outOfSyncCaption;
        }

        /**
         * Sets the message of the notification. Set to null for no message. If
         * both caption and message is null, the notification is disabled;
         * 
         * @param outOfSyncMessage
         *            the message
         */
        public void setOutOfSyncMessage(String outOfSyncMessage) {
            this.outOfSyncMessage = outOfSyncMessage;
        }

    }

    public class ApplicationError implements Terminal.ErrorEvent {
        private final Throwable throwable;

        public ApplicationError(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }

    }
}