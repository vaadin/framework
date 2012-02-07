/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.richtextarea;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;
import com.vaadin.terminal.gwt.client.ui.VAbstractPaintableWidget;

public class VRichTextAreaPaintable extends VAbstractPaintableWidget implements
        BeforeShortcutActionListener {

    public void updateFromUIDL(final UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().client = client;
        getWidgetForPaintable().id = uidl.getId();

        if (uidl.hasVariable("text")) {
            getWidgetForPaintable().currentValue = uidl
                    .getStringVariable("text");
            if (getWidgetForPaintable().rta.isAttached()) {
                getWidgetForPaintable().rta
                        .setHTML(getWidgetForPaintable().currentValue);
            } else {
                getWidgetForPaintable().html
                        .setHTML(getWidgetForPaintable().currentValue);
            }
        }
        if (!uidl.hasAttribute("cached")) {
            getWidgetForPaintable().setEnabled(
                    !uidl.getBooleanAttribute("disabled"));
        }

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        getWidgetForPaintable().setReadOnly(
                uidl.getBooleanAttribute("readonly"));
        getWidgetForPaintable().immediate = uidl
                .getBooleanAttribute("immediate");
        int newMaxLength = uidl.hasAttribute("maxLength") ? uidl
                .getIntAttribute("maxLength") : -1;
        if (newMaxLength >= 0) {
            if (getWidgetForPaintable().maxLength == -1) {
                getWidgetForPaintable().keyPressHandler = getWidgetForPaintable().rta
                        .addKeyPressHandler(getWidgetForPaintable());
            }
            getWidgetForPaintable().maxLength = newMaxLength;
        } else if (getWidgetForPaintable().maxLength != -1) {
            getWidgetForPaintable().getElement().setAttribute("maxlength", "");
            getWidgetForPaintable().maxLength = -1;
            getWidgetForPaintable().keyPressHandler.removeHandler();
        }

        if (uidl.hasAttribute("selectAll")) {
            getWidgetForPaintable().selectAll();
        }

    }

    public void onBeforeShortcutAction(Event e) {
        getWidgetForPaintable().synchronizeContentToServer();
    }

    @Override
    public VRichTextArea getWidgetForPaintable() {
        return (VRichTextArea) super.getWidgetForPaintable();
    };

    @Override
    protected Widget createWidget() {
        return GWT.create(VRichTextArea.class);
    }

}
