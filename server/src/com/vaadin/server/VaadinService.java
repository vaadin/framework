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

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.portlet.PortletContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

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
    private static final String REINITIALIZING_SESSION_MARKER = VaadinService.class
            .getName() + ".reinitializing";

    private static final Method SESSION_INIT_METHOD = ReflectTools.findMethod(
            SessionInitListener.class, "sessionInit", SessionInitEvent.class);

    private static final Method SESSION_DESTROY_METHOD = ReflectTools
            .findMethod(SessionDestroyListener.class, "sessionDestroy",
                    SessionDestroyEvent.class);

    /**
     * @deprecated Only supported for {@link LegacyApplication}.
     */
    @Deprecated
    public static final String URL_PARAMETER_RESTART_APPLICATION = "restartApplication";

    /**
     * @deprecated Only supported for {@link LegacyApplication}.
     */
    @Deprecated
    public static final String URL_PARAMETER_CLOSE_APPLICATION = "closeApplication";

    private final DeploymentConfiguration deploymentConfiguration;

    private final EventRouter eventRouter = new EventRouter();

    private SystemMessagesProvider systemMessagesProvider = DefaultSystemMessagesProvider
            .get();

    /**
     * Creates a new vaadin service based on a deployment configuration
     * 
     * @param deploymentConfiguration
     *            the deployment configuration for the service
     */
    public VaadinService(DeploymentConfiguration deploymentConfiguration) {
        this.deploymentConfiguration = deploymentConfiguration;
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
     * Get the class loader to use for loading classes loaded by name, e.g.
     * custom UI classes. <code>null</code> indicates that the default class
     * loader should be used.
     * 
     * @return the class loader to use, or <code>null</code>
     */
    public ClassLoader getClassLoader() {
        final String classLoaderName = getDeploymentConfiguration()
                .getApplicationOrSystemProperty("ClassLoader", null);
        ClassLoader classLoader;
        if (classLoaderName == null) {
            classLoader = getClass().getClassLoader();
        } else {
            try {
                final Class<?> classLoaderClass = getClass().getClassLoader()
                        .loadClass(classLoaderName);
                final Constructor<?> c = classLoaderClass
                        .getConstructor(new Class[] { ClassLoader.class });
                classLoader = (ClassLoader) c
                        .newInstance(new Object[] { getClass().getClassLoader() });
            } catch (final Exception e) {
                throw new RuntimeException(
                        "Could not find specified class loader: "
                                + classLoaderName, e);
            }
        }
        return classLoader;
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

    public void fireSessionDestroy(VaadinSession vaadinSession) {
        // Ignore if the session is being moved to a different backing session
        if (vaadinSession.getAttribute(REINITIALIZING_SESSION_MARKER) == Boolean.TRUE) {
            return;
        }

        for (UI ui : new ArrayList<UI>(vaadinSession.getUIs())) {
            vaadinSession.cleanupUI(ui);
        }

        eventRouter.fireEvent(new SessionDestroyEvent(this, vaadinSession));
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

    private VaadinSession findOrCreateVaadinSession(VaadinRequest request)
            throws SessionExpiredException, ServiceException {
        boolean requestCanCreateSession = requestCanCreateSession(request);

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

    private VaadinSession createAndRegisterSession(VaadinRequest request)
            throws ServiceException {
        VaadinSession session = createVaadinSession(request);

        VaadinSession.setCurrent(session);

        session.storeInSession(this, request.getWrappedSession());

        // Initial locale comes from the request
        Locale locale = request.getLocale();
        session.setLocale(locale);
        session.setConfiguration(getDeploymentConfiguration());
        session.setCommunicationManager(createCommunicationManager(session));

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
     * @deprecated Only used to support {@link LegacyApplication}.
     */
    @Deprecated
    protected URL getApplicationUrl(VaadinRequest request)
            throws MalformedURLException {
        return null;
    }

    /**
     * Create a communication manager to use for the given service session.
     * 
     * @param session
     *            the service session for which a new communication manager is
     *            needed
     * @return a new communication manager
     */
    protected abstract AbstractCommunicationManager createCommunicationManager(
            VaadinSession session);

    /**
     * Creates a new Vaadin service session.
     * 
     * @param request
     * @return
     * @throws ServletException
     * @throws MalformedURLException
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

        // Ensures that the session is still valid
        final WrappedSession session = request
                .getWrappedSession(allowSessionCreation);
        if (session == null) {
            throw new SessionExpiredException();
        }

        VaadinSession vaadinSession = VaadinSession
                .getForSession(this, session);

        if (vaadinSession == null) {
            return null;
        }

        return vaadinSession;
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
        CurrentInstance.setInheritable(VaadinService.class, this);
        CurrentInstance.set(VaadinRequest.class, request);
        CurrentInstance.set(VaadinResponse.class, response);
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
                serviceSession.storeInSession(serviceSession.getService(),
                        newSession);
                serviceSession
                        .setAttribute(REINITIALIZING_SESSION_MARKER, null);
            }
        }

    }

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
     * Closes the VaadinServiceSession and discards all associated UI state.
     * After the session has been discarded, any UIs that have been left open
     * will give an Out of sync error (
     * {@link SystemMessages#getOutOfSyncCaption()}) error and a new session
     * will be created for serving new UIs.
     * <p>
     * To avoid causing out of sync errors, you should typically redirect to
     * some other page using {@link Page#setLocation(String)} to make the
     * browser unload the invalidated UI.
     * 
     * @param session
     *            the session to close
     */
    public void closeSession(VaadinSession session) {
        session.removeFromSession(this);
    }
}
