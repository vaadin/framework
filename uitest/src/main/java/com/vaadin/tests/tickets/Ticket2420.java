package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.ProgressIndicator;

public class Ticket2420 extends LegacyApplication {

    @Override
    public void init() {
        final LegacyWindow main = new LegacyWindow("Hello window");
        setMainWindow(main);

        setTheme("tests-tickets");

        ProgressIndicator pi = new ProgressIndicator();
        pi.setCaption("Visible");
        pi.setIndeterminate(false);
        pi.setValue(new Float(0.5));
        main.addComponent(pi);

        pi = new ProgressIndicator();
        pi.setCaption("Visible (indeterminate)");
        pi.setIndeterminate(true);

        main.addComponent(pi);

        main.addComponent(pi);

        pi = new ProgressIndicator();
        pi.setCaption("Visible (indeterminate, with .redborder css)");
        pi.addStyleName("redborder");
        pi.setIndeterminate(true);

        main.addComponent(pi);

        pi = new ProgressIndicator();
        pi.setCaption("Disabled ");
        pi.setEnabled(false);
        pi.setIndeterminate(true);

        main.addComponent(pi);

        pi = new ProgressIndicator();

        pi.setCaption("Hidden (via css)");

        pi.addStyleName("dispnone");

        main.addComponent(pi);

    }

}
