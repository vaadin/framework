/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.navigator;

import java.io.Serializable;

/**
 * An interface for handling interaction between Navigator and the browser
 * location URI or other similar view identification and bookmarking system. The
 * state is limited to a single string because in the usual cases it forms a
 * part of a URI.
 * <p>
 * Different implementations can be created for hashbang URIs, HTML5 pushState,
 * portlet URL navigation and other similar systems.
 * <p>
 * This interface is mostly for internal use by {@link Navigator}.
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public interface NavigationStateManager extends Serializable {
    /**
     * Returns the current navigation state including view name and any optional
     * parameters.
     * 
     * @return current view and parameter string, not null
     */
    public String getState();

    /**
     * Set the current navigation state in the location URI or similar location,
     * including view name and any optional parameters.
     * 
     * @param fragment
     *            new view and parameter string, not null
     */
    public void setState(String state);
}