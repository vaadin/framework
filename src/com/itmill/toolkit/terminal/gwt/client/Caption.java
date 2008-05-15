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

    private Element requiredFieldIndicator;

    private Icon icon;

    private Element captionText;

    private ErrorMessage errorMessage;

    private final ApplicationConnection client;

    /**
     * 
     * @param component
     *                optional owner of caption. If not set, getOwner will
     *                return null
     * @param client
     */
    public Caption(Paintable component, ApplicationConnection client) {
        super();
        this.client = client;
        owner = component;
        setStyleName(CLASSNAME);
    }

    public void updateCaption(UIDL uidl) {
        setVisible(!uidl.getBooleanAttribute("invisible"));

        setStyleName(getElement(), "i-disabled", uidl.hasAttribute("disabled"));

        boolean isEmpty = true;

        if (uidl.hasAttribute("error")) {
            isEmpty = false;
            final UIDL errorUidl = uidl.getErrors();

            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createDiv();
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "i-errorindicator");
                DOM.insertChild(getElement(), errorIndicatorElement, 0);
            }
            if (errorMessage == null) {
                errorMessage = new ErrorMessage();
            }
            errorMessage.updateFromUIDL(errorUidl);

        } else if (errorIndicatorElement != null) {
            DOM.removeChild(getElement(), errorIndicatorElement);
            errorIndicatorElement = null;
        }

        if (uidl.hasAttribute("icon")) {
            if (icon == null) {
                icon = new Icon(client);

                DOM.appendChild(getElement(), icon.getElement());
            }
            icon.setUri(uidl.getStringAttribute("icon"));
            isEmpty = false;
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
            String c = uidl.getStringAttribute("caption");
            if (c == null) {
                c = "";
            } else {
                isEmpty = false;
            }
            DOM.setInnerText(captionText, c);
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

        if (uidl.getBooleanAttribute("required")) {
            if (requiredFieldIndicator == null) {
                requiredFieldIndicator = DOM.createSpan();
                DOM.setInnerText(requiredFieldIndicator, "*");
                DOM.setElementProperty(requiredFieldIndicator, "className",
                        "i-required-field-indicator");
                DOM.appendChild(getElement(), requiredFieldIndicator);
            }
        } else {
            if (requiredFieldIndicator != null) {
                DOM.removeChild(getElement(), requiredFieldIndicator);
            }
        }

        // Workaround for IE weirdness, sometimes returns bad height in some
        // circumstances when Caption is empty. See #1444
        // IE7 bugs more often. I wonder what happens when IE8 arrives...
        if (Util.isIE()) {
            if (isEmpty) {
                setHeight("0px");
                DOM.setStyleAttribute(getElement(), "overflow", "hidden");
            } else {
                setHeight("");
                DOM.setStyleAttribute(getElement(), "overflow", "");
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
        } else {
            super.onBrowserEvent(event);
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
