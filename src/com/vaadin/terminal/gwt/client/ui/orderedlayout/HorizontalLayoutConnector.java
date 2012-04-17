/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.orderedlayout;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.Connect.LoadStyle;
import com.vaadin.ui.HorizontalLayout;

@Connect(value = HorizontalLayout.class, loadStyle = LoadStyle.EAGER)
public class HorizontalLayoutConnector extends AbstractOrderedLayoutConnector {

    @Override
    public VHorizontalLayout getWidget() {
        return (VHorizontalLayout) super.getWidget();
    }

    @Override
    protected VHorizontalLayout createWidget() {
        return GWT.create(VHorizontalLayout.class);
    }

}
