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
package com.vaadin.tests.minitutorials.v7_4;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.FooterCell;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.UI;

@Theme("valo")
public class ConfiguringGridHeadersAndFooters extends UI {
    @Override
    protected void init(VaadinRequest request) {
        Grid grid = new Grid(GridExampleHelper.createContainer());
        grid.setColumnOrder("name", "amount", "count");

        grid.getDefaultHeaderRow().getCell("amount")
                .setHtml("The <u>amount</u>");
        grid.getDefaultHeaderRow().getCell("count")
                .setComponent(new Button("Button caption"));

        grid.getColumn("name").setHeaderCaption("Bean name");

        HeaderRow extraHeader = grid.prependHeaderRow();
        HeaderCell joinedCell = extraHeader.join("amount", "count");
        joinedCell.setText("Joined cell");

        FooterCell footer = grid.appendFooterRow().join("name", "amount",
                "count");
        footer.setText("Right aligned footer");

        getPage().getStyles().add(".footer-right { text-align: right }");
        footer.setStyleName("footer-right");

        setContent(grid);
    }
}
