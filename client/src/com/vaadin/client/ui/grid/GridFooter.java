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

import com.google.gwt.core.client.Scheduler;

/**
 * Represents the footer section of a Grid. The footer is always empty.
 * 
 * TODO Arbitrary number of footer rows (zero by default)
 * 
 * TODO Merging footer cells
 * 
 * TODO Widgets in cells
 * 
 * TODO HTML in cells
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridFooter extends GridStaticSection<GridFooter.FooterRow> {

    /**
     * A single row in a grid Footer section.
     * 
     */
    public class FooterRow extends GridStaticSection.StaticRow<FooterCell> {

        @Override
        protected FooterCell createCell() {
            return new FooterCell();
        }
    }

    /**
     * A single cell in a grid Footer row. Has a textual caption.
     * 
     */
    public class FooterCell extends GridStaticSection.StaticCell {
    }

    private boolean markAsDirty = false;

    @Override
    protected FooterRow createRow() {
        return new FooterRow();
    }

    @Override
    protected void refreshSection() {
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
                    getGrid().refreshFooter();
                }
            }
        });
    }
}
