/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.event;

import com.vaadin.server.ClientConnector;
import com.vaadin.ui.UI;

/**
 * Event which is fired for all registered MarkDirtyListeners when a connector
 * is marked as dirty.
 *
 * @since 7.7.14
 */
public class MarkedAsDirtyConnectorEvent extends ConnectorEvent {

    private final UI ui;

    public MarkedAsDirtyConnectorEvent(ClientConnector source, UI ui) {
        super(source);
        this.ui = ui;
    }

    /**
     * Get the UI for which the connector event was fired
     *
     * @return target ui for event
     */
    public UI getUi() {
        return ui;
    }
}
