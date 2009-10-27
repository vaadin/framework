package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.demo.Calc;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.ExpandLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

public class Ticket1737 extends Application {

    Resource slowRes = new ClassResource(Calc.class, "m-bullet-blue.gif", this) {
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

        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        ExpandLayout el = new ExpandLayout();
        main.setLayout(el);

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