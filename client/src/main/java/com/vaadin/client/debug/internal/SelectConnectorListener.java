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
package com.vaadin.client.debug.internal;

import com.google.gwt.dom.client.Element;
import com.vaadin.client.ServerConnector;

/**
 * Listener for the selection of a connector in the debug window.
 * 
 * @since 7.1.4
 */
public interface SelectConnectorListener {
    /**
     * Listener method called when a connector has been selected. If a specific
     * element of the connector was selected, it is also given.
     * 
     * @param connector
     *            selected connector
     * @param element
     *            selected element of the connector or null if unknown
     */
    public void select(ServerConnector connector, Element element);
}
