/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.UIDL;

public class VAudioPaintable extends VMediaBasePaintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
        Style style = getWidgetForPaintable().getElement().getStyle();

        // Make sure that the controls are not clipped if visible.
        if (shouldShowControls(uidl)
                && (style.getHeight() == null || "".equals(style.getHeight()))) {
            if (BrowserInfo.get().isChrome()) {
                style.setHeight(32, Unit.PX);
            } else {
                style.setHeight(25, Unit.PX);
            }
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VAudio.class);
    }

}
