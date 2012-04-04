/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.ui.Component.LoadStyle;
import com.vaadin.ui.VerticalLayout;

@Component(value = VerticalLayout.class, loadStyle = LoadStyle.EAGER)
public class VerticalLayoutConnector extends AbstractOrderedLayoutConnector {

    @Override
    public VVerticalLayout getWidget() {
        return (VVerticalLayout) super.getWidget();
    }

    @Override
    protected VVerticalLayout createWidget() {
        return GWT.create(VVerticalLayout.class);
    }

}
