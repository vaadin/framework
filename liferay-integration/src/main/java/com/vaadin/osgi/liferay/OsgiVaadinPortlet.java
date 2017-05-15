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
import com.vaadin.ui.UI;

/**
 * {@link VaadinPortlet} that uses an {@link OsgiUIProvider} to configure its
 * {@link UI}.
 * <p>
 * This only applies to Liferay Portal 7+ with OSGi support.
 *
 * @author Sampsa Sohlman
 *
 * @since 8.1
 */
@SuppressWarnings("serial")
public class OsgiVaadinPortlet extends VaadinPortlet {
    private OsgiUIProvider uiProvider;

    public OsgiVaadinPortlet(OsgiUIProvider uiProvider) {
        this.uiProvider = uiProvider;
    }

    @Override
    protected VaadinPortletService createPortletService(
            DeploymentConfiguration configuration) throws ServiceException {
        OsgiVaadinPortletService osgiVaadinPortletService = new OsgiVaadinPortletService(
                this, configuration, uiProvider);
        osgiVaadinPortletService.init();
        return osgiVaadinPortletService;
    }
}
