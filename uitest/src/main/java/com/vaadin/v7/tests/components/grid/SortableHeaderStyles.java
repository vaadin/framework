package com.vaadin.v7.tests.components.grid;

import java.util.Collection;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.Column;
import com.vaadin.v7.ui.OptionGroup;

public class SortableHeaderStyles extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getPage().getStyles().add(
                ".valo .v-grid-header th.v-grid-cell.sortable { font-weight: bold;}");

        PersonContainer container = PersonContainer.createWithTestData();

        Collection<?> sortableContainerPropertyIds = container
                .getSortableContainerPropertyIds();

        final OptionGroup sortableSelector = new OptionGroup("Sortable columns",
                sortableContainerPropertyIds);
        sortableSelector.setMultiSelect(true);
        sortableSelector.setValue(sortableContainerPropertyIds);

        final Grid grid = new Grid(container);

        sortableSelector.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Collection<?> sortableCols = (Collection<?>) sortableSelector
                        .getValue();
                for (Column column : grid.getColumns()) {
                    column.setSortable(
                            sortableCols.contains(column.getPropertyId()));
                }
            }
        });

        addComponent(sortableSelector);
        addComponent(grid);
    }

}
