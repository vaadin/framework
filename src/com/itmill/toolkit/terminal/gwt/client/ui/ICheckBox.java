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
import com.itmill.toolkit.terminal.gwt.client.ErrorMessage;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ICheckBox extends com.google.gwt.user.client.ui.CheckBox implements
        Paintable {

    public static final String CLASSNAME = "i-checkbox";

    String id;

    boolean immediate;

    ApplicationConnection client;

    private Element errorIndicatorElement;

    private Icon icon;

    private ErrorMessage errorMessage;

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
            final UIDL errorUidl = uidl.getErrors();

            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createDiv();
                DOM.sinkEvents(errorIndicatorElement, Event.MOUSEEVENTS);
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "i-errorindicator");
                DOM.appendChild(getElement(), errorIndicatorElement);
            }
            if (errorMessage == null) {
                errorMessage = new ErrorMessage();
            }
            errorMessage.updateFromUIDL(errorUidl);

        } else if (errorIndicatorElement != null) {
            DOM.setStyleAttribute(errorIndicatorElement, "display", "none");
        }

        if (uidl.hasAttribute("description")) {
            setTitle(uidl.getStringAttribute("description"));
        }

        if (uidl.hasAttribute("icon")) {
            if (icon == null) {
                icon = new Icon(client);
                DOM.insertChild(getElement(), icon.getElement(), 1);
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

    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        final Element target = DOM.eventGetTarget(event);
        if (errorIndicatorElement != null
                && DOM.compare(target, errorIndicatorElement)) {
            switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEOVER:
                errorMessage.showAt(errorIndicatorElement);
                break;
            case Event.ONMOUSEOUT:
                errorMessage.hide();
                break;
            case Event.ONCLICK:
                ApplicationConnection.getConsole().log(
                        DOM.getInnerHTML(errorMessage.getElement()));
            default:
                break;
            }
        }

    }
}
