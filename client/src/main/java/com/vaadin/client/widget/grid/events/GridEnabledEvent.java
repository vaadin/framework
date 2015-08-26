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
package com.vaadin.client.widget.grid.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An enabled/disabled event, fired by the Grid when it is disabled or enabled.
 *
 * @since
 * @author Vaadin Ltd
 */
public class GridEnabledEvent extends GwtEvent<GridEnabledHandler> {
    /**
     * The type of this event
     */
    public static final Type<GridEnabledHandler> TYPE = new Type<GridEnabledHandler>();
    private final boolean enabled;

    public GridEnabledEvent(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Type<GridEnabledHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final GridEnabledHandler handler) {
        handler.onEnabled(enabled);
    }
}
