package com.itmill.toolkit.demo.sampler.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.ui.ILink;

public class IActiveLink extends ILink {

    String id;
    ApplicationConnection client;
    boolean listening = false;

    public IActiveLink() {
        addMouseListener(new MouseListener() {
            public void onMouseDown(Widget sender, int x, int y) {
            }

            public void onMouseEnter(Widget sender) {
            }

            public void onMouseLeave(Widget sender) {
            }

            public void onMouseMove(Widget sender, int x, int y) {
            }

            public void onMouseUp(Widget sender, int x, int y) {
                Event e = DOM.eventGetCurrentEvent();
                if (e.getButton() == Event.BUTTON_MIDDLE) {
                    sendVariables();
                }
            }
        });
    }

    /**
     * Sends variables, returns true if default handler should be called (i.e if
     * server is listening and the link was claimed to be opened by the client)
     * 
     * @return
     */
    private boolean sendVariables() {
        Event e = DOM.eventGetCurrentEvent();
        boolean opened = (e.getCtrlKey() || e.getAltKey() || e.getShiftKey()
                || e.getMetaKey() || e.getButton() == Event.BUTTON_MIDDLE);

        // Works as ILink if no-one is listening
        if (listening) {
            if (opened) {
                // ILink will open, notify server
                client.updateVariable(id, "opened", true, false);
            } else {
                e.preventDefault();
            }
            client.updateVariable(id, "activated", true, true);
        }
        return !listening || opened;
    }

    @Override
    public void onClick(Widget sender) {

        if (sendVariables()) {
            // run default if not listening, or we claimed link was opened
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
