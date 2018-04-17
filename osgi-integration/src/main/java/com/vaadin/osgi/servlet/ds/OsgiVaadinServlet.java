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
package com.vaadin.osgi.servlet.ds;

import com.vaadin.osgi.servlet.VaadinServletRegistration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * OSGi Servlet that uses an {@link OsgiUIProvider} to support {@link UI}
 * Declarative Service injection.
 * 
 * The {@link VaadinServletRegistration} sets the {@link OsgiUIProvider} by
 * calling {@link #setUIProvider(OsgiUIProvider)} which is then set as the first
 * {@link UIProvider} on session initialization.
 * 
 * If you need to use a different {@link UIProvider} to conditionally provide a
 * different {@link UI} extend this servlet and override
 * {@link #createServletService(DeploymentConfiguration))} to add your
 * customized provider on session initialization.
 * 
 * @author Vaadin Ltd.
 *
 * @since 8.5
 */
public class OsgiVaadinServlet extends VaadinServlet {
    private static final long serialVersionUID = 1L;

    private OsgiUIProvider osgiUIProvider;

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        VaadinServletService service = super.createServletService(deploymentConfiguration);
        service.setClassLoader(getClass().getClassLoader());
        service.addSessionInitListener(event -> {
            VaadinSession session = event.getSession();
            session.addUIProvider(osgiUIProvider);
        });
        return service;
    }

    /**
     * Simple setter for the {@link OsgiUIProvider} used by the
     * {@link VaadinServletRegistration}. This is needed for the servlet to use
     * {@link UI UIs} as Declarative Services.
     * 
     * @param osgiUIProvider
     *            the {@link OsgiUIProvider} used by the servlet tracker
     * 
     */
    public void setUIProvider(OsgiUIProvider osgiUIProvider) {
        this.osgiUIProvider = osgiUIProvider;
    }

}
