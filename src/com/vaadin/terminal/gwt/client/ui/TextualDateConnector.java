/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class TextualDateConnector extends AbstractDateFieldConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        int origRes = getWidget().currentResolution;
        String oldLocale = getWidget().currentLocale;
        super.updateFromUIDL(uidl, client);
        if (origRes != getWidget().currentResolution
                || oldLocale != getWidget().currentLocale) {
            // force recreating format string
            getWidget().formatStr = null;
        }
        if (uidl.hasAttribute("format")) {
            getWidget().formatStr = uidl
                    .getStringAttribute("format");
        }

        getWidget().inputPrompt = uidl
                .getStringAttribute(VTextualDate.ATTR_INPUTPROMPT);

        getWidget().lenient = !uidl.getBooleanAttribute("strict");

        getWidget().buildDate();
        // not a FocusWidget -> needs own tabindex handling
        if (uidl.hasAttribute("tabindex")) {
            getWidget().text.setTabIndex(uidl
                    .getIntAttribute("tabindex"));
        }

        if (getWidget().readonly) {
            getWidget().text.addStyleDependentName("readonly");
        } else {
            getWidget().text.removeStyleDependentName("readonly");
        }

    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VTextualDate.class);
    }

    @Override
    public VTextualDate getWidget() {
        return (VTextualDate) super.getWidget();
    }
}
