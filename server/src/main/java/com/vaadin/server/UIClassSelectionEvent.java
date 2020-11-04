/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.server;

/**
 * Contains information used by
 * {@link UIProvider#getUIClass(UIClassSelectionEvent)} to choose a UI class to
 * use in a specific situation.
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class UIClassSelectionEvent extends UIProviderEvent {

    /**
     * Creates a new event for a specific request.
     *
     * @param request
     *            the Vaadin request for which a UI class is wanted.
     */
    public UIClassSelectionEvent(VaadinRequest request) {
        super(request);
    }

}
