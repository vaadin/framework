package com.vaadin.tests.tickets;

import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Ticket2407 extends com.vaadin.Application {

    @Override
    public void init() {
        final Window main = new Window("Ticket2407");
        setMainWindow(main);

        Form form = new Form(new VerticalLayout());
        TextField text = new TextField("This caption shall be visible");
        text.setRequired(true);
        form.addField("test", text);
        main.addComponent(form);
    }
}
