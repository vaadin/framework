/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.EventHelper;
import com.vaadin.terminal.gwt.client.UIDL;

public class VNativeButtonPaintable extends VAbstractPaintableWidget {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Ensure correct implementation,
        // but don't let container manage caption etc.
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        getWidgetForPaintable().disableOnClick = uidl
                .hasAttribute(VButton.ATTR_DISABLE_ON_CLICK);

        getWidgetForPaintable().focusHandlerRegistration = EventHelper
                .updateFocusHandler(this, client,
                        getWidgetForPaintable().focusHandlerRegistration);
        getWidgetForPaintable().blurHandlerRegistration = EventHelper
                .updateBlurHandler(this, client,
                        getWidgetForPaintable().blurHandlerRegistration);

        // Save details
        getWidgetForPaintable().client = client;
        getWidgetForPaintable().paintableId = uidl.getId();

        // Set text
        getWidgetForPaintable().setText(uidl.getStringAttribute("caption"));

        // handle error
        if (uidl.hasAttribute("error")) {
            if (getWidgetForPaintable().errorIndicatorElement == null) {
                getWidgetForPaintable().errorIndicatorElement = DOM
                        .createSpan();
                getWidgetForPaintable().errorIndicatorElement
                        .setClassName("v-errorindicator");
            }
            getWidgetForPaintable().getElement().insertBefore(
                    getWidgetForPaintable().errorIndicatorElement,
                    getWidgetForPaintable().captionElement);

        } else if (getWidgetForPaintable().errorIndicatorElement != null) {
            getWidgetForPaintable().getElement().removeChild(
                    getWidgetForPaintable().errorIndicatorElement);
            getWidgetForPaintable().errorIndicatorElement = null;
        }

        if (uidl.hasAttribute("icon")) {
            if (getWidgetForPaintable().icon == null) {
                getWidgetForPaintable().icon = new Icon(client);
                getWidgetForPaintable().getElement().insertBefore(
                        getWidgetForPaintable().icon.getElement(),
                        getWidgetForPaintable().captionElement);
            }
            getWidgetForPaintable().icon
                    .setUri(uidl.getStringAttribute("icon"));
        } else {
            if (getWidgetForPaintable().icon != null) {
                getWidgetForPaintable().getElement().removeChild(
                        getWidgetForPaintable().icon.getElement());
                getWidgetForPaintable().icon = null;
            }
        }

    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VNativeButton.class);
    }

    @Override
    public VNativeButton getWidgetForPaintable() {
        return (VNativeButton) super.getWidgetForPaintable();
    }
}