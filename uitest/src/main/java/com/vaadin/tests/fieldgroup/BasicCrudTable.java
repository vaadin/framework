package com.vaadin.tests.fieldgroup;

import com.vaadin.server.VaadinRequest;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.Table;

public class BasicCrudTable extends AbstractBasicCrud {

    private Table table;

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);

        table = new Table();
        table.setSelectable(true);

        table.setContainerDataSource(container);

        table.setVisibleColumns((Object[]) columns);
        table.addValueChangeListener(event -> form.edit(
                (BeanItem<ComplexPerson>) table.getItem(table.getValue())));

        table.setSizeFull();

        addComponent(table);
        addComponent(form);
        getLayout().setExpandRatio(table, 1);
    }

    @Override
    protected void deselectAll() {
        table.setValue(null);

    }

}
