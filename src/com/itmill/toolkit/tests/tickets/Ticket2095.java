package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.Window;

public class Ticket2095 extends Application {

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        // uncomment to workaround iorderedlayout bug in current trunk
        // w.setLayout(new ExpandLayout());
        w.getLayout().setSizeFull();

        Embedded em = new Embedded();
        em.setType(Embedded.TYPE_BROWSER);
        em
                .setSource(new ExternalResource(
                        "../statictestfiles/ticket2095.html"));
        em.setDebugId("MYIFRAME");

        em.setSizeFull();

        w.addComponent(em);

    }
}
