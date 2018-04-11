/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.client.widget.grid;

import com.google.gwt.event.shared.GwtEvent;
import com.vaadin.shared.Range;

/**
 * Event object describing a change of row availability in DataSource of a Grid.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DataAvailableEvent extends GwtEvent<DataAvailableHandler> {

    private Range rowsAvailable;
    public static final Type<DataAvailableHandler> TYPE = new Type<DataAvailableHandler>();

    public DataAvailableEvent(Range rowsAvailable) {
        this.rowsAvailable = rowsAvailable;
    }

    /**
     * Returns the range of available rows in {@link com.vaadin.client.data.DataSource DataSource} for this event.
     *
     * @return range of available rows
     */
    public Range getAvailableRows() {
        return rowsAvailable;
    }

    @Override
    public Type<DataAvailableHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DataAvailableHandler handler) {
        handler.onDataAvailable(this);
    }

}
