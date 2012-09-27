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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import com.vaadin.ui.UI;

/**
 * Application manager processes changes and paints for single application
 * instance.
 * 
 * This class handles applications running as servlets.
 * 
 * @see AbstractCommunicationManager
 * 
 * @author Vaadin Ltd.
 * @since 5.0
 * 
 * @deprecated might be refactored or removed before 7.0.0
 */
@Deprecated
@SuppressWarnings("serial")
public class CommunicationManager extends AbstractCommunicationManager {

    /**
     * TODO New constructor - document me!
     * 
     * @param session
     */
    public CommunicationManager(VaadinServiceSession session) {
        super(session);
    }

    @Override
    protected BootstrapHandler createBootstrapHandler() {
        return new BootstrapHandler() {
            @Override
            protected String getApplicationId(BootstrapContext context) {
                String appUrl = getAppUri(context);

                String appId = appUrl;
                if ("".equals(appUrl)) {
                    appId = "ROOT";
                }
                appId = appId.replaceAll("[^a-zA-Z0-9]", "");
                // Add hashCode to the end, so that it is still (sort of)
                // predictable, but indicates that it should not be used in CSS
                // and
                // such:
                int hashCode = appId.hashCode();
                if (hashCode < 0) {
                    hashCode = -hashCode;
                }
                appId = appId + "-" + hashCode;
                return appId;
            }

            @Override
            protected String getAppUri(BootstrapContext context) {
                /* Fetch relative url to application */
                // don't use server and port in uri. It may cause problems with
                // some
                // virtual server configurations which lose the server name
                URL url;

                try {
                    url = context.getRequest().getService()
                            .getApplicationUrl(context.getRequest());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                String appUrl = url.getPath();
                if (appUrl.endsWith("/")) {
                    appUrl = appUrl.substring(0, appUrl.length() - 1);
                }
                return appUrl;
            }

            @Override
            public String getThemeName(BootstrapContext context) {
                String themeName = context.getRequest().getParameter(
                        VaadinServlet.URL_PARAMETER_THEME);
                if (themeName == null) {
                    themeName = super.getThemeName(context);
                }
                return themeName;
            }
        };
    }

    @Override
    protected InputStream getThemeResourceAsStream(UI uI, String themeName,
            String resource) {
        VaadinServletSession session = (VaadinServletSession) uI.getSession();
        ServletContext servletContext = session.getHttpSession()
                .getServletContext();
        return servletContext.getResourceAsStream("/"
                + VaadinServlet.THEME_DIRECTORY_PATH + themeName + "/"
                + resource);
    }
}
