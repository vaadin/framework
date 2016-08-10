package com.vaadin.tests.components.datefield;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.legacy.data.validator.LegacyNullValidator;
import com.vaadin.legacy.ui.LegacyDateField;
import com.vaadin.tests.components.TestBase;

@SuppressWarnings("serial")
public class DefaultHandleUnparsableDateField extends TestBase {

    @Override
    protected void setup() {
        final LegacyDateField date = new LegacyDateField("Default DateField");
        date.setImmediate(true);
        addComponent(date);
        date.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (date.isValid()) {
                    getMainWindow()
                            .showNotification(date.getValue().toString());
                }

            }
        });

        final LegacyDateField validated = new LegacyDateField(
                "Validated Default DateField");
        validated.setImmediate(true);
        validated.addValidator(
                new LegacyNullValidator("Validator: Date is NULL", false));
        addComponent(validated);
    }

    @Override
    protected String getDescription() {
        return "By default the DateField should handle an unparsable date field without throwing an exception";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4311;
    }

}
