/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.listselect;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.VTooltip;

/**
 * Extended ListBox to listen tooltip events and forward them to generic
 * handler.
 */
public class TooltipListBox extends ListBox {
    private ApplicationConnection client;
    private Widget widget;

    public TooltipListBox(boolean isMultiselect) {
        super(isMultiselect);
        sinkEvents(VTooltip.TOOLTIP_EVENTS);
    }

    public void setClient(ApplicationConnection client) {
        this.client = client;
    }

    public void setSelect(Widget widget) {
        this.widget = widget;
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (client != null) {
            client.handleTooltipEvent(event, widget);
        }
    }

}