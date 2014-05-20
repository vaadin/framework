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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.UI;

/**
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 * 
 * @deprecated As of 7.0. Used only to support LegacyApplication - will be
 *             removed when LegacyApplication support is removed.
 */
@Deprecated
public abstract class LegacyApplicationUIProvider extends UIProvider {
    /**
     * Ignore initial / and then get everything up to the next /
     */
    private static final Pattern WINDOW_NAME_PATTERN = Pattern
            .compile("^/?([^/]+).*");

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        UI uiInstance = getUIInstance(event);
        if (uiInstance != null) {
            return uiInstance.getClass();
        }
        return null;
    }

    @Override
    public UI createInstance(UICreateEvent event) {
        return getUIInstance(event);
    }

    @Override
    public String getTheme(UICreateEvent event) {
        LegacyApplication application = getApplication();
        if (application != null) {
            return application.getTheme();
        } else {
            return null;
        }
    }

    @Override
    public String getPageTitle(UICreateEvent event) {
        UI uiInstance = getUIInstance(event);
        if (uiInstance != null) {
            return uiInstance.getCaption();
        } else {
            return super.getPageTitle(event);
        }
    }

    private UI getUIInstance(UIProviderEvent event) {
        VaadinRequest request = event.getRequest();
        String pathInfo = request.getPathInfo();
        String name = null;
        if (pathInfo != null && pathInfo.length() > 0) {
            Matcher matcher = WINDOW_NAME_PATTERN.matcher(pathInfo);
            if (matcher.matches()) {
                // Skip the initial slash
                name = matcher.group(1);
            }
        }

        LegacyApplication application = getApplication();
        if (application == null) {
            return null;
        }
        LegacyWindow window = application.getWindow(name);
        if (window != null) {
            return window;
        }
        return application.getMainWindow();
    }

    /**
     * Hack used to return existing LegacyWindow instances without regard for
     * out-of-sync problems.
     * 
     * @param event
     * @return
     */
    public UI getExistingUI(UIClassSelectionEvent event) {
        UI uiInstance = getUIInstance(event);
        if (uiInstance == null || uiInstance.getUIId() == -1) {
            // Not initialized -> Let go through createUIInstance to make it
            // initialized
            return null;
        } else {
            UI.setCurrent(uiInstance);
            return uiInstance;
        }
    }

    private LegacyApplication getApplication() {
        LegacyApplication application = VaadinSession.getCurrent()
                .getAttribute(LegacyApplication.class);
        if (application == null) {
            application = createApplication();
            if (application == null) {
                return null;
            }
            VaadinSession.getCurrent().setAttribute(LegacyApplication.class,
                    application);

            URL applicationUrl;
            try {
                applicationUrl = VaadinService.getCurrent().getApplicationUrl(
                        VaadinService.getCurrentRequest());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            application.doInit(applicationUrl);
        }

        if (application != null && !application.isRunning()) {
            VaadinSession.getCurrent().setAttribute(LegacyApplication.class,
                    null);
            // Run again without a current application
            return getApplication();
        }

        return application;
    }

    protected abstract LegacyApplication createApplication();

}
