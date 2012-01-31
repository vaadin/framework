package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VVideoPaintable extends VMediaBasePaintable {
    public static final String ATTR_POSTER = "poster";

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }
        super.updateFromUIDL(uidl, client);
        setPosterFromUIDL(uidl);
    }

    private void setPosterFromUIDL(UIDL uidl) {
        if (uidl.hasAttribute(ATTR_POSTER)) {
            getWidgetForPaintable().setPoster(
                    getConnection().translateVaadinUri(
                            uidl.getStringAttribute(ATTR_POSTER)));
        }
    }

    @Override
    public VVideo getWidgetForPaintable() {
        return (VVideo) super.getWidgetForPaintable();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VVideo.class);
    }

}
