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
package com.vaadin.client.widget.grid.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for a Grid enabled/disabled event, called when the Grid is enabled or
 * disabled.
 *
 * @since 7.7
 * @author Vaadin Ltd
 */
public interface GridEnabledHandler extends EventHandler {

    /**
     * Called when Grid is enabled or disabled.
     *
     * @param enabled
     *            true if status changes from disabled to enabled, otherwise
     *            false.
     */
    public void onEnabled(boolean enabled);
}
