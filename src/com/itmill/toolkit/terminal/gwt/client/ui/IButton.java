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
import com.itmill.toolkit.terminal.gwt.client.ErrorMessage;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IButton extends Button implements Paintable {

    public static final String CLASSNAME = "i-button";

    String id;

    ApplicationConnection client;

    private Element errorIndicatorElement;

    private final Element captionElement = DOM.createSpan();

    private ErrorMessage errorMessage;

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
            final UIDL errorUidl = uidl.getErrors();
            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createDiv();
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "i-errorindicator");
                DOM.sinkEvents(errorIndicatorElement, Event.MOUSEEVENTS);
                sinkEvents(Event.MOUSEEVENTS);
            }
            DOM.insertChild(getElement(), errorIndicatorElement, 0);
            if (errorMessage == null) {
                errorMessage = new ErrorMessage();
            }
            errorMessage.updateFromUIDL(errorUidl);

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

        // handle description
        if (uidl.hasAttribute("description")) {
            setTitle(uidl.getStringAttribute("description"));
        } else {
            setTitle(null);
        }

    }

    public void setText(String text) {
        DOM.setInnerText(captionElement, text);
    }

    public void onBrowserEvent(Event event) {
        final Element target = DOM.eventGetTarget(event);
        if (errorIndicatorElement != null
                && DOM.compare(target, errorIndicatorElement)) {
            switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEOVER:
                showErrorMessage();
                break;
            case Event.ONMOUSEOUT:
                hideErrorMessage();
                break;
            case Event.ONCLICK:
                ApplicationConnection.getConsole().log(
                        DOM.getInnerHTML(errorMessage.getElement()));
                return;
            default:
                break;
            }
        }
        super.onBrowserEvent(event);
    }

    private void hideErrorMessage() {
        errorMessage.hide();
    }

    private void showErrorMessage() {
        if (errorMessage != null) {
            errorMessage.showAt(errorIndicatorElement);
        }
    }

}
