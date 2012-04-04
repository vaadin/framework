/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.ui.NativeSelect;

@Component(NativeSelect.class)
public class NativeSelectConnector extends OptionGroupBaseConnector {

    @Override
    protected Widget createWidget() {
        return GWT.create(VNativeSelect.class);
    }

    @Override
    public VNativeSelect getWidget() {
        return (VNativeSelect) super.getWidget();
    }
}
