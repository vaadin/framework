package com.vaadin.tests.components.grid.basics;

import java.util.List;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

/**
 * @author Vaadin Ltd
 *
 */
public class RefreshDataProvider extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<DataObject> grid = new Grid<>();
        List<DataObject> data = DataObject.generateObjects();

        ListDataProvider<DataObject> dataProvider = DataProvider
                .ofCollection(data);
        grid.setDataProvider(dataProvider);

        grid.setDataProvider(dataProvider);
        grid.addColumn(DataObject::getCoordinates).setCaption("Coordinates")
                .setId("Coordinates");
        addComponent(grid);

        Button update = new Button("Update data",
                event -> updateData(dataProvider, data));
        update.setId("update");
        addComponent(update);

        Button add = new Button("Add data",
                event -> addData(dataProvider, data));
        add.setId("add");
        addComponent(add);

        Button remove = new Button("Remove data",
                event -> removeData(dataProvider, data));
        remove.setId("remove");
        addComponent(remove);
    }

    private void updateData(DataProvider<DataObject, ?> dataProvider,
            List<DataObject> data) {
        data.get(0).setCoordinates("Updated coordinates");
        dataProvider.refreshAll();
    }

    private void addData(DataProvider<DataObject, ?> dataProvider,
            List<DataObject> data) {
        DataObject dataObject = new DataObject();
        dataObject.setCoordinates("Added");
        data.add(0, dataObject);
        dataProvider.refreshAll();
    }

    private void removeData(DataProvider<DataObject, ?> dataProvider,
            List<DataObject> data) {
        data.remove(0);
        dataProvider.refreshAll();
    }
}
