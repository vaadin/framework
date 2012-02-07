/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;

public class VHorizontalLayoutPaintable extends VOrderedLayoutPaintable {

    @Override
    public VHorizontalLayout getWidgetForPaintable() {
        return (VHorizontalLayout) super.getWidgetForPaintable();
    }

    @Override
    protected VHorizontalLayout createWidget() {
        return GWT.create(VHorizontalLayout.class);
    }

}
