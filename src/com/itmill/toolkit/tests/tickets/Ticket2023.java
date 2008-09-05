package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.ui.AbstractComponent;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2023 extends com.itmill.toolkit.Application implements
        Button.ClickListener {

    AbstractComponent c = new Button();

    public void init() {
        Window main = new Window();
        setMainWindow(main);

        String[] sizes = { "20", "100", "1", "0", "-1", "", "z" };
        String[] units = { "%", "px", "em", "ex", "in", "cm", "mm", "pt", "pc",
                "", "p", "zyx" };

        GridLayout gl = new GridLayout(units.length, sizes.length);
        main.addComponent(gl);
        for (int i = 0; i < sizes.length; i++) {
            for (int j = 0; j < units.length; j++) {
                String s = sizes[i] + units[j];
                gl.addComponent(new Button(s, this));
            }
        }

        gl.addComponent(new Button("null", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                c.setWidth(null);
                c.setHeight(null);

            }

        }));

        main.addComponent(c);

    }

    public void buttonClick(ClickEvent event) {
        c.setWidth(event.getButton().getCaption());
        c.setHeight(event.getButton().getCaption());

    }
}
