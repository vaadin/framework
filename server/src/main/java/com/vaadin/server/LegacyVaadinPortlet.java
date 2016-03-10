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

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

public class LegacyVaadinPortlet extends VaadinPortlet {

    private static final LegacyApplicationUIProvider provider = new LegacyApplicationUIProvider() {
        @Override
        protected LegacyApplication createApplication() {
            VaadinPortlet portlet = VaadinPortlet.getCurrent();
            if (portlet instanceof LegacyVaadinPortlet) {
                LegacyVaadinPortlet legacyPortlet = (LegacyVaadinPortlet) portlet;
                PortletRequest request = VaadinPortletService
                        .getCurrentPortletRequest();
                if (legacyPortlet.shouldCreateApplication(request)) {
                    try {
                        return legacyPortlet.getNewApplication(request);
                    } catch (PortletException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return null;
        }
    };

    @Override
    public void init(PortletConfig portletConfig) throws PortletException {
        super.init(portletConfig);

        getService().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event)
                    throws ServiceException {
                try {
                    onVaadinSessionStarted(
                            (VaadinPortletRequest) event.getRequest(),
                            (VaadinPortletSession) event.getSession());
                } catch (PortletException e) {
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

    protected LegacyApplication getNewApplication(PortletRequest request)
            throws PortletException {
        try {
            Class<? extends LegacyApplication> applicationClass = getApplicationClass();
            return applicationClass.newInstance();
        } catch (Exception e) {
            throw new PortletException(e);
        }
    }

    private void onVaadinSessionStarted(VaadinPortletRequest request,
            VaadinPortletSession session) throws PortletException {
        session.addUIProvider(provider);
    }

    protected boolean shouldCreateApplication(PortletRequest request) {
        return true;
    }
}
