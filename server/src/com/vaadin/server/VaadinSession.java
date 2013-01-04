/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.data.util.converter.DefaultConverterFactory;
import com.vaadin.event.EventRouter;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
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

    private final Lock lock = new ReentrantLock();

    /**
     * Configuration for the session.
     */
    private DeploymentConfiguration configuration;

    /**
     * Default locale of the session.
     */
    private Locale locale;

    /**
     * Session wide error handler which is used by default if an error is left
     * unhandled.
     */
    private ErrorHandler errorHandler = new DefaultErrorHandler();

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

    private GlobalResourceHandler globalResourceHandler;

    protected WebBrowser browser = new WebBrowser();

    private AbstractCommunicationManager communicationManager;

    private long cumulativeRequestDuration = 0;

    private long lastRequestDuration = -1;

    private long lastRequestTimestamp = System.currentTimeMillis();

    private boolean closing = false;

    private transient WrappedSession session;

    private final Map<String, Object> attributes = new HashMap<String, Object>();

    private LinkedList<UIProvider> uiProviders = new LinkedList<UIProvider>();

    private transient VaadinService service;

    /**
     * Create a new service session tied to a Vaadin service
     * 
     * @param service
     *            the Vaadin service for the new session
     */
    public VaadinSession(VaadinService service) {
        this.service = service;
    }

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
        // Notify the service
        if (service == null) {
            getLogger()
                    .warning(
                            "A VaadinSession instance not associated to any service is getting unbound. "
                                    + "Session destroy events will not be fired and UIs in the session will not get detached. "
                                    + "This might happen if a session is deserialized but never used before it expires.");
        } else if (VaadinService.getCurrentRequest() != null
                && getCurrent() == this) {
            // Ignore if the session is being moved to a different backing
            // session
            if (getAttribute(VaadinService.REINITIALIZING_SESSION_MARKER) == Boolean.TRUE) {
                return;
            }

            // There is still a request in progress for this session. The
            // session will be destroyed after the response has been written.
            if (!isClosing()) {
                close();
            }
        } else {
            /*
             * We are not in a request related to this session so we can
             * immediately destroy it
             */
            service.fireSessionDestroy(this);
        }
        session = null;
    }

    /**
     * Get the web browser associated with this session.
     * 
     * @return
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public WebBrowser getBrowser() {
        return browser;
    }

    /**
     * @return The total time spent servicing requests in this session, in
     *         milliseconds.
     */
    public long getCumulativeRequestDuration() {
        return cumulativeRequestDuration;
    }

    /**
     * Sets the time spent servicing the last request in the session and updates
     * the total time spent servicing requests in this session.
     * 
     * @param time
     *            The time spent in the last request, in milliseconds.
     */
    public void setLastRequestDuration(long time) {
        lastRequestDuration = time;
        cumulativeRequestDuration += time;
    }

    /**
     * @return The time spent servicing the last request in this session, in
     *         milliseconds.
     */
    public long getLastRequestDuration() {
        return lastRequestDuration;
    }

    /**
     * Sets the time when the last UIDL request was serviced in this session.
     * 
     * @param timestamp
     *            The time when the last request was handled, in milliseconds
     *            since the epoch.
     * 
     */
    public void setLastRequestTimestamp(long timestamp) {
        lastRequestTimestamp = timestamp;
    }

    /**
     * Returns the time when the last request was serviced in this session.
     * 
     * @return The time when the last request was handled, in milliseconds since
     *         the epoch.
     */
    public long getLastRequestTimestamp() {
        return lastRequestTimestamp;
    }

    /**
     * Gets the underlying session to which this service session is currently
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
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public AbstractCommunicationManager getCommunicationManager() {
        return communicationManager;
    }

    /**
     * @param service
     *            TODO
     * @param underlyingSession
     * @return
     * 
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public static VaadinSession getForSession(VaadinService service,
            WrappedSession underlyingSession) {
        Object attribute = underlyingSession.getAttribute(VaadinSession.class
                .getName() + "." + service.getServiceName());
        if (attribute instanceof VaadinSession) {
            VaadinSession vaadinSession = (VaadinSession) attribute;
            vaadinSession.session = underlyingSession;
            vaadinSession.service = service;
            return vaadinSession;
        }

        return null;
    }

    /**
     * 
     * @param service
     *            TODO
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public void removeFromSession(VaadinService service) {
        assert (getForSession(service, session) == this);
        session.setAttribute(
                VaadinSession.class.getName() + "." + service.getServiceName(),
                null);
    }

    /**
     * @param session
     * 
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public void storeInSession(VaadinService service, WrappedSession session) {
        session.setAttribute(
                VaadinSession.class.getName() + "." + service.getServiceName(),
                this);
        this.session = session;
    }

    public void setCommunicationManager(
            AbstractCommunicationManager communicationManager) {
        if (communicationManager == null) {
            throw new IllegalArgumentException("Can not set to null");
        }
        assert this.communicationManager == null : "Communication manager can only be set once";
        this.communicationManager = communicationManager;
    }

    public void setConfiguration(DeploymentConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("Can not set to null");
        }
        assert this.configuration == null : "Configuration can only be set once";
        this.configuration = configuration;
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
     * Gets the session's error handler.
     * 
     * @return the current error handler
     */
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Sets the session error handler.
     * 
     * @param errorHandler
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
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

    /**
     * Gets all the UIs of this session. This includes UIs that have been
     * requested but not yet initialized. UIs that receive no heartbeat requests
     * from the client are eventually removed from the session.
     * 
     * @return a collection of UIs belonging to this application
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
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public String createConnectorId(ClientConnector connector) {
        return String.valueOf(connectorIdSequence++);
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
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public void modifyBootstrapResponse(BootstrapResponse response) {
        eventRouter.fireEvent(response);
    }

    /**
     * Called by the framework to remove an UI instance from the session because
     * it has been closed.
     * 
     * @param ui
     *            the UI to remove
     */
    public void removeUI(UI ui) {
        int id = ui.getUIId();
        ui.setSession(null);
        uIs.remove(id);
        retainOnRefreshUIs.values().remove(id);
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

    /**
     * Gets the {@link Lock} instance that is used for protecting the data of
     * this session from concurrent access.
     * <p>
     * The <code>Lock</code> can be used to gain more control than what is
     * available only using {@link #lock()} and {@link #unlock()}. The returned
     * instance is not guaranteed to support any other features of the
     * <code>Lock</code> interface than {@link Lock#lock()} and
     * {@link Lock#unlock()}.
     * 
     * @return the <code>Lock</code> that is used for synchronization, never
     *         <code>null</code>
     * 
     * @see #lock()
     * @see Lock
     */
    public Lock getLockInstance() {
        return lock;
    }

    /**
     * Locks this session to protect its data from concurrent access. Accessing
     * the UI state from outside the normal request handling should always lock
     * the session and unlock it when done. To ensure that the lock is always
     * released, you should typically wrap the code in a <code>try</code> block
     * and unlock the session in <code>finally</code>:
     * 
     * <pre>
     * session.lock();
     * try {
     *     doSomething();
     * } finally {
     *     session.unlock();
     * }
     * </pre>
     * <p>
     * This method will block until the lock can be retrieved.
     * <p>
     * {@link #getLockInstance()} can be used if more control over the locking
     * is required.
     * 
     * @see #unlock()
     * @see #getLockInstance()
     */
    public void lock() {
        getLockInstance().lock();
    }

    /**
     * Unlocks this session. This method should always be used in a finally
     * block after {@link #lock()} to ensure that the lock is always released.
     * 
     * @see #unlock()
     */
    public void unlock() {
        getLockInstance().unlock();
    }

    /**
     * Stores a value in this service session. This can be used to associate
     * data with the current user so that it can be retrieved at a later point
     * from some other part of the application. Setting the value to
     * <code>null</code> clears the stored value.
     * 
     * @see #getAttribute(String)
     * 
     * @param name
     *            the name to associate the value with, can not be
     *            <code>null</code>
     * @param value
     *            the value to associate with the name, or <code>null</code> to
     *            remove a previous association.
     */
    public void setAttribute(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("name can not be null");
        }
        if (value != null) {
            attributes.put(name, value);
        } else {
            attributes.remove(name);
        }
    }

    /**
     * Stores a value in this service session. This can be used to associate
     * data with the current user so that it can be retrieved at a later point
     * from some other part of the application. Setting the value to
     * <code>null</code> clears the stored value.
     * <p>
     * The fully qualified name of the type is used as the name when storing the
     * value. The outcome of calling this method is thus the same as if calling<br />
     * <br />
     * <code>setAttribute(type.getName(), value);</code>
     * 
     * @see #getAttribute(Class)
     * @see #setAttribute(String, Object)
     * 
     * @param type
     *            the type that the stored value represents, can not be null
     * @param value
     *            the value to associate with the type, or <code>null</code> to
     *            remove a previous association.
     */
    public <T> void setAttribute(Class<T> type, T value) {
        if (type == null) {
            throw new IllegalArgumentException("type can not be null");
        }
        if (value != null && !type.isInstance(value)) {
            throw new IllegalArgumentException("value of type "
                    + type.getName() + " expected but got "
                    + value.getClass().getName());
        }
        setAttribute(type.getName(), value);
    }

    /**
     * Gets a stored attribute value. If a value has been stored for the
     * session, that value is returned. If no value is stored for the name,
     * <code>null</code> is returned.
     * 
     * @see #setAttribute(String, Object)
     * 
     * @param name
     *            the name of the value to get, can not be <code>null</code>.
     * @return the value, or <code>null</code> if no value has been stored or if
     *         it has been set to null.
     */
    public Object getAttribute(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name can not be null");
        }
        return attributes.get(name);
    }

    /**
     * Gets a stored attribute value. If a value has been stored for the
     * session, that value is returned. If no value is stored for the name,
     * <code>null</code> is returned.
     * <p>
     * The fully qualified name of the type is used as the name when getting the
     * value. The outcome of calling this method is thus the same as if calling<br />
     * <br />
     * <code>getAttribute(type.getName());</code>
     * 
     * @see #setAttribute(Class, Object)
     * @see #getAttribute(String)
     * 
     * @param type
     *            the type of the value to get, can not be <code>null</code>.
     * @return the value, or <code>null</code> if no value has been stored or if
     *         it has been set to null.
     */
    public <T> T getAttribute(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type can not be null");
        }
        Object value = getAttribute(type.getName());
        if (value == null) {
            return null;
        } else {
            return type.cast(value);
        }
    }

    /**
     * Creates a new unique id for a UI.
     * 
     * @return a unique UI id
     */
    public int getNextUIid() {
        return nextUIId++;
    }

    /**
     * Gets the mapping from <code>window.name</code> to UI id for UIs that are
     * should be retained on refresh.
     * 
     * @see VaadinService#preserveUIOnRefresh(VaadinRequest, UI, UIProvider)
     * @see PreserveOnRefresh
     * 
     * @return the mapping between window names and UI ids for this session.
     */
    public Map<String, Integer> getPreserveOnRefreshUIs() {
        return retainOnRefreshUIs;
    }

    /**
     * Adds an initialized UI to this session.
     * 
     * @param ui
     *            the initialized UI to add.
     */
    public void addUI(UI ui) {
        if (ui.getUIId() == -1) {
            throw new IllegalArgumentException(
                    "Can not add an UI that has not been initialized.");
        }
        if (ui.getSession() != this) {
            throw new IllegalArgumentException(
                    "The UI belongs to a different session");
        }

        uIs.put(Integer.valueOf(ui.getUIId()), ui);
    }

    /**
     * Adds a UI provider to this session.
     * 
     * @param uiProvider
     *            the UI provider that should be added
     */
    public void addUIProvider(UIProvider uiProvider) {
        uiProviders.addFirst(uiProvider);
    }

    /**
     * Removes a UI provider association from this session.
     * 
     * @param uiProvider
     *            the UI provider that should be removed
     */
    public void removeUIProvider(UIProvider uiProvider) {
        uiProviders.remove(uiProvider);
    }

    /**
     * Gets the UI providers configured for this session.
     * 
     * @return an unmodifiable list of UI providers
     */
    public List<UIProvider> getUIProviders() {
        return Collections.unmodifiableList(uiProviders);
    }

    public VaadinService getService() {
        return service;
    }

    /**
     * Sets this session to be closed and all UI state to be discarded at the
     * end of the current request, or at the end of the next request if there is
     * no ongoing one.
     * <p>
     * After the session has been discarded, any UIs that have been left open
     * will give a Session Expired error and a new session will be created for
     * serving new UIs.
     * <p>
     * To avoid causing out of sync errors, you should typically redirect to
     * some other page using {@link Page#setLocation(String)} to make the
     * browser unload the invalidated UI.
     * 
     * @see SystemMessages#getSessionExpiredCaption()
     * 
     */
    public void close() {
        closing = true;
    }

    /**
     * Returns whether this session is marked to be closed.
     * 
     * @see #close()
     * 
     * @return
     */
    public boolean isClosing() {
        return closing;
    }

    private static final Logger getLogger() {
        return Logger.getLogger(VaadinSession.class.getName());
    }
}
