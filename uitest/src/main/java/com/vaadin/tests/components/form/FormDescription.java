package com.vaadin.tests.components.form;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;

public class FormDescription extends TestBase {

    @Override
    protected void setup() {
        final Form form = new Form();
        form.setDescription("Some description");
        form.addField("AAAA", new TextField());
        addComponent(form);

        addComponent(new Button("Toggle description",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (form.getDescription() == null) {
                            form.setDescription("Form description");
                        } else {
                            form.setDescription(null);
                        }
                    }
                }));

    }

    @Override
    protected String getDescription() {
        return "Description element should be removed from DOM when not used";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3725;
    }

}
