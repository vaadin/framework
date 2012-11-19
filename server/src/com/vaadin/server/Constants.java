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

/**
 * TODO Document me!
 * 
 * @author peholmst
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

    static final String WIDGETSET_MISMATCH_INFO = "\n"
            + "=================================================================\n"
            + "The widgetset in use does not seem to be built for the Vaadin\n"
            + "version in use. This might cause strange problems - a\n"
            + "recompile/deploy is strongly recommended.\n"
            + " Vaadin version: %s\n"
            + " Widgetset version: %s\n"
            + "=================================================================";

    static final String URL_PARAMETER_THEME = "theme";

    static final String SERVLET_PARAMETER_PRODUCTION_MODE = "productionMode";
    static final String SERVLET_PARAMETER_DISABLE_XSRF_PROTECTION = "disable-xsrf-protection";
    static final String SERVLET_PARAMETER_RESOURCE_CACHE_TIME = "resourceCacheTime";
    static final String SERVLET_PARAMETER_HEARTBEAT_INTERVAL = "heartbeatInterval";
    static final String SERVLET_PARAMETER_CLOSE_IDLE_SESSIONS = "closeIdleSessions";
    static final String SERVLET_PARAMETER_UI_PROVIDER = "UIProvider";

    // Configurable parameter names
    static final String PARAMETER_VAADIN_RESOURCES = "Resources";

    static final int DEFAULT_BUFFER_SIZE = 32 * 1024;

    static final int MAX_BUFFER_SIZE = 64 * 1024;

    final String THEME_DIRECTORY_PATH = "VAADIN/themes/";

    static final int DEFAULT_THEME_CACHETIME = 1000 * 60 * 60 * 24;

    static final String WIDGETSET_DIRECTORY_PATH = "VAADIN/widgetsets/";

    // Name of the default widget set, used if not specified in web.xml
    static final String DEFAULT_WIDGETSET = "com.vaadin.DefaultWidgetSet";

    // Widget set parameter name
    static final String PARAMETER_WIDGETSET = "widgetset";

    static final String ERROR_NO_UI_FOUND = "No UIProvider returned a UI for the request.";

    static final String DEFAULT_THEME_NAME = "reindeer";

    static final String INVALID_SECURITY_KEY_MSG = "Invalid security key.";

    // portal configuration parameters
    static final String PORTAL_PARAMETER_VAADIN_WIDGETSET = "vaadin.widgetset";
    static final String PORTAL_PARAMETER_VAADIN_RESOURCE_PATH = "vaadin.resources.path";
    static final String PORTAL_PARAMETER_VAADIN_THEME = "vaadin.theme";
}
