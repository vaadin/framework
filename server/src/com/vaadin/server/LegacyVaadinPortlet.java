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

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import com.vaadin.LegacyApplication;
import com.vaadin.server.ServletPortletHelper.ApplicationClassException;

public class LegacyVaadinPortlet extends VaadinPortlet {

    @Override
    public void init() throws PortletException {
        super.init();

        getVaadinService().addVaadinSessionInitializationListener(
                new VaadinSessionInitializationListener() {
                    @Override
                    public void vaadinSessionInitialized(
                            VaadinSessionInitializeEvent event)
                            throws ServiceException {
                        try {
                            onVaadinSessionStarted(WrappedPortletRequest
                                    .cast(event.getRequest()),
                                    (VaadinPortletSession) event
                                            .getVaadinSession());
                        } catch (PortletException e) {
                            throw new ServiceException(e);
                        }
                    }
                });
    }

    protected Class<? extends LegacyApplication> getApplicationClass()
            throws ClassNotFoundException {
        try {
            return ServletPortletHelper
                    .getLegacyApplicationClass(getVaadinService());
        } catch (ApplicationClassException e) {
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

    private void onVaadinSessionStarted(WrappedPortletRequest request,
            VaadinPortletSession session) throws PortletException {
        if (shouldCreateApplication(request)) {
            // Must set current before running init()
            VaadinSession.setCurrent(session);

            // XXX Must update details here so they are available in init.
            session.getBrowser().updateRequestDetails(request);

            LegacyApplication legacyApplication = getNewApplication(request
                    .getPortletRequest());
            legacyApplication.doInit();
            session.addUIProvider(legacyApplication);
        }
    }

    protected boolean shouldCreateApplication(WrappedPortletRequest request) {
        return true;
    }
}
