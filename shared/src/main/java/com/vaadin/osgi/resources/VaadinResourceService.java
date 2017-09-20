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
package com.vaadin.osgi.resources;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * Service used to publish themes, widgetsets and static resources at the root
 * of a versioned namespaced /VAADIN/ folder.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.1
 */
public interface VaadinResourceService {

    /**
     * Register the theme with the given name under the
     * {@link VaadinResourceService} versioned namespace. The theme folder is
     * expected to be compiled and under "/VAADIN/themes/" in the calling
     * bundle.
     *
     * The theme will become accessible under the url
     * "/vaadin-x.x.x/VAADIN/themes/themeName" where x.x.x is the version of the
     * Vaadin Shared bundle
     *
     * @param themeName
     *            the name of the theme
     * @param httpService
     *            the {@link HttpService} instance for the calling bundle
     * @throws NamespaceException
     *             if there is a clash during the theme registration
     */
    void publishTheme(String themeName, HttpService httpService)
            throws NamespaceException;

    /**
     * Register the resource with the given name under the
     * {@link VaadinResourceService} versioned namespace. The resource is
     * expected to be under "/VAADIN/" in the calling bundle.
     *
     * The resource will become accessible under the url "/vaadin-x.x.x/VAADIN/"
     * where x.x.x is the version of the Vaadin Shared bundle
     *
     * @param resourceName
     *            the name of the resource
     * @param httpService
     *            the {@link HttpService} instance for the calling bundle
     * @throws NamespaceException
     *             if there is a clash during the theme registration
     */
    void publishResource(String resourceName, HttpService httpService)
            throws NamespaceException;

    /**
     * Register the widgetset with the given name under the
     * {@link VaadinResourceService} versioned namespace. The resource is
     * expected to be under "/VAADIN/widgetsets" in the calling bundle.
     *
     * The resource will become accessible under the url
     * "/vaadin-x.x.x/VAADIN/widgetsets" where x.x.x is the version of the
     * Vaadin Shared bundle
     *
     * @param widgetsetName
     *            the name of the resource
     * @param httpService
     *            the {@link HttpService} instance for the calling bundle
     * @throws NamespaceException
     *             if there is a clash during the theme registration
     */
    void publishWidgetset(String widgetsetName, HttpService httpService)
            throws NamespaceException;

    /**
     * Returns the prefix of the versioned namespace for the resources. The
     * result can't be null and is of the format "vaadin-x.x.x" where x.x.x the
     * version of the Vaadin Shared bundle.
     *
     * @return the prefix of the resources folder managed by this service
     */
    String getResourcePathPrefix();

}
