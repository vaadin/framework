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
package com.vaadin.client.widget.escalator.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Event handler for a row height changed event.
 *
 * @since 7.7
 * @author Vaadin Ltd
 */
public interface RowHeightChangedHandler extends EventHandler {

    /**
     * A row height changed event, fired by Escalator when the header, body or
     * footer row height has changed.
     *
     * @param event Row height changed event
     */
    public void onRowHeightChanged(RowHeightChangedEvent event);
}
