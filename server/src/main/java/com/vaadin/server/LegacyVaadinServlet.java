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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class LegacyVaadinServlet extends VaadinServlet {

    private static final UIProvider provider = new LegacyApplicationUIProvider() {
        @Override
        protected LegacyApplication createApplication() {

            VaadinServlet servlet = VaadinServlet.getCurrent();
            if (servlet instanceof LegacyVaadinServlet) {
                LegacyVaadinServlet legacyServlet = (LegacyVaadinServlet) servlet;
                HttpServletRequest request = VaadinServletService
                        .getCurrentServletRequest();
                try {
                    if (legacyServlet.shouldCreateApplication(request)) {
                        return legacyServlet.getNewApplication(request);
                    }
                } catch (ServletException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    };

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        getService().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event)
                    throws ServiceException {
                try {
                    onVaadinSessionStarted(event.getRequest(),
                            event.getSession());
                } catch (ServletException e) {
                    throw new ServiceException(e);
                }
            }
        });
    }

    protected Class<? extends LegacyApplication> getApplicationClass()
            throws ClassNotFoundException {
        try {
            return ServletPortletHelper.getLegacyApplicationClass(getService());
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    protected LegacyApplication getNewApplication(HttpServletRequest request)
            throws ServletException {
        try {
            Class<? extends LegacyApplication> applicationClass = getApplicationClass();
            return applicationClass.newInstance();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected boolean shouldCreateApplication(HttpServletRequest request)
            throws ServletException {
        return true;
    }

    private void onVaadinSessionStarted(VaadinRequest request,
            VaadinSession session) throws ServletException {
        session.addUIProvider(provider);
    }

}
