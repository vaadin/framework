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

package com.vaadin.navigator;

import java.io.Serializable;

/**
 * A provider for view instances that can return pre-registered views or
 * dynamically create new views.
 * 
 * If multiple providers are used, {@link #getViewName(String)} of each is
 * called (in registration order) until one of them returns a non-null value.
 * The {@link #getView(String)} method of that provider is then used.
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public interface ViewProvider extends Serializable {
    /**
     * Extract the view name from a combined view name and parameter string.
     * This method should return a view name if and only if this provider
     * handles creation of such views.
     * 
     * @param viewAndParameters
     *            string with view name and its fragment parameters (if given),
     *            not null
     * @return view name if the view is handled by this provider, null otherwise
     */
    public String getViewName(String viewAndParameters);

    /**
     * Create or return a pre-created instance of a view.
     * 
     * The parameters for the view are set separately by the navigator when the
     * view is activated.
     * 
     * @param viewName
     *            name of the view, not null
     * @return newly created view (null if none available for the view name)
     */
    public View getView(String viewName);
}
