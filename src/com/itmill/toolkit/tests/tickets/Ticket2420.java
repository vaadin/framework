package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.ProgressIndicator;
import com.itmill.toolkit.ui.Window;

public class Ticket2420 extends Application {

    @Override
    public void init() {
        final Window main = new Window("Hello window");
        setMainWindow(main);

        main.setTheme("tests-tickets");

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
