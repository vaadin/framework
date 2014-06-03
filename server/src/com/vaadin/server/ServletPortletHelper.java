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

import java.io.Serializable;
import java.util.Locale;
import java.util.Properties;

import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * Contains helper methods shared by {@link VaadinServlet} and
 * {@link VaadinPortlet}.
 * 
 * @deprecated As of 7.1. Will be removed or refactored in the future.
 */
@Deprecated
public class ServletPortletHelper implements Serializable {
    public static final String UPLOAD_URL_PREFIX = "APP/UPLOAD/";
    /**
     * The default SystemMessages (read-only).
     */
    static final SystemMessages DEFAULT_SYSTEM_MESSAGES = new SystemMessages();

    static Class<? extends LegacyApplication> getLegacyApplicationClass(
            VaadinService vaadinService) throws ServiceException {
        Properties initParameters = vaadinService.getDeploymentConfiguration()
                .getInitParameters();
        String applicationParameter = initParameters.getProperty("application");
        ClassLoader classLoader = vaadinService.getClassLoader();

        if (applicationParameter == null) {
            throw new ServiceException(
                    "No \"application\" init parameter found");
        }

        try {
            return classLoader.loadClass(applicationParameter).asSubclass(
                    LegacyApplication.class);
        } catch (final ClassNotFoundException e) {
            throw new ServiceException("Failed to load application class: "
                    + applicationParameter, e);
        }
    }

    private static void verifyUIClass(String className, ClassLoader classLoader)
            throws ServiceException {
        if (className == null) {
            throw new ServiceException(VaadinSession.UI_PARAMETER
                    + " init parameter not defined");
        }

        // Check that the UI layout class can be found
        try {
            Class<?> uiClass = classLoader.loadClass(className);
            if (!UI.class.isAssignableFrom(uiClass)) {
                throw new ServiceException(className + " does not implement UI");
            }
            // Try finding a default constructor, else throw exception
            uiClass.getConstructor();
        } catch (ClassNotFoundException e) {
            throw new ServiceException(className + " could not be loaded", e);
        } catch (SecurityException e) {
            throw new ServiceException("Could not access " + className
                    + " class", e);
        } catch (NoSuchMethodException e) {
            throw new ServiceException(className
                    + " doesn't have a public no-args constructor");
        }
    }

    private static boolean hasPathPrefix(VaadinRequest request, String prefix) {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            return false;
        }

        if (!prefix.startsWith("/")) {
            prefix = '/' + prefix;
        }

        if (pathInfo.startsWith(prefix)) {
            return true;
        }

        return false;
    }

    public static boolean isFileUploadRequest(VaadinRequest request) {
        return hasPathPrefix(request, UPLOAD_URL_PREFIX);
    }

    public static boolean isPublishedFileRequest(VaadinRequest request) {
        return hasPathPrefix(request, ApplicationConstants.PUBLISHED_FILE_PATH
                + "/");
    }

    public static boolean isUIDLRequest(VaadinRequest request) {
        return hasPathPrefix(request, ApplicationConstants.UIDL_PATH + '/');
    }

    public static boolean isAppRequest(VaadinRequest request) {
        return hasPathPrefix(request, ApplicationConstants.APP_PATH + '/');
    }

    public static boolean isHeartbeatRequest(VaadinRequest request) {
        return hasPathPrefix(request, ApplicationConstants.HEARTBEAT_PATH + '/');
    }

    public static boolean isPushRequest(VaadinRequest request) {
        return hasPathPrefix(request, ApplicationConstants.PUSH_PATH + '/');
    }

    public static void initDefaultUIProvider(VaadinSession session,
            VaadinService vaadinService) throws ServiceException {
        String uiProperty = vaadinService.getDeploymentConfiguration()
                .getApplicationOrSystemProperty(VaadinSession.UI_PARAMETER,
                        null);

        // Add provider for UI parameter first to give it lower priority
        // (providers are FILO)
        if (uiProperty != null) {
            verifyUIClass(uiProperty, vaadinService.getClassLoader());
            session.addUIProvider(new DefaultUIProvider());
        }

        String uiProviderProperty = vaadinService.getDeploymentConfiguration()
                .getApplicationOrSystemProperty(
                        Constants.SERVLET_PARAMETER_UI_PROVIDER, null);
        // Then add custom UI provider if defined
        if (uiProviderProperty != null) {
            UIProvider uiProvider = getUIProvider(uiProviderProperty,
                    vaadinService.getClassLoader());
            session.addUIProvider(uiProvider);
        }
    }

    private static UIProvider getUIProvider(String uiProviderProperty,
            ClassLoader classLoader) throws ServiceException {
        try {
            Class<?> providerClass = classLoader.loadClass(uiProviderProperty);
            Class<? extends UIProvider> subclass = providerClass
                    .asSubclass(UIProvider.class);
            return subclass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new ServiceException("Could not load UIProvider class "
                    + uiProviderProperty, e);
        } catch (ClassCastException e) {
            throw new ServiceException("UIProvider class " + uiProviderProperty
                    + " does not extend UIProvider", e);
        } catch (InstantiationException e) {
            throw new ServiceException("Could not instantiate UIProvider "
                    + uiProviderProperty, e);
        } catch (IllegalAccessException e) {
            throw new ServiceException("Could not instantiate UIProvider "
                    + uiProviderProperty, e);
        }
    }

    public static void checkUiProviders(VaadinSession session,
            VaadinService vaadinService) throws ServiceException {
        if (session.getUIProviders().isEmpty()) {
            throw new ServiceException(
                    "No UIProvider has been added and there is no \""
                            + VaadinSession.UI_PARAMETER + "\" init parameter.");
        }
    }

    /**
     * Helper to find the most most suitable Locale. These potential sources are
     * checked in order until a Locale is found:
     * <ol>
     * <li>The passed component (or UI) if not null</li>
     * <li>{@link UI#getCurrent()} if defined</li>
     * <li>The passed session if not null</li>
     * <li>{@link VaadinSession#getCurrent()} if defined</li>
     * <li>The passed request if not null</li>
     * <li>{@link VaadinService#getCurrentRequest()} if defined</li>
     * <li>{@link Locale#getDefault()}</li>
     * </ol>
     */
    public static Locale findLocale(Component component, VaadinSession session,
            VaadinRequest request) {
        if (component == null) {
            component = UI.getCurrent();
        }
        if (component != null) {
            Locale locale = component.getLocale();
            if (locale != null) {
                return locale;
            }
        }

        if (session == null) {
            session = VaadinSession.getCurrent();
        }
        if (session != null) {
            Locale locale = session.getLocale();
            if (locale != null) {
                return locale;
            }
        }

        if (request == null) {
            request = VaadinService.getCurrentRequest();
        }
        if (request != null) {
            Locale locale = request.getLocale();
            if (locale != null) {
                return locale;
            }
        }

        return Locale.getDefault();
    }
}
