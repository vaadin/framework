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
package com.vaadin.client.ui.grid;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.BrowserEvents;

/**
 * Represents the header section of a Grid. A header consists of a single header
 * row containing a header cell for each column. Each cell has a simple textual
 * caption.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridHeader extends GridStaticSection<GridHeader.HeaderRow> {

    /**
     * A single row in a grid header section.
     * 
     */
    public class HeaderRow extends GridStaticSection.StaticRow<HeaderCell> {

        private boolean isDefault = false;

        protected void setDefault(boolean isDefault) {
            this.isDefault = isDefault;
        }

        public boolean isDefault() {
            return isDefault;
        }

        @Override
        protected HeaderCell createCell() {
            return new HeaderCell();
        }
    }

    /**
     * A single cell in a grid header row. Has a textual caption.
     * 
     */
    public class HeaderCell extends GridStaticSection.StaticCell {
    }

    private HeaderRow defaultRow;

    private boolean markAsDirty = false;

    @Override
    public void removeRow(int index) {
        HeaderRow removedRow = getRow(index);
        super.removeRow(index);
        if (removedRow == defaultRow) {
            setDefaultRow(null);
        }
    }

    /**
     * Sets the default row of this header. The default row is a special header
     * row providing a user interface for sorting columns.
     * 
     * @param row
     *            the new default row, or null for no default row
     * 
     * @throws IllegalArgumentException
     *             this header does not contain the row
     */
    public void setDefaultRow(HeaderRow row) {
        if (row == defaultRow) {
            return;
        }
        if (row != null && !getRows().contains(row)) {
            throw new IllegalArgumentException(
                    "Cannot set a default row that does not exist in the container");
        }
        if (defaultRow != null) {
            defaultRow.setDefault(false);
        }
        if (row != null) {
            row.setDefault(true);
        }
        defaultRow = row;
        requestSectionRefresh();
    }

    /**
     * Returns the current default row of this header. The default row is a
     * special header row providing a user interface for sorting columns.
     * 
     * @return the default row or null if no default row set
     */
    public HeaderRow getDefaultRow() {
        return defaultRow;
    }

    @Override
    protected HeaderRow createRow() {
        return new HeaderRow();
    }

    @Override
    protected void requestSectionRefresh() {
        markAsDirty = true;

        /*
         * Defer the refresh so if we multiple times call refreshSection() (for
         * example when updating cell values) we only get one actual refresh in
         * the end.
         */
        Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                if (markAsDirty) {
                    markAsDirty = false;
                    getGrid().refreshHeader();
                }
            }
        });
    }

    /**
     * Returns the events consumed by the header
     * 
     * @return a collection of BrowserEvents
     */
    public Collection<String> getConsumedEvents() {
        return Arrays.asList(BrowserEvents.TOUCHSTART, BrowserEvents.TOUCHMOVE,
                BrowserEvents.TOUCHEND, BrowserEvents.TOUCHCANCEL,
                BrowserEvents.CLICK);
    }
}
