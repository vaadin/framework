/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class VListSelectPaintable extends VOptionGroupBasePaintable {

    @Override
    protected Widget createWidget() {
        return GWT.create(VListSelect.class);
    }

    @Override
    public VListSelect getWidgetForPaintable() {
        return (VListSelect) super.getWidgetForPaintable();
    }
}
