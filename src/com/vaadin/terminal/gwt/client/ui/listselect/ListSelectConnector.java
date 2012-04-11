/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.listselect;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.optiongroup.OptionGroupBaseConnector;
import com.vaadin.ui.ListSelect;

@Component(ListSelect.class)
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
