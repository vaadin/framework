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
package com.vaadin.server.osgi;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.vaadin.osgi.resources.OsgiVaadinResources;
import com.vaadin.osgi.resources.OsgiVaadinResources.ResourceBundleInactiveException;
import com.vaadin.osgi.resources.VaadinResourceService;

/**
 * OSGi service component registering bootstrap JS as published resources in
 * OSGi environments.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Component(immediate = true)
public class BootstrapContribution {
    private static final String[] RESOURCES = { "vaadinBootstrap.js",
            "vaadinBootstrap.js.gz" };
    private HttpService httpService;

    @Activate
    void startup() throws NamespaceException, ResourceBundleInactiveException {
        VaadinResourceService service = OsgiVaadinResources.getService();
        for (String resourceName : RESOURCES) {
            service.publishResource(resourceName, httpService);
        }
    }

    @Reference
    void setHttpService(HttpService service) {
        httpService = service;
    }

    void unsetHttpService(HttpService service) {
        httpService = null;
    }
}
