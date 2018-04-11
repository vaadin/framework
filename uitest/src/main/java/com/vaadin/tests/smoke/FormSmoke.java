package com.vaadin.tests.smoke;

import java.util.Arrays;

import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.Form;
import com.vaadin.v7.ui.TextField;

public class FormSmoke extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Form form = new Form();
        form.setId("tooltipForm");
        form.setDescription("Some description");
        form.setItemDataSource(
                new BeanItem<>(
                        new Person("foo", "bar", "baz", 12, Sex.MALE, null)),
                Arrays.asList(new String[] { "firstName", "lastName", "age" }));
        ((TextField) form.getField("firstName"))
                .setDescription("Fields own tooltip");

        form.setComponentError(new UserError("Form error"));
        addComponent(form);

    }

    @Override
    protected String getTestDescription() {
        return "The 'first name' should show its own tooltip, the other fields should show no tooltip";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9173;
    }

}
