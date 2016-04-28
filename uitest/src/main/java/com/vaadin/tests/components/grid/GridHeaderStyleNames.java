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
package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.components.beanitemcontainer.BeanItemContainerGenerator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.FooterCell;
import com.vaadin.ui.Grid.FooterRow;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;

@Theme("valo")
public class GridHeaderStyleNames extends AbstractTestUIWithLog {

    private HeaderCell ageHeaderCell;
    private HeaderCell mergedCityCountryCell;
    private FooterCell ageFooterCell;
    private HeaderRow headerRow;
    private FooterRow footerRow;

    @Override
    protected void setup(VaadinRequest request) {
        Grid grid = new Grid();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.setContainerDataSource(BeanItemContainerGenerator
                .createContainer(100));

        ageHeaderCell = grid.getDefaultHeaderRow().getCell("age");
        grid.getDefaultHeaderRow().setStyleName("foo");
        headerRow = grid.prependHeaderRow();
        mergedCityCountryCell = headerRow.join("city", "country");
        mergedCityCountryCell.setText("Merged cell");
        addComponent(grid);

        footerRow = grid.appendFooterRow();
        ageFooterCell = footerRow.getCell("age");

        getPage()
                .getStyles()
                .add(".age {background-image: linear-gradient(to bottom,green 2%, #efefef 98%) !important;}");
        getPage()
                .getStyles()
                .add(".valo .v-grid-header .v-grid-cell.city-country {background-image: linear-gradient(to bottom,yellow 2%, #efefef 98%) !important;}");
        getPage()
                .getStyles()
                .add(".valo .v-grid-footer .v-grid-cell.age-footer {background-image: linear-gradient(to bottom,blue 2%, #efefef 98%) !important;}");
        getPage()
                .getStyles()
                .add(".valo .v-grid .v-grid-row.custom-row > * {background-image: linear-gradient(to bottom,purple 2%, #efefef 98%);}");

        setCellStyles(true);
        setRowStyles(true);

        Button b = new Button("Toggle styles");
        b.addClickListener(new ClickListener() {
            private boolean stylesOn = true;

            @Override
            public void buttonClick(ClickEvent event) {
                setCellStyles(!stylesOn);
                setRowStyles(!stylesOn);
                stylesOn = !stylesOn;
            }
        });
        addComponent(b);
    }

    protected void setCellStyles(boolean set) {
        if (set) {
            ageHeaderCell.setStyleName("age");
            ageFooterCell.setStyleName("age-footer");
            mergedCityCountryCell.setStyleName("city-country");
        } else {
            ageHeaderCell.setStyleName(null);
            ageFooterCell.setStyleName(null);
            mergedCityCountryCell.setStyleName(null);
        }

    }

    protected void setRowStyles(boolean set) {
        if (set) {
            headerRow.setStyleName("custom-row");
            footerRow.setStyleName("custom-row");
        } else {
            headerRow.setStyleName(null);
            footerRow.setStyleName(null);
        }

    }
}
