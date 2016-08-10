package com.vaadin.tests.components.datefield;

import com.vaadin.legacy.ui.LegacyPopupDateField;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;

/**
 * First entering an invalid date, forcing a server roundtrip and then selecting
 * a valid date from the popup in a non-immediate {@link LegacyPopupDateField} caused
 * the invalid date string to continue to show.
 */
public class ShowSelectedDateAfterInvalid extends TestBase {

    @Override
    protected void setup() {
        final Label result = new Label();

        final Form form = new Form();

        LegacyPopupDateField datefield = new LegacyPopupDateField();
        datefield.setResolution(LegacyPopupDateField.RESOLUTION_DAY);
        datefield.setDateFormat("dd/MM/yyyy");

        form.addField("datefield", datefield);

        Button button = new Button("Validate");

        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                form.setValidationVisible(true);
                if (form.isValid()) {
                    result.setValue("Valid!");
                } else {
                    result.setValue("Invalid");
                }
            }

        });

        addComponent(form);
        addComponent(button);
        addComponent(result);
    }

    @Override
    protected String getDescription() {
        return "DateField doesn't show selected value after invalid and then selecting a value";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5369;
    }

}
