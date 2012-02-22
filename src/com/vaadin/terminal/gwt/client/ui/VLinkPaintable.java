/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VLinkPaintable extends VAbstractPaintableWidget {

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

        getWidgetForPaintable().client = client;

        getWidgetForPaintable().enabled = uidl.hasAttribute(ATTRIBUTE_DISABLED) ? false
                : true;
        getWidgetForPaintable().readonly = getState().isReadOnly();

        if (uidl.hasAttribute("name")) {
            getWidgetForPaintable().target = uidl.getStringAttribute("name");
            getWidgetForPaintable().anchor.setAttribute("target",
                    getWidgetForPaintable().target);
        }
        if (uidl.hasAttribute("src")) {
            getWidgetForPaintable().src = client.translateVaadinUri(uidl
                    .getStringAttribute("src"));
            getWidgetForPaintable().anchor.setAttribute("href",
                    getWidgetForPaintable().src);
        }

        if (uidl.hasAttribute("border")) {
            if ("none".equals(uidl.getStringAttribute("border"))) {
                getWidgetForPaintable().borderStyle = VLink.BORDER_STYLE_NONE;
            } else {
                getWidgetForPaintable().borderStyle = VLink.BORDER_STYLE_MINIMAL;
            }
        } else {
            getWidgetForPaintable().borderStyle = VLink.BORDER_STYLE_DEFAULT;
        }

        getWidgetForPaintable().targetHeight = uidl
                .hasAttribute("targetHeight") ? uidl
                .getIntAttribute("targetHeight") : -1;
        getWidgetForPaintable().targetWidth = uidl.hasAttribute("targetWidth") ? uidl
                .getIntAttribute("targetWidth") : -1;

        // Set link caption
        getWidgetForPaintable().captionElement.setInnerText(uidl
                .getStringAttribute(ATTRIBUTE_CAPTION));

        // handle error
        if (uidl.hasAttribute("error")) {
            if (getWidgetForPaintable().errorIndicatorElement == null) {
                getWidgetForPaintable().errorIndicatorElement = DOM.createDiv();
                DOM.setElementProperty(
                        getWidgetForPaintable().errorIndicatorElement,
                        "className", "v-errorindicator");
            }
            DOM.insertChild(getWidgetForPaintable().getElement(),
                    getWidgetForPaintable().errorIndicatorElement, 0);
        } else if (getWidgetForPaintable().errorIndicatorElement != null) {
            DOM.setStyleAttribute(
                    getWidgetForPaintable().errorIndicatorElement, "display",
                    "none");
        }

        if (uidl.hasAttribute(ATTRIBUTE_ICON)) {
            if (getWidgetForPaintable().icon == null) {
                getWidgetForPaintable().icon = new Icon(client);
                getWidgetForPaintable().anchor.insertBefore(
                        getWidgetForPaintable().icon.getElement(),
                        getWidgetForPaintable().captionElement);
            }
            getWidgetForPaintable().icon.setUri(uidl
                    .getStringAttribute(ATTRIBUTE_ICON));
        }

    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VLink.class);
    }

    @Override
    public VLink getWidgetForPaintable() {
        return (VLink) super.getWidgetForPaintable();
    }
}
