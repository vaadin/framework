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
package com.vaadin.osgi.resources;

import java.util.List;

/**
 * Used to declare multiple OsgiVaadinResources with a single OSGi component.
 * Each vaadin resource will be checked for the type (theme, widgetset,
 * resource) and registered to the OSGi context with the appropriate type.
 *
 * @since 8.6.0
 */
public interface OsgiVaadinContributor {
    List<OsgiVaadinResource> getContributions();
}
