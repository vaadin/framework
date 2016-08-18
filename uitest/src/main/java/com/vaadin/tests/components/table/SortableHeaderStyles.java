/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.components.table;

import java.util.Collection;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;

public class SortableHeaderStyles extends AbstractTestUI {

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

        sortableSelector.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                // Trigger repaint that will read the value again
                table.markAsDirty();
            }
        });

        addComponent(sortableSelector);
        addComponent(table);
    }
}
