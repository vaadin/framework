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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.ServiceLoader;

import javax.servlet.ServletException;

import com.vaadin.LegacyApplication;
import com.vaadin.event.EventRouter;
import com.vaadin.server.ServletPortletHelper.ApplicationClassException;
import com.vaadin.server.VaadinSession.SessionStartEvent;
import com.vaadin.util.ReflectTools;

/**
 * Abstract implementation of VaadinService that takes care of those parts that
 * are common to both servlets and portlets.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public abstract class AbstractVaadinService implements VaadinService {

    private static final Method SESSION_INIT_METHOD = ReflectTools.findMethod(
            VaadinSessionInitializationListener.class,
            "vaadinSessionInitialized", VaadinSessionInitializeEvent.class);

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
    public AbstractVaadinService(DeploymentConfiguration deploymentConfiguration) {
        this.deploymentConfiguration = deploymentConfiguration;
    }

    @Override
    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

    @Override
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

    @Override
    public Iterator<AddonContextListener> getAddonContextListeners() {
        // Called once for init and then no more, so there's no point in caching
        // the instance
        ServiceLoader<AddonContextListener> contextListenerLoader = ServiceLoader
                .load(AddonContextListener.class, getClassLoader());
        return contextListenerLoader.iterator();
    }

    @Override
    public void setAddonContext(AddonContext addonContext) {
        this.addonContext = addonContext;
    }

    @Override
    public AddonContext getAddonContext() {
        return addonContext;
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
    public VaadinSession findVaadinSession(WrappedRequest request)
            throws ServiceException, SessionExpiredException {

        boolean requestCanCreateApplication = requestCanCreateSession(request);

        /* Find an existing application for this request. */
        VaadinSession session = getExistingSession(request,
                requestCanCreateApplication);

        if (session != null) {
            /*
             * There is an existing application. We can use this as long as the
             * user not specifically requested to close or restart it.
             */

            final boolean restartApplication = (request
                    .getParameter(URL_PARAMETER_RESTART_APPLICATION) != null);
            final boolean closeApplication = (request
                    .getParameter(URL_PARAMETER_CLOSE_APPLICATION) != null);

            if (restartApplication) {
                closeApplication(session, request.getWrappedSession(false));
                return createAndRegisterApplication(request);
            } else if (closeApplication) {
                closeApplication(session, request.getWrappedSession(false));
                return null;
            } else {
                return session;
            }
        }

        // No existing application was found

        if (requestCanCreateApplication) {
            /*
             * If the request is such that it should create a new application if
             * one as not found, we do that.
             */
            return createAndRegisterApplication(request);
        } else {
            /*
             * The application was not found and a new one should not be
             * created. Assume the session has expired.
             */
            throw new SessionExpiredException();
        }

    }

    private VaadinSession createAndRegisterApplication(WrappedRequest request)
            throws ServiceException {
        VaadinSession session = createVaadinSession(request);

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
    protected URL getApplicationUrl(WrappedRequest request)
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
    private VaadinServletSession createVaadinSession(WrappedRequest request)
            throws ServiceException {
        VaadinServletSession session = new VaadinServletSession();

        try {
            ServletPortletHelper.initDefaultUIProvider(session, this);
        } catch (ApplicationClassException e) {
            throw new ServiceException(e);
        }

        return session;
    }

    private void onVaadinSessionStarted(WrappedRequest request,
            VaadinSession session) throws ServiceException {
        addonContext.fireApplicationStarted(session);
        eventRouter.fireEvent(new VaadinSessionInitializeEvent(this, session,
                request));

        try {
            ServletPortletHelper.checkUiProviders(session);
        } catch (ApplicationClassException e) {
            throw new ServiceException(e);
        }
    }

    private void closeApplication(VaadinSession application,
            WrappedSession session) {
        if (application == null) {
            return;
        }

        application.close();
        if (session != null) {
            application.removeFromSession();
        }
    }

    protected VaadinSession getExistingSession(WrappedRequest request,
            boolean allowSessionCreation) throws SessionExpiredException {

        // Ensures that the session is still valid
        final WrappedSession session = request
                .getWrappedSession(allowSessionCreation);
        if (session == null) {
            throw new SessionExpiredException();
        }

        VaadinSession sessionApplication = VaadinSession.getForSession(session);

        if (sessionApplication == null) {
            return null;
        }

        if (!sessionApplication.isRunning()) {
            sessionApplication.removeFromSession();
            return null;
        }

        return sessionApplication;
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
    protected abstract boolean requestCanCreateSession(WrappedRequest request);

    @Override
    public void addVaadinSessionInitializationListener(
            VaadinSessionInitializationListener listener) {
        eventRouter.addListener(VaadinSessionInitializeEvent.class, listener,
                SESSION_INIT_METHOD);
    }

    @Override
    public void removeVaadinSessionInitializationListener(
            VaadinSessionInitializationListener listener) {
        eventRouter.removeListener(VaadinSessionInitializeEvent.class,
                listener, SESSION_INIT_METHOD);
    }
}
