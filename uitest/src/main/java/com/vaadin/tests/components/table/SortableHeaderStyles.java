package com.vaadin.tests.components.table;

import java.util.Collection;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.Table;

public class SortableHeaderStyles extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getPage().getStyles()
                .add(".v-table-header-sortable { font-weight: bold;}");

        PersonContainer container = PersonContainer.createWithTestData();

        Collection<?> sortableContainerPropertyIds = container
                .getSortableContainerPropertyIds();

        final OptionGroup sortableSelector = new OptionGroup("Sortable columns",
                sortableContainerPropertyIds);
        sortableSelector.setMultiSelect(true);
        sortableSelector.setValue(sortableContainerPropertyIds);

        final Table table = new Table() {
            @Override
            public Collection<?> getSortableContainerPropertyIds() {
                return (Collection<?>) sortableSelector.getValue();
            }
        };
        table.setContainerDataSource(container);

        sortableSelector.addValueChangeListener(event -> {
            // Trigger repaint that will read the value again
            table.markAsDirty();
        });

        addComponent(sortableSelector);
        addComponent(table);
    }
}
