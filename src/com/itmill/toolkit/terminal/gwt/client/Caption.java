/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.itmill.toolkit.terminal.gwt.client.ui.Icon;

public class Caption extends HTML {

    public static final String CLASSNAME = "i-caption";

    private final Paintable owner;

    private Element errorIndicatorElement;

    private Icon icon;

    private Element captionText;

    private ErrorMessage errorMessage;

    private final ApplicationConnection client;

    public Caption(Paintable component, ApplicationConnection client) {
        super();
        this.client = client;
        owner = component;
        setStyleName(CLASSNAME);
    }

    public void updateCaption(UIDL uidl) {
        setVisible(!uidl.getBooleanAttribute("invisible"));

        setStyleName(getElement(), "i-disabled", uidl.hasAttribute("disabled"));

        if (uidl.hasAttribute("error")) {
            final UIDL errorUidl = uidl.getErrors();

            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createDiv();
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "i-errorindicator");
                DOM.insertChild(getElement(), errorIndicatorElement, 0);
            } else {
                // Restore the indicator that was previously made invisible 
                DOM.setStyleAttribute(errorIndicatorElement, "display", "inline");
            }
            if (errorMessage == null) {
                errorMessage = new ErrorMessage();
            }
            errorMessage.updateFromUIDL(errorUidl);

        } else if (errorIndicatorElement != null) {
            // Just make the error indicator element invisible
            DOM.setStyleAttribute(errorIndicatorElement, "display", "none");
        }

        if (uidl.hasAttribute("icon")) {
            if (icon == null) {
                icon = new Icon(client);

                DOM.appendChild(getElement(), icon.getElement());
            }
            icon.setUri(uidl.getStringAttribute("icon"));
        } else {
            if (icon != null) {
                DOM.removeChild(getElement(), icon.getElement());
                icon = null;
            }

        }

        if (uidl.hasAttribute("caption")) {
            if (captionText == null) {
                captionText = DOM.createSpan();
                DOM.appendChild(getElement(), captionText);
            }
            DOM.setInnerText(captionText, uidl.getStringAttribute("caption"));
        } else {
            // TODO should span also be removed
        }

        if (uidl.hasAttribute("description")) {
            if (captionText != null) {
                DOM.setElementProperty(captionText, "title", uidl
                        .getStringAttribute("description"));
            } else {
                setTitle(uidl.getStringAttribute("description"));
            }
        }

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
            default:
                break;
            }
        }
    }

    private void hideErrorMessage() {
        if (errorMessage != null) {
            errorMessage.hide();
        }
    }

    private void showErrorMessage() {
        if (errorMessage != null) {
            errorMessage.showAt(errorIndicatorElement);
        }
    }

    public static boolean isNeeded(UIDL uidl) {
        if (uidl.getStringAttribute("caption") != null) {
            return true;
        }
        if (uidl.hasAttribute("error")) {
            return true;
        }
        if (uidl.hasAttribute("icon")) {
            return true;
        }

        // TODO Description ??

        return false;
    }

    /**
     * Returns Paintable for which this Caption belongs to.
     * 
     * @return owner Widget
     */
    public Paintable getOwner() {
        return owner;
    }
}
