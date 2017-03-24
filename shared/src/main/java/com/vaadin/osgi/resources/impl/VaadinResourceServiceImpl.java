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
package com.vaadin.osgi.resources.impl;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.vaadin.osgi.resources.VaadinResourceService;

public class VaadinResourceServiceImpl implements VaadinResourceService {
    public static final String NAMESPACE_PREFIX = "vaadin-%s";

    public static final String VAADIN_ROOT_ALIAS_FORMAT = "/%s/VAADIN/%s";
    public static final String VAADIN_ROOT_FORMAT = "/VAADIN/%s";

    public static final String VAADIN_THEME_ALIAS_FORMAT = "/%s/VAADIN/themes/%s";
    public static final String VAADIN_WIDGETSET_ALIAS_FORMAT = "/%s/VAADIN/widgetsets/%s";

    public static final String VAADIN_THEME_PATH_FORMAT = "/VAADIN/themes/%s";
    public static final String VAADIN_WIDGETSET_PATH_FORMAT = "/VAADIN/widgetsets/%s";

    private String bundleVersion;

    public void setBundleVersion(String bundleVersion) {
        this.bundleVersion = bundleVersion;
    }

    @Override
    public void publishTheme(String themeName, HttpService httpService)
            throws NamespaceException {
        doPublish(themeName, VAADIN_THEME_ALIAS_FORMAT,
                VAADIN_THEME_PATH_FORMAT, httpService);
    }

    private void doPublish(String resourceName, String aliasFormat,
            String pathFormat, HttpService httpService)
            throws NamespaceException {
        String bundleVersionPrefix = String.format(NAMESPACE_PREFIX,
                bundleVersion);

        String resourcePath = String.format(pathFormat, resourceName);
        String resourceAlias = String.format(aliasFormat, bundleVersionPrefix,
                resourceName);

        httpService.registerResources(resourceAlias, resourcePath, null);
    }

    @Override
    public void publishResource(String resource, HttpService httpService)
            throws NamespaceException {
        doPublish(resource, VAADIN_ROOT_ALIAS_FORMAT, VAADIN_ROOT_FORMAT,
                httpService);
    }

    @Override
    public void publishWidgetset(String widgetset, HttpService httpService)
            throws NamespaceException {
        doPublish(widgetset, VAADIN_WIDGETSET_ALIAS_FORMAT,
                VAADIN_WIDGETSET_PATH_FORMAT, httpService);
    }

    @Override
    public String getResourcePathPrefix() {
        return String.format(NAMESPACE_PREFIX, bundleVersion);
    }
}
