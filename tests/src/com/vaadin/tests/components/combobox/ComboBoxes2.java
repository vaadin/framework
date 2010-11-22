package com.vaadin.tests.components.combobox;

import java.util.LinkedHashMap;

import com.vaadin.terminal.Resource;
import com.vaadin.tests.components.select.AbstractSelectTestCase;
import com.vaadin.ui.ComboBox;

public class ComboBoxes2 extends AbstractSelectTestCase<ComboBox> {

    private Command<ComboBox, String> inputPromptCommand = new Command<ComboBox, String>() {
        public void execute(ComboBox c, String value, Object data) {
            c.setInputPrompt(value);
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
