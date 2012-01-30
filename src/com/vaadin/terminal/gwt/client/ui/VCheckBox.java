/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;

public class VCheckBox extends com.google.gwt.user.client.ui.CheckBox implements
        Field, FocusHandler, BlurHandler {

    public static final String VARIABLE_STATE = "state";

    public static final String CLASSNAME = "v-checkbox";

    String id;

    boolean immediate;

    ApplicationConnection client;

    Element errorIndicatorElement;

    Icon icon;

    HandlerRegistration focusHandlerRegistration;
    HandlerRegistration blurHandlerRegistration;

    public VCheckBox() {
        setStyleName(CLASSNAME);
        addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (id == null || client == null || !isEnabled()) {
                    return;
                }

                // Add mouse details
                MouseEventDetails details = new MouseEventDetails(
                        event.getNativeEvent(), getElement());
                client.updateVariable(id, "mousedetails", details.serialize(),
                        false);
                client.updateVariable(id, VARIABLE_STATE, getValue(), immediate);
            }

        });
        sinkEvents(VTooltip.TOOLTIP_EVENTS);
        Element el = DOM.getFirstChild(getElement());
        while (el != null) {
            DOM.sinkEvents(el,
                    (DOM.getEventsSunk(el) | VTooltip.TOOLTIP_EVENTS));
            el = DOM.getNextSibling(el);
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (icon != null && (event.getTypeInt() == Event.ONCLICK)
                && (DOM.eventGetTarget(event) == icon.getElement())) {
            setValue(!getValue());
        }
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);
        }
        if (client != null) {
            client.handleWidgetTooltipEvent(event, this);
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
    }

    public void onFocus(FocusEvent arg0) {
        client.updateVariable(id, EventId.FOCUS, "", true);
    }

    public void onBlur(BlurEvent arg0) {
        client.updateVariable(id, EventId.BLUR, "", true);
    }

    public Widget getWidgetForPaintable() {
        return this;
    }

}
