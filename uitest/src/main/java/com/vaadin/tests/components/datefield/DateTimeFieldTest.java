package com.vaadin.tests.components.datefield;

import java.util.LinkedHashMap;

import com.vaadin.ui.DateTimeField;

/**
 * @author Vaadin Ltd
 *
 */
public class DateTimeFieldTest
        extends AbstractDateTimeFieldTest<DateTimeField> {

    @Override
    protected Class<DateTimeField> getTestClass() {
        return DateTimeField.class;
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
                new Command<DateTimeField, String>() {

                    @Override
                    public void execute(DateTimeField c, String value,
                            Object data) {
                        c.setPlaceholder(value);

                    }
                });
    }

    private void createTextEnabledAction(String category) {
        this.createBooleanAction("Text field enabled", category, true,
                (field, value, data) -> field.setTextFieldEnabled(value));
    }
}
