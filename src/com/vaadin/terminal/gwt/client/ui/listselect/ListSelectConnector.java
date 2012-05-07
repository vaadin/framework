/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.listselect;

import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.optiongroup.OptionGroupBaseConnector;
import com.vaadin.ui.ListSelect;

@Connect(ListSelect.class)
public class ListSelectConnector extends OptionGroupBaseConnector {

    @Override
    public VListSelect getWidget() {
        return (VListSelect) super.getWidget();
    }
}
