package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Root;
import com.vaadin.ui.themes.Reindeer;

public class Ticket2304 extends Application.LegacyApplication {

    @Override
    public void init() {
        final Root main = new Root(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        Panel p = new Panel();
        p.setStyleName(Reindeer.PANEL_LIGHT);
        main.addComponent(p);
        p.setHeight("100px");

        Label l = new Label(
                "a\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\n");
        l.setContentMode(Label.CONTENT_PREFORMATTED);
        p.addComponent(l);
        main.addComponent(new Label(
                "This text should be right below the panel, w/o spacing"));
    }

}
