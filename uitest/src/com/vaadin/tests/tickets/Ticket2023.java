package com.vaadin.tests.tickets;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.LegacyWindow;

public class Ticket2023 extends com.vaadin.server.LegacyApplication implements
        Button.ClickListener {

    AbstractComponent c = new Button();

    @Override
    public void init() {
        LegacyWindow main = new LegacyWindow();
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

            @Override
            public void buttonClick(ClickEvent event) {
                c.setWidth(null);
                c.setHeight(null);

            }

        }));

        main.addComponent(c);

    }

    @Override
    public void buttonClick(ClickEvent event) {
        c.setWidth(event.getButton().getCaption());
        c.setHeight(event.getButton().getCaption());

    }
}
