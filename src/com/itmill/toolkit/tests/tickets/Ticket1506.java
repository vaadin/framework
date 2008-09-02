package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Panel;

public class Ticket1506 extends CustomComponent {

    Panel p;

    public Ticket1506() {
        p = new Ticket1506_Panel();
        setCompositionRoot(p);
    }

}
