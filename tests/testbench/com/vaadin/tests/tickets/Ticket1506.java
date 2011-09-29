package com.vaadin.tests.tickets;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;

public class Ticket1506 extends CustomComponent {

    Panel p;

    public Ticket1506() {
        p = new Ticket1506_Panel();
        setCompositionRoot(p);
    }

}
