/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class ListSelectConnector extends OptionGroupBaseConnector {

    @Override
    protected Widget createWidget() {
        return GWT.create(VListSelect.class);
    }

    @Override
    public VListSelect getWidget() {
        return (VListSelect) super.getWidget();
    }
}
