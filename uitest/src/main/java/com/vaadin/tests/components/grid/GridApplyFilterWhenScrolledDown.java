package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@Theme("valo")
public class GridApplyFilterWhenScrolledDown extends UI {

    Grid grid = new Grid();

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        grid.addColumn("Name", String.class);

        HeaderRow appendHeaderRow = grid.appendHeaderRow();
        TextField filter = getColumnFilter("Name");
        appendHeaderRow.getCell("Name").setComponent(filter);

        for (int i = 0; i < 1000; i++) {
            Item addItem = grid.getContainerDataSource().addItem(i);
            addItem.getItemProperty("Name").setValue("Name " + i);

        }

        Item addItem = grid.getContainerDataSource().addItem(1000);
        addItem.getItemProperty("Name").setValue("Test");

        grid.scrollToStart();
        setContent(grid);
    }

    private TextField getColumnFilter(final Object columnId) {
        TextField filter = new TextField();
        filter.setWidth("100%");
        filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filter.addTextChangeListener(new TextChangeListener() {
            SimpleStringFilter filter = null;

            @Override
            public void textChange(TextChangeEvent event) {
                Filterable f = (Filterable) grid.getContainerDataSource();
                if (filter != null) {
                    f.removeContainerFilter(filter);
                }
                filter = new SimpleStringFilter(columnId, event.getText(), true,
                        true);
                f.addContainerFilter(filter);
            }
        });
        return filter;
    }

}