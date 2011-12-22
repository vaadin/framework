/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.annotations.RootInitRequiresBrowserDetails;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.data.util.converter.DefaultConverterFactory;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.CombinedRequest;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.RequestHandler;
import com.vaadin.terminal.SystemError;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.VariableOwner;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedRequest.BrowserDetails;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;
import com.vaadin.terminal.gwt.server.ChangeVariablesErrorEvent;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Root;
import com.vaadin.ui.Table;
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
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Application implements Terminal.ErrorListener, Serializable {

    /**
     * The name of the parameter that is by default used in e.g. web.xml to
     * define the name of the default {@link Root} class.
     */
    public static final String ROOT_PARAMETER = "root";

    /**
     * Represents a strategy for retaining root instances when the contents of
     * the browser have been reloaded, e.g. if the user presses refresh or
     * navigates back to the application from another page.
     * 
     * @since 7.0
     */
    public interface RootPreserveStrategy extends Serializable {
        /**
         * Finds the id of an existing root that should be used to serve a
         * request.
         * 
         * @param request
         *            the request to get the root for
         * @return integer id of the root, or <code>null</code> if no root is
         *         found
         * @throws RootRequiresMoreInformationException
         *             if full details from the browser are required to resolve
         *             the root
         */
        public Integer getPreservedRootForRequest(WrappedRequest request)
                throws RootRequiresMoreInformationException;

        /**
         * Used to tell whether the browser details are required when
         * {@link #registerRoot(Root, WrappedRequest)} is called.
         * 
         * @return <code>true</code> if the browser details are needed,
         *         <code>false</code> if they are not needed
         */
        public boolean reqsterRequiresBrowserDetails();

        /**
         * This method is called by the framework when a root has been
         * initialized and should possibly be preserved for future lookups.
         * 
         * @param root
         *            the newly initialized root
         * @param request
         *            the associated request
         */
        public void registerRoot(Root root, WrappedRequest request);
    }

    public static class WindowNameRootPreserveStrategy implements
            RootPreserveStrategy {
        /**
         * Keeps track of the roots that should be remembered when the browser
         * is refreshed.
         */
        private final Map<String, Integer> retainOnRefreshRoots = new HashMap<String, Integer>();

        public Integer getPreservedRootForRequest(WrappedRequest request)
                throws RootRequiresMoreInformationException {
            if (retainOnRefreshRoots.isEmpty()) {
                return null;
            }
            BrowserDetails browserDetails = request.getBrowserDetails();
            if (browserDetails == null) {
                throw new RootRequiresMoreInformationException();
            } else {
                String windowName = browserDetails.getWindowName();
                return retainOnRefreshRoots.get(windowName);
            }
        }

        public void registerRoot(Root root, WrappedRequest request) {
            String windowName = request.getBrowserDetails().getWindowName();
            Integer rootId = Integer.valueOf(root.getRootId());
            retainOnRefreshRoots.put(windowName, rootId);
        }

        public boolean reqsterRequiresBrowserDetails() {
            return true;
        }
    }

    /**
     * A special application designed to help migrating applications from Vaadin
     * 6 to Vaadin 7. The legacy application supports setting a main window,
     * adding additional browser level windows and defining the theme for the
     * entire application.
     * 
     * @deprecated This class is only intended to ease migration and should not
     *             be used for new projects.
     * 
     * @since 7.0
     */
    @Deprecated
    public static class LegacyApplication extends Application {
        /**
         * Ignore initial / and then get everything up to the next /
         */
        private static final Pattern WINDOW_NAME_PATTERN = Pattern
                .compile("^/?([^/]+).*");

        private Root.LegacyWindow mainWindow;
        private String theme;

        private Map<String, Root.LegacyWindow> legacyRootNames = new HashMap<String, Root.LegacyWindow>();

        /**
         * Sets the main window of this application. Setting window as a main
         * window of this application also adds the window to this application.
         * 
         * @param mainWindow
         *            the root to set as the default window
         */
        public void setMainWindow(Root.LegacyWindow mainWindow) {
            if (this.mainWindow != null) {
                throw new IllegalStateException(
                        "mainWindow has already been set");
            }
            if (mainWindow.getApplication() == null) {
                mainWindow.setApplication(this);
            } else if (mainWindow.getApplication() != this) {
                throw new IllegalStateException(
                        "mainWindow is attached to another application");
            }
            this.mainWindow = mainWindow;
        }

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
         * @return the root used as the default window
         */
        public Root.LegacyWindow getMainWindow() {
            return mainWindow;
        }

        /**
         * This implementation simulates the way of finding a window for a
         * request by extracting a window name from the requested path and
         * passes that name to {@link #getWindow(String)}.
         * 
         * {@inheritDoc}
         * 
         * @see #getWindow(String)
         * @see Application#getRoot(WrappedRequest)
         */
        @Override
        public Root.LegacyWindow getRoot(WrappedRequest request) {
            String pathInfo = request.getRequestPathInfo();
            String name = null;
            if (pathInfo != null && pathInfo.length() > 0) {
                Matcher matcher = WINDOW_NAME_PATTERN.matcher(pathInfo);
                if (matcher.matches()) {
                    // Skip the initial slash
                    name = matcher.group(1);
                }
            }
            Root.LegacyWindow window = getWindow(name);
            if (window != null) {
                return window;
            }
            return mainWindow;
        }

        /**
         * Sets the application's theme.
         * <p>
         * Note that this theme can be overridden for a specific root with
         * {@link Application#getThemeForRoot(Root)}. Setting theme to be
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
         * theme used by all the roots for which a theme is not explicitly
         * defined. If the application theme is not explicitly set,
         * <code>null</code> is returned.
         * 
         * @return the name of the application's theme.
         */
        public String getTheme() {
            return theme;
        }

        /**
         * This implementation returns the theme that has been set using
         * {@link #setTheme(String)}
         * <p>
         * {@inheritDoc}
         */
        @Override
        public String getThemeForRoot(Root root) {
            return theme;
        }

        /**
         * <p>
         * Gets a root by name. Returns <code>null</code> if the application is
         * not running or it does not contain a window corresponding to the
         * name.
         * </p>
         * 
         * @param name
         *            the name of the requested window
         * @return a root corresponding to the name, or <code>null</code> to use
         *         the default window
         */
        public Root.LegacyWindow getWindow(String name) {
            return legacyRootNames.get(name);
        }

        /**
         * Counter to get unique names for windows with no explicit name
         */
        private int namelessRootIndex = 0;

        /**
         * Adds a new browser level window to this application. Please note that
         * Root doesn't have a name that is used in the URL - to add a named
         * window you should instead use {@link #addWindow(Root, String)}
         * 
         * @param root
         *            the root window to add to the application
         * @return returns the name that has been assigned to the window
         * 
         * @see #addWindow(Root, String)
         */
        public void addWindow(Root.LegacyWindow root) {
            if (root.getName() == null) {
                String name = Integer.toString(namelessRootIndex++);
                root.setName(name);
            }

            legacyRootNames.put(root.getName(), root);
            root.setApplication(this);
        }

        /**
         * Removes the specified window from the application. This also removes
         * all name mappings for the window (see
         * {@link #addWindow(Root, String) and #getWindowName(Root)}.
         * 
         * <p>
         * Note that removing window from the application does not close the
         * browser window - the window is only removed from the server-side.
         * </p>
         * 
         * @param root
         *            the root to remove
         */
        public void removeWindow(Root.LegacyWindow root) {
            for (Entry<String, Root.LegacyWindow> entry : legacyRootNames
                    .entrySet()) {
                if (entry.getValue() == root) {
                    legacyRootNames.remove(entry.getKey());
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
        public Collection<Root.LegacyWindow> getWindows() {
            return Collections.unmodifiableCollection(legacyRootNames.values());
        }
    }

    /**
     * Helper class to keep track of the information from an initial request if
     * another request is required before the <code>Root</code> can be
     * initialized.
     * 
     * The saved information is then used together with information from the
     * second request to create a {@link CombinedRequest} containing relevant
     * parts from each request.
     */
    private static class PendingRootRequest implements Serializable {

        private final Map<String, String[]> parameterMap;
        private final String pathInfo;

        /**
         * Creates a new pending request from an initial request. This is done
         * by saving the parameterMap and the pathInfo from the provided wrapped
         * request.
         * 
         * @param request
         *            the initial request from which the required data is
         *            extracted
         */
        public PendingRootRequest(WrappedRequest request) {
            // Create a defensive copy in case the Map instance is reused
            parameterMap = new HashMap<String, String[]>(
                    request.getParameterMap());
            pathInfo = request.getRequestPathInfo();
        }

        /**
         * Creates a new request by combining information from the initial
         * request with information from the provided second request.
         * 
         * @param secondRequest
         *            the second request, should contain the information
         *            required for providing {@link BrowserDetails}
         * @return a request providing a combined view of the information from
         *         the two original requests
         * 
         * @see CombinedRequest#CombinedRequest(WrappedRequest, Map, String)
         */
        public CombinedRequest getCombinedRequest(
                final WrappedRequest secondRequest) {
            return new CombinedRequest(secondRequest,
                    Collections.unmodifiableMap(parameterMap), pathInfo);
        }
    }

    private final static Logger logger = Logger.getLogger(Application.class
            .getName());

    /**
     * Application context the application is running in.
     */
    private ApplicationContext context;

    /**
     * The current user or <code>null</code> if no user has logged in.
     */
    private Object user;

    /**
     * The application's URL.
     */
    private URL applicationUrl;

    /**
     * Application status.
     */
    private volatile boolean applicationIsRunning = false;

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
    private LinkedList<UserChangeListener> userChangeListeners = null;

    /**
     * Application resource mapping: key <-> resource.
     */
    private final Hashtable<ApplicationResource, String> resourceKeyMap = new Hashtable<ApplicationResource, String>();

    private final Hashtable<String, ApplicationResource> keyResourceMap = new Hashtable<String, ApplicationResource>();

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
     * The converter factory that is used to provide default converters for the
     * application.
     */
    private ConverterFactory converterFactory = new DefaultConverterFactory();

    private LinkedList<RequestHandler> requestHandlers = new LinkedList<RequestHandler>();

    private int nextRootId = 0;
    private Map<Integer, Root> roots = new HashMap<Integer, Root>();

    private boolean productionMode = true;

    /**
     * Keeps track of requests for which a root should be created once more
     * information is available.
     */
    private Map<Integer, PendingRootRequest> pendingRoots = new HashMap<Integer, PendingRootRequest>();

    private RootPreserveStrategy rootPreserveStrategy = null;

    /**
     * Keeps track of which roots have been inited.
     * <p>
     * TODO Investigate whether this might be derived from the different states
     * in getRootForRrequest.
     * </p>
     */
    private Set<Integer> initedRoots = new HashSet<Integer>();

    /**
     * Gets the user of the application.
     * 
     * <p>
     * Vaadin doesn't define of use user object in any way - it only provides
     * this getter and setter methods for convenience. The user is any object
     * that has been stored to the application with {@link #setUser(Object)}.
     * </p>
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
     * <p>
     * Vaadin doesn't define of use user object in any way - it only provides
     * getter and setter methods for convenience. The user reference stored to
     * the application can be read with {@link #getUser()}.
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
     * <p>
     * This is the URL what can be entered to a browser window to start the
     * application. Navigating to the application URL shows the main window (
     * {@link #getMainWindow()}) of the application. Note that the main window
     * can also be shown by navigating to the window url (
     * {@link com.vaadin.ui.Window#getURL()}).
     * </p>
     * 
     * @return the application's URL.
     */
    public URL getURL() {
        return applicationUrl;
    }

    /**
     * Ends the Application.
     * 
     * <p>
     * In effect this will cause the application stop returning any windows when
     * asked. When the application is closed, its state is removed from the
     * session and the browser window is redirected to the application logout
     * url set with {@link #setLogoutURL(String)}. If the logout url has not
     * been set, the browser window is reloaded and the application is
     * restarted.
     * </p>
     * .
     */
    public void close() {
        applicationIsRunning = false;
    }

    /**
     * Starts the application on the given URL.
     * 
     * <p>
     * This method is called by Vaadin framework when a user navigates to the
     * application. After this call the application corresponds to the given URL
     * and it will return windows when asked for them. There is no need to call
     * this method directly.
     * </p>
     * 
     * <p>
     * Application properties are defined by servlet configuration object
     * {@link javax.servlet.ServletConfig} and they are overridden by
     * context-wide initialization parameters
     * {@link javax.servlet.ServletContext}.
     * </p>
     * 
     * @param applicationUrl
     *            the URL the application should respond to.
     * @param applicationProperties
     *            the Application properties as specified by the servlet
     *            configuration.
     * @param context
     *            the context application will be running in.
     * @param productionMode
     * 
     */
    public void start(URL applicationUrl, Properties applicationProperties,
            ApplicationContext context, boolean productionMode) {
        this.applicationUrl = applicationUrl;
        this.productionMode = productionMode;
        properties = applicationProperties;
        this.context = context;
        init();
        applicationIsRunning = true;
    }

    /**
     * Tests if the application is running or if it has been finished.
     * 
     * <p>
     * Application starts running when its
     * {@link #start(URL, Properties, ApplicationContext)} method has been
     * called and stops when the {@link #close()} is called.
     * </p>
     * 
     * @return <code>true</code> if the application is running,
     *         <code>false</code> if not.
     */
    public boolean isRunning() {
        return applicationIsRunning;
    }

    /**
     * <p>
     * Main initializer of the application. The <code>init</code> method is
     * called by the framework when the application is started, and it should
     * perform whatever initialization operations the application needs.
     * </p>
     */
    public void init() {
        // Default implementation does nothing
    }

    /**
     * Returns an enumeration of all the names in this application.
     * 
     * <p>
     * See {@link #start(URL, Properties, ApplicationContext)} how properties
     * are defined.
     * </p>
     * 
     * @return an enumeration of all the keys in this property list, including
     *         the keys in the default property list.
     * 
     */
    public Enumeration<?> getPropertyNames() {
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
     * Gets the relative uri of the resource. This method is intended to be
     * called only be the terminal implementation.
     * 
     * This method can only be called from within the processing of a UIDL
     * request, not from a background thread.
     * 
     * @param resource
     *            the resource to get relative location.
     * @return the relative uri of the resource or null if called in a
     *         background thread
     * 
     * @deprecated this method is intended to be used by the terminal only. It
     *             may be removed or moved in the future.
     */
    @Deprecated
    public String getRelativeLocation(ApplicationResource resource) {

        // Gets the key
        final String key = resourceKeyMap.get(resource);

        // If the resource is not registered, return null
        if (key == null) {
            return null;
        }

        return context.generateApplicationResourceURL(resource, key);
    }

    /**
     * Gets the default locale for this application.
     * 
     * By default this is the preferred locale of the user using the
     * application. In most cases it is read from the browser defaults.
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
     * By default this is the preferred locale of the user using the
     * application. In most cases it is read from the browser defaults.
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
     * This allows one to get notification each time {@link #setUser(Object)} is
     * called.
     * 
     * @param listener
     *            the user change listener to add.
     */
    public void addListener(UserChangeListener listener) {
        if (userChangeListeners == null) {
            userChangeListeners = new LinkedList<UserChangeListener>();
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
     * 
     * This event is sent each time a window is removed from the application
     * with {@link com.vaadin.Application#removeWindow(Window)}.
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
     * 
     * This event is sent each time a window is attached tothe application with
     * {@link com.vaadin.Application#addWindow(Window)}.
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
     * You can customize the messages by "overriding" this method and returning
     * {@link CustomizedSystemMessages}. To "override" this method, re-implement
     * this method in your application (the class that extends
     * {@link Application}). Even though overriding static methods is not
     * possible in Java, Vaadin selects to call the static method from the
     * subclass instead of the original {@link #getSystemMessages()} if such a
     * method exists.
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
            logger.info("SocketException in CommunicationManager."
                    + " Most likely client (browser) closed socket.");
            return;
        }

        // Finds the original source of the error/exception
        Object owner = null;
        if (event instanceof VariableOwner.ErrorEvent) {
            owner = ((VariableOwner.ErrorEvent) event).getVariableOwner();
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
        logger.log(Level.SEVERE, "Terminal error:", t);
    }

    /**
     * Gets the application context.
     * <p>
     * The application context is the environment where the application is
     * running in. The actual implementation class of may contains quite a lot
     * more functionality than defined in the {@link ApplicationContext}
     * interface.
     * </p>
     * <p>
     * By default, when you are deploying your application to a servlet
     * container, the implementation class is {@link WebApplicationContext} -
     * you can safely cast to this class and use the methods from there. When
     * you are deploying your application as a portlet, context implementation
     * is {@link PortletApplicationContext}.
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
     * Tools test results. By default this returns a string "NONVERSIONED".
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
     * The default error handler is the application itself. By overriding this,
     * you can redirect the error messages to your selected target (log for
     * example).
     * 
     * @param errorHandler
     */
    public void setErrorHandler(Terminal.ErrorListener errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Gets the {@link ConverterFactory} used to locate a suitable
     * {@link Converter} for fields in the application.
     * 
     * See {@link #setConverterFactory(ConverterFactory)} for more details
     * 
     * @return The converter factory used in the application
     */
    public ConverterFactory getConverterFactory() {
        return converterFactory;
    }

    /**
     * Sets the {@link ConverterFactory} used to locate a suitable
     * {@link Converter} for fields in the application.
     * <p>
     * The {@link ConverterFactory} is used to find a suitable converter when
     * binding data to a UI component and the data type does not match the UI
     * component type, e.g. binding a Double to a TextField (which is based on a
     * String).
     * </p>
     * <p>
     * The {@link Converter} for an individual field can be overridden using
     * {@link AbstractField#setConverter(Converter)} and for individual property
     * ids in a {@link Table} using
     * {@link Table#setConverter(Object, Converter)}.
     * </p>
     * <p>
     * The converter factory must never be set to null.
     * 
     * @param converterFactory
     *            The converter factory used in the application
     */
    public void setConverterFactory(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    /**
     * Contains the system messages used to notify the user about various
     * critical situations that can occur.
     * <p>
     * Customize by overriding the static
     * {@link Application#getSystemMessages()} and returning
     * {@link CustomizedSystemMessages}.
     * </p>
     * <p>
     * The defaults defined in this class are:
     * <ul>
     * <li><b>sessionExpiredURL</b> = null</li>
     * <li><b>sessionExpiredNotificationEnabled</b> = true</li>
     * <li><b>sessionExpiredCaption</b> = ""</li>
     * <li><b>sessionExpiredMessage</b> =
     * "Take note of any unsaved data, and <u>click here</u> to continue."</li>
     * <li><b>communicationErrorURL</b> = null</li>
     * <li><b>communicationErrorNotificationEnabled</b> = true</li>
     * <li><b>communicationErrorCaption</b> = "Communication problem"</li>
     * <li><b>communicationErrorMessage</b> =
     * "Take note of any unsaved data, and <u>click here</u> to continue."</li>
     * <li><b>internalErrorURL</b> = null</li>
     * <li><b>internalErrorNotificationEnabled</b> = true</li>
     * <li><b>internalErrorCaption</b> = "Internal error"</li>
     * <li><b>internalErrorMessage</b> = "Please notify the administrator.<br/>
     * Take note of any unsaved data, and <u>click here</u> to continue."</li>
     * <li><b>outOfSyncURL</b> = null</li>
     * <li><b>outOfSyncNotificationEnabled</b> = true</li>
     * <li><b>outOfSyncCaption</b> = "Out of sync"</li>
     * <li><b>outOfSyncMessage</b> = "Something has caused us to be out of sync
     * with the server.<br/>
     * Take note of any unsaved data, and <u>click here</u> to re-sync."</li>
     * <li><b>cookiesDisabledURL</b> = null</li>
     * <li><b>cookiesDisabledNotificationEnabled</b> = true</li>
     * <li><b>cookiesDisabledCaption</b> = "Cookies disabled"</li>
     * <li><b>cookiesDisabledMessage</b> = "This application requires cookies to
     * function.<br/>
     * Please enable cookies in your browser and <u>click here</u> to try again.
     * </li>
     * </ul>
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

        protected String authenticationErrorURL = null;
        protected boolean authenticationErrorNotificationEnabled = true;
        protected String authenticationErrorCaption = "Authentication problem";
        protected String authenticationErrorMessage = "Take note of any unsaved data, and <u>click here</u> to continue.";

        protected String internalErrorURL = null;
        protected boolean internalErrorNotificationEnabled = true;
        protected String internalErrorCaption = "Internal error";
        protected String internalErrorMessage = "Please notify the administrator.<br/>Take note of any unsaved data, and <u>click here</u> to continue.";

        protected String outOfSyncURL = null;
        protected boolean outOfSyncNotificationEnabled = true;
        protected String outOfSyncCaption = "Out of sync";
        protected String outOfSyncMessage = "Something has caused us to be out of sync with the server.<br/>Take note of any unsaved data, and <u>click here</u> to re-sync.";

        protected String cookiesDisabledURL = null;
        protected boolean cookiesDisabledNotificationEnabled = true;
        protected String cookiesDisabledCaption = "Cookies disabled";
        protected String cookiesDisabledMessage = "This application requires cookies to function.<br/>Please enable cookies in your browser and <u>click here</u> to try again.";

        /**
         * Use {@link CustomizedSystemMessages} to customize
         */
        private SystemMessages() {

        }

        /**
         * @return null to indicate that the application will be restarted after
         *         session expired message has been shown.
         */
        public String getSessionExpiredURL() {
            return sessionExpiredURL;
        }

        /**
         * @return true to show session expiration message.
         */
        public boolean isSessionExpiredNotificationEnabled() {
            return sessionExpiredNotificationEnabled;
        }

        /**
         * @return "" to show no caption.
         */
        public String getSessionExpiredCaption() {
            return (sessionExpiredNotificationEnabled ? sessionExpiredCaption
                    : null);
        }

        /**
         * @return 
         *         "Take note of any unsaved data, and <u>click here</u> to continue."
         */
        public String getSessionExpiredMessage() {
            return (sessionExpiredNotificationEnabled ? sessionExpiredMessage
                    : null);
        }

        /**
         * @return null to reload the application after communication error
         *         message.
         */
        public String getCommunicationErrorURL() {
            return communicationErrorURL;
        }

        /**
         * @return true to show the communication error message.
         */
        public boolean isCommunicationErrorNotificationEnabled() {
            return communicationErrorNotificationEnabled;
        }

        /**
         * @return "Communication problem"
         */
        public String getCommunicationErrorCaption() {
            return (communicationErrorNotificationEnabled ? communicationErrorCaption
                    : null);
        }

        /**
         * @return 
         *         "Take note of any unsaved data, and <u>click here</u> to continue."
         */
        public String getCommunicationErrorMessage() {
            return (communicationErrorNotificationEnabled ? communicationErrorMessage
                    : null);
        }

        /**
         * @return null to reload the application after authentication error
         *         message.
         */
        public String getAuthenticationErrorURL() {
            return authenticationErrorURL;
        }

        /**
         * @return true to show the authentication error message.
         */
        public boolean isAuthenticationErrorNotificationEnabled() {
            return authenticationErrorNotificationEnabled;
        }

        /**
         * @return "Authentication problem"
         */
        public String getAuthenticationErrorCaption() {
            return (authenticationErrorNotificationEnabled ? authenticationErrorCaption
                    : null);
        }

        /**
         * @return 
         *         "Take note of any unsaved data, and <u>click here</u> to continue."
         */
        public String getAuthenticationErrorMessage() {
            return (authenticationErrorNotificationEnabled ? authenticationErrorMessage
                    : null);
        }

        /**
         * @return null to reload the current URL after internal error message
         *         has been shown.
         */
        public String getInternalErrorURL() {
            return internalErrorURL;
        }

        /**
         * @return true to enable showing of internal error message.
         */
        public boolean isInternalErrorNotificationEnabled() {
            return internalErrorNotificationEnabled;
        }

        /**
         * @return "Internal error"
         */
        public String getInternalErrorCaption() {
            return (internalErrorNotificationEnabled ? internalErrorCaption
                    : null);
        }

        /**
         * @return "Please notify the administrator.<br/>
         *         Take note of any unsaved data, and <u>click here</u> to
         *         continue."
         */
        public String getInternalErrorMessage() {
            return (internalErrorNotificationEnabled ? internalErrorMessage
                    : null);
        }

        /**
         * @return null to reload the application after out of sync message.
         */
        public String getOutOfSyncURL() {
            return outOfSyncURL;
        }

        /**
         * @return true to enable showing out of sync message
         */
        public boolean isOutOfSyncNotificationEnabled() {
            return outOfSyncNotificationEnabled;
        }

        /**
         * @return "Out of sync"
         */
        public String getOutOfSyncCaption() {
            return (outOfSyncNotificationEnabled ? outOfSyncCaption : null);
        }

        /**
         * @return "Something has caused us to be out of sync with the server.<br/>
         *         Take note of any unsaved data, and <u>click here</u> to
         *         re-sync."
         */
        public String getOutOfSyncMessage() {
            return (outOfSyncNotificationEnabled ? outOfSyncMessage : null);
        }

        /**
         * Returns the URL the user should be redirected to after dismissing the
         * "you have to enable your cookies" message. Typically null.
         * 
         * @return A URL the user should be redirected to after dismissing the
         *         message or null to reload the current URL.
         */
        public String getCookiesDisabledURL() {
            return cookiesDisabledURL;
        }

        /**
         * Determines if "cookies disabled" messages should be shown to the end
         * user or not. If the notification is disabled the user will be
         * immediately redirected to the URL returned by
         * {@link #getCookiesDisabledURL()}.
         * 
         * @return true to show "cookies disabled" messages to the end user,
         *         false to redirect to the given URL directly
         */
        public boolean isCookiesDisabledNotificationEnabled() {
            return cookiesDisabledNotificationEnabled;
        }

        /**
         * Returns the caption of the message shown to the user when cookies are
         * disabled in the browser.
         * 
         * @return The caption of the "cookies disabled" message
         */
        public String getCookiesDisabledCaption() {
            return (cookiesDisabledNotificationEnabled ? cookiesDisabledCaption
                    : null);
        }

        /**
         * Returns the message shown to the user when cookies are disabled in
         * the browser.
         * 
         * @return The "cookies disabled" message
         */
        public String getCookiesDisabledMessage() {
            return (cookiesDisabledNotificationEnabled ? cookiesDisabledMessage
                    : null);
        }

    }

    /**
     * Contains the system messages used to notify the user about various
     * critical situations that can occur.
     * <p>
     * Vaadin gets the SystemMessages from your application by calling a static
     * getSystemMessages() method. By default the
     * Application.getSystemMessages() is used. You can customize this by
     * defining a static MyApplication.getSystemMessages() and returning
     * CustomizedSystemMessages. Note that getSystemMessages() is static -
     * changing the system messages will by default change the message for all
     * users of the application.
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
         * Sets the URL to go to when there is a authentication error.
         * 
         * @param authenticationErrorURL
         *            the URL to go to, or null to reload current
         */
        public void setAuthenticationErrorURL(String authenticationErrorURL) {
            this.authenticationErrorURL = authenticationErrorURL;
        }

        /**
         * Enables or disables the notification. If disabled, the set URL (or
         * current) is loaded directly.
         * 
         * @param authenticationErrorNotificationEnabled
         *            true = enabled, false = disabled
         */
        public void setAuthenticationErrorNotificationEnabled(
                boolean authenticationErrorNotificationEnabled) {
            this.authenticationErrorNotificationEnabled = authenticationErrorNotificationEnabled;
        }

        /**
         * Sets the caption of the notification. Set to null for no caption. If
         * both caption and message is null, the notification is disabled;
         * 
         * @param authenticationErrorCaption
         *            the caption
         */
        public void setAuthenticationErrorCaption(
                String authenticationErrorCaption) {
            this.authenticationErrorCaption = authenticationErrorCaption;
        }

        /**
         * Sets the message of the notification. Set to null for no message. If
         * both caption and message is null, the notification is disabled;
         * 
         * @param authenticationErrorMessage
         *            the message
         */
        public void setAuthenticationErrorMessage(
                String authenticationErrorMessage) {
            this.authenticationErrorMessage = authenticationErrorMessage;
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

        /**
         * Sets the URL to redirect to when the browser has cookies disabled.
         * 
         * @param cookiesDisabledURL
         *            the URL to redirect to, or null to reload the current URL
         */
        public void setCookiesDisabledURL(String cookiesDisabledURL) {
            this.cookiesDisabledURL = cookiesDisabledURL;
        }

        /**
         * Enables or disables the notification for "cookies disabled" messages.
         * If disabled, the URL returned by {@link #getCookiesDisabledURL()} is
         * loaded directly.
         * 
         * @param cookiesDisabledNotificationEnabled
         *            true to enable "cookies disabled" messages, false
         *            otherwise
         */
        public void setCookiesDisabledNotificationEnabled(
                boolean cookiesDisabledNotificationEnabled) {
            this.cookiesDisabledNotificationEnabled = cookiesDisabledNotificationEnabled;
        }

        /**
         * Sets the caption of the "cookies disabled" notification. Set to null
         * for no caption. If both caption and message is null, the notification
         * is disabled.
         * 
         * @param cookiesDisabledCaption
         *            the caption for the "cookies disabled" notification
         */
        public void setCookiesDisabledCaption(String cookiesDisabledCaption) {
            this.cookiesDisabledCaption = cookiesDisabledCaption;
        }

        /**
         * Sets the message of the "cookies disabled" notification. Set to null
         * for no message. If both caption and message is null, the notification
         * is disabled.
         * 
         * @param cookiesDisabledMessage
         *            the message for the "cookies disabled" notification
         */
        public void setCookiesDisabledMessage(String cookiesDisabledMessage) {
            this.cookiesDisabledMessage = cookiesDisabledMessage;
        }

    }

    /**
     * Application error is an error message defined on the application level.
     * 
     * When an error occurs on the application level, this error message type
     * should be used. This indicates that the problem is caused by the
     * application - not by the user.
     */
    public class ApplicationError implements Terminal.ErrorEvent {
        private final Throwable throwable;

        public ApplicationError(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }

    }

    /**
     * Gets a root for a request for which no root is already known. This method
     * is called when the framework processes a request that does not originate
     * from an existing root instance. This typically happens when a host page
     * is requested.
     * 
     * <p>
     * Subclasses of Application may override this method to provide custom
     * logic for choosing how to create a suitable root or for picking an
     * already created root. If an existing root is picked, care should be taken
     * to avoid keeping the same root open in multiple browser windows, as that
     * will cause the states to go out of sync.
     * </p>
     * 
     * <p>
     * If {@link BrowserDetails} are required to create a Root, the
     * implementation can throw a {@link RootRequiresMoreInformationException} exception.
     * In this case, the framework will instruct the browser to send the
     * additional details, whereupon this method is invoked again with the
     * browser details present in the wrapped request. Throwing the exception if
     * the browser details are already available is not supported.
     * </p>
     * 
     * <p>
     * The default implementation in {@link Application} creates a new instance
     * of the Root class returned by {@link #getRootClassName(WrappedRequest},
     * which in turn uses the {@value #ROOT_PARAMETER} parameter from web.xml.
     * </p>
     * 
     * @param request
     *            the wrapped request for which a root is needed
     * @return a root instance to use for the request
     * @throws RootRequiresMoreInformationException
     *             may be thrown by an implementation to indicate that
     *             {@link BrowserDetails} are required to create a root
     * 
     * @see #getRootClassName(WrappedRequest)
     * @see Root
     * @see RootRequiresMoreInformationException
     * @see WrappedRequest#getBrowserDetails()
     * 
     * @since 7.0
     */
    protected Root getRoot(WrappedRequest request)
            throws RootRequiresMoreInformationException {
        String rootClassName = getRootClassName(request);
        try {
            Class<? extends Root> rootClass = Class.forName(rootClassName)
                    .asSubclass(Root.class);
            try {
                Root root = rootClass.newInstance();
                return root;
            } catch (Exception e) {
                throw new RuntimeException("Could not instantiate root class "
                        + rootClassName, e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load root class "
                    + rootClassName, e);
        }
    }

    /**
     * Provides the name of the <code>Root</code> class that should be used for
     * a request. The class must have an accessible no-args constructor.
     * <p>
     * The default implementation uses the {@value #ROOT_PARAMETER} parameter
     * from web.xml.
     * </p>
     * <p>
     * This method is mainly used by the default implementation of
     * {@link #getRoot(WrappedRequest)}. If you override that method with your
     * own functionality, the results of this method might not be used.
     * </p>
     * 
     * @param request
     *            the request for which a new root is required
     * @return the name of the root class to use
     * 
     * @since 7.0
     */
    protected String getRootClassName(WrappedRequest request) {
        Object rootClassNameObj = properties.get(ROOT_PARAMETER);
        if (rootClassNameObj instanceof String) {
            return (String) rootClassNameObj;
        } else {
            throw new RuntimeException("No " + ROOT_PARAMETER
                    + " defined in web.xml");
        }
    }

    /**
     * Finds the theme to use for a specific root. If no specific theme is
     * required, <code>null</code> is returned.
     * 
     * TODO Tell what the default implementation does once it does something.
     * 
     * @param root
     *            the root to get a theme for
     * @return the name of the theme, or <code>null</code> if the default theme
     *         should be used
     * 
     * @since 7.0
     */
    public String getThemeForRoot(Root root) {
        Theme rootTheme = getAnnotationFor(root.getClass(), Theme.class);
        if (rootTheme != null) {
            return rootTheme.value();
        } else {
            return null;
        }
    }

    /**
     * Finds the widgetset to use for a specific root. If no specific widgetset
     * is required, <code>null</code> is returned.
     * 
     * TODO Tell what the default implementation does once it does something.
     * 
     * @param root
     *            the root to get a widgetset for
     * @return the name of the widgetset, or <code>null</code> if the default
     *         widgetset should be used
     * 
     * @since 7.0
     */
    public String getWidgetsetForRoot(Root root) {
        Widgetset rootWidgetset = getAnnotationFor(root.getClass(),
                Widgetset.class);
        if (rootWidgetset != null) {
            return rootWidgetset.value();
        } else {
            return null;
        }
    }

    /**
     * Helper to get an annotation for a class. If the annotation is not present
     * on the target class, it's superclasses and implemented interfaces are
     * also searched for the annotation.
     * 
     * @param type
     *            the target class from which the annotation should be found
     * @param annotationType
     *            the annotation type to look for
     * @return an annotation of the given type, or <code>null</code> if the
     *         annotation is not present on the class
     */
    private static <T extends Annotation> T getAnnotationFor(Class<?> type,
            Class<T> annotationType) {
        // Find from the class hierarchy
        Class<?> currentType = type;
        while (currentType != Object.class) {
            T annotation = currentType.getAnnotation(annotationType);
            if (annotation != null) {
                return annotation;
            } else {
                currentType = currentType.getSuperclass();
            }
        }

        // Find from an implemented interface
        for (Class<?> iface : type.getInterfaces()) {
            T annotation = iface.getAnnotation(annotationType);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    /**
     * Handles a request by passing it to each registered {@link RequestHandler}
     * in turn until one produces a response. This method is used for requests
     * that have not been handled by any specific functionality in the terminal
     * implementation (e.g. {@link AbstractApplicationServlet}).
     * <p>
     * The request handlers are invoked in the revere order in which they were
     * added to the application until a response has been produced. This means
     * that the most recently added handler is used first and the first request
     * handler that was added to the application is invoked towards the end
     * unless any previous handler has already produced a response.
     * </p>
     * 
     * @param request
     *            the wrapped request to get information from
     * @param response
     *            the response to which data can be written
     * @return returns <code>true</code> if a {@link RequestHandler} has
     *         produced a response and <code>false</code> if no response has
     *         been written.
     * @throws IOException
     * 
     * @see #addRequestHandler(RequestHandler)
     * @see RequestHandler
     * 
     * @since 7.0
     */
    public boolean handleRequest(WrappedRequest request,
            WrappedResponse response) throws IOException {
        // Use a copy to avoid ConcurrentModificationException
        for (RequestHandler handler : new ArrayList<RequestHandler>(
                requestHandlers)) {
            if (handler.handleRequest(this, request, response)) {
                return true;
            }
        }
        // If not handled
        return false;
    }

    /**
     * Adds a request handler to this application. Request handlers can be added
     * to provide responses to requests that are not handled by the default
     * functionality of the framework.
     * <p>
     * Handlers are called in reverse order of addition, so the most recently
     * added handler will be called first.
     * </p>
     * 
     * @param handler
     *            the request handler to add
     * 
     * @see #handleRequest(WrappedRequest, WrappedResponse)
     * @see #removeRequestHandler(RequestHandler)
     * 
     * @since 7.0
     */
    public void addRequestHandler(RequestHandler handler) {
        requestHandlers.addFirst(handler);
    }

    /**
     * Removes a request handler from the application.
     * 
     * @param handler
     *            the request handler to remove
     * 
     * @since 7.0
     */
    public void removeRequestHandler(RequestHandler handler) {
        requestHandlers.remove(handler);
    }

    /**
     * Gets the request handlers that are registered to the application. The
     * iteration order of the returned collection is the same as the order in
     * which the request handlers will be invoked when a request is handled.
     * 
     * @return a collection of request handlers, with the iteration order
     *         according to the order they would be invoked
     * 
     * @see #handleRequest(WrappedRequest, WrappedResponse)
     * @see #addRequestHandler(RequestHandler)
     * @see #removeRequestHandler(RequestHandler)
     * 
     * @since 7.0
     */
    public Collection<RequestHandler> getRequestHandlers() {
        return Collections.unmodifiableCollection(requestHandlers);
    }

    /**
     * Find an application resource with a given key.
     * 
     * @param key
     *            The key of the resource
     * @return The application resource corresponding to the provided key, or
     *         <code>null</code> if no resource is registered for the key
     * 
     * @since 7.0
     */
    public ApplicationResource getResource(String key) {
        return keyResourceMap.get(key);
    }

    /**
     * Thread local for keeping track of currently used application instance
     * 
     * @since 7.0
     */
    private static final ThreadLocal<Application> currentApplication = new ThreadLocal<Application>();

    /**
     * Gets the currently used application. The current application is
     * automatically defined when processing requests to the server. In other
     * cases, (e.g. from background threads), the current application is not
     * automatically defined.
     * 
     * @return the current application instance if available, otherwise
     *         <code>null</code>
     * 
     * @see #setCurrentApplication(Application)
     * 
     * @since 7.0
     */
    public static Application getCurrentApplication() {
        return currentApplication.get();
    }

    /**
     * Sets the thread local for the current application. This method is used by
     * the framework to set the current application whenever a new request is
     * processed and it is cleared when the request has been processed.
     * <p>
     * The application developer can also use this method to define the current
     * application outside the normal request handling, e.g. when initiating
     * custom background threads.
     * </p>
     * 
     * @param application
     * 
     * @see #getCurrentApplication()
     * @see ThreadLocal
     * 
     * @since 7.0
     */
    public static void setCurrentApplication(Application application) {
        currentApplication.set(application);
    }

    /**
     * Check whether this application is in production mode. If an application
     * is in production mode, certain debugging facilities are not available.
     * 
     * @return the status of the production mode flag
     * 
     * @since 7.0
     */
    public boolean isProductionMode() {
        return productionMode;
    }

    /**
     * Registers a request that will lead to a root being created in a
     * subsequent request. When the initial request does not contain all the
     * information required to initialize a {@link Root}, some information from
     * the initial request is still needed when processing a subsequent request
     * containing the rest of the required information. By registering the
     * initial request, it can be combined with the subsequent request using the
     * root id returned by this method.
     * 
     * @param request
     *            the initial request from which information is required when
     *            the subsequent request is processed
     * @return the root id that should be used to associate the passed request
     *         with future requests related to the same Root
     * 
     * @see #getCombinedRequest(WrappedRequest)
     * @see #getRoot(WrappedRequest)
     * 
     * @since 7.0
     */
    public int registerPendingRoot(WrappedRequest request) {
        int rootId = nextRootId++;
        pendingRoots.put(Integer.valueOf(rootId), new PendingRootRequest(
                request));
        return rootId;
    }

    /**
     * Gets a request containing some aspects from the original request and some
     * aspects from the current request. This is used during the two phase
     * initialization of Roots with the first request registered using
     * {@link #registerPendingRoot(WrappedRequest)}
     * 
     * @param request
     *            the second request, should be sent from the bootstrap
     *            javascript
     * @return a request containing some aspects of the initial request and some
     *         aspects from the current request
     * 
     * @see #registerPendingRoot(WrappedRequest)
     * 
     * @since 7.0
     */
    public CombinedRequest getCombinedRequest(WrappedRequest request) {
        PendingRootRequest pendingRootRequest = pendingRoots
                .get(getRootId(request));
        if (pendingRootRequest == null) {
            return null;
        } else {
            return pendingRootRequest.getCombinedRequest(request);
        }
    }

    /**
     * Finds the {@link Root} to which a particular request belongs. If the
     * request originates from an existing Root, that root is returned. In other
     * cases, the method attempts to create and initialize a new root and might
     * throw a {@link RootRequiresMoreInformationException} if all required information
     * is not available.
     * <p>
     * Please note that this method can also return a newly created
     * <code>Root</code> which has not yet been initialized. You can use
     * {@link #isRootInitPending(int)} with the root's id (
     * {@link Root#getRootId()} to check whether the initialization is still
     * pending.
     * </p>
     * 
     * @param request
     *            the request for which a root is desired
     * @return a root belonging to the request
     * @throws RootRequiresMoreInformationException
     *             if no existing root could be found and creating a new root
     *             requires additional information from the browser
     * 
     * @see #getRoot(WrappedRequest)
     * @see RootRequiresMoreInformationException
     * 
     * @since 7.0
     */
    public Root getRootForRequest(WrappedRequest request)
            throws RootRequiresMoreInformationException {
        Root root = Root.getCurrentRoot();
        if (root != null) {
            return root;
        }
        Integer rootId = getRootId(request);

        synchronized (this) {
            RootPreserveStrategy rootPreserveStrategy = getRootPreserveStrategy();

            BrowserDetails browserDetails = request.getBrowserDetails();

            if (browserDetails != null) {
                // Don't wait for a second request any more
                pendingRoots.remove(rootId);
            }

            root = roots.get(rootId);

            if (root == null && rootPreserveStrategy != null) {
                // Check for a known root
                Integer retainedRootId = rootPreserveStrategy
                        .getPreservedRootForRequest(request);
                if (retainedRootId != null) {
                    rootId = retainedRootId;
                    root = roots.get(rootId);
                }
            }

            if (root == null) {
                // Throws exception if root can not yet be created
                root = getRoot(request);

                // Initialize some fields for a newly created root
                if (root.getApplication() == null) {
                    root.setApplication(this);
                }
                if (root.getRootId() < 0) {

                    if (rootId == null) {
                        // Get the next id if none defined
                        rootId = Integer.valueOf(nextRootId++);
                    }
                    root.setRootId(rootId.intValue());
                    roots.put(rootId, root);
                }
            }

            if (!initedRoots.contains(rootId)) {
                boolean initRequiresBrowserDetails = (rootPreserveStrategy != null && rootPreserveStrategy
                        .reqsterRequiresBrowserDetails())
                        || root.getClass().isAnnotationPresent(
                                RootInitRequiresBrowserDetails.class);
                if (initRequiresBrowserDetails && browserDetails == null) {
                    pendingRoots.put(rootId, new PendingRootRequest(request));
                } else {
                    if (rootPreserveStrategy != null) {
                        // Remember this root
                        rootPreserveStrategy.registerRoot(root, request);
                    }
                    root.doInit(request);

                    // Remember that this root has been initialized
                    initedRoots.add(rootId);
                }
            }
        } // end synchronized block

        Root.setCurrentRoot(root);
        return root;
    }

    /**
     * Internal helper to finds the root id for a request.
     * 
     * @param request
     *            the request to get the root id for
     * @return a root id, or <code>null</code> if no root id is defined
     * 
     * @since 7.0
     */
    private static Integer getRootId(WrappedRequest request) {
        if (request instanceof CombinedRequest) {
            // Combined requests has the rootid parameter in the second request
            CombinedRequest combinedRequest = (CombinedRequest) request;
            request = combinedRequest.getSecondRequest();
        }
        String rootIdString = request
                .getParameter(ApplicationConnection.ROOT_ID_PARAMETER);
        Integer rootId = rootIdString == null ? null
                : new Integer(rootIdString);
        return rootId;
    }

    /**
     * Gets the current strategy for preserving Root instances e.g. when the
     * browser window is reloaded.
     * 
     * @return the current root preserver strategy, or <code>null</code> if
     *         roots should not be preserved
     * 
     * @see RootPreserveStrategy
     */
    public RootPreserveStrategy getRootPreserveStrategy() {
        return rootPreserveStrategy;
    }

    /**
     * Sets the current strategy for preserving Roots e.g. when the browser
     * window is reloaded.
     * 
     * @param rootPreserveStrategy
     *            the new root preserve strategy
     */
    public void setRootPreserveStrategy(
            RootPreserveStrategy rootPreserveStrategy) {
        this.rootPreserveStrategy = rootPreserveStrategy;
    }

    /**
     * Checks whether there's a pending initialization for the root with the
     * given id.
     * 
     * @param rootId
     *            root id to check for
     * @return <code>true</code> of the initialization is pending,
     *         <code>false</code> if the root id is not registered or if the
     *         root has already been initialized
     * 
     * @see #getRootForRequest(WrappedRequest)
     */
    public boolean isRootInitPending(int rootId) {
        return !initedRoots.contains(Integer.valueOf(rootId));
    }

    /**
     * Gets all the roots of this application. This includes roots that have
     * been requested but not yet initialized. Please note, that roots are not
     * automatically removed e.g. if the browser window is closed and that there
     * is no way to manually remove a root. Inactive roots will thus not be
     * released for GC until the entire application is released when the session
     * has timed out (unless there are dangling references). Improved support
     * for releasing unused roots is planned for an upcoming alpha release of
     * Vaadin 7.
     * 
     * @return a collection of roots belonging to this application
     * 
     * @since 7.0
     */
    public Collection<Root> getRoots() {
        return Collections.unmodifiableCollection(roots.values());
    }
}
