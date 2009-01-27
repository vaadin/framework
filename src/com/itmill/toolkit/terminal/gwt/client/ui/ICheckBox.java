/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ITooltip;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class ICheckBox extends com.google.gwt.user.client.ui.CheckBox implements
        Paintable, Field {

    public static final String CLASSNAME = "i-checkbox";

    String id;

    boolean immediate;

    ApplicationConnection client;

    private Element errorIndicatorElement;

    private Icon icon;

    private boolean isBlockMode = false;

    public ICheckBox() {
        setStyleName(CLASSNAME);
        addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                if (id == null || client == null) {
                    return;
                }
                client.updateVariable(id, "state", isChecked(), immediate);
            }

        });
        sinkEvents(ITooltip.TOOLTIP_EVENTS);
        Element el = DOM.getFirstChild(getElement());
        while (el != null) {
            DOM.sinkEvents(el,
                    (DOM.getEventsSunk(el) | ITooltip.TOOLTIP_EVENTS));
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
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "i-errorindicator");
                DOM.appendChild(getElement(), errorIndicatorElement);
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
                icon.sinkEvents(ITooltip.TOOLTIP_EVENTS);
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
        if ((event.getTypeInt() == Event.ONCLICK)
                && (event.getTarget() == icon.getElement())) {
            // react on icon clicks too
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
