/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Internal constants used by both the client and the server side framework.
 *
 * @since 6.2
 *
 */
public interface Constants {

    static final String NOT_PRODUCTION_MODE_INFO = "\n"
            + "=================================================================\n"
            + "Vaadin is running in DEBUG MODE.\nAdd productionMode=true to web.xml "
            + "to disable debug features.\nTo show debug window, add ?debug to "
            + "your application URL.\n"
            + "=================================================================";

    static final String WARNING_XSRF_PROTECTION_DISABLED = "\n"
            + "===========================================================\n"
            + "WARNING: Cross-site request forgery protection is disabled!\n"
            + "===========================================================";

    static final String WARNING_RESOURCE_CACHING_TIME_NOT_NUMERIC = "\n"
            + "===========================================================\n"
            + "WARNING: resourceCacheTime has been set to a non integer value "
            + "in web.xml. The default of 1h will be used.\n"
            + "===========================================================";

    static final String WARNING_HEARTBEAT_INTERVAL_NOT_NUMERIC = "\n"
            + "===========================================================\n"
            + "WARNING: heartbeatInterval has been set to a non integer value "
            + "in web.xml. The default of 5min will be used.\n"
            + "===========================================================";

    static final String WARNING_PUSH_MODE_NOT_RECOGNIZED = "\n"
            + "===========================================================\n"
            + "WARNING: pushMode has been set to an unrecognized value\n"
            + "in web.xml. The permitted values are \"disabled\", \"manual\",\n"
            + "and \"automatic\". The default of \"disabled\" will be used.\n"
            + "===========================================================";

    static final String WIDGETSET_MISMATCH_INFO = "\n"
            + "=================================================================\n"
            + "The widgetset in use does not seem to be built for the Vaadin\n"
            + "version in use. This might cause strange problems - a\n"
            + "recompile/deploy is strongly recommended.\n"
            + " Vaadin version: %s\n" + " Widgetset version: %s\n"
            + "=================================================================";

    // Keep the version number in sync with pom.xml
    static final String REQUIRED_ATMOSPHERE_RUNTIME_VERSION = "2.4.30.vaadin1";

    static final String INVALID_ATMOSPHERE_VERSION_WARNING = "\n"
            + "=================================================================\n"
            + "Vaadin depends on Atmosphere {0} but version {1} was found.\n"
            + "This might cause compatibility problems if push is used.\n"
            + "=================================================================";

    static final String ATMOSPHERE_MISSING_ERROR = "\n"
            + "=================================================================\n"
            + "Atmosphere could not be loaded. When using push with Vaadin, the\n"
            + "Atmosphere framework must be present on the classpath.\n"
            + "If using a dependency management system, please add a dependency\n"
            + "to vaadin-push.\n"
            + "If managing dependencies manually, please make sure Atmosphere\n"
            + REQUIRED_ATMOSPHERE_RUNTIME_VERSION
            + " is included on the classpath.\n" + "Will fall back to using "
            + PushMode.class.getSimpleName() + "." + PushMode.DISABLED.name()
            + ".\n"
            + "=================================================================";

    static final String PUSH_NOT_SUPPORTED_ERROR = "\n"
            + "=================================================================\n"
            + "Push is not supported for {0}\n" + "Will fall back to using "
            + PushMode.class.getSimpleName() + "." + PushMode.DISABLED.name()
            + ".\n"
            + "=================================================================";

    static final String CANNOT_ACQUIRE_CLASSLOADER_SEVERE = "\n"
            + "=================================================================\n"
            + "Vaadin was unable to acquire class loader from servlet container\n"
            + "to load your application classes. Setup appropriate security\n"
            + "policy to allow invoking Thread.getContextClassLoader() from\n"
            + "VaadinService if you're not using custom class loader.\n"
            + "NullPointerExceptions will be thrown later."
            + "=================================================================";

    static final String URL_PARAMETER_THEME = "theme";

    static final String SERVLET_PARAMETER_PRODUCTION_MODE = "productionMode";
    // Javadocs for VaadinService should be updated if this value is changed
    static final String SERVLET_PARAMETER_DISABLE_XSRF_PROTECTION = "disable-xsrf-protection";
    static final String SERVLET_PARAMETER_RESOURCE_CACHE_TIME = "resourceCacheTime";
    static final String SERVLET_PARAMETER_HEARTBEAT_INTERVAL = "heartbeatInterval";
    static final String SERVLET_PARAMETER_CLOSE_IDLE_SESSIONS = "closeIdleSessions";
    static final String SERVLET_PARAMETER_PUSH_MODE = "pushMode";
    static final String SERVLET_PARAMETER_UI_PROVIDER = "UIProvider";
    static final String SERVLET_PARAMETER_SYNC_ID_CHECK = "syncIdCheck";
    static final String SERVLET_PARAMETER_SENDURLSASPARAMETERS = "sendUrlsAsParameters";
    static final String SERVLET_PARAMETER_PUSH_SUSPEND_TIMEOUT_LONGPOLLING = "pushLongPollingSuspendTimeout";
    /**
     * Name of system or context property to write declarative syntax with the
     * old "v-" prefix or with the new "vaadin-" prefix. The default value
     * depends on the Vaadin branch used.
     *
     * @see DesignContext
     * @since 7.5.7
     */
    static final String SERVLET_PARAMETER_LEGACY_DESIGN_PREFIX = "legacyDesignPrefix";

    // Configurable parameter names
    static final String PARAMETER_VAADIN_RESOURCES = "Resources";

    static final int DEFAULT_BUFFER_SIZE = 32 * 1024;

    static final int MAX_BUFFER_SIZE = 64 * 1024;

    final String THEME_DIR_PATH = "VAADIN/themes";

    static final int DEFAULT_THEME_CACHETIME = 1000 * 60 * 60 * 24;

    static final String WIDGETSET_DIR_PATH = "VAADIN/widgetsets";

    // Name of the default widget set, used if not specified in web.xml
    static final String DEFAULT_WIDGETSET = "com.vaadin.DefaultWidgetSet";

    // Widget set parameter name
    static final String PARAMETER_WIDGETSET = "widgetset";

    /**
     * @deprecated As of 7.1, this message is no longer used and might be
     *             removed from the code.
     */
    @Deprecated
    static final String ERROR_NO_UI_FOUND = "No UIProvider returned a UI for the request.";

    static final String DEFAULT_THEME_NAME = "valo";

    static final String INVALID_SECURITY_KEY_MSG = "Invalid security key.";

    // portal configuration parameters
    static final String PORTAL_PARAMETER_VAADIN_WIDGETSET = "vaadin.widgetset";
    static final String PORTAL_PARAMETER_VAADIN_RESOURCE_PATH = "vaadin.resources.path";
    static final String PORTAL_PARAMETER_VAADIN_THEME = "vaadin.theme";

    static final String PORTLET_CONTEXT = "PORTLET_CONTEXT";

}
