/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.progressindicator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.AbstractFieldConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.ui.ProgressIndicator;

@Connect(ProgressIndicator.class)
public class ProgressIndicatorConnector extends AbstractFieldConnector
        implements Paintable {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        if (!isRealUpdate(uidl)) {
            return;
        }

        // Save details
        getWidget().client = client;

        getWidget().indeterminate = uidl.getBooleanAttribute("indeterminate");

        if (getWidget().indeterminate) {
            String basename = VProgressIndicator.CLASSNAME + "-indeterminate";
            getWidget().addStyleName(basename);
            if (!isEnabled()) {
                getWidget().addStyleName(basename + "-disabled");
            } else {
                getWidget().removeStyleName(basename + "-disabled");
            }
        } else {
            try {
                final float f = Float.parseFloat(uidl
                        .getStringAttribute("state"));
                final int size = Math.round(100 * f);
                DOM.setStyleAttribute(getWidget().indicator, "width", size
                        + "%");
            } catch (final Exception e) {
            }
        }

        if (isEnabled()) {
            getWidget().interval = uidl.getIntAttribute("pollinginterval");
            getWidget().poller.scheduleRepeating(getWidget().interval);
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VProgressIndicator.class);
    }

    @Override
    public VProgressIndicator getWidget() {
        return (VProgressIndicator) super.getWidget();
    }
}
