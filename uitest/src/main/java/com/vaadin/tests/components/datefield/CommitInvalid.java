package com.vaadin.tests.components.datefield;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.legacy.data.Validator.InvalidValueException;
import com.vaadin.legacy.ui.LegacyDateField;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;

public class CommitInvalid extends TestBase {

    @Override
    protected String getDescription() {
        return "DateField with parsing error is committed regardless "
                + "of the invalidity. Parsing error should be handled"
                + " as a builtin validator, similar to isRequired()"
                + " in LegacyAbstractField.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5927;
    }

    ObjectProperty<Date> dateProperty;
    private LegacyDateField dateField;
    private ObjectProperty<Integer> integerProperty;
    private TextField integerField;
    private Log log;

    @Override
    protected void setup() {
        /*
         * Create and configure form.
         */
        final Form form = new Form();
        form.setBuffered(true); // set write buffering on
        form.setImmediate(true); // make form (and especially its fields
                                 // immediate)

        /*
         * Create and configure Date LegacyField, the actual component to be tested.
         */
        dateProperty = new ObjectProperty<Date>(new Date(2009 - 1900, 4 - 1, 1));

        dateField = new LegacyDateField("Year", dateProperty);
        dateField.setLocale(new Locale("fi", "FI"));
        dateField.setResolution(LegacyDateField.RESOLUTION_DAY);
        dateField.setId("_DF");
        form.addField("date", dateField);

        /*
         * Create a TextField with integer property (and integer validator,
         * preventing invalid typed values to be pushded to property) as a
         * reference component.
         */
        integerProperty = new ObjectProperty<Integer>(42);
        integerField = new TextField("Another LegacyField", integerProperty);
        integerField.setId("_IF");
        form.addField("text", integerField);

        /*
         * Action buttons.
         */
        Button validate = new Button("Validate");
        validate.setId("_validate");
        validate.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    form.validate();
                    log.log("Form is valid");
                } catch (InvalidValueException e) {
                    log.log("Validation failed:" + e.getLocalizedMessage());
                } finally {
                    printState();
                }
            }
        });
        form.getFooter().addComponent(validate);

        Button commit = new Button("Commit");
        commit.setId("_commit");
        commit.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    form.commit();
                    log.log("Commit succeeded");
                } catch (InvalidValueException e) {
                    log.log("Commit failed : " + e.getLocalizedMessage());
                } finally {
                    printState();
                }
            }
        });

        form.getFooter().addComponent(commit);
        Button printState = new Button("printState");
        printState.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                printState();
            }

        });
        form.getFooter().addComponent(printState);

        log = new Log(7);
        log.log("Test app started");
        printState();

        addComponent(form);
        addComponent(log);

    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String f(Date date) {
        if (date == null) {
            return "null";
        }
        return dateFormat.format(date);
    }

    private void printState() {
        log.log("Date. LegacyField: " + f(dateField.getValue()) + " Property: "
                + f(dateProperty.getValue()));
        log.log("Integer: LegacyField: " + integerField.getValue() + " Property: "
                + integerProperty.getValue());
    }
}
