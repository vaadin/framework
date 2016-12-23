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
package com.vaadin.tests.components.radiobutton;

import java.util.LinkedHashMap;
import java.util.stream.IntStream;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.tests.components.abstractlisting.AbstractListingTestUI;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.RadioButtonGroup;

/**
 * Test UI for RadioButtonGroup component
 *
 * @author Vaadin Ltd
 */
public class RadioButtonGroupTestUI
        extends AbstractListingTestUI<RadioButtonGroup<Object>> {

    private final String selectionCategory = "Selection";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class<RadioButtonGroup<Object>> getTestClass() {
        return (Class) RadioButtonGroup.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createListenerMenu();
        createSelectionMenu();
        createItemIconGeneratorMenu();
        createItemCaptionGeneratorMenu();
    }

    protected void createSelectionMenu() {
        createClickAction("Clear selection", selectionCategory,
                (component, item, data) -> component.getSelectedItem()
                        .ifPresent(value -> component.setValue(null)),
                "");

        Command<RadioButtonGroup<Object>, String> toggleSelection = (component,
                item, data) -> toggleSelection(item);

        IntStream.of(0, 1, 5, 10, 25).mapToObj(i -> "Item " + i)
                .forEach(item -> createClickAction("Toggle " + item,
                        selectionCategory, toggleSelection, item));
    }

    private void createItemIconGeneratorMenu() {
        createBooleanAction("Use Item Icon Generator", "Item Icon Generator",
                false, this::useItemIconGenerator);
    }

    private void useItemIconGenerator(RadioButtonGroup<Object> group,
            boolean activate, Object data) {
        if (activate) {
            group.setItemIconGenerator(
                    item -> VaadinIcons.values()[getIndex(item) + 1]);
        } else {
            group.setItemIconGenerator(item -> null);
        }
        group.getDataProvider().refreshAll();
    }

    private void createItemCaptionGeneratorMenu() {
        LinkedHashMap<String, ItemCaptionGenerator<Object>> options = new LinkedHashMap<>();
        options.put("Null Caption Generator", item -> null);
        options.put("Default Caption Generator", item -> item.toString());
        options.put("Custom Caption Generator",
                item -> item.toString() + " Caption");

        createSelectAction("Item Caption Generator", "Item Caption Generator",
                options, "None", (radioButtonGroup, captionGenerator, data) -> {
                    radioButtonGroup.setItemCaptionGenerator(captionGenerator);
                    radioButtonGroup.getDataProvider().refreshAll();
                }, true);
    }

    private void toggleSelection(String item) {
        if (getComponent().isSelected(item)) {
            getComponent().setValue(null);
        } else {
            getComponent().setValue(item);
        }
    }

    protected void createListenerMenu() {
        createListenerAction("Selection listener", "Listeners",
                c -> c.addSelectionListener(
                        e -> log("Selected: " + e.getSelectedItem())));
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
