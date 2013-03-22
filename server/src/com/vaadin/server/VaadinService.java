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

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.PortletContext;
import javax.servlet.ServletContext;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.event.EventRouter;
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
    static final String REINITIALIZING_SESSION_MARKER = VaadinService.class
            .getName() + ".reinitializing";

    private static final Method SESSION_INIT_METHOD = ReflectTools.findMethod(
            SessionInitListener.class, "sessionInit", SessionInitEvent.class);

    private static final Method SESSION_DESTROY_METHOD = ReflectTools
            .findMethod(SessionDestroyListener.class, "sessionDestroy",
                    SessionDestroyEvent.class);

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
     * Creates the bootstrap handler that should be used to generate the initial
     * HTML bootstrapping a new {@link UI} in the given session.
     */
    protected abstract BootstrapHandler createBootstrapHandler(
            VaadinSession session);

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
        session.runSafely(new Runnable() {
            @Override
            public void run() {
                ArrayList<UI> uis = new ArrayList<UI>(session.getUIs());
                for (final UI ui : uis) {
                    ui.runSafely(new Runnable() {
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
                eventRouter.fireEvent(new SessionDestroyEvent(
                        VaadinService.this, session));
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
        assert wrappedSession != null : "Can't set a lock for a null session";
        assert wrappedSession.getAttribute(getLockAttributeName()) == null : "Changing the lock for a session is not allowed";

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

        lockSession(wrappedSession);
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

            final boolean restartApplication = (request
                    .getParameter(URL_PARAMETER_RESTART_APPLICATION) != null);
            final boolean closeApplication = (request
                    .getParameter(URL_PARAMETER_CLOSE_APPLICATION) != null);

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
        eventRouter.fireEvent(new SessionInitEvent(this, session, request));

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
     * @return the UI belonging to the request
     * 
     */
    public UI findUI(VaadinRequest request) {
        VaadinSession session = VaadinSession.getForSession(this,
                request.getWrappedSession());

        // Get UI id from the request
        String uiIdString = request.getParameter(UIConstants.UI_ID_PARAMETER);
        int uiId = Integer.parseInt(uiIdString);

        // Get lock before accessing data in session
        session.lock();
        try {
            UI ui = session.getUIById(uiId);

            UI.setCurrent(ui);
            return ui;
        } finally {
            session.unlock();
        }
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
        HashMap<String, Object> attrs = new HashMap<String, Object>();
        for (String name : oldSession.getAttributeNames()) {
            Object value = oldSession.getAttribute(name);
            if (value instanceof VaadinSession) {
                // set flag to avoid cleanup
                VaadinSession serviceSession = (VaadinSession) value;
                serviceSession.setAttribute(REINITIALIZING_SESSION_MARKER,
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
                serviceSession
                        .setAttribute(REINITIALIZING_SESSION_MARKER, null);
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
            if (!session.isClosing()) {
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
    private void removeClosedUIs(VaadinSession session) {
        for (UI ui : new ArrayList<UI>(session.getUIs())) {
            if (ui.isClosing()) {
                getLogger().log(Level.FINER, "Removing closed UI {0}",
                        ui.getUIId());
                session.removeUI(ui);
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
        String sessionId = session.getSession().getId();
        for (UI ui : session.getUIs()) {
            if (!isUIActive(ui) && !ui.isClosing()) {
                getLogger().log(Level.FINE,
                        "Closing inactive UI #{0} in session {1}",
                        new Object[] { ui.getUIId(), sessionId });
                ui.close();
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
        if (session.isClosing() || session.getSession() == null) {
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
            cleanupSession(session);
            long duration = (System.nanoTime() - (Long) request
                    .getAttribute(REQUEST_START_TIME_ATTRIBUTE)) / 1000000;
            session.setLastRequestDuration(duration);
        }
        CurrentInstance.clearAll();
    }
}
