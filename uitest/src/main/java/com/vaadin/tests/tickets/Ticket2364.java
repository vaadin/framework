package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Form;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

public class Ticket2364 extends LegacyApplication {

    @Override
    public void init() {

        LegacyWindow main = new LegacyWindow("The Main Window!!!");
        setMainWindow(main);
        Form form = new Form();
        VerticalLayout formLayout = new VerticalLayout();
        form.setLayout(formLayout);
        Select formSelect = new Select("hello");
        Select select = new Select("hello");
        form.setEnabled(false);
        select.setEnabled(false);
        formLayout.addComponent(formSelect);

        VerticalLayout l2 = new VerticalLayout();
        l2.addComponent(new Select("hello"));
        l2.setEnabled(false);

        form.setCaption("Form");
        main.addComponent(form);
        l2.setCaption("VerticalLayout");
        main.addComponent(l2);
        main.addComponent(select);
        main.addComponent(new Select("Enabled=true select"));
    }

}
