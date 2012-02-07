/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class VNativeSelectPaintable extends VOptionGroupBasePaintable {

    @Override
    protected Widget createWidget() {
        return GWT.create(VNativeSelect.class);
    }

    @Override
    public VNativeSelect getWidgetForPaintable() {
        return (VNativeSelect) super.getWidgetForPaintable();
    }
}
