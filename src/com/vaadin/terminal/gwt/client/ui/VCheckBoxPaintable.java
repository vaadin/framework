/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.EventHelper;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VTooltip;

public class VCheckBoxPaintable extends VAbstractPaintableWidget {

    @Override
    protected boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Save details
        getWidgetForPaintable().client = client;
        getWidgetForPaintable().id = uidl.getId();

        // Ensure correct implementation
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

        if (uidl.hasAttribute("error")) {
            if (getWidgetForPaintable().errorIndicatorElement == null) {
                getWidgetForPaintable().errorIndicatorElement = DOM
                        .createSpan();
                getWidgetForPaintable().errorIndicatorElement
                        .setInnerHTML("&nbsp;");
                DOM.setElementProperty(
                        getWidgetForPaintable().errorIndicatorElement,
                        "className", "v-errorindicator");
                DOM.appendChild(getWidgetForPaintable().getElement(),
                        getWidgetForPaintable().errorIndicatorElement);
                DOM.sinkEvents(getWidgetForPaintable().errorIndicatorElement,
                        VTooltip.TOOLTIP_EVENTS | Event.ONCLICK);
            } else {
                DOM.setStyleAttribute(
                        getWidgetForPaintable().errorIndicatorElement,
                        "display", "");
            }
        } else if (getWidgetForPaintable().errorIndicatorElement != null) {
            DOM.setStyleAttribute(
                    getWidgetForPaintable().errorIndicatorElement, "display",
                    "none");
        }

        if (uidl.hasAttribute("readonly")) {
            getWidgetForPaintable().setEnabled(false);
        }

        if (uidl.hasAttribute("icon")) {
            if (getWidgetForPaintable().icon == null) {
                getWidgetForPaintable().icon = new Icon(client);
                DOM.insertChild(getWidgetForPaintable().getElement(),
                        getWidgetForPaintable().icon.getElement(), 1);
                getWidgetForPaintable().icon
                        .sinkEvents(VTooltip.TOOLTIP_EVENTS);
                getWidgetForPaintable().icon.sinkEvents(Event.ONCLICK);
            }
            getWidgetForPaintable().icon
                    .setUri(uidl.getStringAttribute("icon"));
        } else if (getWidgetForPaintable().icon != null) {
            // detach icon
            DOM.removeChild(getWidgetForPaintable().getElement(),
                    getWidgetForPaintable().icon.getElement());
            getWidgetForPaintable().icon = null;
        }

        // Set text
        getWidgetForPaintable().setText(uidl.getStringAttribute("caption"));
        getWidgetForPaintable()
                .setValue(
                        uidl.getBooleanVariable(getWidgetForPaintable().VARIABLE_STATE));
        getWidgetForPaintable().immediate = uidl
                .getBooleanAttribute("immediate");
    }

    @Override
    public VCheckBox getWidgetForPaintable() {
        return (VCheckBox) super.getWidgetForPaintable();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VCheckBox.class);
    }

}
