/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.checkbox;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ui.Field;
import com.vaadin.terminal.gwt.client.ui.Icon;

public class VCheckBox extends com.google.gwt.user.client.ui.CheckBox implements
        Field {

    public static final String CLASSNAME = "v-checkbox";

    String id;

    boolean immediate;

    ApplicationConnection client;

    Element errorIndicatorElement;

    Icon icon;

    public VCheckBox() {
        setStyleName(CLASSNAME);

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
            // Click on icon should do nothing if widget is disabled
            if (isEnabled()) {
                setValue(!getValue());
            }
        }
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);
        }
        if (client != null) {
            client.handleTooltipEvent(event, this);
        }
    }

}
