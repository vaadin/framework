package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket1772 extends com.itmill.toolkit.Application {

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
