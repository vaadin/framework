package com.vaadin.tests.components.datefield;

import java.util.LinkedHashMap;

import com.vaadin.v7.ui.LegacyPopupDateField;

public class LegacyPopupDateFieldTest
        extends LegacyDateFieldTest<LegacyPopupDateField> {

    @Override
    protected Class<LegacyPopupDateField> getTestClass() {
        return LegacyPopupDateField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();

        createInputPromptSelectAction(CATEGORY_FEATURES);
        createTextEnabledAction(CATEGORY_FEATURES);
    }

    private void createInputPromptSelectAction(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("<none>", null);
        options.put("Please enter date", "Please enter date");
        options.put("åäöÅÄÖ", "åäöÅÄÖ");

        createSelectAction("Input prompt", category, options, "<none>",
                new Command<LegacyPopupDateField, String>() {

                    @Override
                    public void execute(LegacyPopupDateField c, String value,
                            Object data) {
                        c.setInputPrompt(value);

                    }
                });
    }

    private void createTextEnabledAction(String category) {
        this.createBooleanAction("Text field enabled", category, true,
                new Command<LegacyPopupDateField, Boolean>() {

                    @Override
                    public void execute(LegacyPopupDateField c, Boolean value,
                            Object data) {
                        c.setTextFieldEnabled(value);
                    }

                });
    }
}
