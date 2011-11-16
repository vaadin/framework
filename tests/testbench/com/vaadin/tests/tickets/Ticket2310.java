package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Root;
import com.vaadin.ui.themes.Reindeer;

public class Ticket2310 extends Application.LegacyApplication {

    @Override
    public void init() {
        final Root main = new Root(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        main.addComponent(new Label("Instructions: change label when panel is "
                + "invisible -> invalid change (with disabled "
                + "flag) is sent to client. Label is grey when panel is shown."));

        final Panel p = new Panel();
        p.setStyleName(Reindeer.PANEL_LIGHT);
        main.addComponent(p);
        p.setHeight("100px");

        final Label l = new Label("foobar");

        p.addComponent(l);

        Button b = new Button("change label");

        b.addListener(new Button.ClickListener() {
            int i = 0;

            public void buttonClick(ClickEvent event) {

                l.setValue("foobar " + i++);

            }
        });

        Button b2 = new Button("toggle panel visibility");
        b2.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                p.setVisible(!p.isVisible());
            }
        });

        main.addComponent(b);
        main.addComponent(b2);

    }

}
