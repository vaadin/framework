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
package com.vaadin.tests.requesthandlers;

import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinServletService;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.integration.FlagSeResource;
import com.vaadin.ui.Link;

public class AppResource404 extends TestBase {

    @Override
    protected void setup() {
        // Add one existing resource
        final FlagSeResource resource = new FlagSeResource();
        resource.setCacheTime(0);

        HttpServletRequest request = VaadinServletService
                .getCurrentServletRequest();
        String baseUrl = request.getContextPath() + request.getServletPath();

        addComponent(new Link("Existing resource", resource));
        addComponent(new Link("Non-existing resource",
                new ExternalResource(baseUrl + "/APP/connector/0/4/asdfasdf")));
        addComponent(new Link("/APP/ url that should give 404",
                new ExternalResource(baseUrl + "/APP/")));
        addComponent(new Link("/APPLE url that should go to UI providers",
                new ExternalResource(baseUrl + "/APPLE")));
    }

    @Override
    protected String getDescription() {
        return "Accessing the URL of a resource that has been removed or the URL following the resource URL pattern but not currently mapped to a resource should give a 404 error message";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(6702);
    }

}
