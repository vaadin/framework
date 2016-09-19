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

import java.util.function.Function;
import java.util.stream.IntStream;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
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

    private static final Function<Object, Resource> DEFAULT_ICON_PROVIDER = item -> "Item 2"
            .equals(item) ? ICON_16_HELP_PNG_CACHEABLE : null;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class<CheckBoxGroup<Object>> getTestClass() {
        return (Class) CheckBoxGroup.class;
    }

    @Override
    protected CheckBoxGroup<Object> constructComponent() {
        CheckBoxGroup<Object> checkBoxGroup = super.constructComponent();
        checkBoxGroup.setItemIconProvider(DEFAULT_ICON_PROVIDER);
        checkBoxGroup.setItemEnabledProvider(item -> !"Item 10".equals(item));
        return checkBoxGroup;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createListenerMenu();
        createSelectionMenu();
        createItemProviderMenu();
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

    private void createItemProviderMenu() {
        createBooleanAction("Use Item Caption Provider", "Item Provider", false,
                this::useItemCaptionProvider);
        createBooleanAction("Use Item Icon Provider", "Item Provider", false,
                this::useItemIconProvider);
    }

    private void useItemCaptionProvider(CheckBoxGroup<Object> group,
            boolean activate, Object data) {
        if (activate) {
            group.setItemCaptionProvider(item -> item.toString() + " Caption");
        } else {
            group.setItemCaptionProvider(item -> item.toString());
        }
        group.getDataSource().refreshAll();
    }

    private void useItemIconProvider(CheckBoxGroup<Object> group,
            boolean activate, Object data) {
        if (activate) {
            group.setItemIconProvider(
                    item -> FontAwesome.values()[getIndex(item) + 1]);
        } else {
            group.setItemIconProvider(DEFAULT_ICON_PROVIDER);
        }
        group.getDataSource().refreshAll();
    }

    protected void createListenerMenu() {
        createListenerAction("Selection listener", "Listeners",
                c -> c.addSelectionListener(
                        e -> log("Selected: " + e.getNewSelection())));
    }

    private int getIndex(Object item) {
        int index = item.toString().indexOf(' ');
        if (index < 0) {
            return 0;
        }
        String postfix = item.toString().substring(index + 1);
        index = postfix.indexOf(' ');
        if (index >= 0) {
            postfix = postfix.substring(0, index);
        }
        try {
            return Integer.parseInt(postfix);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
