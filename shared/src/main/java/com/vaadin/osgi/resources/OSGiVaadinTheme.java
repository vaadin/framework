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

/**
 * Used to declare a Vaadin Theme for use in OSGi. The theme is expected to be
 * in the same OSGi bundle as the class implementing this interface, under the
 * path "/VAADIN/themes/{themeName}" where {themeName} is what is returned by
 * {@link OSGiVaadinTheme#getName()}.
 * <p>
 * To publish a theme, an implementation of this interface needs to be
 * registered as an OSGi service, which makes
 * <code>VaadinResourceTrackerComponent</code> automatically publish the theme
 * with the given name.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.1
 */
public interface OSGiVaadinTheme {
    public String getName();
}
