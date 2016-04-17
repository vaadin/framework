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
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;

@Theme("valo")
public class GridSidebarPosition extends AbstractTestUI {

    static final String POPUP_ABOVE = "above";
    static final String POPUP_WINDOW_MOVED_UP = "movedup";
    static final String POPUP_WINDOW_HEIGHT = "windowheight";

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setHeight("100%");
        setContent(hl);
        Grid grid = new Grid("Popup window height");
        grid.setId(POPUP_WINDOW_HEIGHT);
        grid.setWidth("100px");
        for (int i = 0; i < 30; i++) {
            grid.addColumn(
                    "This is a really really really really long column name "
                            + i).setHidable(true);
        }
        hl.addComponent(grid);

        grid = new Grid("Popup moved up");
        grid.setId(POPUP_WINDOW_MOVED_UP);
        grid.setWidth("100px");
        grid.setHeight("400px");
        for (int i = 0; i < 15; i++) {
            grid.addColumn("Column " + i).setHidable(true);
        }
        hl.addComponent(grid);
        hl.setComponentAlignment(grid, Alignment.BOTTOM_LEFT);

        grid = new Grid("Popup above");
        grid.setId(POPUP_ABOVE);
        grid.setWidth("100px");
        grid.setHeight("200px");
        for (int i = 0; i < 10; i++) {
            grid.addColumn("Column " + i).setHidable(true);
        }
        hl.addComponent(grid);
        hl.setComponentAlignment(grid, Alignment.BOTTOM_LEFT);
    }

}
