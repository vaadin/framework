/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;

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
