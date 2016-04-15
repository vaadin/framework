package com.vaadin.tests.components.form;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

public class FormCommitWithInvalidValues extends TestBase {

    private Form form;

    @Override
    protected String getDescription() {
        return "Commiting a form with invalid values should throw an exception";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2466;
    }

    @Override
    protected void setup() {
        form = new Form();
        form.setFooter(null);
        TextField tf = new TextField("A field, must contain 1-2 chars");
        tf.addValidator(new StringLengthValidator("Invalid length", 1, 2, false));
        tf.setRequired(true);

        form.addField("a", tf);

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
