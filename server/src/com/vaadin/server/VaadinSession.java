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

package com.vaadin.server;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.data.util.converter.DefaultConverterFactory;
import com.vaadin.event.EventRouter;
import com.vaadin.server.WrappedRequest.BrowserDetails;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.util.CurrentInstance;
import com.vaadin.util.ReflectTools;

/**
 * Contains everything that Vaadin needs to store for a specific user. This is
 * typically stored in a {@link HttpSession} or {@link PortletSession}, but
 * others storage mechanisms might also be used.
 * <p>
 * Everything inside a {@link VaadinSession} should be serializable to ensure
 * compatibility with schemes using serialization for persisting the session
 * data.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
@SuppressWarnings("serial")
public class VaadinSession implements HttpSessionBindingListener, Serializable {

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
     * An event sent to {@link #start(SessionStartEvent)} when a new Application
     * is being started.
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public static class SessionStartEvent implements Serializable {
        private final URL applicationUrl;

        private final DeploymentConfiguration configuration;

        private final AbstractCommunicationManager communicationManager;

        /**
         * @param applicationUrl
         *            the URL the application should respond to.
         * @param configuration
         *            the deployment configuration for the session.
         * @param communicationManager
         *            the communication manager for the session.
         */
        public SessionStartEvent(URL applicationUrl,
                DeploymentConfiguration configuration,
                AbstractCommunicationManager communicationManager) {
            this.applicationUrl = applicationUrl;
            this.configuration = configuration;
            this.communicationManager = communicationManager;
        }

        /**
         * Gets the URL the application should respond to.
         * 
         * @return the URL the application should respond to or
         *         <code>null</code> if the URL is not defined.
         * 
         * @see VaadinSession#getURL()
         */
        public URL getApplicationUrl() {
            return applicationUrl;
        }

        /**
         * Returns the deployment configuration used by this session.
         * 
         * @return the deployment configuration.
         */
        public DeploymentConfiguration getConfiguration() {
            return configuration;
        }

        /**
         * Gets the communication manager for this application.
         * 
         * @return the communication manager for this application.
         * 
         * @see VaadinSession#getCommunicationManager
         */
        public AbstractCommunicationManager getCommunicationManager() {
            return communicationManager;
        }
    }

    /**
     * Configuration for the session.
     */
    private DeploymentConfiguration configuration;

    /**
     * The application's URL.
     */
    private URL applicationUrl;

    /**
     * Application status.
     */
    private volatile boolean applicationIsRunning = false;

    /**
     * Default locale of the session.
     */
    private Locale locale;

    /**
     * URL where the user is redirected to on application close, or null if
     * application is just closed without redirection.
     */
    private String logoutURL = null;

    /**
     * Session wide error handler which is used by default if an error is left
     * unhandled.
     */
    private Terminal.ErrorListener errorHandler = new DefaultErrorListener();

    /**
     * The converter factory that is used to provide default converters for the
     * session.
     */
    private ConverterFactory converterFactory = new DefaultConverterFactory();

    private LinkedList<RequestHandler> requestHandlers = new LinkedList<RequestHandler>();

    private int nextUIId = 0;
    private Map<Integer, UI> uIs = new HashMap<Integer, UI>();

    private final Map<String, Integer> retainOnRefreshUIs = new HashMap<String, Integer>();

    private final EventRouter eventRouter = new EventRouter();

    private List<UIProvider> uiProviders = new LinkedList<UIProvider>();

    private GlobalResourceHandler globalResourceHandler;

    protected WebBrowser browser = new WebBrowser();

    private AbstractCommunicationManager communicationManager;

    private long totalSessionTime = 0;

    private long lastRequestTime = -1;

    private transient WrappedSession session;

    /**
     * @see javax.servlet.http.HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
     */
    @Override
    public void valueBound(HttpSessionBindingEvent arg0) {
        // We are not interested in bindings
    }

    /**
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
     */
    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        // If we are going to be unbound from the session, the session must be
        // closing
        close();
    }

    /**
     * Get the web browser associated with this session.
     * 
     * @return
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public WebBrowser getBrowser() {
        return browser;
    }

    /**
     * @return The total time spent servicing requests in this session.
     */
    public long getTotalSessionTime() {
        return totalSessionTime;
    }

    /**
     * Sets the time spent servicing the last request in the session and updates
     * the total time spent servicing requests in this session.
     * 
     * @param time
     *            the time spent in the last request.
     */
    public void setLastRequestTime(long time) {
        lastRequestTime = time;
        totalSessionTime += time;
    }

    /**
     * @return the time spent servicing the last request in this session.
     */
    public long getLastRequestTime() {
        return lastRequestTime;
    }

    /**
     * Gets the underlying session to which this vaadin session is currently
     * associated.
     * 
     * @return the wrapped session for this context
     */
    public WrappedSession getSession() {
        return session;
    }

    /**
     * @return
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public AbstractCommunicationManager getApplicationManager() {
        return communicationManager;
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
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public URL getURL() {
        return applicationUrl;
    }

    /**
     * Ends the session.
     * <p>
     * When the session is closed, close events are fired for its UIs, its state
     * is removed from the underlying session, and the browser window is
     * redirected to the application logout url set with
     * {@link #setLogoutURL(String)}. If the logout url has not been set, the
     * browser window is reloaded and the application is restarted.
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public void close() {
        applicationIsRunning = false;
        for (UI ui : getUIs()) {
            ui.fireCloseEvent();
        }
    }

    /**
     * @param underlyingSession
     * @return
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public static VaadinSession getForSession(WrappedSession underlyingSession) {
        Object attribute = underlyingSession.getAttribute(VaadinSession.class
                .getName());
        if (attribute instanceof VaadinSession) {
            VaadinSession vaadinSession = (VaadinSession) attribute;
            vaadinSession.session = underlyingSession;
            return vaadinSession;
        }

        return null;
    }

    /**
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public void removeFromSession() {
        assert (getForSession(session) == this);

        session.setAttribute(VaadinSession.class.getName(), null);
    }

    /**
     * @param session
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public void storeInSession(WrappedSession session) {
        session.setAttribute(VaadinSession.class.getName(), this);
        this.session = session;
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
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public void start(SessionStartEvent event) {
        applicationUrl = event.getApplicationUrl();
        configuration = event.getConfiguration();
        communicationManager = event.getCommunicationManager();
        applicationIsRunning = true;
    }

    /**
     * Tests if the application is running or if it has been finished.
     * 
     * <p>
     * Application starts running when its {@link #start(SessionStartEvent)}
     * method has been called and stops when the {@link #close()} is called.
     * </p>
     * 
     * @return <code>true</code> if the application is running,
     *         <code>false</code> if not.
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public boolean isRunning() {
        return applicationIsRunning;
    }

    /**
     * Gets the configuration for this session
     * 
     * @return the deployment configuration
     */
    public DeploymentConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Gets the default locale for this session.
     * 
     * By default this is the preferred locale of the user using the session. In
     * most cases it is read from the browser defaults.
     * 
     * @return the locale of this session.
     */
    public Locale getLocale() {
        if (locale != null) {
            return locale;
        }
        return Locale.getDefault();
    }

    /**
     * Sets the default locale for this session.
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
     * with {@link com.vaadin.server.VaadinSession#removeWindow(Window)}.
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
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
        public WindowDetachEvent(VaadinSession application, Window window) {
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
        public VaadinSession getApplication() {
            return (VaadinSession) getSource();
        }
    }

    /**
     * Window attach event.
     * 
     * This event is sent each time a window is attached tothe application with
     * {@link com.vaadin.server.VaadinSession#addWindow(Window)}.
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
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
        public WindowAttachEvent(VaadinSession application, Window window) {
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
        public VaadinSession getApplication() {
            return (VaadinSession) getSource();
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
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
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
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public void setLogoutURL(String logoutURL) {
        this.logoutURL = logoutURL;
    }

    /**
     * Gets the session's error handler.
     * 
     * @return the current error handler
     */
    public Terminal.ErrorListener getErrorHandler() {
        return errorHandler;
    }

    /**
     * Sets the session error handler.
     * 
     * @param errorHandler
     */
    public void setErrorHandler(Terminal.ErrorListener errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Gets the {@link ConverterFactory} used to locate a suitable
     * {@link Converter} for fields in the session.
     * 
     * See {@link #setConverterFactory(ConverterFactory)} for more details
     * 
     * @return The converter factory used in the session
     */
    public ConverterFactory getConverterFactory() {
        return converterFactory;
    }

    /**
     * Sets the {@link ConverterFactory} used to locate a suitable
     * {@link Converter} for fields in the session.
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
     *            The converter factory used in the session
     */
    public void setConverterFactory(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    /**
     * Application error is an error message defined on the application level.
     * 
     * When an error occurs on the application level, this error message4 type
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
     * 
     * @param request
     *            the wrapped request for which a UI is needed
     * @return a UI instance to use for the request
     * 
     * @see UI
     * @see WrappedRequest#getBrowserDetails()
     * 
     * @since 7.0
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public Class<? extends UI> getUIClass(WrappedRequest request) {
        UIProvider uiProvider = getUiProvider(request, null);
        return uiProvider.getUIClass(request);
    }

    /**
     * Creates an UI instance for a request for which no UI is already known.
     * This method is called when the framework processes a request that does
     * not originate from an existing UI instance. This typically happens when a
     * host page is requested.
     * 
     * @param request
     * @param uiClass
     * @return
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    protected <T extends UI> T createUIInstance(WrappedRequest request,
            Class<T> uiClass) {
        UIProvider uiProvider = getUiProvider(request, uiClass);
        return uiClass.cast(uiProvider.createInstance(uiClass, request));
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
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public UIProvider getUiProvider(WrappedRequest request, Class<?> uiClass) {
        UIProvider provider = (UIProvider) request
                .getAttribute(UIProvider.class.getName());
        if (provider != null) {
            // Cached provider found, verify that it's a sensible selection
            Class<? extends UI> providerClass = provider.getUIClass(request);
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

    /**
     * @param request
     * @param uiClass
     * @return
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    private UIProvider doGetUiProvider(WrappedRequest request, Class<?> uiClass) {
        int providersSize = uiProviders.size();
        if (providersSize == 0) {
            throw new IllegalStateException("There are no UI providers");
        }

        for (int i = providersSize - 1; i >= 0; i--) {
            UIProvider provider = uiProviders.get(i);

            Class<? extends UI> providerClass = provider.getUIClass(request);
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
     * Adds a request handler to this session. Request handlers can be added to
     * provide responses to requests that are not handled by the default
     * functionality of the framework.
     * <p>
     * Handlers are called in reverse order of addition, so the most recently
     * added handler will be called first.
     * </p>
     * 
     * @param handler
     *            the request handler to add
     * 
     * @see #removeRequestHandler(RequestHandler)
     * 
     * @since 7.0
     */
    public void addRequestHandler(RequestHandler handler) {
        requestHandlers.addFirst(handler);
    }

    /**
     * Removes a request handler from the session.
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
     * Gets the request handlers that are registered to the session. The
     * iteration order of the returned collection is the same as the order in
     * which the request handlers will be invoked when a request is handled.
     * 
     * @return a collection of request handlers, with the iteration order
     *         according to the order they would be invoked
     * 
     * @see #addRequestHandler(RequestHandler)
     * @see #removeRequestHandler(RequestHandler)
     * 
     * @since 7.0
     */
    public Collection<RequestHandler> getRequestHandlers() {
        return Collections.unmodifiableCollection(requestHandlers);
    }

    /**
     * Gets the currently used session. The current session is automatically
     * defined when processing requests to the server and in threads started at
     * a point when the current session is defined (see
     * {@link InheritableThreadLocal}). In other cases, (e.g. from background
     * threads started in some other way), the current session is not
     * automatically defined.
     * 
     * @return the current session instance if available, otherwise
     *         <code>null</code>
     * 
     * @see #setCurrent(VaadinSession)
     * 
     * @since 7.0
     */
    public static VaadinSession getCurrent() {
        return CurrentInstance.get(VaadinSession.class);
    }

    /**
     * Sets the thread local for the current session. This method is used by the
     * framework to set the current session whenever a new request is processed
     * and it is cleared when the request has been processed.
     * <p>
     * The application developer can also use this method to define the current
     * session outside the normal request handling and treads started from
     * request handling threads, e.g. when initiating custom background threads.
     * </p>
     * 
     * @param session
     * 
     * @see #getCurrent()
     * @see ThreadLocal
     * 
     * @since 7.0
     */
    public static void setCurrent(VaadinSession session) {
        CurrentInstance.setInheritable(VaadinSession.class, session);
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
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
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

    /**
     * @param request
     * @return
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
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

    /**
     * @param request
     * @return
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public UI createUI(WrappedRequest request) {
        Class<? extends UI> uiClass = getUIClass(request);

        UI ui = createUIInstance(request, uiClass);

        // Initialize some fields for a newly created UI
        if (ui.getSession() == null) {
            ui.setSession(this);
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
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
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
     * Gets all the UIs of this session. This includes UIs that have been
     * requested but not yet initialized. Please note, that UIs are not
     * automatically removed e.g. if the browser window is closed and that there
     * is no way to manually remove a UI. Inactive UIs will thus not be released
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
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public String createConnectorId(ClientConnector connector) {
        return String.valueOf(connectorIdSequence++);
    }

    private static final Logger getLogger() {
        return Logger.getLogger(VaadinSession.class.getName());
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
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
    public void modifyBootstrapResponse(BootstrapResponse response) {
        eventRouter.fireEvent(response);
    }

    /**
     * Removes all those UIs from the session for which {@link #isUIAlive}
     * returns false. Close events are fired for the removed UIs.
     * <p>
     * Called by the framework at the end of every request.
     * 
     * @see UI.CloseEvent
     * @see UI.CloseListener
     * @see #isUIAlive(UI)
     * 
     * @since 7.0.0
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
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
     * being received from a UI before the UI is removed from the session, even
     * though heartbeat requests are received. This is a lower bound; it might
     * take longer to close an inactive UI. Returns a negative number if
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
        return configuration.isIdleUICleanupEnabled() ? getSession()
                .getMaxInactiveInterval() : -1;
    }

    /**
     * Returns whether the given UI is alive (the client-side actively
     * communicates with the server) or whether it can be removed from the
     * session and eventually collected.
     * 
     * @since 7.0.0
     * 
     * @param ui
     *            The UI whose status to check
     * @return true if the UI is alive, false if it could be removed.
     * 
     * @deprecated might be refactored or removed before 7.0.0
     */
    @Deprecated
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
     * Gets this session's global resource handler that takes care of serving
     * connector resources that are not served by any single connector because
     * e.g. because they are served with strong caching or because of legacy
     * reasons.
     * 
     * @param createOnDemand
     *            <code>true</code> if a resource handler should be initialized
     *            if there is no handler associated with this application.
     *            </code>false</code> if </code>null</code> should be returned
     *            if there is no registered handler.
     * @return this session's global resource handler, or <code>null</code> if
     *         there is no handler and the createOnDemand parameter is
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
