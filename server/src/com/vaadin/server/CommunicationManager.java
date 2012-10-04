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
            protected String getAppUri(BootstrapContext context) {
                String pathInfo = context.getRequest().getRequestPathInfo();
                if (pathInfo == null) {
                    return null;
                } else {
                    /*
                     * Make a relative URL to the servlet by adding one ../ for
                     * each path segment in pathInfo (i.e. the part of the
                     * requested path that comes after the servlet mapping)
                     */
                    StringBuilder sb = new StringBuilder("./");
                    // Start from i = 1 to ignore initial /
                    for (int i = 1; i < pathInfo.length(); i++) {
                        if (pathInfo.charAt(i) == '/') {
                            sb.append("../");
                        }
                    }
                    return sb.toString();
                }
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
        VaadinServletService service = (VaadinServletService) uI.getSession()
                .getService();
        ServletContext servletContext = service.getServlet()
                .getServletContext();
        return servletContext.getResourceAsStream("/"
                + VaadinServlet.THEME_DIRECTORY_PATH + themeName + "/"
                + resource);
    }
}
