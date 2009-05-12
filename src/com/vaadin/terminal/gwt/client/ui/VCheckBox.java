/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;

public class VCheckBox extends com.google.gwt.user.client.ui.CheckBox implements
        Paintable, Field {

    public static final String CLASSNAME = "v-checkbox";

    String id;

    boolean immediate;

    ApplicationConnection client;

    private Element errorIndicatorElement;

    private Icon icon;

    private boolean isBlockMode = false;

    public VCheckBox() {
        setStyleName(CLASSNAME);
        addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                if (id == null || client == null) {
                    return;
                }
                client.updateVariable(id, "state", isChecked(), immediate);
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

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Save details
        this.client = client;
        id = uidl.getId();

        // Ensure correct implementation
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        if (uidl.hasAttribute("error")) {
            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createDiv();
                errorIndicatorElement.setInnerHTML("&nbsp;");
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "v-errorindicator");
                DOM.appendChild(getElement(), errorIndicatorElement);
                DOM.sinkEvents(errorIndicatorElement, VTooltip.TOOLTIP_EVENTS
                        | Event.ONCLICK);
            }
        } else if (errorIndicatorElement != null) {
            DOM.setStyleAttribute(errorIndicatorElement, "display", "none");
        }

        if (uidl.hasAttribute("readonly")) {
            setEnabled(false);
        }

        if (uidl.hasAttribute("icon")) {
            if (icon == null) {
                icon = new Icon(client);
                DOM.insertChild(getElement(), icon.getElement(), 1);
                icon.sinkEvents(VTooltip.TOOLTIP_EVENTS);
                icon.sinkEvents(Event.ONCLICK);
            }
            icon.setUri(uidl.getStringAttribute("icon"));
        } else if (icon != null) {
            // detach icon
            DOM.removeChild(getElement(), icon.getElement());
            icon = null;
        }

        // Set text
        setText(uidl.getStringAttribute("caption"));
        setChecked(uidl.getBooleanVariable("state"));
        immediate = uidl.getBooleanAttribute("immediate");
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (icon != null && (event.getTypeInt() == Event.ONCLICK)
                && (event.getTarget() == icon.getElement())) {
            setChecked(!isChecked());
        }
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);
        }
        if (client != null) {
            client.handleTooltipEvent(event, this);
        }
    }

    @Override
    public void setWidth(String width) {
        setBlockMode();
        super.setWidth(width);
    }

    @Override
    public void setHeight(String height) {
        setBlockMode();
        super.setHeight(height);
    }

    /**
     * makes container element (span) to be block element to enable sizing.
     */
    private void setBlockMode() {
        if (!isBlockMode) {
            DOM.setStyleAttribute(getElement(), "display", "block");
            isBlockMode = true;
        }
    }
}
