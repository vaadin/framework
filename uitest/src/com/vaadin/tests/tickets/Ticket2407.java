package com.vaadin.tests.tickets;

import com.vaadin.ui.Form;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket2407 extends com.vaadin.server.LegacyApplication {

    @Override
    public void init() {
        final LegacyWindow main = new LegacyWindow("Ticket2407");
        setMainWindow(main);

        Form form = new Form(new VerticalLayout());
        TextField text = new TextField("This caption shall be visible");
        text.setRequired(true);
        form.addField("test", text);
        main.addComponent(form);
    }
}
