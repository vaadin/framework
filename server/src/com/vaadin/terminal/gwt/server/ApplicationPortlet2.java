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

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.ServletPortletHelper.ApplicationClassException;

/**
 * TODO Write documentation, fix JavaDoc tags.
 * 
 * @author peholmst
 */
public class ApplicationPortlet2 extends AbstractApplicationPortlet {

    private Class<? extends Application> applicationClass;

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        try {
            applicationClass = ServletPortletHelper
                    .getApplicationClass(getDeploymentConfiguration());
        } catch (ApplicationClassException e) {
            throw new PortletException(e);
        }
    }

    @Override
    protected Class<? extends Application> getApplicationClass() {
        return applicationClass;
    }

}
