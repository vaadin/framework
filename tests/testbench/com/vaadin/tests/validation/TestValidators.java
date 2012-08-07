package com.vaadin.tests.validation;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.data.validator.CompositeValidator;
import com.vaadin.data.validator.CompositeValidator.CombinationMode;
import com.vaadin.data.validator.DoubleValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class TestValidators extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 680;
    }

    @Override
    protected String getDescription() {
        return "This test verifies that various validators work correctly";
    }

    @Override
    public void setup() {
        final Form form = new Form(new VerticalLayout());

        // simple validators

        TextField tf = new TextField("A field, must contain 1-2 chars");
        tf.addValidator(new StringLengthValidator("Invalid length", 1, 2, false));
        tf.setRequired(true);
        tf.setValue("ab");
        form.addField("a", tf);

        tf = new TextField("A field, must contain an integer");
        tf.addValidator(new IntegerValidator("Invalid integer {0}"));
        tf.setRequired(true);
        tf.setValue("123");
        form.addField("b", tf);

        tf = new TextField("A field, must contain an integer or be empty");
        tf.addValidator(new IntegerValidator("Invalid integer {0}"));
        tf.setValue("-321");
        form.addField("c", tf);

        tf = new TextField(
                "A field, must contain a floating point number or be empty");
        tf.addValidator(new DoubleValidator("Invalid double {0}"));
        tf.setValue("-123.45e6");
        form.addField("d", tf);

        tf = new TextField(
                "A field, must contain an e-mail address or be empty");
        tf.addValidator(new EmailValidator("Invalid e-mail address {0}"));
        tf.setValue("a.b@example.com");
        form.addField("e", tf);

        // regular expressions

        tf = new TextField("A field, must match the regular expression a.*b.*c");
        tf.addValidator(new RegexpValidator("a.*b.*c",
                "{0} does not match the regular expression"));
        tf.setValue("aagsabeqgc");
        form.addField("f", tf);

        tf = new TextField(
                "A field, must contain the regular expression a.*b.*c");
        tf.addValidator(new RegexpValidator("a.*b.*c", false,
                "{0} does not contain the regular expression"));
        tf.setValue("aagsabeqgc");
        form.addField("g", tf);

        tf = new TextField(
                "A field, must match the regular expression ^a.*b.*c$");
        tf.addValidator(new RegexpValidator("^a.*b.*c$", false,
                "{0} does not match the regular expression with ^ and $"));
        tf.setValue("aagsabeqgc");
        form.addField("h", tf);

        tf = new TextField(
                "A field, must contain the regular expression ^a.*b.*c$");
        tf.addValidator(new RegexpValidator("^a.*b.*c$", false,
                "{0} does not contain the regular expression with ^ and $"));
        tf.setValue("aagsabeqgc");
        form.addField("i", tf);

        // TODO CompositeValidator
        tf = new TextField(
                "A field, must be a floating point number with 4-5 chars");
        CompositeValidator cv = new CompositeValidator(CombinationMode.AND,
                "The field must contain a floating point number with 4-5 characters");
        cv.addValidator(new StringLengthValidator(
                "String length of '{0}' should be 4-5 characters", 4, 5, false));
        cv.addValidator(new DoubleValidator(
                "{0} must be a floating point number"));
        tf.addValidator(cv);
        tf.setValue("12.34");
        form.addField("j", tf);

        tf = new TextField(
                "A field, must be a floating point number or 4-5 chars");
        cv = new CompositeValidator(CombinationMode.OR,
                "The field must contain a floating point  or with 4-5 characters");
        cv.addValidator(new StringLengthValidator(
                "String length of '{0}' should be 4-5 characters", 4, 5, false));
        cv.addValidator(new DoubleValidator(
                "{0} must be a floating point number"));
        tf.addValidator(cv);
        tf.setValue("12.34g");
        form.addField("jb", tf);

        // Postal code that must be 5 digits (10000-99999).
        tf = new TextField("Postal Code");
        tf.setColumns(5);

        // Create the validator - this would be even easier with RegexpValidator
        Validator postalCodeValidator = new AbstractStringValidator(
                "Postal code must be a number 10000-99999.") {
            @Override
            protected boolean isValidValue(String value) {
                return value.matches("[1-9][0-9]{4}");
            }
        };
        tf.addValidator(postalCodeValidator);
        tf.setValue("12345");
        form.addField("k", tf);

        Button b = new Button("Commit", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    form.commit();
                    if (form.isValid()) {
                        getMainWindow().showNotification(
                                "OK! Form validated and no error was thrown",
                                Notification.TYPE_HUMANIZED_MESSAGE);
                    } else {
                        getMainWindow().showNotification(
                                "Form is invalid but no exception was thrown",
                                Notification.TYPE_ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    if (form.isValid()) {
                        getMainWindow().showNotification(
                                "Form is valid but an exception was thrown",
                                Notification.TYPE_ERROR_MESSAGE);
                    } else {
                        getMainWindow().showNotification(
                                "OK! Error was thrown for an invalid input",
                                Notification.TYPE_HUMANIZED_MESSAGE);

                    }
                }
            }

        });

        addComponent(form);
        addComponent(b);
    }
}
