/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VUIDLBrowser;

public class VUnknownComponentPaintable extends VAbstractPaintableWidget {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, false)) {
            return;
        }
        getWidgetForPaintable().setCaption(
                "Widgetset does not contain implementation for "
                        + getWidgetForPaintable().serverClassName
                        + ". Check its @ClientWidget mapping, widgetsets "
                        + "GWT module description file and re-compile your"
                        + " widgetset. In case you have downloaded a vaadin"
                        + " add-on package, you might want to refer to "
                        + "<a href='http://vaadin.com/using-addons'>add-on "
                        + "instructions</a>. Unrendered UIDL:");
        if (getWidgetForPaintable().uidlTree != null) {
            getWidgetForPaintable().uidlTree.removeFromParent();
        }

        getWidgetForPaintable().uidlTree = new VUIDLBrowser(uidl,
                client.getConfiguration());
        getWidgetForPaintable().uidlTree.open(true);
        getWidgetForPaintable().uidlTree.setText("Unrendered UIDL");
        getWidgetForPaintable().panel.add(getWidgetForPaintable().uidlTree);
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VUnknownComponent.class);
    }

    @Override
    public VUnknownComponent getWidgetForPaintable() {
        return (VUnknownComponent) super.getWidgetForPaintable();
    }

    public void setServerSideClassName(String serverClassName) {
        getWidgetForPaintable().setServerSideClassName(serverClassName);
    }
}
