/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VideoConnector extends MediaBaseConnector {
    public static final String ATTR_POSTER = "poster";

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
        super.updateFromUIDL(uidl, client);
        setPosterFromUIDL(uidl);
    }

    private void setPosterFromUIDL(UIDL uidl) {
        if (uidl.hasAttribute(ATTR_POSTER)) {
            getWidget().setPoster(
                    getConnection().translateVaadinUri(
                            uidl.getStringAttribute(ATTR_POSTER)));
        }
    }

    @Override
    public VVideo getWidget() {
        return (VVideo) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VVideo.class);
    }

}
