/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VTabsheetPaintable extends VTabsheetBasePaintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().rendering = true;

        if (isRealUpdate(uidl)) {
            // Handle stylename changes before generics (might affect size
            // calculations)
            getWidgetForPaintable().handleStyleNames(uidl, getState());
        }

        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            getWidgetForPaintable().rendering = false;
            return;
        }

        // tabs; push or not
        if (!getWidgetForPaintable().isDynamicWidth()) {
            // FIXME: This makes tab sheet tabs go to 1px width on every update
            // and then back to original width
            // update width later, in updateTabScroller();
            DOM.setStyleAttribute(getWidgetForPaintable().tabs, "width", "1px");
            DOM.setStyleAttribute(getWidgetForPaintable().tabs, "overflow",
                    "hidden");
        } else {
            getWidgetForPaintable().showAllTabs();
            DOM.setStyleAttribute(getWidgetForPaintable().tabs, "width", "");
            DOM.setStyleAttribute(getWidgetForPaintable().tabs, "overflow",
                    "visible");
            getWidgetForPaintable().updateDynamicWidth();
        }

        if (!getWidgetForPaintable().isDynamicHeight()) {
            // Must update height after the styles have been set
            getWidgetForPaintable().updateContentNodeHeight();
            getWidgetForPaintable().updateOpenTabSize();
        }

        getWidgetForPaintable().iLayout();

        // Re run relative size update to ensure optimal scrollbars
        // TODO isolate to situation that visible tab has undefined height
        try {
            client.handleComponentRelativeSize(getWidgetForPaintable().tp
                    .getWidget(getWidgetForPaintable().tp.getVisibleWidget()));
        } catch (Exception e) {
            // Ignore, most likely empty tabsheet
        }

        getWidgetForPaintable().renderInformation
                .updateSize(getWidgetForPaintable().getElement());

        getWidgetForPaintable().waitingForResponse = false;
        getWidgetForPaintable().rendering = false;
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VTabsheet.class);
    }

    @Override
    public VTabsheet getWidgetForPaintable() {
        return (VTabsheet) super.getWidgetForPaintable();
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        /* Tabsheet does not render its children's captions */
    }

}
