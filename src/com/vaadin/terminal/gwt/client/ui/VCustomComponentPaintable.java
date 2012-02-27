/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VCustomComponentPaintable extends
        VAbstractPaintableWidgetContainer {

    @Override
    public void updateFromUIDL(UIDL uidl, final ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
        final UIDL child = uidl.getChildUIDL(0);
        if (child != null) {
            final VPaintableWidget paintable = client.getPaintable(child);
            Widget widget = paintable.getWidget();
            if (widget != getWidget().getWidget()) {
                if (getWidget().getWidget() != null) {
                    client.unregisterPaintable(VPaintableMap.get(client)
                            .getPaintable(getWidget().getWidget()));
                    getWidget().clear();
                }
                getWidget().setWidget(widget);
            }
            paintable.updateFromUIDL(child, client);
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VCustomComponent.class);
    }

    @Override
    public VCustomComponent getWidget() {
        return (VCustomComponent) super.getWidget();
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        // NOP, custom component dont render composition roots caption
    }

}
