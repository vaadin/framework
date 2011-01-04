package com.vaadin.tests.components.datefield;

import java.util.Date;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;

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
        ObjectProperty<Date> property = new ObjectProperty<Date>(new Date(
                2009 - 1900, 4 - 1, 1));

        final DateField df = new DateField("Year", property);
        df.setResolution(DateField.RESOLUTION_DAY);
        df.setReadThrough(false);
        df.setWriteThrough(false);
        df.setImmediate(true);
        form.addField("date", df);

        Button validate = new Button("Validate");
        form.getFooter().addComponent(validate);

        Button commit = new Button("Commit");
        form.getFooter().addComponent(commit);

        Label value = new Label("Value");
        addComponent(value);
        value.setPropertyDataSource(property);

        validate.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                df.validate();
            }
        });

        commit.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                df.commit();
            }
        });


    }
}
