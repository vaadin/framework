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
package com.vaadin.ui.components.grid;

import com.vaadin.shared.ui.grid.GridStaticSectionState;

/**
 * Represents the footer section of a Grid. By default Footer is not visible.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridFooter extends GridStaticSection<GridFooter.FooterRow> {

    public class FooterRow extends GridStaticSection.StaticRow<FooterCell> {

        protected FooterRow(GridStaticSection<?> section) {
            super(section);
        }

        @Override
        protected FooterCell createCell() {
            return new FooterCell(this);
        }

    }

    public class FooterCell extends GridStaticSection.StaticCell {

        protected FooterCell(FooterRow row) {
            super(row);
        }
    }

    private final GridStaticSectionState footerState = new GridStaticSectionState();

    protected GridFooter(Grid grid) {
        this.grid = grid;
        grid.getState(true).footer = footerState;
    }

    @Override
    protected GridStaticSectionState getSectionState() {
        return footerState;
    }

    @Override
    protected FooterRow createRow() {
        return new FooterRow(this);
    }

}
