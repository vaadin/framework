/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VTextualDatePaintable extends VDateFieldPaintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        int origRes = getWidgetForPaintable().currentResolution;
        String oldLocale = getWidgetForPaintable().currentLocale;
        super.updateFromUIDL(uidl, client);
        if (origRes != getWidgetForPaintable().currentResolution
                || oldLocale != getWidgetForPaintable().currentLocale) {
            // force recreating format string
            getWidgetForPaintable().formatStr = null;
        }
        if (uidl.hasAttribute("format")) {
            getWidgetForPaintable().formatStr = uidl
                    .getStringAttribute("format");
        }

        getWidgetForPaintable().inputPrompt = uidl
                .getStringAttribute(VTextualDate.ATTR_INPUTPROMPT);

        getWidgetForPaintable().lenient = !uidl.getBooleanAttribute("strict");

        getWidgetForPaintable().buildDate();
        // not a FocusWidget -> needs own tabindex handling
        if (uidl.hasAttribute("tabindex")) {
            getWidgetForPaintable().text.setTabIndex(uidl
                    .getIntAttribute("tabindex"));
        }

        if (getWidgetForPaintable().readonly) {
            getWidgetForPaintable().text.addStyleDependentName("readonly");
        } else {
            getWidgetForPaintable().text.removeStyleDependentName("readonly");
        }

    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VTextualDate.class);
    }

    @Override
    public VTextualDate getWidgetForPaintable() {
        return (VTextualDate) super.getWidgetForPaintable();
    }
}
