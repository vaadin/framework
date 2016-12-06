/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import com.google.gwt.event.shared.GwtEvent;

/**
 * A selection allowed event, fired by the Grid when its selection allowed value
 * changes.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
public class GridSelectionAllowedEvent
        extends GwtEvent<GridSelectionAllowedHandler> {
    /**
     * The type of this event
     */
    public static final Type<GridSelectionAllowedHandler> TYPE = new Type<>();
    private final boolean isSelectionAllowed;

    /**
     * Creates a new event instance.
     *
     * @param selectionAllowed
     *            selection allowed value
     */
    public GridSelectionAllowedEvent(boolean selectionAllowed) {
        isSelectionAllowed = selectionAllowed;
    }

    @Override
    public Type<GridSelectionAllowedHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets selection allowed value.
     *
     * @return {@code true} if selection is allowed, {@code false} otherwise
     */
    public boolean isSelectionAllowed() {
        return isSelectionAllowed;
    }

    @Override
    protected void dispatch(final GridSelectionAllowedHandler handler) {
        handler.onSelectionAllowed(this);
    }
}
