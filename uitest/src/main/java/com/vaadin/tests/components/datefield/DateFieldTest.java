package com.vaadin.tests.components.datefield;

import java.util.LinkedHashMap;

import com.vaadin.ui.DateField;

public class DateFieldTest extends AbstractDateFieldTest<DateField> {

    @Override
    protected Class<DateField> getTestClass() {
        return DateField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();

        createInputPromptSelectAction(CATEGORY_FEATURES);
        createTextEnabledAction(CATEGORY_FEATURES);
    }

    private void createInputPromptSelectAction(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("<none>", null);
        options.put("Please enter date", "Please enter date");
        options.put("åäöÅÄÖ", "åäöÅÄÖ");

        createSelectAction("Input prompt", category, options, "<none>",
                new Command<DateField, String>() {

                    @Override
                    public void execute(DateField c, String value,
                            Object data) {
                        c.setInputPrompt(value);

                    }
                });
    }

    private void createTextEnabledAction(String category) {
        this.createBooleanAction("Text field enabled", category, true,
                (field, value, data) -> field.setTextFieldEnabled(value));
    }
}
