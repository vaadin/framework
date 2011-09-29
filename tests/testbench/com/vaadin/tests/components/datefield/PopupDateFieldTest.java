package com.vaadin.tests.components.datefield;

import java.util.LinkedHashMap;

import com.vaadin.ui.PopupDateField;

public class PopupDateFieldTest extends DateFieldTest<PopupDateField> {

    @Override
    protected Class<PopupDateField> getTestClass() {
        return PopupDateField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();

        createInputPromptSelectAction(CATEGORY_FEATURES);
    }

    private void createInputPromptSelectAction(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("<none>", null);
        options.put("Please enter date", "Please enter date");
        options.put("åäöÅÄÖ", "åäöÅÄÖ");

        createSelectAction("Input prompt", category, options, "<none>",
                new Command<PopupDateField, String>() {

                    public void execute(PopupDateField c, String value,
                            Object data) {
                        c.setInputPrompt(value);

                    }
                });
    }

}
