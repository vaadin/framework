/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.client;

/**
 * ComponentLocator provides methods for generating a String locator for a given
 * DOM element and for locating a DOM element using a String locator.
 * 
 * @since 5.4
 * @deprecated Moved to com.vaadin.client.componentlocator.ComponentLocator
 */
public class ComponentLocator extends
        com.vaadin.client.componentlocator.ComponentLocator {
    /**
     * Construct a ComponentLocator for the given ApplicationConnection.
     *
     * @param client
     *            ApplicationConnection instance for the application.
     */
    public ComponentLocator(ApplicationConnection client) {
        super(client);
    }
}
