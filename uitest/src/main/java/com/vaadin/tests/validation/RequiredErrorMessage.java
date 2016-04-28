package com.vaadin.tests.validation;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Form;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class RequiredErrorMessage extends AbstractTestCase {

    @Override
    protected Integer getTicketNumber() {
        return 2442;
    }

    @Override
    protected String getDescription() {
        return "This test verifies that the tooltip for a required field contains the requiredError message if such has been given. The tooltip for the first field should contain a message, the second field has no required error message set";
    }

    @Override
    public void init() {

        final LegacyWindow main = new LegacyWindow(getClass().getName());
        setMainWindow(main);

        final Form form = new Form(new VerticalLayout());
        final TextField requiredFieldWithError = new TextField(
                "Field with requiredError");
        requiredFieldWithError.setRequired(true);
        requiredFieldWithError
                .setRequiredError("Error message for required field");
        form.addField("field1", requiredFieldWithError);

        final TextField requiredFieldNoError = new TextField(
                "Field without requiredError");
        requiredFieldNoError.setRequired(true);
        form.addField("field2", requiredFieldNoError);

        final TextField requiredFieldDescriptionAndError = new TextField(
                "Field with requiredError and description");
        requiredFieldDescriptionAndError.setRequired(true);
        requiredFieldDescriptionAndError
                .setRequiredError("Error message for required field");
        requiredFieldDescriptionAndError
                .setDescription("Description message for the field");
        form.addField("field3", requiredFieldDescriptionAndError);

        main.addComponent(form);
    }

}
