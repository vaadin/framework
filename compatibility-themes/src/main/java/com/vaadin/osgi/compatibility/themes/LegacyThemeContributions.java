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
package com.vaadin.osgi.compatibility.themes;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;

import com.vaadin.osgi.resources.OSGiVaadinResources;
import com.vaadin.osgi.resources.VaadinResourceService;

@Component(immediate = true)
public class LegacyThemeContributions {
    private static final String[] LEGACY_THEMES = { "base", "chameleon",
            "reindeer", "runo" };

    private HttpService httpService;

    @Activate
    void startup() throws Exception {
        VaadinResourceService service = OSGiVaadinResources.getService();
        for (String themeName : LEGACY_THEMES) {
            service.publishTheme(themeName, httpService);
        }
    }

    @Reference
    void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }
}
