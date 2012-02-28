/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VUIDLBrowser;

public class UnknownComponentConnector extends AbstractComponentConnector {

    @Override
    protected boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
        getWidget().setCaption(
                "Widgetset does not contain implementation for "
                        + getWidget().serverClassName
                        + ". Check its @ClientWidget mapping, widgetsets "
                        + "GWT module description file and re-compile your"
                        + " widgetset. In case you have downloaded a vaadin"
                        + " add-on package, you might want to refer to "
                        + "<a href='http://vaadin.com/using-addons'>add-on "
                        + "instructions</a>. Unrendered UIDL:");
        if (getWidget().uidlTree != null) {
            getWidget().uidlTree.removeFromParent();
        }

        getWidget().uidlTree = new VUIDLBrowser(uidl, client.getConfiguration());
        getWidget().uidlTree.open(true);
        getWidget().uidlTree.setText("Unrendered UIDL");
        getWidget().panel.add(getWidget().uidlTree);
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VUnknownComponent.class);
    }

    @Override
    public VUnknownComponent getWidget() {
        return (VUnknownComponent) super.getWidget();
    }

    public void setServerSideClassName(String serverClassName) {
        getWidget().setServerSideClassName(serverClassName);
    }
}
