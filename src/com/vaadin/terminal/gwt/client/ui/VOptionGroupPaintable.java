/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.UIDL;

public class VOptionGroupPaintable extends VOptionGroupBasePaintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().htmlContentAllowed = uidl
                .hasAttribute(VOptionGroup.HTML_CONTENT_ALLOWED);

        super.updateFromUIDL(uidl, client);

        getWidgetForPaintable().sendFocusEvents = client.hasEventListeners(
                this, EventId.FOCUS);
        getWidgetForPaintable().sendBlurEvents = client.hasEventListeners(this,
                EventId.BLUR);

        if (getWidgetForPaintable().focusHandlers != null) {
            for (HandlerRegistration reg : getWidgetForPaintable().focusHandlers) {
                reg.removeHandler();
            }
            getWidgetForPaintable().focusHandlers.clear();
            getWidgetForPaintable().focusHandlers = null;

            for (HandlerRegistration reg : getWidgetForPaintable().blurHandlers) {
                reg.removeHandler();
            }
            getWidgetForPaintable().blurHandlers.clear();
            getWidgetForPaintable().blurHandlers = null;
        }

        if (getWidgetForPaintable().sendFocusEvents
                || getWidgetForPaintable().sendBlurEvents) {
            getWidgetForPaintable().focusHandlers = new ArrayList<HandlerRegistration>();
            getWidgetForPaintable().blurHandlers = new ArrayList<HandlerRegistration>();

            // add focus and blur handlers to checkboxes / radio buttons
            for (Widget wid : getWidgetForPaintable().panel) {
                if (wid instanceof CheckBox) {
                    getWidgetForPaintable().focusHandlers.add(((CheckBox) wid)
                            .addFocusHandler(getWidgetForPaintable()));
                    getWidgetForPaintable().blurHandlers.add(((CheckBox) wid)
                            .addBlurHandler(getWidgetForPaintable()));
                }
            }
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VOptionGroup.class);
    }

    @Override
    public VOptionGroup getWidgetForPaintable() {
        return (VOptionGroup) super.getWidgetForPaintable();
    }
}
