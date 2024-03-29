/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.widget.grid.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for a Grid {@link GridSelectionAllowedEvent}, called when the Grid is
 * becomes allowed for selection or disallowed.
 *
 * @see GridSelectionAllowedEvent
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public interface GridSelectionAllowedHandler extends EventHandler {

    /**
     * Called when Grid selection is allowed value changes.
     *
     * @param event
     *            the {@link GridSelectionAllowedEvent} that was fired
     *
     */
    public void onSelectionAllowed(GridSelectionAllowedEvent event);
}
