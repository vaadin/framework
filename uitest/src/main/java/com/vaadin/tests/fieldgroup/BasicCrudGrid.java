package com.vaadin.tests.fieldgroup;

import com.vaadin.server.VaadinRequest;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.Grid;

public class BasicCrudGrid extends AbstractBasicCrud {

    private Grid grid;

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);
        grid = new Grid();

        grid.setContainerDataSource(container);

        grid.setColumnOrder((Object[]) columns);
        grid.removeColumn("salary");
        grid.addSelectionListener(event -> {
            Item item = grid.getContainerDataSource()
                    .getItem(grid.getSelectedRow());
            form.edit((BeanItem<ComplexPerson>) item);
        });

        grid.setSizeFull();

        addComponent(grid);
        addComponent(form);
        getLayout().setExpandRatio(grid, 1);
    }

    @Override
    protected void deselectAll() {
        grid.select(null);
    }

}
