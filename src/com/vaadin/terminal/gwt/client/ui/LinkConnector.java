/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class LinkConnector extends AbstractComponentConnector {

    @Override
    protected boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Ensure correct implementation,
        // but don't let container manage caption etc.
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().client = client;

        getWidget().enabled = isEnabled();

        if (uidl.hasAttribute("name")) {
            getWidget().target = uidl.getStringAttribute("name");
            getWidget().anchor.setAttribute("target", getWidget().target);
        }
        if (uidl.hasAttribute("src")) {
            getWidget().src = client.translateVaadinUri(uidl
                    .getStringAttribute("src"));
            getWidget().anchor.setAttribute("href", getWidget().src);
        }

        if (uidl.hasAttribute("border")) {
            if ("none".equals(uidl.getStringAttribute("border"))) {
                getWidget().borderStyle = VLink.BORDER_STYLE_NONE;
            } else {
                getWidget().borderStyle = VLink.BORDER_STYLE_MINIMAL;
            }
        } else {
            getWidget().borderStyle = VLink.BORDER_STYLE_DEFAULT;
        }

        getWidget().targetHeight = uidl.hasAttribute("targetHeight") ? uidl
                .getIntAttribute("targetHeight") : -1;
        getWidget().targetWidth = uidl.hasAttribute("targetWidth") ? uidl
                .getIntAttribute("targetWidth") : -1;

        // Set link caption
        getWidget().captionElement.setInnerText(getState().getCaption());

        // handle error
        if (uidl.hasAttribute("error")) {
            if (getWidget().errorIndicatorElement == null) {
                getWidget().errorIndicatorElement = DOM.createDiv();
                DOM.setElementProperty(getWidget().errorIndicatorElement,
                        "className", "v-errorindicator");
            }
            DOM.insertChild(getWidget().getElement(),
                    getWidget().errorIndicatorElement, 0);
        } else if (getWidget().errorIndicatorElement != null) {
            DOM.setStyleAttribute(getWidget().errorIndicatorElement, "display",
                    "none");
        }

        if (getState().getIcon() != null) {
            if (getWidget().icon == null) {
                getWidget().icon = new Icon(client);
                getWidget().anchor.insertBefore(getWidget().icon.getElement(),
                        getWidget().captionElement);
            }
            getWidget().icon.setUri(getState().getIcon().getURL());
        }

    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VLink.class);
    }

    @Override
    public VLink getWidget() {
        return (VLink) super.getWidget();
    }
}
