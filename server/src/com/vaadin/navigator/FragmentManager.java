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
 * Fragment manager that handles interaction between Navigator and URI fragments
 * or other similar view identification and bookmarking system.
 * 
 * Alternative implementations can be created for HTML5 pushState, for portlet
 * URL navigation and other similar systems.
 * 
 * This interface is mostly for internal use by {@link Navigator}.
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public interface FragmentManager extends Serializable {
    /**
     * Return the current fragment (location string) including view name and any
     * optional parameters.
     * 
     * @return current view and parameter string, not null
     */
    public String getFragment();

    /**
     * Set the current fragment (location string) in the application URL or
     * similar location, including view name and any optional parameters.
     * 
     * @param fragment
     *            new view and parameter string, not null
     */
    public void setFragment(String fragment);
}