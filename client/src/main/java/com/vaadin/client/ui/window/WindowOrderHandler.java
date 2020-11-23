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
package com.vaadin.client.ui.window;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link WindowOrderEvent}s.
 *
 * @since 7.7.12
 *
 * @author Vaadin Ltd
 */
public interface WindowOrderHandler extends EventHandler {

    /**
     * Called when the VWindow instances changed their order position.
     *
     * @param event
     *            Contains windows whose position has changed
     */
    public void onWindowOrderChange(WindowOrderEvent event);
}