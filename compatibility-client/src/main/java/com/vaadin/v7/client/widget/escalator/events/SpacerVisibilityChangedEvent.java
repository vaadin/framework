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
package com.vaadin.v7.client.widget.escalator.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired when a spacer element is hidden or shown in Escalator.
 *
 * @author Vaadin Ltd
 * @since 7.7.13
 */
public class SpacerVisibilityChangedEvent
        extends GwtEvent<SpacerVisibilityChangedHandler> {

    /**
     * Handler type.
     */
    public static final Type<SpacerVisibilityChangedHandler> TYPE = new Type<SpacerVisibilityChangedHandler>();

    public static final Type<SpacerVisibilityChangedHandler> getType() {
        return TYPE;
    }

    private final int rowIndex;
    private final boolean visible;

    /**
     * Creates a spacer visibility changed event.
     *
     * @param rowIndex
     *            index of row to which the spacer belongs
     * @param visible
     *            {@code true} if the spacer element is shown, {@code false} if
     *            the spacer element is hidden
     */
    public SpacerVisibilityChangedEvent(int rowIndex, boolean visible) {
        this.rowIndex = rowIndex;
        this.visible = visible;
    }

    /**
     * Gets the row index to which the spacer element belongs.
     *
     * @return the row index to which the spacer element belongs
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Gets whether the spacer element is displayed.
     *
     * @return {@code true} if the spacer element is shown, {@code false} if the
     *         spacer element is hidden
     */
    public boolean isVisible() {
        return visible;
    }

    @Override
    public Type<SpacerVisibilityChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SpacerVisibilityChangedHandler handler) {
        handler.onSpacerVisibilityChanged(this);
    }

}
