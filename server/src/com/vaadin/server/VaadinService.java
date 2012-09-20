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
import java.util.Iterator;
import java.util.Locale;
import java.util.ServiceLoader;

import javax.portlet.PortletContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.vaadin.LegacyApplication;
import com.vaadin.event.EventRouter;
import com.vaadin.server.VaadinSession.SessionStartEvent;
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

    private static final Method SESSION_INIT_METHOD = ReflectTools.findMethod(
            VaadinSessionInitializationListener.class,
            "vaadinSessionInitialized", VaadinSessionInitializeEvent.class);

    private static final Method SESSION_DESTROY_METHOD = ReflectTools
            .findMethod(VaadinSessionDestroyListener.class,
                    "vaadinSessionDestroyed", VaadinSessionDestroyEvent.class);

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

    private AddonContext addonContext;
    private final DeploymentConfiguration deploymentConfiguration;

    private final EventRouter eventRouter = new EventRouter();

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

    public Iterator<AddonContextListener> getAddonContextListeners() {
        // Called once for init and then no more, so there's no point in caching
        // the instance
        ServiceLoader<AddonContextListener> contextListenerLoader = ServiceLoader
                .load(AddonContextListener.class, getClassLoader());
        return contextListenerLoader.iterator();
    }

    public AddonContext getAddonContext() {
        return addonContext;
    }

    public void setAddonContext(AddonContext addonContext) {
        this.addonContext = addonContext;
    }

    /**
     * Gets the system messages object
     * 
     * @return the system messages object
     */
    public abstract SystemMessages getSystemMessages();

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
     * Adds a listener that gets notified when a new Vaadin session is
     * initialized for this service.
     * <p>
     * Because of the way different service instances share the same session,
     * the listener is not necessarily notified immediately when the session is
     * created but only when the first request for that session is handled by
     * this service.
     * 
     * @see #removeVaadinSessionInitializationListener(VaadinSessionInitializationListener)
     * @see VaadinSessionInitializationListener
     * 
     * @param listener
     *            the vaadin session initialization listener
     */
    public void addVaadinSessionInitializationListener(
            VaadinSessionInitializationListener listener) {
        eventRouter.addListener(VaadinSessionInitializeEvent.class, listener,
                SESSION_INIT_METHOD);
    }

    /**
     * Removes a Vaadin session initialization listener from this service.
     * 
     * @see #addVaadinSessionInitializationListener(VaadinSessionInitializationListener)
     * 
     * @param listener
     *            the Vaadin session initialization listener to remove.
     */
    public void removeVaadinSessionInitializationListener(
            VaadinSessionInitializationListener listener) {
        eventRouter.removeListener(VaadinSessionInitializeEvent.class,
                listener, SESSION_INIT_METHOD);
    }

    /**
     * Adds a listener that gets notified when a Vaadin session that has been
     * initialized for this service is destroyed.
     * 
     * @see #addVaadinSessionInitializationListener(VaadinSessionInitializationListener)
     * 
     * @param listener
     *            the vaadin session destroy listener
     */
    public void addVaadinSessionDestroyListener(
            VaadinSessionDestroyListener listener) {
        eventRouter.addListener(VaadinSessionDestroyEvent.class, listener,
                SESSION_DESTROY_METHOD);
    }

    public void fireSessionDestroy(VaadinSession vaadinSession) {
        for (UI ui : new ArrayList<UI>(vaadinSession.getUIs())) {
            vaadinSession.cleanupUI(ui);
        }

        eventRouter
                .fireEvent(new VaadinSessionDestroyEvent(this, vaadinSession));
    }

    /**
     * Removes a Vaadin session destroy listener from this service.
     * 
     * @see #addVaadinSessionDestroyListener(VaadinSessionDestroyListener)
     * 
     * @param listener
     *            the vaadin session destroy listener
     */
    public void removeVaadinSessionDestroyListener(
            VaadinSessionDestroyListener listener) {
        eventRouter.removeListener(VaadinSessionDestroyEvent.class, listener,
                SESSION_DESTROY_METHOD);
    }

    /**
     * Attempts to find a Vaadin session associated with this request.
     * 
     * @param request
     *            the request to get a vaadin session for.
     * 
     * @see VaadinSession
     * 
     * @return the vaadin session for the request, or <code>null</code> if no
     *         session is found and this is a request for which a new session
     *         shouldn't be created.
     */
    public VaadinSession findVaadinSession(VaadinRequest request)
            throws ServiceException, SessionExpiredException {

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

        ServletPortletHelper.initDefaultUIProvider(session, this);

        session.setVaadinService(this);
        session.storeInSession(request.getWrappedSession());

        URL applicationUrl;
        try {
            applicationUrl = getApplicationUrl(request);
        } catch (MalformedURLException e) {
            throw new ServiceException(e);
        }

        // Initial locale comes from the request
        Locale locale = request.getLocale();
        session.setLocale(locale);
        session.start(new SessionStartEvent(applicationUrl,
                getDeploymentConfiguration(),
                createCommunicationManager(session)));

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
     * Create a communication manager to use for the given Vaadin session.
     * 
     * @param session
     *            the vaadin session for which a new communication manager is
     *            needed
     * @return a new communication manager
     */
    protected abstract AbstractCommunicationManager createCommunicationManager(
            VaadinSession session);

    /**
     * Creates a new vaadin session.
     * 
     * @param request
     * @return
     * @throws ServletException
     * @throws MalformedURLException
     */
    protected abstract VaadinSession createVaadinSession(VaadinRequest request)
            throws ServiceException;

    private void onVaadinSessionStarted(VaadinRequest request,
            VaadinSession session) throws ServiceException {
        addonContext.fireApplicationStarted(session);
        eventRouter.fireEvent(new VaadinSessionInitializeEvent(this, session,
                request));

        ServletPortletHelper.checkUiProviders(session);
    }

    private void closeSession(VaadinSession vaadinSession,
            WrappedSession session) {
        if (vaadinSession == null) {
            return;
        }

        if (session != null) {
            vaadinSession.removeFromSession();
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

        VaadinSession vaadinSession = VaadinSession.getForSession(session);

        if (vaadinSession == null) {
            return null;
        }

        return vaadinSession;
    }

    /**
     * Checks whether it's valid to create a new Vaadin session as a result of
     * the given request.
     * 
     * @param request
     *            the request
     * @return <code>true</code> if it's valid to create a new Vaadin session
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

}
