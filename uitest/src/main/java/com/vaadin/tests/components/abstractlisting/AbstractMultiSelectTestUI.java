package com.vaadin.tests.components.abstractlisting;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.ui.AbstractMultiSelect;
import com.vaadin.ui.ItemCaptionGenerator;

public abstract class AbstractMultiSelectTestUI<MULTISELECT extends AbstractMultiSelect<Object>>
        extends AbstractListingTestUI<MULTISELECT> {

    protected final String selectionCategory = "Selection";

    @Override
    protected void createActions() {
        super.createActions();
        createItemCaptionGeneratorMenu();
        createSelectionMenu();
        createListenerMenu();
    }

    protected void createItemCaptionGeneratorMenu() {
        LinkedHashMap<String, ItemCaptionGenerator<Object>> options = new LinkedHashMap<>();
        options.put("Null Caption Generator", item -> null);
        options.put("Default Caption Generator", item -> item.toString());
        options.put("Custom Caption Generator",
                item -> item.toString() + " Caption");

        createSelectAction("Item Caption Generator", "Item Generator", options,
                "None", (abstractMultiSelect, captionGenerator, data) -> {
                    abstractMultiSelect
                            .setItemCaptionGenerator(captionGenerator);
                    abstractMultiSelect.getDataCommunicator().getDataProvider()
                            .refreshAll();
                }, true);
    }

    protected void createSelectionMenu() {
        createClickAction("Clear selection", selectionCategory,
                (component, item, data) -> component.deselectAll(), "");

        Command<MULTISELECT, String> toggleSelection = (component, item,
                data) -> toggleSelection(item);

        List<String> items = IntStream.of(0, 1, 5, 10, 25)
                .mapToObj(i -> "Item " + i).collect(Collectors.toList());
        items.forEach(item -> createClickAction("Toggle " + item,
                selectionCategory, toggleSelection, item));

        Command<MULTISELECT, Boolean> toggleMultiSelection = (component, i,
                data) -> toggleMultiSelection(i, items);

        createBooleanAction("Toggle items 0, 1, 5, 10, 25", selectionCategory,
                false, toggleMultiSelection, items);
    }

    private void toggleSelection(String item) {
        if (getComponent().isSelected(item)) {
            getComponent().deselect(item);
        } else {
            getComponent().select(item);
        }
    }

    private void toggleMultiSelection(boolean add, List<String> items) {
        if (add) {
            getComponent().select(items.toArray());
        } else {
            getComponent().deselect(items.toArray());
        }
    }

    protected void createListenerMenu() {
        createListenerAction("Selection listener", "Listeners",
                c -> c.addSelectionListener(
                        e -> log("Selected: " + e.getNewSelection())));
    }
}
