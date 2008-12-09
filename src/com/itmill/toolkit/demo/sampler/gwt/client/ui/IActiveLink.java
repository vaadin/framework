package com.itmill.toolkit.demo.sampler.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.ui.ILink;

public class IActiveLink extends ILink {

    String id;
    ApplicationConnection client;
    boolean listening = false;

    @Override
    public void onClick(Widget sender) {
        boolean opened = true;
        Event e = DOM.eventGetCurrentEvent();
        if (listening && !e.getCtrlKey() && !e.getAltKey() && !e.getShiftKey()
                && !e.getMetaKey()) {
            if (opened) {
                client.updateVariable(id, "opened", true, false);
            }
            client.updateVariable(id, "activated", true, true);
            e.preventDefault();
        } else {
            super.onClick(sender);
        }
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Ensure correct implementation,
        // but don't let container manage caption etc.
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        // Save details
        this.client = client;
        id = uidl.getId();
        listening = uidl.hasVariable("activated");

        super.updateFromUIDL(uidl, client);
    }

}
