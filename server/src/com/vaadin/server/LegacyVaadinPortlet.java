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

import com.vaadin.Application;
import com.vaadin.server.ServletPortletHelper.ApplicationClassException;

public class LegacyVaadinPortlet extends VaadinPortlet {

    protected Class<? extends Application> getApplicationClass()
            throws ClassNotFoundException {
        try {
            return ServletPortletHelper
                    .getLegacyApplicationClass(getVaadinService());
        } catch (ApplicationClassException e) {
            throw new RuntimeException(e);
        }
    }

    protected Application getNewApplication(PortletRequest request)
            throws PortletException {
        try {
            Class<? extends Application> applicationClass = getApplicationClass();
            return applicationClass.newInstance();
        } catch (Exception e) {
            throw new PortletException(e);
        }
    }

    @Override
    protected VaadinPortletSession createApplication(PortletRequest request)
            throws PortletException {
        VaadinPortletSession application = super.createApplication(request);

        // Must set current before running init()
        VaadinSession.setCurrent(application);

        Application legacyApplication = getNewApplication(request);
        legacyApplication.doInit();
        application.addUIProvider(legacyApplication);

        return application;
    }
}
