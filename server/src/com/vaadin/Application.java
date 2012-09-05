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

package com.vaadin;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.data.util.converter.DefaultConverterFactory;
import com.vaadin.event.EventRouter;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.AbstractUIProvider;
import com.vaadin.server.ApplicationConfiguration;
import com.vaadin.server.ApplicationContext;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.BootstrapResponse;
import com.vaadin.server.ChangeVariablesErrorEvent;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.CombinedRequest;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.GlobalResourceHandler;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServletApplicationContext;
import com.vaadin.server.Terminal;
import com.vaadin.server.Terminal.ErrorEvent;
import com.vaadin.server.Terminal.ErrorListener;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VariableOwner;
import com.vaadin.server.WrappedRequest;
import com.vaadin.server.WrappedRequest.BrowserDetails;
import com.vaadin.server.WrappedResponse;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.util.CurrentInstance;
import com.vaadin.util.ReflectTools;

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
 * the {@link com.vaadin.server.Terminal terminal} is used. The terminal always
 * defines a default theme.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Application implements Terminal.ErrorListener, Serializable {

    /**
     * The name of the parameter that is by default used in e.g. web.xml to
     * define the name of the default {@link UI} class.
     */
    public static final String UI_PARAMETER = "UI";

    private static final Method BOOTSTRAP_FRAGMENT_METHOD = ReflectTools
            .findMethod(BootstrapListener.class, "modifyBootstrapFragment",
                    BootstrapFragmentResponse.class);
    private static final Method BOOTSTRAP_PAGE_METHOD = ReflectTools
            .findMethod(BootstrapListener.class, "modifyBootstrapPage",
                    BootstrapPageResponse.class);

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
    public static abstract class LegacyApplication extends AbstractUIProvider
            implements ErrorListener {
        /**
         * Ignore initial / and then get everything up to the next /
         */
        private static final Pattern WINDOW_NAME_PATTERN = Pattern
                .compile("^/?([^/]+).*");

        private UI.LegacyWindow mainWindow;
        private String theme;

        private Map<String, UI.LegacyWindow> legacyUINames = new HashMap<String, UI.LegacyWindow>();

        /**
         * Sets the main window of this application. Setting window as a main
         * window of this application also adds the window to this application.
         * 
         * @param mainWindow
         *            the UI to set as the default window
         */
        public void setMainWindow(UI.LegacyWindow mainWindow) {
            if (this.mainWindow != null) {
                throw new IllegalStateException(
                        "mainWindow has already been set");
            }
            if (mainWindow.getApplication() == null) {
                mainWindow.setApplication(Application.getCurrent());
            } else if (mainWindow.getApplication() != Application.getCurrent()) {
                throw new IllegalStateException(
                        "mainWindow is attached to another application");
            }
            if (UI.getCurrent() == null) {
                // Assume setting a main window from Application.init if there's
                // no current UI -> set the main window as the current UI
                UI.setCurrent(mainWindow);
            }
            this.mainWindow = mainWindow;
        }

        public void doInit() {
            Application.getCurrent().setErrorHandler(this);
            init();
        }

        protected abstract void init();

        @Override
        public Class<? extends UI> getUIClass(Application application,
                WrappedRequest request) {
            UI uiInstance = getUIInstance(request);
            if (uiInstance != null) {
                return uiInstance.getClass();
            }
            return null;
        }

        @Override
        public UI createInstance(Application application,
                Class<? extends UI> type, WrappedRequest request) {
            return getUIInstance(request);
        }

        @Override
        public String getThemeForUI(WrappedRequest request,
                Class<? extends UI> uiClass) {
            return theme;
        }

        @Override
        public String getPageTitleForUI(WrappedRequest request,
                Class<? extends UI> uiClass) {
            UI uiInstance = getUIInstance(request);
            if (uiInstance != null) {
                return uiInstance.getCaption();
            } else {
                return super.getPageTitleForUI(request, uiClass);
            }
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
         * @return the UI used as the default window
         */
        public UI.LegacyWindow getMainWindow() {
            return mainWindow;
        }

        private UI getUIInstance(WrappedRequest request) {
            String pathInfo = request.getRequestPathInfo();
            String name = null;
            if (pathInfo != null && pathInfo.length() > 0) {
                Matcher matcher = WINDOW_NAME_PATTERN.matcher(pathInfo);
                if (matcher.matches()) {
                    // Skip the initial slash
                    name = matcher.group(1);
                }
            }
            UI.LegacyWindow window = getWindow(name);
            if (window != null) {
                return window;
            }
            return mainWindow;
        }

        /**
         * This implementation simulates the way of finding a window for a
         * request by extracting a window name from the requested path and
         * passes that name to {@link #getWindow(String)}.
         * <p>
         * {@inheritDoc}
         */
        @Override
        public UI getExistingUI(WrappedRequest request) {
            UI uiInstance = getUIInstance(request);
            if (uiInstance.getUIId() == -1) {
                // Not initialized -> Let go through createUIInstance to make it
                // initialized
                return null;
            } else {
                UI.setCurrent(uiInstance);
                return uiInstance;
            }
        }

        /**
         * Sets the application's theme.
         * <p>
         * Note that this theme can be overridden for a specific UI with
         * {@link Application#getThemeForUI(UI)}. Setting theme to be
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
         * theme used by all the uIs for which a theme is not explicitly
         * defined. If the application theme is not explicitly set,
         * <code>null</code> is returned.
         * 
         * @return the name of the application's theme.
         */
        public String getTheme() {
            return theme;
        }

        /**
         * <p>
         * Gets a UI by name. Returns <code>null</code> if the application is
         * not running or it does not contain a window corresponding to the
         * name.
         * </p>
         * 
         * @param name
         *            the name of the requested window
         * @return a UI corresponding to the name, or <code>null</code> to use
         *         the default window
         */
        public UI.LegacyWindow getWindow(String name) {
            return legacyUINames.get(name);
        }

        /**
         * Counter to get unique names for windows with no explicit name
         */
        private int namelessUIIndex = 0;

        /**
         * Adds a new browser level window to this application. Please note that
         * UI doesn't have a name that is used in the URL - to add a named
         * window you should instead use {@link #addWindow(UI, String)}
         * 
         * @param uI
         *            the UI window to add to the application
         * @return returns the name that has been assigned to the window
         * 
         * @see #addWindow(UI, String)
         */
        public void addWindow(UI.LegacyWindow uI) {
            if (uI.getName() == null) {
                String name = Integer.toString(namelessUIIndex++);
                uI.setName(name);
            }

            legacyUINames.put(uI.getName(), uI);
            uI.setApplication(Application.getCurrent());
        }

        /**
         * Removes the specified window from the application. This also removes
         * all name mappings for the window (see {@link #addWindow(UI, String)
         * and #getWindowName(UI)}.
         * 
         * <p>
         * Note that removing window from the application does not close the
         * browser window - the window is only removed from the server-side.
         * </p>
         * 
         * @param uI
         *            the UI to remove
         */
        public void removeWindow(UI.LegacyWindow uI) {
            for (Entry<String, UI.LegacyWindow> entry : legacyUINames
                    .entrySet()) {
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
        public Collection<UI.LegacyWindow> getWindows() {
            return Collections.unmodifiableCollection(legacyUINames.values());
        }

        @Override
        public void terminalError(ErrorEvent event) {
            Application.getCurrent().terminalError(event);
        }

        public ApplicationContext getContext() {
            return Application.getCurrent().getContext();
        }

        protected void close() {
            Application.getCurrent().close();
        }

        public boolean isRunning() {
            return Application.getCurrent().isRunning();
        }

        public URL getURL() {
            return Application.getCurrent().getURL();
        }
    }

    /**
     * An event sent to {@link #start(ApplicationStartEvent)} when a new
     * Application is being started.
     * 
     * @since 7.0
     */
    public static class ApplicationStartEvent implements Serializable {
        private final URL applicationUrl;

        private final ApplicationConfiguration configuration;

        private final ApplicationContext context;

        /**
         * @param applicationUrl
         *            the URL the application should respond to.
         * @param configuration
         *            the application configuration for the application.
         * @param context
         *            the context application will be running in.
         */
        public ApplicationStartEvent(URL applicationUrl,
                ApplicationConfiguration configuration,
                ApplicationContext context) {
            this.applicationUrl = applicationUrl;
            this.configuration = configuration;
            this.context = context;
        }

        /**
         * Gets the URL the application should respond to.
         * 
         * @return the URL the application should respond to or
         *         <code>null</code> if the URL is not defined.
         * 
         * @see Application#getURL()
         */
        public URL getApplicationUrl() {
            return applicationUrl;
        }

        /**
         * Returns the application configuration used by this application.
         * 
         * @return the deployment configuration.
         */
        public ApplicationConfiguration getConfiguration() {
            return configuration;
        }

        /**
         * Gets the context application will be running in.
         * 
         * @return the context application will be running in.
         * 
         * @see Application#getContext()
         */
        public ApplicationContext getContext() {
            return context;
        }
    }

    private final static Logger logger = Logger.getLogger(Application.class
            .getName());

    /**
     * Application context the application is running in.
     */
    private ApplicationContext context;

    /**
     * Configuration for the application.
     */
    private ApplicationConfiguration configuration;

    /**
     * The application's URL.
     */
    private URL applicationUrl;

    /**
     * Application status.
     */
    private volatile boolean applicationIsRunning = false;

    /**
     * Default locale of the application.
     */
    private Locale locale;

    /**
     * URL where the user is redirected to on application close, or null if
     * application is just closed without redirection.
     */
    private String logoutURL = null;

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

    private int nextUIId = 0;
    private Map<Integer, UI> uIs = new HashMap<Integer, UI>();

    private final Map<String, Integer> retainOnRefreshUIs = new HashMap<String, Integer>();

    private final EventRouter eventRouter = new EventRouter();

    private List<UIProvider> uiProviders = new LinkedList<UIProvider>();

    private GlobalResourceHandler globalResourceHandler;

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
     * <p>
     * In effect this will cause the application stop returning any windows when
     * asked. When the application is closed, close events are fired for its
     * UIs, its state is removed from the session, and the browser window is
     * redirected to the application logout url set with
     * {@link #setLogoutURL(String)}. If the logout url has not been set, the
     * browser window is reloaded and the application is restarted.
     */
    public void close() {
        applicationIsRunning = false;
        for (UI ui : getUIs()) {
            ui.fireCloseEvent();
        }
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
     * @param event
     *            the application start event containing details required for
     *            starting the application.
     * 
     */
    public void start(ApplicationStartEvent event) {
        applicationUrl = event.getApplicationUrl();
        configuration = event.getConfiguration();
        context = event.getContext();
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
     * Gets the configuration for this application
     * 
     * @return the application configuration
     */
    public ApplicationConfiguration getConfiguration() {
        return configuration;
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
     * Window detach event.
     * 
     * This event is sent each time a window is removed from the application
     * with {@link com.vaadin.Application#removeWindow(Window)}.
     */
    public static class WindowDetachEvent extends EventObject {

        private final Window window;

        /**
         * Creates a event.
         * 
         * @param application
         *            the application to which the detached window belonged.
         * @param window
         *            the Detached window.
         */
        public WindowDetachEvent(Application application, Window window) {
            super(application);
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
    public static class WindowAttachEvent extends EventObject {

        private final Window window;

        /**
         * Creates a event.
         * 
         * @param application
         *            the application to which the detached window belonged.
         * @param window
         *            the Attached window.
         */
        public WindowAttachEvent(Application application, Window window) {
            super(application);
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
     * @see com.vaadin.server.Terminal.ErrorListener#terminalError(com.vaadin.server.Terminal.ErrorEvent)
     */

    @Override
    public void terminalError(Terminal.ErrorEvent event) {
        final Throwable t = event.getThrowable();
        if (t instanceof SocketException) {
            // Most likely client browser closed socket
            getLogger().info(
                    "SocketException in CommunicationManager."
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
            ((AbstractComponent) owner).setComponentError(AbstractErrorMessage
                    .getErrorMessageForException(t));
        }

        // also print the error on console
        getLogger().log(Level.SEVERE, "Terminal error:", t);
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
     * container, the implementation class is {@link ServletApplicationContext}
     * - you can safely cast to this class and use the methods from there. When
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

        @Override
        public Throwable getThrowable() {
            return throwable;
        }

    }

    /**
     * Gets the UI class for a request for which no UI is already known. This
     * method is called when the framework processes a request that does not
     * originate from an existing UI instance. This typically happens when a
     * host page is requested.
     * <p>
     * Subclasses of Application may override this method to provide custom
     * logic for choosing what kind of UI to use.
     * <p>
     * The default implementation in {@link Application} uses the
     * {@value #UI_PARAMETER} parameter from web.xml for finding the name of the
     * UI class. If {@link DeploymentConfiguration#getClassLoader()} does not
     * return <code>null</code>, the returned {@link ClassLoader} is used for
     * loading the UI class. Otherwise the {@link ClassLoader} used to load this
     * class is used.
     * 
     * </p>
     * 
     * @param request
     *            the wrapped request for which a UI is needed
     * @return a UI instance to use for the request
     * 
     * @see UI
     * @see WrappedRequest#getBrowserDetails()
     * 
     * @since 7.0
     */
    public Class<? extends UI> getUIClass(WrappedRequest request) {
        UIProvider uiProvider = getUiProvider(request, null);
        return uiProvider.getUIClass(this, request);
    }

    /**
     * Creates an UI instance for a request for which no UI is already known.
     * This method is called when the framework processes a request that does
     * not originate from an existing UI instance. This typically happens when a
     * host page is requested.
     * <p>
     * Subclasses of Application may override this method to provide custom
     * logic for choosing how to create a suitable UI or for picking an already
     * created UI. If an existing UI is picked, care should be taken to avoid
     * keeping the same UI open in multiple browser windows, as that will cause
     * the states to go out of sync.
     * </p>
     * 
     * @param request
     * @param uiClass
     * @return
     */
    protected <T extends UI> T createUIInstance(WrappedRequest request,
            Class<T> uiClass) {
        UIProvider uiProvider = getUiProvider(request, uiClass);
        return uiClass.cast(uiProvider.createInstance(this, uiClass, request));
    }

    /**
     * Gets the {@link UIProvider} that should be used for a request. The
     * selection can further be restricted by also requiring the UI provider to
     * support a specific UI class.
     * 
     * @see UIProvider
     * @see #addUIProvider(UIProvider)
     * 
     * @param request
     *            the request for which to get an UI provider
     * @param uiClass
     *            the UI class for which a provider is required, or
     *            <code>null</code> to use the first UI provider supporting the
     *            request.
     * @return an UI provider supporting the request (and the UI class if
     *         provided).
     * 
     * @since 7.0.0
     */
    public UIProvider getUiProvider(WrappedRequest request, Class<?> uiClass) {
        UIProvider provider = (UIProvider) request
                .getAttribute(UIProvider.class.getName());
        if (provider != null) {
            // Cached provider found, verify that it's a sensible selection
            Class<? extends UI> providerClass = provider.getUIClass(this,
                    request);
            if (uiClass == null && providerClass != null) {
                // Use it if it gives any answer if no specific class is
                // required
                return provider;
            } else if (uiClass == providerClass) {
                // Use it if it gives the expected UI class
                return provider;
            } else {
                // Don't keep it cached if it doesn't match the expectations
                request.setAttribute(UIProvider.class.getName(), null);
            }
        }

        // Iterate all current providers if no matching cached provider found
        provider = doGetUiProvider(request, uiClass);

        // Cache the found provider
        request.setAttribute(UIProvider.class.getName(), provider);

        return provider;
    }

    private UIProvider doGetUiProvider(WrappedRequest request, Class<?> uiClass) {
        int providersSize = uiProviders.size();
        if (providersSize == 0) {
            throw new IllegalStateException("There are no UI providers");
        }

        for (int i = providersSize - 1; i >= 0; i--) {
            UIProvider provider = uiProviders.get(i);

            Class<? extends UI> providerClass = provider.getUIClass(this,
                    request);
            // If we found something
            if (providerClass != null) {
                if (uiClass == null) {
                    // Not looking for anything particular -> anything is ok
                    return provider;
                } else if (providerClass == uiClass) {
                    // Looking for a specific provider -> only use if matching
                    return provider;
                } else {
                    getLogger().warning(
                            "Mismatching UI classes. Expected " + uiClass
                                    + " but got " + providerClass + " from "
                                    + provider);
                    // Continue looking
                }
            }
        }

        throw new RuntimeException("No UI provider found for request");
    }

    /**
     * Handles a request by passing it to each registered {@link RequestHandler}
     * in turn until one produces a response. This method is used for requests
     * that have not been handled by any specific functionality in the terminal
     * implementation (e.g. {@link VaadinServlet}).
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
     * Gets the currently used application. The current application is
     * automatically defined when processing requests to the server. In other
     * cases, (e.g. from background threads), the current application is not
     * automatically defined.
     * 
     * @return the current application instance if available, otherwise
     *         <code>null</code>
     * 
     * @see #setCurrent(Application)
     * 
     * @since 7.0
     */
    public static Application getCurrent() {
        return CurrentInstance.get(Application.class);
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
     * @see #getCurrent()
     * @see ThreadLocal
     * 
     * @since 7.0
     */
    public static void setCurrent(Application application) {
        CurrentInstance.setInheritable(Application.class, application);
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
        return configuration.isProductionMode();
    }

    public void addUIProvider(UIProvider uIProvider) {
        uiProviders.add(uIProvider);
    }

    public void removeUIProvider(UIProvider uIProvider) {
        uiProviders.remove(uIProvider);
    }

    /**
     * Finds the {@link UI} to which a particular request belongs. If the
     * request originates from an existing UI, that UI is returned. In other
     * cases, the method attempts to create and initialize a new UI and might
     * throw a {@link UIRequiresMoreInformationException} if all required
     * information is not available.
     * <p>
     * Please note that this method can also return a newly created
     * <code>UI</code> which has not yet been initialized. You can use
     * {@link #isUIInitPending(int)} with the UI's id ( {@link UI#getUIId()} to
     * check whether the initialization is still pending.
     * </p>
     * 
     * @param request
     *            the request for which a UI is desired
     * @return a UI belonging to the request
     * 
     * @see #createUI(WrappedRequest)
     * 
     * @since 7.0
     */
    public UI getUIForRequest(WrappedRequest request) {
        UI uI = UI.getCurrent();
        if (uI != null) {
            return uI;
        }
        Integer uiId = getUIId(request);

        synchronized (this) {
            uI = uIs.get(uiId);

            if (uI == null) {
                uI = findExistingUi(request);
            }

        } // end synchronized block

        UI.setCurrent(uI);

        return uI;
    }

    private UI findExistingUi(WrappedRequest request) {
        // Check if some UI provider has an existing UI available
        for (int i = uiProviders.size() - 1; i >= 0; i--) {
            UIProvider provider = uiProviders.get(i);
            UI existingUi = provider.getExistingUI(request);
            if (existingUi != null) {
                return existingUi;
            }
        }

        BrowserDetails browserDetails = request.getBrowserDetails();
        boolean hasBrowserDetails = browserDetails != null
                && browserDetails.getUriFragment() != null;

        if (hasBrowserDetails && !retainOnRefreshUIs.isEmpty()) {
            // Check for a known UI

            @SuppressWarnings("null")
            String windowName = browserDetails.getWindowName();
            Integer retainedUIId = retainOnRefreshUIs.get(windowName);

            if (retainedUIId != null) {
                Class<? extends UI> expectedUIClass = getUIClass(request);
                UI retainedUI = uIs.get(retainedUIId);
                // We've had the same UI instance in a window with this
                // name, but should we still use it?
                if (retainedUI.getClass() == expectedUIClass) {
                    return retainedUI;
                } else {
                    getLogger().info(
                            "Not using retained UI in " + windowName
                                    + " because retained UI was of type "
                                    + retainedUIId.getClass() + " but "
                                    + expectedUIClass
                                    + " is expected for the request.");
                }
            }
        }

        return null;
    }

    public UI createUI(WrappedRequest request) {
        Class<? extends UI> uiClass = getUIClass(request);

        UI ui = createUIInstance(request, uiClass);

        // Initialize some fields for a newly created UI
        if (ui.getApplication() == null) {
            ui.setApplication(this);
        }
        // Get the next id
        Integer uiId = Integer.valueOf(nextUIId++);

        uIs.put(uiId, ui);

        // Set thread local here so it is available in init
        UI.setCurrent(ui);

        ui.doInit(request, uiId.intValue());

        if (getUiProvider(request, uiClass).isUiPreserved(request, uiClass)) {
            // Remember this UI
            String windowName = request.getBrowserDetails().getWindowName();
            if (windowName == null) {
                getLogger().warning(
                        "There is no window.name available for UI " + uiClass
                                + " that should be preserved.");
            } else {
                retainOnRefreshUIs.put(windowName, uiId);
            }
        }

        return ui;
    }

    /**
     * Internal helper to finds the UI id for a request.
     * 
     * @param request
     *            the request to get the UI id for
     * @return a UI id, or <code>null</code> if no UI id is defined
     * 
     * @since 7.0
     */
    private static Integer getUIId(WrappedRequest request) {
        if (request instanceof CombinedRequest) {
            // Combined requests has the uiId parameter in the second request
            CombinedRequest combinedRequest = (CombinedRequest) request;
            request = combinedRequest.getSecondRequest();
        }
        String uiIdString = request.getParameter(UIConstants.UI_ID_PARAMETER);
        Integer uiId = uiIdString == null ? null : new Integer(uiIdString);
        return uiId;
    }

    /**
     * Gets all the uIs of this application. This includes uIs that have been
     * requested but not yet initialized. Please note, that uIs are not
     * automatically removed e.g. if the browser window is closed and that there
     * is no way to manually remove a UI. Inactive uIs will thus not be released
     * for GC until the entire application is released when the session has
     * timed out (unless there are dangling references). Improved support for
     * releasing unused uIs is planned for an upcoming alpha release of Vaadin
     * 7.
     * 
     * @return a collection of uIs belonging to this application
     * 
     * @since 7.0
     */
    public Collection<UI> getUIs() {
        return Collections.unmodifiableCollection(uIs.values());
    }

    private int connectorIdSequence = 0;

    /**
     * Generate an id for the given Connector. Connectors must not call this
     * method more than once, the first time they need an id.
     * 
     * @param connector
     *            A connector that has not yet been assigned an id.
     * @return A new id for the connector
     */
    public String createConnectorId(ClientConnector connector) {
        return String.valueOf(connectorIdSequence++);
    }

    private static final Logger getLogger() {
        return Logger.getLogger(Application.class.getName());
    }

    /**
     * Returns a UI with the given id.
     * <p>
     * This is meant for framework internal use.
     * </p>
     * 
     * @param uiId
     *            The UI id
     * @return The UI with the given id or null if not found
     */
    public UI getUIById(int uiId) {
        return uIs.get(uiId);
    }

    /**
     * Adds a listener that will be invoked when the bootstrap HTML is about to
     * be generated. This can be used to modify the contents of the HTML that
     * loads the Vaadin application in the browser and the HTTP headers that are
     * included in the response serving the HTML.
     * 
     * @see BootstrapListener#modifyBootstrapFragment(BootstrapFragmentResponse)
     * @see BootstrapListener#modifyBootstrapPage(BootstrapPageResponse)
     * 
     * @param listener
     *            the bootstrap listener to add
     */
    public void addBootstrapListener(BootstrapListener listener) {
        eventRouter.addListener(BootstrapFragmentResponse.class, listener,
                BOOTSTRAP_FRAGMENT_METHOD);
        eventRouter.addListener(BootstrapPageResponse.class, listener,
                BOOTSTRAP_PAGE_METHOD);
    }

    /**
     * Remove a bootstrap listener that was previously added.
     * 
     * @see #addBootstrapListener(BootstrapListener)
     * 
     * @param listener
     *            the bootstrap listener to remove
     */
    public void removeBootstrapListener(BootstrapListener listener) {
        eventRouter.removeListener(BootstrapFragmentResponse.class, listener,
                BOOTSTRAP_FRAGMENT_METHOD);
        eventRouter.removeListener(BootstrapPageResponse.class, listener,
                BOOTSTRAP_PAGE_METHOD);
    }

    /**
     * Fires a bootstrap event to all registered listeners. There are currently
     * two supported events, both inheriting from {@link BootstrapResponse}:
     * {@link BootstrapFragmentResponse} and {@link BootstrapPageResponse}.
     * 
     * @param response
     *            the bootstrap response event for which listeners should be
     *            fired
     */
    public void modifyBootstrapResponse(BootstrapResponse response) {
        eventRouter.fireEvent(response);
    }

    /**
     * Removes all those UIs from the application for which {@link #isUIAlive}
     * returns false. Close events are fired for the removed UIs.
     * <p>
     * Called by the framework at the end of every request.
     * 
     * @see UI.CloseEvent
     * @see UI.CloseListener
     * @see #isUIAlive(UI)
     * 
     * @since 7.0.0
     */
    public void closeInactiveUIs() {
        for (Iterator<UI> i = uIs.values().iterator(); i.hasNext();) {
            UI ui = i.next();
            if (!isUIAlive(ui)) {
                i.remove();
                retainOnRefreshUIs.values().remove(ui.getUIId());
                ui.fireCloseEvent();
                getLogger().info(
                        "Closed UI #" + ui.getUIId() + " due to inactivity");
            }
        }
    }

    /**
     * Returns the number of seconds that must pass without a valid heartbeat or
     * UIDL request being received from a UI before that UI is removed from the
     * application. This is a lower bound; it might take longer to close an
     * inactive UI. Returns a negative number if heartbeat is disabled and
     * timeout never occurs.
     * 
     * @see #getUidlRequestTimeout()
     * @see #closeInactiveUIs()
     * @see DeploymentConfiguration#getHeartbeatInterval()
     * 
     * @since 7.0.0
     * 
     * @return The heartbeat timeout in seconds or a negative number if timeout
     *         never occurs.
     */
    protected int getHeartbeatTimeout() {
        // Permit three missed heartbeats before closing the UI
        return (int) (configuration.getHeartbeatInterval() * (3.1));
    }

    /**
     * Returns the number of seconds that must pass without a valid UIDL request
     * being received from a UI before the UI is removed from the application,
     * even though heartbeat requests are received. This is a lower bound; it
     * might take longer to close an inactive UI. Returns a negative number if
     * <p>
     * This timeout only has effect if cleanup of inactive UIs is enabled;
     * otherwise heartbeat requests are enough to extend UI lifetime
     * indefinitely.
     * 
     * @see DeploymentConfiguration#isIdleUICleanupEnabled()
     * @see #getHeartbeatTimeout()
     * @see #closeInactiveUIs()
     * 
     * @since 7.0.0
     * 
     * @return The UIDL request timeout in seconds, or a negative number if
     *         timeout never occurs.
     */
    protected int getUidlRequestTimeout() {
        return configuration.isIdleUICleanupEnabled() ? getContext()
                .getSession().getMaxInactiveInterval() : -1;
    }

    /**
     * Returns whether the given UI is alive (the client-side actively
     * communicates with the server) or whether it can be removed from the
     * application and eventually collected.
     * 
     * @since 7.0.0
     * 
     * @param ui
     *            The UI whose status to check
     * @return true if the UI is alive, false if it could be removed.
     */
    protected boolean isUIAlive(UI ui) {
        long now = System.currentTimeMillis();
        if (getHeartbeatTimeout() >= 0
                && now - ui.getLastHeartbeatTime() > 1000 * getHeartbeatTimeout()) {
            return false;
        }
        if (getUidlRequestTimeout() >= 0
                && now - ui.getLastUidlRequestTime() > 1000 * getUidlRequestTimeout()) {
            return false;
        }
        return true;
    }

    /**
     * Gets this application's global resource handler that takes care of
     * serving connector resources that are not served by any single connector
     * because e.g. because they are served with strong caching or because of
     * legacy reasons.
     * 
     * @param createOnDemand
     *            <code>true</code> if a resource handler should be initialized
     *            if there is no handler associated with this application.
     *            </code>false</code> if </code>null</code> should be returned
     *            if there is no registered handler.
     * @return this application's global resource handler, or <code>null</code>
     *         if there is no handler and the createOnDemand parameter is
     *         <code>false</code>.
     * 
     * @since 7.0.0
     */
    public GlobalResourceHandler getGlobalResourceHandler(boolean createOnDemand) {
        if (globalResourceHandler == null && createOnDemand) {
            globalResourceHandler = new GlobalResourceHandler();
            addRequestHandler(globalResourceHandler);
        }

        return globalResourceHandler;
    }

    public Collection<UIProvider> getUIProviders() {
        return Collections.unmodifiableCollection(uiProviders);
    }

}
