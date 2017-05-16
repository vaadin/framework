/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.osgi.liferay;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinPortlet;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinPortletSession;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * {@link VaadinPortletService} class that uses the {@link OsgiUIProvider} to
 * configure the {@link UI} class for a {@link VaadinPortlet}.
 * <p>
 * This only applies to Liferay Portal 7+ with OSGi support.
 *
 * @author Sampsa Sohlman
 *
 * @since 8.1
 */
@SuppressWarnings("serial")
public class OsgiVaadinPortletService extends VaadinPortletService {
    private OsgiUIProvider osgiUIProvider;

    public OsgiVaadinPortletService(VaadinPortlet portlet,
            DeploymentConfiguration deploymentConfiguration,
            OsgiUIProvider osgiUIProvider) throws ServiceException {

        super(portlet, deploymentConfiguration);
        this.osgiUIProvider = osgiUIProvider;
    }

    @Override
    protected VaadinSession createVaadinSession(VaadinRequest request)
            throws ServiceException {

        VaadinSession vaadinSession = new VaadinPortletSession(this);
        vaadinSession.addUIProvider(osgiUIProvider);

        return vaadinSession;
    }

}
