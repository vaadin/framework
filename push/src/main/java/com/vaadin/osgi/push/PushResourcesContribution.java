/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.osgi.push;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.vaadin.osgi.resources.OsgiVaadinContributor;
import com.vaadin.osgi.resources.OsgiVaadinResource;

@Component
public class PushResourcesContribution implements OsgiVaadinContributor {
    private static final String[] RESOURCES = { "vaadinPush.js",
            "vaadinPush.js.gz", "vaadinPush.debug.js",
            "vaadinPush.debug.js.gz" };

    @Override
    public List<OsgiVaadinResource> getContributions() {
        final List<OsgiVaadinResource> contributions = new ArrayList<>(
                RESOURCES.length);
        for (final String theme : RESOURCES) {
            contributions.add(OsgiVaadinResource.create(theme));
        }
        return contributions;
    }
}
