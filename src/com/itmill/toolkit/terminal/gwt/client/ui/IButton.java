/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.Tooltip;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IButton extends Button implements Paintable {

    public static final String CLASSNAME = "i-button";

    String id;

    ApplicationConnection client;

    private Element errorIndicatorElement;

    private final Element captionElement = DOM.createSpan();

    private Icon icon;

    public IButton() {
        setStyleName(CLASSNAME);

        DOM.appendChild(getElement(), captionElement);

        addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                if (id == null || client == null) {
                    return;
                }
                /*
                 * TODO isolata workaround. Safari don't always seem to fire
                 * onblur previously focused component before button is clicked.
                 */
                IButton.this.setFocus(true);
                client.updateVariable(id, "state", true, true);
            }
        });
        sinkEvents(Tooltip.TOOLTIP_EVENTS);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Ensure correct implementation,
        // but don't let container manage caption etc.
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        // Save details
        this.client = client;
        id = uidl.getId();

        // Set text
        setText(uidl.getStringAttribute("caption"));

        // handle error
        if (uidl.hasAttribute("error")) {
            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createDiv();
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "i-errorindicator");
            }
            DOM.insertChild(getElement(), errorIndicatorElement, 0);

        } else if (errorIndicatorElement != null) {
            DOM.removeChild(getElement(), errorIndicatorElement);
            errorIndicatorElement = null;
        }

        if (uidl.hasAttribute("readonly")) {
            setEnabled(false);
        }

        if (uidl.hasAttribute("icon")) {
            if (icon == null) {
                icon = new Icon(client);
                DOM.insertChild(getElement(), icon.getElement(), 0);
            }
            icon.setUri(uidl.getStringAttribute("icon"));
        } else {
            if (icon != null) {
                DOM.removeChild(getElement(), icon.getElement());
                icon = null;
            }
        }
    }

    public void setText(String text) {
        DOM.setInnerText(captionElement, text);
    }

    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (client != null) {
            client.handleTooltipEvent(event, this);
        }
    }

}
