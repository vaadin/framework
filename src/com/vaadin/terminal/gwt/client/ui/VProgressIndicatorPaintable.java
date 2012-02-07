/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VProgressIndicatorPaintable extends VAbstractPaintableWidget {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Ensure correct implementation,
        // but don't let container manage caption etc.
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        // Save details
        getWidgetForPaintable().client = client;

        getWidgetForPaintable().indeterminate = uidl
                .getBooleanAttribute("indeterminate");

        if (getWidgetForPaintable().indeterminate) {
            String basename = VProgressIndicator.CLASSNAME + "-indeterminate";
            getWidgetForPaintable().addStyleName(basename);
            if (uidl.getBooleanAttribute("disabled")) {
                getWidgetForPaintable().addStyleName(basename + "-disabled");
            } else {
                getWidgetForPaintable().removeStyleName(basename + "-disabled");
            }
        } else {
            try {
                final float f = Float.parseFloat(uidl
                        .getStringAttribute("state"));
                final int size = Math.round(100 * f);
                DOM.setStyleAttribute(getWidgetForPaintable().indicator,
                        "width", size + "%");
            } catch (final Exception e) {
            }
        }

        if (!uidl.getBooleanAttribute("disabled")) {
            getWidgetForPaintable().interval = uidl
                    .getIntAttribute("pollinginterval");
            getWidgetForPaintable().poller
                    .scheduleRepeating(getWidgetForPaintable().interval);
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VProgressIndicator.class);
    }

    @Override
    public VProgressIndicator getWidgetForPaintable() {
        return (VProgressIndicator) super.getWidgetForPaintable();
    }
}
