package com.vaadin.tests.dd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VAbstractPaintableWidget;

public class VMyDragSourcePaintable extends VAbstractPaintableWidget {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VMyDragSource.class);
    }

}
