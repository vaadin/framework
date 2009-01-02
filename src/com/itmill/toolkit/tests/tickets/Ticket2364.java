package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;

public class Ticket2364 extends Application {

    @Override
    public void init() {

        Window main = new Window("The Main Window!!!");
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
