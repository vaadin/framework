package com.vaadin.tests.tickets;

import com.vaadin.ui.Form;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket2407 extends com.vaadin.Application.LegacyApplication {

    @Override
    public void init() {
        final Root main = new Root("Ticket2407");
        setMainWindow(main);

        Form form = new Form(new VerticalLayout());
        TextField text = new TextField("This caption shall be visible");
        text.setRequired(true);
        form.addField("test", text);
        main.addComponent(form);
    }
}
