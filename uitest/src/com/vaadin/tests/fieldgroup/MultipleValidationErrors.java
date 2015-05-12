package com.vaadin.tests.fieldgroup;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.apache.commons.lang.StringEscapeUtils;

public class MultipleValidationErrors extends AbstractTestUI {

    public static final String FIRST_NAME_NOT_NULL_VALIDATION_MESSAGE = "first name is null";
    public static final String LAST_NAME_NOT_NULL_VALIDATION_MESSAGE = "last name is null";
    public static final String FIRST_NAME_NOT_EMPTY_VALIDATION_MESSAGE = "first name is empty";
    public static final String LAST_NAME_NOT_EMPTY_VALIDATION_MESSAGE = "last name is empty";

    @Override
    protected void setup(VaadinRequest request) {
        BeanItem<PersonBeanWithValidationAnnotations> item = new BeanItem<PersonBeanWithValidationAnnotations>(
                new PersonBeanWithValidationAnnotations());
        final FieldGroup fieldGroup = new FieldGroup(item);

        bindTextField(item, fieldGroup, "First Name", "firstName");
        bindTextField(item, fieldGroup, "Last Name", "lastName");

        final Label validationErrors = new Label();
        validationErrors.setId("validationErrors");
        addComponent(validationErrors);

        addButton("Submit", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                validationErrors.setValue("");
                try {
                    fieldGroup.commit();
                } catch (FieldGroup.CommitException e) {
                    if (e.getCause() != null
                            && e.getCause() instanceof Validator.InvalidValueException) {
                        validationErrors.setValue(StringEscapeUtils
                                .unescapeHtml(AbstractErrorMessage
                                        .getErrorMessageForException(
                                                e.getCause())
                                        .getFormattedHtmlMessage()));
                    }
                }

            }
        });
    }

    private void bindTextField(
            BeanItem<PersonBeanWithValidationAnnotations> item,
            FieldGroup fieldGroup, String caption, String propertyId) {
        TextField textfield = new TextField(caption,
                item.getItemProperty(propertyId));
        textfield.addValidator(new BeanValidator(
                PersonBeanWithValidationAnnotations.class, propertyId));

        fieldGroup.bind(textfield, propertyId);

        addComponent(textfield);
    }

    @Override
    protected Integer getTicketNumber() {
        return 14742;
    }

    @Override
    protected String getTestDescription() {
        return "All validation errors should be included when committing a field group.";
    }
}
