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
package com.vaadin.client.widget.escalator.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired when a spacer element is moved to a new index in Escalator.
 *
 * @author Vaadin Ltd
 * @since 8.9
 */
public class SpacerIndexChangedEvent
        extends GwtEvent<SpacerIndexChangedHandler> {

    /**
     * Handler type.
     */
    public static final Type<SpacerIndexChangedHandler> TYPE = new Type<>();

    /**
     * Returns the associated handler type.
     *
     * @return the handler type
     */
    public static final Type<SpacerIndexChangedHandler> getType() {
        return TYPE;
    }

    private final int oldIndex;
    private final int newIndex;

    /**
     * Creates a spacer index changed event.
     *
     * @param oldIndex
     *            old index of row to which the spacer belongs
     * @param newIndex
     *            new index of row to which the spacer belongs
     */
    public SpacerIndexChangedEvent(int oldIndex, int newIndex) {
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    /**
     * Gets the old row index to which the spacer element belongs.
     *
     * @return the old row index to which the spacer element belongs
     */
    public int getOldIndex() {
        return oldIndex;
    }

    /**
     * Gets the new row index to which the spacer element belongs.
     *
     * @return the new row index to which the spacer element belongs
     */
    public int getNewIndex() {
        return newIndex;
    }

    @Override
    public Type<SpacerIndexChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SpacerIndexChangedHandler handler) {
        handler.onSpacerIndexChanged(this);
    }

}
