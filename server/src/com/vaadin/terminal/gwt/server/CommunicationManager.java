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

package com.vaadin.terminal.gwt.server;

import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;

import com.vaadin.Application;
import com.vaadin.external.json.JSONException;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.WrappedRequest;
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
 */
@SuppressWarnings("serial")
public class CommunicationManager extends AbstractCommunicationManager {

    /**
     * @deprecated use {@link #CommunicationManager(Application)} instead
     * @param application
     * @param applicationServlet
     */
    @Deprecated
    public CommunicationManager(Application application,
            AbstractApplicationServlet applicationServlet) {
        super(application);
    }

    /**
     * TODO New constructor - document me!
     * 
     * @param application
     */
    public CommunicationManager(Application application) {
        super(application);
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
                Application application = context.getApplication();
                URL url = application.getURL();
                String appUrl = url.getPath();
                if (appUrl.endsWith("/")) {
                    appUrl = appUrl.substring(0, appUrl.length() - 1);
                }
                return appUrl;
            }

            @Override
            public String getThemeName(BootstrapContext context) {
                String themeName = context.getRequest().getParameter(
                        AbstractApplicationServlet.URL_PARAMETER_THEME);
                if (themeName == null) {
                    themeName = super.getThemeName(context);
                }
                return themeName;
            }

            @Override
            protected String getInitialUIDL(WrappedRequest request, UI uI)
                    throws PaintException, JSONException {
                return CommunicationManager.this.getInitialUIDL(request, uI);
            }
        };
    }

    @Override
    protected InputStream getThemeResourceAsStream(UI uI, String themeName,
            String resource) {
        WebApplicationContext context = (WebApplicationContext) uI
                .getApplication().getContext();
        ServletContext servletContext = context.getHttpSession()
                .getServletContext();
        return servletContext.getResourceAsStream("/"
                + AbstractApplicationServlet.THEME_DIRECTORY_PATH + themeName
                + "/" + resource);
    }
}
