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
package com.vaadin.tests.components.grid.basicfeatures;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Container;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@Title("Server Grid height by row on init")
@Theme(ValoTheme.THEME_NAME)
public class GridHeightByRowOnInit extends UI {

    private static final String PROPERTY = "Property";

    @Override
    protected void init(VaadinRequest request) {
        final Grid grid = new Grid();
        Container.Indexed container = grid.getContainerDataSource();
        container.addContainerProperty(PROPERTY, String.class, "");

        container.addItem("A").getItemProperty(PROPERTY).setValue("A");
        container.addItem("B").getItemProperty(PROPERTY).setValue("B");
        container.addItem("C").getItemProperty(PROPERTY).setValue("C");
        container.addItem("D").getItemProperty(PROPERTY).setValue("D");
        container.addItem("E").getItemProperty(PROPERTY).setValue("E");

        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(5);

        setContent(grid);
    }
}
