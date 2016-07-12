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
package com.vaadin.tokka.ui.components.grid;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.vaadin.tokka.server.communication.data.KeyMapper;
import com.vaadin.tokka.server.communication.data.SingleSelection;
import com.vaadin.tokka.ui.components.AbstractListing;

public class Grid<T> extends AbstractListing<T> {

    private KeyMapper<Column<T, ?>> columnKeys = new KeyMapper<>();
    private Set<Column<T, ?>> columnSet = new HashSet<>();

    public Grid() {
        setSelectionModel(new SingleSelection<T>());
    }

    public <V> Column<T, V> addColumn(String caption, Function<T, V> getter) {
        Column<T, V> c = new Column<T, V>(caption, getter);

        c.extend(this);
        c.setCommunicationId(columnKeys.key(c));
        columnSet.add(c);
        addDataGenerator(c);

        return c;
    }

    public void removeColumn(Column<T, ?> column) {
        if (columnSet.remove(column)) {
            columnKeys.remove(column);
            removeDataGenerator(column);
            column.remove();
        }
    }
}
