package com.vaadin.tests.components.combobox;

import java.util.LinkedHashMap;

import com.vaadin.terminal.Resource;
import com.vaadin.tests.components.select.SelectTest;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Select;

public class ComboBoxes2 extends SelectTest<ComboBox> {

    private Command<ComboBox, String> inputPromptCommand = new Command<ComboBox, String>() {
        public void execute(ComboBox c, String value, Object data) {
            c.setInputPrompt(value);
        }
    };
    private Command<ComboBox, Integer> filteringModeCommand = new Command<ComboBox, Integer>() {

        public void execute(ComboBox c, Integer value, Object data) {
            c.setFilteringMode(value);
        }
    };

    @Override
    protected Class<ComboBox> getTestClass() {
        return ComboBox.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createItemIconSelect(CATEGORY_DATA_SOURCE);
        createInputPromptAction(CATEGORY_FEATURES);
        createFilteringModeAction(CATEGORY_FEATURES);
    }

    private void createFilteringModeAction(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("Off", Select.FILTERINGMODE_OFF);
        options.put("Contains", Select.FILTERINGMODE_CONTAINS);
        options.put("Starts with", Select.FILTERINGMODE_STARTSWITH);

        createSelectAction("Filtering mode", category, options, "Contains",
                filteringModeCommand);

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
                new Command<ComboBox, Resource>() {

                    public void execute(ComboBox c, Resource value, Object data) {
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
