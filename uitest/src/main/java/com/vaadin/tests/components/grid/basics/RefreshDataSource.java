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
package com.vaadin.tests.components.grid.basics;

import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.data.DataSource;
import com.vaadin.server.data.ListDataSource;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

/**
 * @author Vaadin Ltd
 *
 */
public class RefreshDataSource extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<DataObject> grid = new Grid<>();
        List<DataObject> data = DataObject.generateObjects();

        ListDataSource<DataObject> dataSource = DataSource.create(data);
        grid.setDataSource(dataSource);

        grid.setDataSource(dataSource);
        grid.addColumn("Coordinates", DataObject::getCoordinates);
        addComponent(grid);

        Button update = new Button("Update data",
                event -> updateData(dataSource, data));
        update.setId("update");
        addComponent(update);

        Button add = new Button("Add data", event -> addData(dataSource, data));
        add.setId("add");
        addComponent(add);

        Button remove = new Button("Remove data",
                event -> removeData(dataSource, data));
        remove.setId("remove");
        addComponent(remove);
    }

    private void updateData(DataSource<DataObject> dataSource,
            List<DataObject> data) {
        data.get(0).setCoordinates("Updated coordinates");
        dataSource.refreshAll();
    }

    private void addData(DataSource<DataObject> dataSource,
            List<DataObject> data) {
        DataObject dataObject = new DataObject();
        dataObject.setCoordinates("Added");
        data.add(0, dataObject);
        dataSource.refreshAll();
    }

    private void removeData(DataSource<DataObject> dataSource,
            List<DataObject> data) {
        data.remove(0);
        dataSource.refreshAll();
    }
}
