package com.vaadin.tests.components.datefield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;

public class DateFieldTextEnabled extends TestBase {

    private static final String ENABLED = "DateField text box enabled";
    private static final String DISABLED = "DateField text box disabled";

    @Override
    public void setup() {
        final DateField field = new DateField();
        final CheckBox box = new CheckBox(ENABLED, true);
        box.addValueChangeListener(event -> {
            field.setTextFieldEnabled(event.getValue());
            if (field.isTextFieldEnabled()) {
                box.setCaption(ENABLED);
            } else {
                box.setCaption(DISABLED);
            }
        });
        addComponent(box);
        addComponent(field);
    }

    @Override
    protected String getDescription() {
        return "tests whether or not enabling text field in the component works";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6790;
    }

}
