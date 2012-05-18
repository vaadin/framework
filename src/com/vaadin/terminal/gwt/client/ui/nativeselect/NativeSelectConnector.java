/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.nativeselect;

import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.optiongroup.OptionGroupBaseConnector;
import com.vaadin.ui.NativeSelect;

@Connect(NativeSelect.class)
public class NativeSelectConnector extends OptionGroupBaseConnector {

    @Override
    public VNativeSelect getWidget() {
        return (VNativeSelect) super.getWidget();
    }
}
