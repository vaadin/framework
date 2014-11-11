/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.Portlet;
import javax.portlet.PortletContext;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.event.EventRouter;
import com.vaadin.server.VaadinSession.FutureAccess;
import com.vaadin.server.VaadinSession.State;
import com.vaadin.server.communication.FileUploadHandler;
import com.vaadin.server.communication.HeartbeatHandler;
import com.vaadin.server.communication.PublishedFileHandler;
import com.vaadin.server.communication.SessionRequestHandler;
import com.vaadin.server.communication.UidlRequestHandler;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.JsonConstants;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import com.vaadin.util.ReflectTools;

/**
 * Provide deployment specific settings that are required outside terminal
 * specific code.
 *
 * @author Vaadin Ltd.
 *
 * @since 7.0
 */
public abstract class VaadinService implements Serializable {
    /**
     * Attribute name for telling
     * {@link VaadinSession#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)}
     * that it should not close a {@link VaadinSession} even though it gets
     * unbound. If a {@code VaadinSession} has an attribute with this name and
     * the attribute value is {@link Boolean#TRUE}, that session will not be
     * closed when it is unbound from the underlying session.
     */
    // Use the old name.reinitializing value for backwards compatibility
    static final String PRESERVE_UNBOUND_SESSION_ATTRIBUTE = VaadinService.class
            .getName() + ".reinitializing";

    /**
     * @deprecated As of 7.1.1, use {@link #PRESERVE_UNBOUND_SESSION_ATTRIBUTE}
     *             instead
     */
    @Deprecated
    static final String REINITIALIZING_SESSION_MARKER = PRESERVE_UNBOUND_SESSION_ATTRIBUTE;

    private static final Method SESSION_INIT_METHOD = ReflectTools.findMethod(
            SessionInitListener.class, "sessionInit", SessionInitEvent.class);

    private static final Method SESSION_DESTROY_METHOD = ReflectTools
            .findMethod(SessionDestroyListener.class, "sessionDestroy",
                    SessionDestroyEvent.class);

    private static final Method SERVICE_DESTROY_METHOD = ReflectTools
            .findMethod(ServiceDestroyListener.class, "serviceDestroy",
                    ServiceDestroyEvent.class);

    /**
     * @deprecated As of 7.0. Only supported for {@link LegacyApplication}.
     */
    @Deprecated
    public static final String URL_PARAMETER_RESTART_APPLICATION = "restartApplication";

    /**
     * @deprecated As of 7.0. Only supported for {@link LegacyApplication}.
     */
    @Deprecated
    public static final String URL_PARAMETER_CLOSE_APPLICATION = "closeApplication";

    private static final String REQUEST_START_TIME_ATTRIBUTE = "requestStartTime";

    private final DeploymentConfiguration deploymentConfiguration;

    private final EventRouter eventRouter = new EventRouter();

    private SystemMessagesProvider systemMessagesProvider = DefaultSystemMessagesProvider
            .get();

    private ClassLoader classLoader;

    private Iterable<RequestHandler> requestHandlers;

    /**
     * Keeps track of whether a warning about missing push support has already
     * been logged. This is used to avoid spamming the log with the same message
     * every time a new UI is bootstrapped.
     */
    private boolean pushWarningEmitted = false;

    /**
     * Has {@link #init()} been run?
     */
    private boolean initialized = false;

    /**
     * Creates a new vaadin service based on a deployment configuration
     *
     * @param deploymentConfiguration
     *            the deployment configuration for the service
     */
    public VaadinService(DeploymentConfiguration deploymentConfiguration) {
        this.deploymentConfiguration = deploymentConfiguration;

        final String classLoaderName = getDeploymentConfiguration()
                .getApplicationOrSystemProperty("ClassLoader", null);
        if (classLoaderName != null) {
            try {
                final Class<?> classLoaderClass = getClass().getClassLoader()
                        .loadClass(classLoaderName);
                final Constructor<?> c = classLoaderClass
                        .getConstructor(new Class[] { ClassLoader.class });
                setClassLoader((ClassLoader) c
                        .newInstance(new Object[] { getClass().getClassLoader() }));
            } catch (final Exception e) {
                throw new RuntimeException(
                        "Could not find specified class loader: "
                                + classLoaderName, e);
            }
        }

        if (getClassLoader() == null) {
            setDefaultClassLoader();
        }
    }

    /**
     * Initializes this service. The service should be initialized before it is
     * used.
     *
     * @since 7.1
     * @throws ServiceException
     *             if a problem occurs when creating the service
     */
    public void init() throws ServiceException {
        List<RequestHandler> handlers = createRequestHandlers();
        Collections.reverse(handlers);
        requestHandlers = Collections.unmodifiableCollection(handlers);

        initialized = true;
    }

    /**
     * Called during initialization to add the request handlers for the service.
     * Note that the returned list will be reversed so the last handler will be
     * called first. This enables overriding this method and using add on the
     * returned list to add a custom request handler which overrides any
     * predefined handler.
     *
     * @return The list of request handlers used by this service.
     * @throws ServiceException
     *             if a problem occurs when creating the request handlers
     */
    protected List<RequestHandler> createRequestHandlers()
            throws ServiceException {
        ArrayList<RequestHandler> handlers = new ArrayList<RequestHandler>();
        handlers.add(new SessionRequestHandler());
        handlers.add(new PublishedFileHandler());
        handlers.add(new HeartbeatHandler());
        handlers.add(new FileUploadHandler());
        handlers.add(new UidlRequestHandler());
        handlers.add(new UnsupportedBrowserHandler());
        handlers.add(new ConnectorResourceHandler());

        return handlers;
    }

    /**
     * Return the URL from where static files, e.g. the widgetset and the theme,
     * are served. In a standard configuration the VAADIN folder inside the
     * returned folder is what is used for widgetsets and themes.
     *
     * The returned folder is usually the same as the context path and
     * independent of e.g. the servlet mapping.
     *
     * @param request
     *            the request for which the location should be determined
     *
     * @return The location of static resources (should contain the VAADIN
     *         directory). Never ends with a slash (/).
     */
    public abstract String getStaticFileLocation(VaadinRequest request);

    /**
     * Gets the widgetset that is configured for this deployment, e.g. from a
     * parameter in web.xml.
     *
     * @param request
     *            the request for which a widgetset is required
     * @return the name of the widgetset
     */
    public abstract String getConfiguredWidgetset(VaadinRequest request);

    /**
     * Gets the theme that is configured for this deployment, e.g. from a portal
     * parameter or just some sensible default value.
     *
     * @param request
     *            the request for which a theme is required
     * @return the name of the theme
     */
    public abstract String getConfiguredTheme(VaadinRequest request);

    /**
     * Checks whether the UI will be rendered on its own in the browser or
     * whether it will be included into some other context. A standalone UI may
     * do things that might interfere with other parts of a page, e.g. changing
     * the page title and requesting focus upon loading.
     *
     * @param request
     *            the request for which the UI is loaded
     * @return a boolean indicating whether the UI should be standalone
     */
    public abstract boolean isStandalone(VaadinRequest request);

    /**
     * Gets the class loader to use for loading classes loaded by name, e.g.
     * custom UI classes. This is by default the class loader that was used to
     * load the Servlet or Portlet class to which this service belongs.
     *
     * @return the class loader to use, or <code>null</code>
     *
     * @see #setClassLoader(ClassLoader)
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Sets the class loader to use for loading classes loaded by name, e.g.
     * custom UI classes. Invokers of this method should be careful to not break
     * any existing class loader hierarchy, e.g. by ensuring that a class loader
     * set for this service delegates to the previously set class loader if the
     * class is not found.
     *
     * @param classLoader
     *            the new class loader to set, not <code>null</code>.
     *
     * @see #getClassLoader()
     */
    public void setClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            throw new IllegalArgumentException(
                    "Can not set class loader to null");
        }
        this.classLoader = classLoader;
    }

    /**
     * Returns the MIME type of the specified file, or null if the MIME type is
     * not known. The MIME type is determined by the configuration of the
     * container, and may be specified in a deployment descriptor. Common MIME
     * types are "text/html" and "image/gif".
     *
     * @param resourceName
     *            a String specifying the name of a file
     * @return a String specifying the file's MIME type
     *
     * @see ServletContext#getMimeType(String)
     * @see PortletContext#getMimeType(String)
     */
    public abstract String getMimeType(String resourceName);

    /**
     * Gets the deployment configuration.
     *
     * @return the deployment configuration
     */
    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

    /**
     * Sets the system messages provider to use for getting system messages to
     * display to users of this service.
     *
     * @see #getSystemMessagesProvider()
     *
     * @param systemMessagesProvider
     *            the system messages provider; <code>null</code> is not
     *            allowed.
     */
    public void setSystemMessagesProvider(
            SystemMessagesProvider systemMessagesProvider) {
        if (systemMessagesProvider == null) {
            throw new IllegalArgumentException(
                    "SystemMessagesProvider can not be null.");
        }
        this.systemMessagesProvider = systemMessagesProvider;
    }

    /**
     * Gets the system messages provider currently defined for this service.
     * <p>
     * By default, the {@link DefaultSystemMessagesProvider} which always
     * provides the built-in default {@link SystemMessages} is used.
     * </p>
     *
     * @see #setSystemMessagesProvider(SystemMessagesProvider)
     * @see SystemMessagesProvider
     * @see SystemMessages
     *
     * @return the system messages provider; not <code>null</code>
     */
    public SystemMessagesProvider getSystemMessagesProvider() {
        return systemMessagesProvider;
    }

    /**
     * Gets the system message to use for a specific locale. This method may
     * also be implemented to use information from current instances of various
     * objects, which means that this method might return different values for
     * the same locale under different circumstances.
     *
     * @param locale
     *            the desired locale for the system messages
     * @param request
     * @return the system messages to use
     */
    public SystemMessages getSystemMessages(Locale locale, VaadinRequest request) {
        SystemMessagesInfo systemMessagesInfo = new SystemMessagesInfo();
        systemMessagesInfo.setLocale(locale);
        systemMessagesInfo.setService(this);
        systemMessagesInfo.setRequest(request);
        return getSystemMessagesProvider()
                .getSystemMessages(systemMessagesInfo);
    }

    /**
     * Returns the context base directory.
     *
     * Typically an application is deployed in a such way that is has an
     * application directory. For web applications this directory is the root
     * directory of the web applications. In some cases applications might not
     * have an application directory (for example web applications running
     * inside a war).
     *
     * @return The application base directory or null if the application has no
     *         base directory.
     */
    public abstract File getBaseDirectory();

    /**
     * Adds a listener that gets notified when a new Vaadin service session is
     * initialized for this service.
     * <p>
     * Because of the way different service instances share the same session,
     * the listener is not necessarily notified immediately when the session is
     * created but only when the first request for that session is handled by
     * this service.
     *
     * @see #removeSessionInitListener(SessionInitListener)
     * @see SessionInitListener
     *
     * @param listener
     *            the Vaadin service session initialization listener
     */
    public void addSessionInitListener(SessionInitListener listener) {
        eventRouter.addListener(SessionInitEvent.class, listener,
                SESSION_INIT_METHOD);
    }

    /**
     * Removes a Vaadin service session initialization listener from this
     * service.
     *
     * @see #addSessionInitListener(SessionInitListener)
     *
     * @param listener
     *            the Vaadin service session initialization listener to remove.
     */
    public void removeSessionInitListener(SessionInitListener listener) {
        eventRouter.removeListener(SessionInitEvent.class, listener,
                SESSION_INIT_METHOD);
    }

    /**
     * Adds a listener that gets notified when a Vaadin service session that has
     * been initialized for this service is destroyed.
     * <p>
     * The session being destroyed is locked and its UIs have been removed when
     * the listeners are called.
     *
     * @see #addSessionInitListener(SessionInitListener)
     *
     * @param listener
     *            the vaadin service session destroy listener
     */
    public void addSessionDestroyListener(SessionDestroyListener listener) {
        eventRouter.addListener(SessionDestroyEvent.class, listener,
                SESSION_DESTROY_METHOD);
    }

    /**
     * Handles destruction of the given session. Internally ensures proper
     * locking is done.
     *
     * @param vaadinSession
     *            The session to destroy
     */
    public void fireSessionDestroy(VaadinSession vaadinSession) {
        final VaadinSession session = vaadinSession;
        session.access(new Runnable() {
            @Override
            public void run() {
                if (session.getState() == State.CLOSED) {
                    return;
                }
                if (session.getState() == State.OPEN) {
                    closeSession(session);
                }
                ArrayList<UI> uis = new ArrayList<UI>(session.getUIs());
                for (final UI ui : uis) {
                    ui.accessSynchronously(new Runnable() {
                        @Override
                        public void run() {
                            /*
                             * close() called here for consistency so that it is
                             * always called before a UI is removed.
                             * UI.isClosing() is thus always true in UI.detach()
                             * and associated detach listeners.
                             */
                            if (!ui.isClosing()) {
                                ui.close();
                            }
                            session.removeUI(ui);
                        }
                    });
                }
                // for now, use the session error handler; in the future, could
                // have an API for using some other handler for session init and
                // destroy listeners
                eventRouter.fireEvent(new SessionDestroyEvent(
                        VaadinService.this, session), session.getErrorHandler());

                session.setState(State.CLOSED);
            }
        });
    }

    /**
     * Removes a Vaadin service session destroy listener from this service.
     *
     * @see #addSessionDestroyListener(SessionDestroyListener)
     *
     * @param listener
     *            the vaadin service session destroy listener
     */
    public void removeSessionDestroyListener(SessionDestroyListener listener) {
        eventRouter.removeListener(SessionDestroyEvent.class, listener,
                SESSION_DESTROY_METHOD);
    }

    /**
     * Attempts to find a Vaadin service session associated with this request.
     * <p>
     * Handles locking of the session internally to avoid creation of duplicate
     * sessions by two threads simultaneously.
     * </p>
     *
     * @param request
     *            the request to get a vaadin service session for.
     *
     * @see VaadinSession
     *
     * @return the vaadin service session for the request, or <code>null</code>
     *         if no session is found and this is a request for which a new
     *         session shouldn't be created.
     */
    public VaadinSession findVaadinSession(VaadinRequest request)
            throws ServiceException, SessionExpiredException {
        VaadinSession vaadinSession = findOrCreateVaadinSession(request);
        if (vaadinSession == null) {
            return null;
        }

        VaadinSession.setCurrent(vaadinSession);
        request.setAttribute(VaadinSession.class.getName(), vaadinSession);

        return vaadinSession;
    }

    /**
     * Associates the given lock with this service and the given wrapped
     * session. This method should not be called more than once when the lock is
     * initialized for the session.
     *
     * @see #getSessionLock(WrappedSession)
     * @param wrappedSession
     *            The wrapped session the lock is associated with
     * @param lock
     *            The lock object
     */
    private void setSessionLock(WrappedSession wrappedSession, Lock lock) {
        if (wrappedSession == null) {
            throw new IllegalArgumentException(
                    "Can't set a lock for a null session");
        }
        Object currentSessionLock = wrappedSession
                .getAttribute(getLockAttributeName());
        assert (currentSessionLock == null || currentSessionLock == lock) : "Changing the lock for a session is not allowed";

        wrappedSession.setAttribute(getLockAttributeName(), lock);
    }

    /**
     * Returns the name used to store the lock in the HTTP session.
     *
     * @return The attribute name for the lock
     */
    private String getLockAttributeName() {
        return getServiceName() + ".lock";
    }

    /**
     * Gets the lock instance used to lock the VaadinSession associated with the
     * given wrapped session.
     * <p>
     * This method uses the wrapped session instead of VaadinSession to be able
     * to lock even before the VaadinSession has been initialized.
     * </p>
     *
     * @param wrappedSession
     *            The wrapped session
     * @return A lock instance used for locking access to the wrapped session
     */
    protected Lock getSessionLock(WrappedSession wrappedSession) {
        Object lock = wrappedSession.getAttribute(getLockAttributeName());

        if (lock instanceof ReentrantLock) {
            return (ReentrantLock) lock;
        }

        if (lock == null) {
            return null;
        }

        throw new RuntimeException(
                "Something else than a ReentrantLock was stored in the "
                        + getLockAttributeName() + " in the session");
    }

    /**
     * Locks the given session for this service instance. Typically you want to
     * call {@link VaadinSession#lock()} instead of this method.
     *
     * @param wrappedSession
     *            The session to lock
     *
     * @throws IllegalStateException
     *             if the session is invalidated before it can be locked
     */
    protected void lockSession(WrappedSession wrappedSession) {
        Lock lock = getSessionLock(wrappedSession);
        if (lock == null) {
            /*
             * No lock found in the session attribute. Ensure only one lock is
             * created and used by everybody by doing double checked locking.
             * Assumes there is a memory barrier for the attribute (i.e. that
             * the CPU flushes its caches and reads the value directly from main
             * memory).
             */
            synchronized (VaadinService.class) {
                lock = getSessionLock(wrappedSession);
                if (lock == null) {
                    lock = new ReentrantLock();
                    setSessionLock(wrappedSession, lock);
                }
            }
        }
        lock.lock();

        try {
            // Someone might have invalidated the session between fetching the
            // lock and acquiring it. Guard for this by calling a method that's
            // specified to throw IllegalStateException if invalidated
            // (#12282)
            wrappedSession.getAttribute(getLockAttributeName());
        } catch (IllegalStateException e) {
            lock.unlock();
            throw e;
        }
    }

    /**
     * Releases the lock for the given session for this service instance.
     * Typically you want to call {@link VaadinSession#unlock()} instead of this
     * method.
     *
     * @param wrappedSession
     *            The session to unlock
     */
    protected void unlockSession(WrappedSession wrappedSession) {
        assert getSessionLock(wrappedSession) != null;
        assert ((ReentrantLock) getSessionLock(wrappedSession))
                .isHeldByCurrentThread() : "Trying to unlock the session but it has not been locked by this thread";
        getSessionLock(wrappedSession).unlock();
    }

    private VaadinSession findOrCreateVaadinSession(VaadinRequest request)
            throws SessionExpiredException, ServiceException {
        boolean requestCanCreateSession = requestCanCreateSession(request);
        WrappedSession wrappedSession = getWrappedSession(request,
                requestCanCreateSession);

        try {
            lockSession(wrappedSession);
        } catch (IllegalStateException e) {
            throw new SessionExpiredException();
        }

        try {
            return doFindOrCreateVaadinSession(request, requestCanCreateSession);
        } finally {
            unlockSession(wrappedSession);
        }

    }

    /**
     * Finds or creates a Vaadin session. Assumes necessary synchronization has
     * been done by the caller to ensure this is not called simultaneously by
     * several threads.
     *
     * @param request
     * @param requestCanCreateSession
     * @return
     * @throws SessionExpiredException
     * @throws ServiceException
     */
    private VaadinSession doFindOrCreateVaadinSession(VaadinRequest request,
            boolean requestCanCreateSession) throws SessionExpiredException,
            ServiceException {
        assert ((ReentrantLock) getSessionLock(request.getWrappedSession()))
                .isHeldByCurrentThread() : "Session has not been locked by this thread";

        /* Find an existing session for this request. */
        VaadinSession session = getExistingSession(request,
                requestCanCreateSession);

        if (session != null) {
            /*
             * There is an existing session. We can use this as long as the user
             * not specifically requested to close or restart it.
             */

            final boolean restartApplication = hasParameter(request,
                    URL_PARAMETER_RESTART_APPLICATION)
                    && !hasParameter(request,
                            BootstrapHandler.IGNORE_RESTART_PARAM);
            final boolean closeApplication = hasParameter(request,
                    URL_PARAMETER_CLOSE_APPLICATION);

            if (restartApplication) {
                closeSession(session, request.getWrappedSession(false));
                return createAndRegisterSession(request);
            } else if (closeApplication) {
                closeSession(session, request.getWrappedSession(false));
                return null;
            } else {
                return session;
            }
        }

        // No existing session was found

        if (requestCanCreateSession) {
            /*
             * If the request is such that it should create a new session if one
             * as not found, we do that.
             */
            return createAndRegisterSession(request);
        } else {
            /*
             * The session was not found and a new one should not be created.
             * Assume the session has expired.
             */
            throw new SessionExpiredException();
        }

    }

    private static boolean hasParameter(VaadinRequest request,
            String parameterName) {
        return request.getParameter(parameterName) != null;
    }

    /**
     * Creates and registers a new VaadinSession for this service. Assumes
     * proper locking has been taken care of by the caller.
     *
     *
     * @param request
     *            The request which triggered session creation.
     * @return A new VaadinSession instance
     * @throws ServiceException
     */
    private VaadinSession createAndRegisterSession(VaadinRequest request)
            throws ServiceException {
        assert ((ReentrantLock) getSessionLock(request.getWrappedSession()))
                .isHeldByCurrentThread() : "Session has not been locked by this thread";

        VaadinSession session = createVaadinSession(request);

        VaadinSession.setCurrent(session);

        session.storeInSession(this, request.getWrappedSession());

        // Initial WebBrowser data comes from the request
        session.getBrowser().updateRequestDetails(request);

        // Initial locale comes from the request
        Locale locale = request.getLocale();
        session.setLocale(locale);
        session.setConfiguration(getDeploymentConfiguration());
        session.setCommunicationManager(new LegacyCommunicationManager(session));

        ServletPortletHelper.initDefaultUIProvider(session, this);
        onVaadinSessionStarted(request, session);

        return session;
    }

    /**
     * Get the base URL that should be used for sending requests back to this
     * service.
     * <p>
     * This is only used to support legacy cases.
     *
     * @param request
     * @return
     * @throws MalformedURLException
     *
     * @deprecated As of 7.0. Only used to support {@link LegacyApplication}.
     */
    @Deprecated
    protected URL getApplicationUrl(VaadinRequest request)
            throws MalformedURLException {
        return null;
    }

    /**
     * Creates a new Vaadin session for this service and request
     *
     * @param request
     *            The request for which to create a VaadinSession
     * @return A new VaadinSession
     * @throws ServiceException
     *
     */
    protected VaadinSession createVaadinSession(VaadinRequest request)
            throws ServiceException {
        return new VaadinSession(this);
    }

    private void onVaadinSessionStarted(VaadinRequest request,
            VaadinSession session) throws ServiceException {
        // for now, use the session error handler; in the future, could have an
        // API for using some other handler for session init and destroy
        // listeners

        eventRouter.fireEvent(new SessionInitEvent(this, session, request),
                session.getErrorHandler());

        ServletPortletHelper.checkUiProviders(session, this);
    }

    private void closeSession(VaadinSession vaadinSession,
            WrappedSession session) {
        if (vaadinSession == null) {
            return;
        }

        if (session != null) {
            vaadinSession.removeFromSession(this);
        }
    }

    protected VaadinSession getExistingSession(VaadinRequest request,
            boolean allowSessionCreation) throws SessionExpiredException {

        final WrappedSession session = getWrappedSession(request,
                allowSessionCreation);

        VaadinSession vaadinSession = VaadinSession
                .getForSession(this, session);

        if (vaadinSession == null) {
            return null;
        }

        return vaadinSession;
    }

    /**
     * Retrieves the wrapped session for the request.
     *
     * @param request
     *            The request for which to retrieve a session
     * @param requestCanCreateSession
     *            true to create a new session if one currently does not exist
     * @return The retrieved (or created) wrapped session
     * @throws SessionExpiredException
     *             If the request is not associated to a session and new session
     *             creation is not allowed
     */
    private WrappedSession getWrappedSession(VaadinRequest request,
            boolean requestCanCreateSession) throws SessionExpiredException {
        final WrappedSession session = request
                .getWrappedSession(requestCanCreateSession);
        if (session == null) {
            throw new SessionExpiredException();
        }
        return session;
    }

    /**
     * Checks whether it's valid to create a new service session as a result of
     * the given request.
     *
     * @param request
     *            the request
     * @return <code>true</code> if it's valid to create a new service session
     *         for the request; else <code>false</code>
     */
    protected abstract boolean requestCanCreateSession(VaadinRequest request);

    /**
     * Gets the currently used Vaadin service. The current service is
     * automatically defined when processing requests related to the service and
     * in threads started at a point when the current service is defined (see
     * {@link InheritableThreadLocal}). In other cases, (e.g. from background
     * threads started in some other way), the current service is not
     * automatically defined.
     *
     * @return the current Vaadin service instance if available, otherwise
     *         <code>null</code>
     *
     * @see #setCurrentInstances(VaadinRequest, VaadinResponse)
     */
    public static VaadinService getCurrent() {
        return CurrentInstance.get(VaadinService.class);
    }

    /**
     * Sets the this Vaadin service as the current service and also sets the
     * current Vaadin request and Vaadin response. This method is used by the
     * framework to set the current instances when a request related to the
     * service is processed and they are cleared when the request has been
     * processed.
     * <p>
     * The application developer can also use this method to define the current
     * instances outside the normal request handling, e.g. when initiating
     * custom background threads.
     * </p>
     *
     * @param request
     *            the Vaadin request to set as the current request, or
     *            <code>null</code> if no request should be set.
     * @param response
     *            the Vaadin response to set as the current response, or
     *            <code>null</code> if no response should be set.
     *
     * @see #getCurrent()
     * @see #getCurrentRequest()
     * @see #getCurrentResponse()
     */
    public void setCurrentInstances(VaadinRequest request,
            VaadinResponse response) {
        setCurrent(this);
        CurrentInstance.set(VaadinRequest.class, request);
        CurrentInstance.set(VaadinResponse.class, response);
    }

    /**
     * Sets the given Vaadin service as the current service.
     *
     * @param service
     */
    public static void setCurrent(VaadinService service) {
        CurrentInstance.setInheritable(VaadinService.class, service);
    }

    /**
     * Gets the currently processed Vaadin request. The current request is
     * automatically defined when the request is started. The current request
     * can not be used in e.g. background threads because of the way server
     * implementations reuse request instances.
     *
     * @return the current Vaadin request instance if available, otherwise
     *         <code>null</code>
     *
     * @see #setCurrentInstances(VaadinRequest, VaadinResponse)
     */
    public static VaadinRequest getCurrentRequest() {
        return CurrentInstance.get(VaadinRequest.class);
    }

    /**
     * Gets the currently processed Vaadin response. The current response is
     * automatically defined when the request is started. The current response
     * can not be used in e.g. background threads because of the way server
     * implementations reuse response instances.
     *
     * @return the current Vaadin response instance if available, otherwise
     *         <code>null</code>
     *
     * @see #setCurrentInstances(VaadinRequest, VaadinResponse)
     */
    public static VaadinResponse getCurrentResponse() {
        return CurrentInstance.get(VaadinResponse.class);
    }

    /**
     * Gets a unique name for this service. The name should be unique among
     * different services of the same type but the same for corresponding
     * instances running in different JVMs in a cluster. This is typically based
     * on e.g. the configured servlet's or portlet's name.
     *
     * @return the unique name of this service instance.
     */
    public abstract String getServiceName();

    /**
     * Finds the {@link UI} that belongs to the provided request. This is
     * generally only supported for UIDL requests as other request types are not
     * related to any particular UI or have the UI information encoded in a
     * non-standard way. The returned UI is also set as the current UI (
     * {@link UI#setCurrent(UI)}).
     *
     * @param request
     *            the request for which a UI is desired
     * @return the UI belonging to the request or null if no UI is found
     *
     */
    public UI findUI(VaadinRequest request) {
        // getForSession asserts that the lock is held
        VaadinSession session = VaadinSession.getForSession(this,
                request.getWrappedSession());

        // Get UI id from the request
        String uiIdString = request.getParameter(UIConstants.UI_ID_PARAMETER);
        UI ui = null;
        if (uiIdString != null && session != null) {
            int uiId = Integer.parseInt(uiIdString);
            ui = session.getUIById(uiId);
        }

        UI.setCurrent(ui);
        return ui;
    }

    /**
     * Check if the given UI should be associated with the
     * <code>window.name</code> so that it can be re-used if the browser window
     * is reloaded. This is typically determined by the UI provider which
     * typically checks the @{@link PreserveOnRefresh} annotation but UI
     * providers and ultimately VaadinService implementations may choose to
     * override the defaults.
     *
     * @param provider
     *            the UI provider responsible for the UI
     * @param event
     *            the UI create event with details about the UI
     *
     * @return <code>true</code> if the UI should be preserved on refresh;
     *         <code>false</code> if a new UI instance should be initialized on
     *         refreshed.
     */
    public boolean preserveUIOnRefresh(UIProvider provider, UICreateEvent event) {
        return provider.isPreservedOnRefresh(event);
    }

    /**
     * Discards the current session and creates a new session with the same
     * contents. The purpose of this is to introduce a new session key in order
     * to avoid session fixation attacks.
     * <p>
     * Please note that this method makes certain assumptions about how data is
     * stored in the underlying session and may thus not be compatible with some
     * environments.
     *
     * @param request
     *            The Vaadin request for which the session should be
     *            reinitialized
     */
    public static void reinitializeSession(VaadinRequest request) {
        WrappedSession oldSession = request.getWrappedSession();

        // Stores all attributes (security key, reference to this context
        // instance) so they can be added to the new session
        Set<String> attributeNames = oldSession.getAttributeNames();
        HashMap<String, Object> attrs = new HashMap<String, Object>(
                attributeNames.size() * 2);
        for (String name : attributeNames) {
            Object value = oldSession.getAttribute(name);
            if (value instanceof VaadinSession) {
                // set flag to avoid cleanup
                VaadinSession serviceSession = (VaadinSession) value;
                serviceSession.setAttribute(PRESERVE_UNBOUND_SESSION_ATTRIBUTE,
                        Boolean.TRUE);
            }
            attrs.put(name, value);
        }

        // Invalidate the current session
        oldSession.invalidate();

        // Create a new session
        WrappedSession newSession = request.getWrappedSession();

        // Restores all attributes (security key, reference to this context
        // instance)
        for (String name : attrs.keySet()) {
            Object value = attrs.get(name);
            newSession.setAttribute(name, value);

            // Ensure VaadinServiceSession knows where it's stored
            if (value instanceof VaadinSession) {
                VaadinSession serviceSession = (VaadinSession) value;
                VaadinService service = serviceSession.getService();
                // Use the same lock instance in the new session
                service.setSessionLock(newSession,
                        serviceSession.getLockInstance());

                serviceSession.storeInSession(service, newSession);
                serviceSession.setAttribute(PRESERVE_UNBOUND_SESSION_ATTRIBUTE,
                        null);
            }
        }

    }

    /**
     * TODO PUSH Document
     *
     * TODO Pass UI or VaadinSession?
     *
     * @param uI
     * @param themeName
     * @param resource
     * @return
     */
    public abstract InputStream getThemeResourceAsStream(UI uI,
            String themeName, String resource);

    /**
     * Creates and returns a unique ID for the DIV where the UI is to be
     * rendered.
     *
     * @param session
     *            The service session to which the bootstrapped UI will belong.
     * @param request
     *            The request for which a div id is needed
     * @param uiClass
     *            The class of the UI that will be bootstrapped
     *
     * @return the id to use in the DOM
     */
    public abstract String getMainDivId(VaadinSession session,
            VaadinRequest request, Class<? extends UI> uiClass);

    /**
     * Sets the given session to be closed and all its UI state to be discarded
     * at the end of the current request, or at the end of the next request if
     * there is no ongoing one.
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
     * @param session
     *            the session to close
     */
    public void closeSession(VaadinSession session) {
        session.close();
    }

    /**
     * Called at the end of a request, after sending the response. Closes
     * inactive UIs in the given session, removes closed UIs from the session,
     * and closes the session if it is itself inactive.
     *
     * @param session
     */
    void cleanupSession(VaadinSession session) {
        if (isSessionActive(session)) {
            closeInactiveUIs(session);
            removeClosedUIs(session);
        } else {
            if (session.getState() == State.OPEN) {
                closeSession(session);
                if (session.getSession() != null) {
                    getLogger().log(Level.FINE, "Closing inactive session {0}",
                            session.getSession().getId());
                }
            }
            if (session.getSession() != null) {
                /*
                 * If the VaadinSession has no WrappedSession then it has
                 * already been removed from the HttpSession and we do not have
                 * to do it again
                 */
                session.removeFromSession(this);
            }

            /*
             * The session was destroyed during this request and therefore no
             * destroy event has yet been sent
             */
            fireSessionDestroy(session);
        }
    }

    /**
     * Removes those UIs from the given session for which {@link UI#isClosing()
     * isClosing} yields true.
     *
     * @param session
     */
    private void removeClosedUIs(final VaadinSession session) {
        ArrayList<UI> uis = new ArrayList<UI>(session.getUIs());
        for (final UI ui : uis) {
            if (ui.isClosing()) {
                ui.accessSynchronously(new Runnable() {
                    @Override
                    public void run() {
                        getLogger().log(Level.FINER, "Removing closed UI {0}",
                                ui.getUIId());
                        session.removeUI(ui);
                    }
                });
            }
        }
    }

    /**
     * Closes those UIs in the given session for which {@link #isUIActive}
     * yields false.
     *
     * @since 7.0.0
     */
    private void closeInactiveUIs(VaadinSession session) {
        final String sessionId = session.getSession().getId();
        for (final UI ui : session.getUIs()) {
            if (!isUIActive(ui) && !ui.isClosing()) {
                ui.accessSynchronously(new Runnable() {
                    @Override
                    public void run() {
                        getLogger().log(Level.FINE,
                                "Closing inactive UI #{0} in session {1}",
                                new Object[] { ui.getUIId(), sessionId });
                        ui.close();
                    }
                });
            }
        }
    }

    /**
     * Returns the number of seconds that must pass without a valid heartbeat or
     * UIDL request being received from a UI before that UI is removed from its
     * session. This is a lower bound; it might take longer to close an inactive
     * UI. Returns a negative number if heartbeat is disabled and timeout never
     * occurs.
     *
     * @see DeploymentConfiguration#getHeartbeatInterval()
     *
     * @since 7.0.0
     *
     * @return The heartbeat timeout in seconds or a negative number if timeout
     *         never occurs.
     */
    private int getHeartbeatTimeout() {
        // Permit three missed heartbeats before closing the UI
        return (int) (getDeploymentConfiguration().getHeartbeatInterval() * (3.1));
    }

    /**
     * Returns the number of seconds that must pass without a valid UIDL request
     * being received for the given session before the session is closed, even
     * though heartbeat requests are received. This is a lower bound; it might
     * take longer to close an inactive session.
     * <p>
     * Returns a negative number if there is no timeout. In this case heartbeat
     * requests suffice to keep the session alive, but it will still eventually
     * expire in the regular manner if there are no requests at all (see
     * {@link WrappedSession#getMaxInactiveInterval()}).
     *
     * @see DeploymentConfiguration#isCloseIdleSessions()
     * @see #getHeartbeatTimeout()
     *
     * @since 7.0.0
     *
     * @return The UIDL request timeout in seconds, or a negative number if
     *         timeout never occurs.
     */
    private int getUidlRequestTimeout(VaadinSession session) {
        return getDeploymentConfiguration().isCloseIdleSessions() ? session
                .getSession().getMaxInactiveInterval() : -1;
    }

    /**
     * Returns whether the given UI is active (the client-side actively
     * communicates with the server) or whether it can be removed from the
     * session and eventually collected.
     * <p>
     * A UI is active if and only if its {@link UI#isClosing() isClosing}
     * returns false and {@link #getHeartbeatTimeout() getHeartbeatTimeout} is
     * negative or has not yet expired.
     *
     * @since 7.0.0
     *
     * @param ui
     *            The UI whose status to check
     *
     * @return true if the UI is active, false if it could be removed.
     */
    private boolean isUIActive(UI ui) {
        if (ui.isClosing()) {
            return false;
        } else {
            long now = System.currentTimeMillis();
            int timeout = 1000 * getHeartbeatTimeout();
            return timeout < 0
                    || now - ui.getLastHeartbeatTimestamp() < timeout;
        }
    }

    /**
     * Returns whether the given session is active or whether it can be closed.
     * <p>
     * A session is active if and only if its {@link #isClosing} returns false
     * and {@link #getUidlRequestTimeout(VaadinSession) getUidlRequestTimeout}
     * is negative or has not yet expired.
     *
     * @param session
     *            The session whose status to check
     *
     * @return true if the session is active, false if it could be closed.
     */
    private boolean isSessionActive(VaadinSession session) {
        if (session.getState() != State.OPEN || session.getSession() == null) {
            return false;
        } else {
            long now = System.currentTimeMillis();
            int timeout = 1000 * getUidlRequestTimeout(session);
            return timeout < 0
                    || now - session.getLastRequestTimestamp() < timeout;
        }
    }

    private static final Logger getLogger() {
        return Logger.getLogger(VaadinService.class.getName());
    }

    /**
     * Called before the framework starts handling a request
     *
     * @param request
     *            The request
     * @param response
     *            The response
     */
    public void requestStart(VaadinRequest request, VaadinResponse response) {
        if (!initialized) {
            throw new IllegalStateException(
                    "Can not process requests before init() has been called");
        }
        setCurrentInstances(request, response);
        request.setAttribute(REQUEST_START_TIME_ATTRIBUTE, System.nanoTime());
    }

    /**
     * Called after the framework has handled a request and the response has
     * been written.
     *
     * @param request
     *            The request object
     * @param response
     *            The response object
     * @param session
     *            The session which was used during the request or null if the
     *            request did not use a session
     */
    public void requestEnd(VaadinRequest request, VaadinResponse response,
            VaadinSession session) {
        if (session != null) {
            assert VaadinSession.getCurrent() == session;
            session.lock();
            try {
                cleanupSession(session);
                final long duration = (System.nanoTime() - (Long) request
                        .getAttribute(REQUEST_START_TIME_ATTRIBUTE)) / 1000000;
                session.setLastRequestDuration(duration);
            } finally {
                session.unlock();
            }
        }
        CurrentInstance.clearAll();
    }

    /**
     * Returns the request handlers that are registered with this service. The
     * iteration order of the returned collection is the same as the order in
     * which the request handlers will be invoked when a request is handled.
     *
     * @return a collection of request handlers in the order they are invoked
     *
     * @see #createRequestHandlers()
     *
     * @since 7.1
     */
    public Iterable<RequestHandler> getRequestHandlers() {
        return requestHandlers;
    }

    /**
     * Handles the incoming request and writes the response into the response
     * object. Uses {@link #getRequestHandlers()} for handling the request.
     * <p>
     * If a session expiration is detected during request handling then each
     * {@link RequestHandler request handler} has an opportunity to handle the
     * expiration event if it implements {@link SessionExpiredHandler}. If no
     * request handler handles session expiration a default expiration message
     * will be written.
     * </p>
     *
     * @param request
     *            The incoming request
     * @param response
     *            The outgoing response
     * @throws ServiceException
     *             Any exception that occurs during response handling will be
     *             wrapped in a ServiceException
     */
    public void handleRequest(VaadinRequest request, VaadinResponse response)
            throws ServiceException {
        requestStart(request, response);

        VaadinSession vaadinSession = null;
        try {
            // Find out the service session this request is related to
            vaadinSession = findVaadinSession(request);
            if (vaadinSession == null) {
                return;
            }

            for (RequestHandler handler : getRequestHandlers()) {
                if (handler.handleRequest(vaadinSession, request, response)) {
                    return;
                }
            }

            // Request not handled by any RequestHandler
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Request was not handled by any registered handler.");

        } catch (final SessionExpiredException e) {
            handleSessionExpired(request, response);
        } catch (final Throwable e) {
            handleExceptionDuringRequest(request, response, vaadinSession, e);
        } finally {
            requestEnd(request, response, vaadinSession);
        }
    }

    private void handleExceptionDuringRequest(VaadinRequest request,
            VaadinResponse response, VaadinSession vaadinSession, Throwable t)
            throws ServiceException {
        if (vaadinSession != null) {
            vaadinSession.lock();
        }
        try {
            ErrorHandler errorHandler = ErrorEvent
                    .findErrorHandler(vaadinSession);

            // if this was an UIDL request, send UIDL back to the client
            if (ServletPortletHelper.isUIDLRequest(request)) {
                SystemMessages ci = getSystemMessages(
                        ServletPortletHelper.findLocale(null, vaadinSession,
                                request), request);
                try {
                    writeStringResponse(
                            response,
                            JsonConstants.JSON_CONTENT_TYPE,
                            createCriticalNotificationJSON(
                                    ci.getInternalErrorCaption(),
                                    ci.getInternalErrorMessage(), null,
                                    ci.getInternalErrorURL()));
                } catch (IOException e) {
                    // An exception occured while writing the response. Log
                    // it and continue handling only the original error.
                    getLogger()
                            .log(Level.WARNING,
                                    "Failed to write critical notification response to the client",
                                    e);
                }
                if (errorHandler != null) {
                    errorHandler.error(new ErrorEvent(t));
                }
            } else {
                if (errorHandler != null) {
                    errorHandler.error(new ErrorEvent(t));
                }

                // Re-throw other exceptions
                throw new ServiceException(t);
            }
        } finally {
            if (vaadinSession != null) {
                vaadinSession.unlock();
            }
        }

    }

    /**
     * Writes the given string as a response using the given content type.
     *
     * @param response
     *            The response reference
     * @param contentType
     *            The content type of the response
     * @param reponseString
     *            The actual response
     * @throws IOException
     *             If an error occured while writing the response
     */
    public void writeStringResponse(VaadinResponse response,
            String contentType, String reponseString) throws IOException {

        response.setContentType(contentType);

        final OutputStream out = response.getOutputStream();
        final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")));
        outWriter.print(reponseString);
        outWriter.close();
    }

    /**
     * Called when the session has expired and the request handling is therefore
     * aborted.
     *
     * @param request
     *            The request
     * @param response
     *            The response
     * @throws ServiceException
     *             Thrown if there was any problem handling the expiration of
     *             the session
     */
    protected void handleSessionExpired(VaadinRequest request,
            VaadinResponse response) throws ServiceException {
        for (RequestHandler handler : getRequestHandlers()) {
            if (handler instanceof SessionExpiredHandler) {
                try {
                    if (((SessionExpiredHandler) handler).handleSessionExpired(
                            request, response)) {
                        return;
                    }
                } catch (IOException e) {
                    throw new ServiceException(
                            "Handling of session expired failed", e);
                }
            }
        }

        // No request handlers handled the request. Write a normal HTTP response

        try {
            // If there is a URL, try to redirect there
            SystemMessages systemMessages = getSystemMessages(
                    ServletPortletHelper.findLocale(null, null, request),
                    request);
            String sessionExpiredURL = systemMessages.getSessionExpiredURL();
            if (sessionExpiredURL != null
                    && (response instanceof VaadinServletResponse)) {
                ((VaadinServletResponse) response)
                        .sendRedirect(sessionExpiredURL);
            } else {
                /*
                 * Session expired as a result of a standard http request and we
                 * have nowhere to redirect. Reloading would likely cause an
                 * endless loop. This can at least happen if refreshing a
                 * resource when the session has expired.
                 */
                response.sendError(HttpServletResponse.SC_GONE,
                        "Session expired");
            }
        } catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Creates a JSON message which, when sent to client as-is, will cause a
     * critical error to be shown with the given details.
     *
     * @param caption
     *            The caption of the error or null to omit
     * @param message
     *            The error message or null to omit
     * @param details
     *            Additional error details or null to omit
     * @param url
     *            A url to redirect to. If no other details are given then the
     *            user will be immediately redirected to this URL. Otherwise the
     *            message will be shown and the browser will redirect to the
     *            given URL only after the user acknowledges the message. If
     *            null then the browser will refresh the current page.
     * @return A JSON string to be sent to the client
     */
    public static String createCriticalNotificationJSON(String caption,
            String message, String details, String url) {
        String returnString = "";
        try {
            if (message == null) {
                message = details;
            } else if (details != null) {
                message += "<br/><br/>" + details;
            }

            JSONObject appError = new JSONObject();
            appError.put("caption", caption);
            appError.put("message", message);
            appError.put("url", url);

            JSONObject meta = new JSONObject();
            meta.put("appError", appError);

            JSONObject json = new JSONObject();
            json.put("changes", new JSONObject());
            json.put("resources", new JSONObject());
            json.put("locales", new JSONObject());
            json.put("meta", meta);
            json.put(ApplicationConstants.SERVER_SYNC_ID, -1);
            returnString = json.toString();
        } catch (JSONException e) {
            getLogger().log(Level.WARNING,
                    "Error creating critical notification JSON message", e);
        }

        return "for(;;);[" + returnString + "]";
    }

    /**
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public void criticalNotification(VaadinRequest request,
            VaadinResponse response, String caption, String message,
            String details, String url) throws IOException {
        writeStringResponse(response, JsonConstants.JSON_CONTENT_TYPE,
                createCriticalNotificationJSON(caption, message, details, url));
    }

    /**
     * Enables push if push support is available and push has not yet been
     * enabled.
     *
     * If push support is not available, a warning explaining the situation will
     * be logged at least the first time this method is invoked.
     *
     * @return <code>true</code> if push can be used; <code>false</code> if push
     *         is not available.
     */
    public boolean ensurePushAvailable() {
        if (!pushWarningEmitted) {
            pushWarningEmitted = true;
            getLogger().log(Level.WARNING, Constants.PUSH_NOT_SUPPORTED_ERROR,
                    getClass().getSimpleName());
        }
        // Not supported by default for now, sublcasses may override
        return false;
    }

    /**
     * Checks that another {@link VaadinSession} instance is not locked. This is
     * internally used by {@link VaadinSession#accessSynchronously(Runnable)}
     * and {@link UI#accessSynchronously(Runnable)} to help avoid causing
     * deadlocks.
     *
     * @since 7.1
     * @param session
     *            the session that is being locked
     * @throws IllegalStateException
     *             if the current thread holds the lock for another session
     */
    public static void verifyNoOtherSessionLocked(VaadinSession session) {
        if (isOtherSessionLocked(session)) {
            throw new IllegalStateException(
                    "Can't access session while another session is locked by the same thread. This restriction is intended to help avoid deadlocks.");
        }
    }

    /**
     * Checks whether there might be some {@link VaadinSession} other than the
     * provided one for which the current thread holds a lock. This method might
     * not detect all cases where some other session is locked, but it should
     * cover the most typical situations.
     *
     * @since 7.2
     * @param session
     *            the session that is expected to be locked
     * @return <code>true</code> if another session is also locked by the
     *         current thread; <code>false</code> if no such session was found
     */
    public static boolean isOtherSessionLocked(VaadinSession session) {
        VaadinSession otherSession = VaadinSession.getCurrent();
        if (otherSession == null || otherSession == session) {
            return false;
        }
        return otherSession.hasLock();
    }

    /**
     * Verifies that the given CSRF token (aka double submit cookie) is valid
     * for the given session. This is used to protect against Cross Site Request
     * Forgery attacks.
     * <p>
     * This protection is enabled by default, but it might need to be disabled
     * to allow a certain type of testing. For these cases, the check can be
     * disabled by setting the init parameter
     * <code>disable-xsrf-protection</code> to <code>true</code>.
     *
     * @see DeploymentConfiguration#isXsrfProtectionEnabled()
     *
     * @since 7.1
     *
     * @param session
     *            the vaadin session for which the check should be done
     * @param requestToken
     *            the CSRF token provided in the request
     * @return <code>true</code> if the token is valid or if the protection is
     *         disabled; <code>false</code> if protection is enabled and the
     *         token is invalid
     */
    public static boolean isCsrfTokenValid(VaadinSession session,
            String requestToken) {

        if (session.getService().getDeploymentConfiguration()
                .isXsrfProtectionEnabled()) {
            String sessionToken = session.getCsrfToken();

            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Implementation for {@link VaadinSession#access(Runnable)}. This method is
     * implemented here instead of in {@link VaadinSession} to enable overriding
     * the implementation without using a custom subclass of VaadinSession.
     *
     * @since 7.1
     * @see VaadinSession#access(Runnable)
     *
     * @param session
     *            the vaadin session to access
     * @param runnable
     *            the runnable to run with the session locked
     *
     * @return a future that can be used to check for task completion and to
     *         cancel the task
     */
    public Future<Void> accessSession(VaadinSession session, Runnable runnable) {
        FutureAccess future = new FutureAccess(session, runnable);
        session.getPendingAccessQueue().add(future);

        ensureAccessQueuePurged(session);

        return future;
    }

    /**
     * Makes sure the pending access queue is purged for the provided session.
     * If the session is currently locked by the current thread or some other
     * thread, the queue will be purged when the session is unlocked. If the
     * lock is not held by any thread, it is acquired and the queue is purged
     * right away.
     *
     * @since 7.1.2
     * @param session
     *            the session for which the access queue should be purged
     */
    public void ensureAccessQueuePurged(VaadinSession session) {
        /*
         * If no thread is currently holding the lock, pending changes for UIs
         * with automatic push would not be processed and pushed until the next
         * time there is a request or someone does an explicit push call.
         * 
         * To remedy this, we try to get the lock at this point. If the lock is
         * currently held by another thread, we just back out as the queue will
         * get purged once it is released. If the lock is held by the current
         * thread, we just release it knowing that the queue gets purged once
         * the lock is ultimately released. If the lock is not held by any
         * thread and we acquire it, we just release it again to purge the queue
         * right away.
         */
        try {
            // tryLock() would be shorter, but it does not guarantee fairness
            if (session.getLockInstance().tryLock(0, TimeUnit.SECONDS)) {
                // unlock triggers runPendingAccessTasks
                session.unlock();
            }
        } catch (InterruptedException e) {
            // Just ignore
        }
    }

    /**
     * Purges the queue of pending access invocations enqueued with
     * {@link VaadinSession#access(Runnable)}.
     * <p>
     * This method is automatically run by the framework at appropriate
     * situations and is not intended to be used by application developers.
     *
     * @param session
     *            the vaadin session to purge the queue for
     * @since 7.1
     */
    public void runPendingAccessTasks(VaadinSession session) {
        assert session.hasLock();

        if (session.getPendingAccessQueue().isEmpty()) {
            return;
        }

        Map<Class<?>, CurrentInstance> oldInstances = CurrentInstance
                .getInstances(false);

        FutureAccess pendingAccess;
        try {
            while ((pendingAccess = session.getPendingAccessQueue().poll()) != null) {
                if (!pendingAccess.isCancelled()) {
                    CurrentInstance.clearAll();
                    CurrentInstance.restoreInstances(pendingAccess
                            .getCurrentInstances());
                    CurrentInstance.setCurrent(session);
                    pendingAccess.run();

                    try {
                        pendingAccess.get();

                    } catch (Exception exception) {
                        pendingAccess.handleError(exception);
                    }
                }
            }
        } finally {
            CurrentInstance.clearAll();
            CurrentInstance.restoreInstances(oldInstances);
        }
    }

    /**
     * Adds a service destroy listener that gets notified when this service is
     * destroyed.
     *
     * @since 7.2
     * @param listener
     *            the service destroy listener to add
     *
     * @see #destroy()
     * @see #removeServiceDestroyListener(ServiceDestroyListener)
     * @see ServiceDestroyListener
     */
    public void addServiceDestroyListener(ServiceDestroyListener listener) {
        eventRouter.addListener(ServiceDestroyEvent.class, listener,
                SERVICE_DESTROY_METHOD);
    }

    /**
     * Removes a service destroy listener that was previously added with
     * {@link #addServiceDestroyListener(ServiceDestroyListener)}.
     *
     * @since 7.2
     * @param listener
     *            the service destroy listener to remove
     */
    public void removeServiceDestroyListener(ServiceDestroyListener listener) {
        eventRouter.removeListener(ServiceDestroyEvent.class, listener,
                SERVICE_DESTROY_METHOD);
    }

    /**
     * Called when the servlet, portlet or similar for this service is being
     * destroyed. After this method has been called, no more requests will be
     * handled by this service.
     *
     * @see #addServiceDestroyListener(ServiceDestroyListener)
     * @see Servlet#destroy()
     * @see Portlet#destroy()
     *
     * @since 7.2
     */
    public void destroy() {
        eventRouter.fireEvent(new ServiceDestroyEvent(this));
    }

    /**
     * Tries to acquire default class loader and sets it as a class loader for
     * this {@link VaadinService} if found. If current security policy disallows
     * acquiring class loader instance it will log a message and re-throw
     * {@link SecurityException}
     * 
     * @throws SecurityException
     *             If current security policy forbids acquiring class loader
     * 
     * @since 7.3.5
     */
    protected void setDefaultClassLoader() {
        try {
            setClassLoader(VaadinServiceClassLoaderUtil
                    .findDefaultClassLoader());
        } catch (SecurityException e) {
            getLogger().log(Level.SEVERE,
                    Constants.CANNOT_ACQUIRE_CLASSLOADER_SEVERE, e);
            throw e;
        }
    }
}
