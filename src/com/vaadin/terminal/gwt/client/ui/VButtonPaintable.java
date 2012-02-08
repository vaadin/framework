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

public class VButtonPaintable extends VAbstractPaintableWidget {

    @Override
    protected boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Ensure correct implementation,
        // but don't let container manage caption etc.
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

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

        getWidgetForPaintable().disableOnClick = uidl
                .hasAttribute(VButton.ATTR_DISABLE_ON_CLICK);

        // handle error
        if (uidl.hasAttribute("error")) {
            if (getWidgetForPaintable().errorIndicatorElement == null) {
                getWidgetForPaintable().errorIndicatorElement = DOM
                        .createSpan();
                getWidgetForPaintable().errorIndicatorElement
                        .setClassName("v-errorindicator");
            }
            getWidgetForPaintable().wrapper.insertBefore(
                    getWidgetForPaintable().errorIndicatorElement,
                    getWidgetForPaintable().captionElement);

        } else if (getWidgetForPaintable().errorIndicatorElement != null) {
            getWidgetForPaintable().wrapper
                    .removeChild(getWidgetForPaintable().errorIndicatorElement);
            getWidgetForPaintable().errorIndicatorElement = null;
        }

        if (uidl.hasAttribute("icon")) {
            if (getWidgetForPaintable().icon == null) {
                getWidgetForPaintable().icon = new Icon(client);
                getWidgetForPaintable().wrapper.insertBefore(
                        getWidgetForPaintable().icon.getElement(),
                        getWidgetForPaintable().captionElement);
            }
            getWidgetForPaintable().icon
                    .setUri(uidl.getStringAttribute("icon"));
        } else {
            if (getWidgetForPaintable().icon != null) {
                getWidgetForPaintable().wrapper
                        .removeChild(getWidgetForPaintable().icon.getElement());
                getWidgetForPaintable().icon = null;
            }
        }

        if (uidl.hasAttribute("keycode")) {
            getWidgetForPaintable().clickShortcut = uidl
                    .getIntAttribute("keycode");
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VButton.class);
    }

    @Override
    public VButton getWidgetForPaintable() {
        return (VButton) super.getWidgetForPaintable();
    }
}
