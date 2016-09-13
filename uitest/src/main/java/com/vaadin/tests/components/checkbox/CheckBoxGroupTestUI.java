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
package com.vaadin.tests.components.checkbox;

import java.util.stream.IntStream;

import com.vaadin.shared.data.selection.SelectionModel.Multi;
import com.vaadin.tests.components.abstractlisting.AbstractListingTestUI;
import com.vaadin.ui.CheckBoxGroup;

/**
 * Test UI for CheckBoxGroup component
 *
 * @author Vaadin Ltd
 */
public class CheckBoxGroupTestUI
        extends AbstractListingTestUI<CheckBoxGroup<Object>> {

    private final String selectionCategory = "Selection";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class<CheckBoxGroup<Object>> getTestClass() {
        return (Class) CheckBoxGroup.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createListenerMenu();
        createSelectionMenu();
    }

    protected void createSelectionMenu() {
        createClickAction(
                "Clear selection", selectionCategory, (component, item,
                        data) -> component.getSelectionModel().deselectAll(),
                "");

        Command<CheckBoxGroup<Object>, String> toggleSelection = (component,
                item, data) -> toggleSelection(item);

        IntStream.of(0, 1, 5, 10, 25).mapToObj(i -> "Item " + i)
                .forEach(item -> {
                    createClickAction("Toggle " + item, selectionCategory,
                            toggleSelection, item);
                });
    }

    private void toggleSelection(String item) {
        Multi<Object> selectionModel = getComponent().getSelectionModel();
        if (selectionModel.isSelected(item)) {
            selectionModel.deselect(item);
        } else {
            selectionModel.select(item);
        }
    }

    protected void createListenerMenu() {
        createListenerAction("Selection listener", "Listeners",
                c -> c.addSelectionListener(
                        e -> log("Selected: " + e.getNewSelection())));
    }
}
