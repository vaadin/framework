package com.vaadin.tests.components.checkbox;

import java.util.LinkedHashMap;
import java.util.Objects;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.tests.components.abstractlisting.AbstractMultiSelectTestUI;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.DescriptionGenerator;
import com.vaadin.ui.IconGenerator;

/**
 * Test UI for CheckBoxGroup component
 *
 * @author Vaadin Ltd
 */
public class CheckBoxGroupTestUI
        extends AbstractMultiSelectTestUI<CheckBoxGroup<Object>> {

    private static final IconGenerator<Object> DEFAULT_ICON_GENERATOR = item -> "Item 2"
            .equals(item) ? ICON_16_HELP_PNG_CACHEABLE : null;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Class<CheckBoxGroup<Object>> getTestClass() {
        return (Class) CheckBoxGroup.class;
    }

    @Override
    protected CheckBoxGroup<Object> constructComponent() {
        CheckBoxGroup<Object> checkBoxGroup = super.constructComponent();
        checkBoxGroup.setItemIconGenerator(DEFAULT_ICON_GENERATOR);
        checkBoxGroup.setItemEnabledProvider(item -> !"Item 10".equals(item));
        return checkBoxGroup;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createItemIconGenerator();
        createItemDescriptionGeneratorMenu();
        createItemEnabledProviderMenu();
    }

    private void createItemIconGenerator() {
        createBooleanAction("Use Item Icon Generator", "Item Generator", false,
                this::useItemIconProvider);
    }

    private void createItemDescriptionGeneratorMenu() {
        LinkedHashMap<String, DescriptionGenerator<Object>> options = new LinkedHashMap<>();
        options.put("Null Description Generator", item -> null);
        options.put("Default Description Generator", item -> item.toString());
        options.put("Custom Description Generator",
                item -> item + " Description");

        createSelectAction("Item Description Generator",
                "Item Description Generator", options, "None",
                (checkBoxGroup, generator, data) -> {
                    checkBoxGroup.setItemDescriptionGenerator(generator);
                    checkBoxGroup.getDataProvider().refreshAll();
                }, true);
    }

    private void createItemEnabledProviderMenu() {
        LinkedHashMap<String, SerializablePredicate<Object>> options = new LinkedHashMap<>();
        options.put("Disable Item 0", o -> !Objects.equals(o, "Item 0"));
        options.put("Disable Item 3", o -> !Objects.equals(o, "Item 3"));
        options.put("Disable Item 5", o -> !Objects.equals(o, "Item 5"));

        createSelectAction("Item Enabled Provider", "Item Enabled Provider",
                options, "None", (checkBoxGroup, generator, data) -> {
                    checkBoxGroup.setItemEnabledProvider(generator);
                    checkBoxGroup.getDataProvider().refreshAll();
                }, true);
    }

    private void useItemIconProvider(CheckBoxGroup<Object> group,
            boolean activate, Object data) {
        if (activate) {
            group.setItemIconGenerator(
                    item -> VaadinIcons.values()[getIndex(item) + 1]);
        } else {
            group.setItemIconGenerator(DEFAULT_ICON_GENERATOR);
        }
        group.getDataProvider().refreshAll();
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
