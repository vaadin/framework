package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.demo.Calc;
import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;

public class Ticket1737 extends Application {

    Resource slowRes = new ClassResource(Calc.class, "m-bullet-blue.gif", this) {
        public DownloadStream getStream() {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return super.getStream();
        }
    };

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