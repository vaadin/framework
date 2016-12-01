/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.components.grid.basics.DataObject;
import com.vaadin.tests.components.grid.basics.GridBasics;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;

@Widgetset(TestingWidgetSet.NAME)
public class GridCustomSelectionModel extends AbstractTestUI {

    public static class MySelectionModel
            extends MultiSelectionModelImpl<DataObject> {

    }

    private Grid<DataObject> grid;
    private List<DataObject> data;

    @Override
    protected void setup(VaadinRequest request) {
        data = DataObject.generateObjects();

        // Create grid
        grid = new Grid<DataObject>() {
            {
                setSelectionModel(new MySelectionModel());
            }
        };
        grid.setItems(data);
        grid.addColumn(dataObj -> "(" + dataObj.getRowNumber() + ", 0)")
                .setCaption(GridBasics.COLUMN_CAPTIONS[0]);
        grid.addColumn(dataObj -> "(" + dataObj.getRowNumber() + ", 1)")
                .setCaption(GridBasics.COLUMN_CAPTIONS[1]);
        grid.addColumn(dataObj -> "(" + dataObj.getRowNumber() + ", 2)")
                .setCaption(GridBasics.COLUMN_CAPTIONS[2]);
        addComponent(grid);
    }

}
