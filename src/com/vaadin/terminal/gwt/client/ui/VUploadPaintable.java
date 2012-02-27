/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VUploadPaintable extends AbstractComponentConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
        if (uidl.hasAttribute("notStarted")) {
            getWidget().t.schedule(400);
            return;
        }
        if (uidl.hasAttribute("forceSubmit")) {
            getWidget().submit();
            return;
        }
        getWidget().setImmediate(getState().isImmediate());
        getWidget().client = client;
        getWidget().paintableId = uidl.getId();
        getWidget().nextUploadId = uidl.getIntAttribute("nextid");
        final String action = client.translateVaadinUri(uidl
                .getStringVariable("action"));
        getWidget().element.setAction(action);
        if (uidl.hasAttribute("buttoncaption")) {
            getWidget().submitButton.setText(uidl
                    .getStringAttribute("buttoncaption"));
            getWidget().submitButton.setVisible(true);
        } else {
            getWidget().submitButton.setVisible(false);
        }
        getWidget().fu.setName(getWidget().paintableId
                + "_file");

        if (getState().isDisabled() || getState().isReadOnly()) {
            getWidget().disableUpload();
        } else if (!uidl.getBooleanAttribute("state")) {
            // Enable the button only if an upload is not in progress
            getWidget().enableUpload();
            getWidget().ensureTargetFrame();
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VUpload.class);
    }

    @Override
    public VUpload getWidget() {
        return (VUpload) super.getWidget();
    }
}
