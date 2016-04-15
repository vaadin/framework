package com.vaadin.tests.components.combobox;

import java.util.LinkedHashMap;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ComboBox.ItemStyleGenerator;

public class ComboBoxes2<T extends ComboBox> extends AbstractSelectTestCase<T> {

    private Command<T, String> inputPromptCommand = new Command<T, String>() {
        @Override
        public void execute(T c, String value, Object data) {
            c.setInputPrompt(value);
        }
    };
    private Command<T, FilteringMode> filteringModeCommand = new Command<T, FilteringMode>() {

        @Override
        public void execute(T c, FilteringMode value, Object data) {
            c.setFilteringMode(value);
        }
    };

    private Command<T, ItemStyleGenerator> itemStyleGeneratorCommand = new Command<T, ItemStyleGenerator>() {
        @Override
        public void execute(T c, ItemStyleGenerator value, Object data) {
            c.setItemStyleGenerator(value);
        }
    };

    @Override
    protected Class<T> getTestClass() {
        return (Class<T>) ComboBox.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createItemIconSelect(CATEGORY_DATA_SOURCE);
        createInputPromptAction(CATEGORY_FEATURES);
        createFilteringModeAction(CATEGORY_FEATURES);
        createItemStyleGeneratorAction(CATEGORY_FEATURES);
        createNewItemsAllowedAction(CATEGORY_STATE);
        createTextInputAlowedAction(CATEGORY_STATE);
    }

    private void createTextInputAlowedAction(String category) {
        createBooleanAction("Text input allowed", category, true,
                new Command<T, Boolean>() {
                    @Override
                    public void execute(T c, Boolean value, Object data) {
                        c.setTextInputAllowed(value.booleanValue());
                    }
                });
    }

    private void createNewItemsAllowedAction(String category) {
        createBooleanAction("New items allowed", category, false,
                new Command<T, Boolean>() {
                    @Override
                    public void execute(T c, Boolean value, Object data) {
                        c.setNewItemsAllowed(value.booleanValue());
                    }
                });
    }

    private void createFilteringModeAction(String category) {
        LinkedHashMap<String, FilteringMode> options = new LinkedHashMap<String, FilteringMode>();
        options.put("Off", FilteringMode.OFF);
        options.put("Contains", FilteringMode.CONTAINS);
        options.put("Starts with", FilteringMode.STARTSWITH);

        createSelectAction("Filtering mode", category, options, "Contains",
                filteringModeCommand);

    }

    private void createItemStyleGeneratorAction(String category) {
        LinkedHashMap<String, ItemStyleGenerator> options = new LinkedHashMap<String, ItemStyleGenerator>();
        options.put("-", null);
        options.put("Bold fives", new ItemStyleGenerator() {
            @Override
            public String getStyle(ComboBox source, Object itemId) {
                if (String.valueOf(itemId).indexOf('5') != -1) {
                    return "bold";
                } else {
                    return null;
                }
            }
        });
        createSelectAction("Item style generator", category, options, "-",
                itemStyleGeneratorCommand);
    }

    private void createInputPromptAction(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("-", null);
        options.put("Enter a value", "Enter a value");
        options.put("- Click here -", "- Click here -");
        createSelectAction("Input prompt", category, options, "-",
                inputPromptCommand);

    }

    private void createItemIconSelect(String category) {

        createSelectAction("Icon", category, createIconOptions(false), "-",
                new Command<T, Resource>() {

                    @Override
                    public void execute(T c, Resource value, Object data) {
                        for (Object id : c.getItemIds()) {
                            if (value == null) {
                                c.setItemIcon(id, null);
                            } else {
                                c.setItemIcon(id, value);
                            }
                        }
                    }
                });
    }

}
