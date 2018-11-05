package com.vaadin.tests.fieldgroup;

import java.text.DateFormat;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.validator.IntegerRangeValidator;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.DateRenderer;

public class BasicCrudGridEditorRow extends AbstractBasicCrud {

    private Grid grid;

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);
        formType.setVisible(false);
        grid = new Grid();

        grid.setContainerDataSource(container);

        grid.setColumnOrder((Object[]) columns);
        grid.removeColumn("salary");
        grid.addSelectionListener(event -> {
            Item item = grid.getContainerDataSource()
                    .getItem(grid.getSelectedRow());
            form.edit((BeanItem<ComplexPerson>) item);
        });
        grid.setEditorEnabled(true);
        grid.setSizeFull();
        grid.getColumn("age").getEditorField().addValidator(
                new IntegerRangeValidator("Must be between 0 and 100", 0, 100));
        grid.getColumn("birthDate").setRenderer(new DateRenderer(
                DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US)));
        addComponent(grid);
        getLayout().setExpandRatio(grid, 1);
    }

    @Override
    protected void deselectAll() {
        grid.select(null);
    }

}
