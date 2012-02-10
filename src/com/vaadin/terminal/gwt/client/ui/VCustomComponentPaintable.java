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
            Widget widget = paintable.getWidgetForPaintable();
            if (widget != getWidgetForPaintable().getWidget()) {
                if (getWidgetForPaintable().getWidget() != null) {
                    client.unregisterPaintable(VPaintableMap.get(client)
                            .getPaintable(getWidgetForPaintable().getWidget()));
                    getWidgetForPaintable().clear();
                }
                getWidgetForPaintable().setWidget(widget);
            }
            paintable.updateFromUIDL(child, client);
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VCustomComponent.class);
    }

    @Override
    public VCustomComponent getWidgetForPaintable() {
        return (VCustomComponent) super.getWidgetForPaintable();
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        // NOP, custom component dont render composition roots caption
    }

}
