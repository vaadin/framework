/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VUploadPaintable extends VAbstractPaintableWidget {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
        if (uidl.hasAttribute("notStarted")) {
            getWidgetForPaintable().t.schedule(400);
            return;
        }
        if (uidl.hasAttribute("forceSubmit")) {
            getWidgetForPaintable().submit();
            return;
        }
        getWidgetForPaintable().setImmediate(getState().isImmediate());
        getWidgetForPaintable().client = client;
        getWidgetForPaintable().paintableId = uidl.getId();
        getWidgetForPaintable().nextUploadId = uidl.getIntAttribute("nextid");
        final String action = client.translateVaadinUri(uidl
                .getStringVariable("action"));
        getWidgetForPaintable().element.setAction(action);
        if (uidl.hasAttribute("buttoncaption")) {
            getWidgetForPaintable().submitButton.setText(uidl
                    .getStringAttribute("buttoncaption"));
            getWidgetForPaintable().submitButton.setVisible(true);
        } else {
            getWidgetForPaintable().submitButton.setVisible(false);
        }
        getWidgetForPaintable().fu.setName(getWidgetForPaintable().paintableId
                + "_file");

        if (getState().isDisabled() || getState().isReadOnly()) {
            getWidgetForPaintable().disableUpload();
        } else if (!uidl.getBooleanAttribute("state")) {
            // Enable the button only if an upload is not in progress
            getWidgetForPaintable().enableUpload();
            getWidgetForPaintable().ensureTargetFrame();
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VUpload.class);
    }

    @Override
    public VUpload getWidgetForPaintable() {
        return (VUpload) super.getWidgetForPaintable();
    }
}
