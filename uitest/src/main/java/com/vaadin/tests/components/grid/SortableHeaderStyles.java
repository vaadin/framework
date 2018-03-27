/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.util.Collection;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.OptionGroup;

@Theme("valo")
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
