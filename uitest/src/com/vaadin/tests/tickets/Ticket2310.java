package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class Ticket2310 extends LegacyApplication {

    @Override
    public void init() {
        final LegacyWindow main = new LegacyWindow(getClass().getName()
                .substring(getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        main.addComponent(new Label("Instructions: change label when panel is "
                + "invisible -> invalid change (with disabled "
                + "flag) is sent to client. Label is grey when panel is shown."));

        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        final Panel p = new Panel(pl);
        p.setStyleName(Reindeer.PANEL_LIGHT);
        main.addComponent(p);
        p.setHeight("100px");

        final Label l = new Label("foobar");

        pl.addComponent(l);

        Button b = new Button("change label");

        b.addListener(new Button.ClickListener() {
            int i = 0;

            @Override
            public void buttonClick(ClickEvent event) {

                l.setValue("foobar " + i++);

            }
        });

        Button b2 = new Button("toggle panel visibility");
        b2.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                p.setVisible(!p.isVisible());
            }
        });

        main.addComponent(b);
        main.addComponent(b2);

    }

}
