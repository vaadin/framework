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
 * Interface for displaying a view in an appropriate location.
 * 
 * The view display can be a component/layout itself or can modify a separate
 * layout.
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public interface ViewDisplay extends Serializable {
    /**
     * Remove previously shown view and show the newly selected view in its
     * place.
     * 
     * The parameters for the view have been set before this method is called.
     * 
     * @param view
     *            new view to show
     */
    public void showView(View view);
}
