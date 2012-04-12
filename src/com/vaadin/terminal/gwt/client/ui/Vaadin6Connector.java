package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public abstract class Vaadin6Connector extends AbstractComponentConnector
        implements Paintable {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        ((Paintable) getWidget()).updateFromUIDL(uidl, client);
    }

}
