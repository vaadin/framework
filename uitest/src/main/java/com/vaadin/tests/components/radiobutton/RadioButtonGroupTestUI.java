package com.vaadin.tests.components.radiobutton;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.IntStream;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.tests.components.abstractlisting.AbstractListingTestUI;
import com.vaadin.ui.DescriptionGenerator;
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
        createItemDescriptionGeneratorMenu();
        createItemEnabledProviderMenu();
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
        options.put("Custom Caption Generator", item -> item + " Caption");

        createSelectAction("Item Caption Generator", "Item Caption Generator",
                options, "None", (radioButtonGroup, captionGenerator, data) -> {
                    radioButtonGroup.setItemCaptionGenerator(captionGenerator);
                    radioButtonGroup.getDataProvider().refreshAll();
                }, true);
    }

    private void createItemDescriptionGeneratorMenu() {
        LinkedHashMap<String, DescriptionGenerator<Object>> options = new LinkedHashMap<>();
        options.put("Null Description Generator", item -> null);
        options.put("Default Description Generator", item -> item.toString());
        options.put("Custom Description Generator",
                item -> item + " Description");

        createSelectAction("Item Description Generator",
                "Item Description Generator", options, "None",
                (radioButtonGroup, generator, data) -> {
                    radioButtonGroup.setItemDescriptionGenerator(generator);
                }, true);
    }

    private void createItemEnabledProviderMenu() {
        LinkedHashMap<String, SerializablePredicate<Object>> options = new LinkedHashMap<>();
        options.put("Disable Item 0", o -> !Objects.equals(o, "Item 0"));
        options.put("Disable Item 3", o -> !Objects.equals(o, "Item 3"));
        options.put("Disable Item 5", o -> !Objects.equals(o, "Item 5"));

        createSelectAction("Item Enabled Provider", "Item Enabled Provider",
                options, "None", (radioButtonGroup, generator, data) -> {
                    radioButtonGroup.setItemEnabledProvider(generator);
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
                        event -> log("Selected: " + event.getSelectedItem())));
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
