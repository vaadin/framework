package com.vaadin.tests.components.form;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class FormPrimaryStyleName extends TestBase {

    @Override
    protected void setup() {
        final Form form = new Form();
        form.addField("aaa", new TextField());
        form.setDescription("This is a form description");
        form.setCaption("This is a form caption");
        form.setPrimaryStyleName("my-form");

        HorizontalLayout formFooter = new HorizontalLayout();
        formFooter.addComponent(new Label("Form footer"));
        form.setFooter(formFooter);

        addComponent(form);

        addComponent(new Button("Set primary stylename",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        form.setPrimaryStyleName("my-second-form");
                    }
                }));

    }

    @Override
    protected String getDescription() {
        return "Form should handle primary stylenames both initially and dynamically";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9904;
    }

}
