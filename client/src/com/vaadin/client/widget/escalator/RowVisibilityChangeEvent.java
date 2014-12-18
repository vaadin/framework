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

package com.vaadin.client.widget.escalator;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired when the range of visible rows changes e.g. because of scrolling.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class RowVisibilityChangeEvent extends
        GwtEvent<RowVisibilityChangeHandler> {
    /**
     * The type of this event.
     */
    public static final Type<RowVisibilityChangeHandler> TYPE = new Type<RowVisibilityChangeHandler>();

    private final int firstVisibleRow;
    private final int visibleRowCount;

    /**
     * Creates a new row visibility change event
     * 
     * @param firstVisibleRow
     *            the index of the first visible row
     * @param visibleRowCount
     *            the number of visible rows
     */
    public RowVisibilityChangeEvent(int firstVisibleRow, int visibleRowCount) {
        this.firstVisibleRow = firstVisibleRow;
        this.visibleRowCount = visibleRowCount;
    }

    /**
     * Gets the index of the first row that is at least partially visible.
     * 
     * @return the index of the first visible row
     */
    public int getFirstVisibleRow() {
        return firstVisibleRow;
    }

    /**
     * Gets the number of at least partially visible rows.
     * 
     * @return the number of visible rows
     */
    public int getVisibleRowCount() {
        return visibleRowCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<RowVisibilityChangeHandler> getAssociatedType() {
        return TYPE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared
     * .EventHandler)
     */
    @Override
    protected void dispatch(RowVisibilityChangeHandler handler) {
        handler.onRowVisibilityChange(this);
    }

}
