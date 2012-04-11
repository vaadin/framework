/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.orderedlayout;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.Component.LoadStyle;
import com.vaadin.ui.HorizontalLayout;

@Component(value = HorizontalLayout.class, loadStyle = LoadStyle.EAGER)
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
