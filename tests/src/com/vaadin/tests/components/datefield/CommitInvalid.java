package com.vaadin.tests.components.datefield;

import java.util.Date;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class CommitInvalid extends TestBase {

    @Override
    protected String getDescription() {
        return "DateField with error is committed regardless of the invalidity.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5927;
    }

    @Override
    protected void setup() {
        final Form form = new Form();
        addComponent(form);

        @SuppressWarnings("deprecation")
        ObjectProperty property = new ObjectProperty(new Date(
                2009 - 1900, 4 - 1, 1));

        final DateField df = new DateField("Year", property) {
            @Override
            protected Date handleUnparsableDateString(String dateString)
                    throws ConversionException {
                throw new Property.ConversionException(
                        "Date format not recognized");
            }
        };
        df.setResolution(DateField.RESOLUTION_DAY);
        // df.setReadThrough(false);
        // df.setWriteThrough(false);
        // df.setImmediate(true);
        form.addField("date", df);
        form.setValidationVisible(true);

        final ObjectProperty integer = new ObjectProperty("42");
        final TextField another = new TextField("Another Field", integer);
        another.addValidator(new IntegerValidator("Not an integer"));
        // another.setReadThrough(false);
        // another.setWriteThrough(false);
        form.addField("text", another);

        // form.setReadThrough(false);
        form.setWriteThrough(false);

        Button validate = new Button("Validate");
        form.getFooter().addComponent(validate);

        Button commit = new Button("Commit");
        form.getFooter().addComponent(commit);

        validate.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                form.validate();
            }
        });

        commit.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                form.commit();
                System.out.println("Field value: "
                        + ((String) another.getValue()) + ", property value: "
                        + ((String) integer.getValue()));
            }
        });

        Label value = new Label("Date Value");
        addComponent(value);
        value.setPropertyDataSource(property);

        Label value2 = new Label("Text Value");
        addComponent(value2);
        value2.setPropertyDataSource(integer);
    }
}
