package com.vaadin.tests.tickets;

import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class Ticket1772 extends com.vaadin.Application {

    @Override
    public void init() {

        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        Button b = new Button("Add content");
        main.addComponent(b);

        final GridLayout gridLayout = new GridLayout(2, 2);
        main.addComponent(gridLayout);

        b.addListener(new Button.ClickListener() {
            int counter = 0;

            public void buttonClick(ClickEvent event) {

                gridLayout
                        .addComponent(new TextField("Content " + (++counter)));

            }
        });

    }

}
