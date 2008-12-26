package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;

public class Ticket2407 extends com.itmill.toolkit.Application {

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
