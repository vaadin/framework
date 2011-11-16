package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

public class Ticket1737 extends Application.LegacyApplication {

    Resource slowRes = new ClassResource(Ticket1737.class, "m-bullet-blue.gif",
            this) {
        @Override
        public DownloadStream getStream() {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return super.getStream();
        }
    };

    @Override
    public void init() {

        final Root main = new Root(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        VerticalLayout el = new VerticalLayout();
        main.setContent(el);

        Panel p = new Panel("Test panel");
        p.setSizeFull();

        p.addComponent(new Label(
                "Second component is embedded with a slow resource "
                        + "and thus should break layout if Embedded cannot"
                        + " request re-layout after load."));

        Embedded em = new Embedded("TestEmbedded", slowRes);

        el.addComponent(p);
        el.addComponent(em);

    }
}