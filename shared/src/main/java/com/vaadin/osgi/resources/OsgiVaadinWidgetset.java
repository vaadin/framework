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
 * Used to declare a Vaadin Widgetset for use in OSGi. The widgetset is expected
 * to be in the same OSGi bundle as the class implementing this interface, under
 * the path "/VAADIN/widgetsets/{widgetsetName}" where {widgetsetName} is what
 * is returned by {@link OsgiVaadinWidgetset#getName()}.
 * <p>
 * To publish a widgetset, an implementation of this interface needs to be
 * registered as an OSGi service, which makes
 * <code>VaadinResourceTrackerComponent</code> automatically publish the
 * widgetset with the given name.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.1
 */
public interface OsgiVaadinWidgetset {
    /**
     * Return the widgetset name to publish for OSGi.
     *
     * @return widgetset name, not null
     */
    public String getName();
}
