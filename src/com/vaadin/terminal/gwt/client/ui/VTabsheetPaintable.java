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

public class VTabsheetPaintable extends VTabsheetBasePaintable implements
        SimpleManagedLayout {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        if (isRealUpdate(uidl)) {
            // Handle stylename changes before generics (might affect size
            // calculations)
            getWidget().handleStyleNames(uidl, getState());
        }

        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        // tabs; push or not
        if (!isUndefinedWidth()) {
            // FIXME: This makes tab sheet tabs go to 1px width on every update
            // and then back to original width
            // update width later, in updateTabScroller();
            DOM.setStyleAttribute(getWidget().tabs, "width", "1px");
            DOM.setStyleAttribute(getWidget().tabs, "overflow",
                    "hidden");
        } else {
            getWidget().showAllTabs();
            DOM.setStyleAttribute(getWidget().tabs, "width", "");
            DOM.setStyleAttribute(getWidget().tabs, "overflow",
                    "visible");
            getWidget().updateDynamicWidth();
        }

        if (!isUndefinedHeight()) {
            // Must update height after the styles have been set
            getWidget().updateContentNodeHeight();
            getWidget().updateOpenTabSize();
        }

        getWidget().iLayout();

        // Re run relative size update to ensure optimal scrollbars
        // TODO isolate to situation that visible tab has undefined height
        try {
            client.handleComponentRelativeSize(getWidget().tp
                    .getWidget(getWidget().tp.getVisibleWidget()));
        } catch (Exception e) {
            // Ignore, most likely empty tabsheet
        }

        getWidget().waitingForResponse = false;
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VTabsheet.class);
    }

    @Override
    public VTabsheet getWidget() {
        return (VTabsheet) super.getWidget();
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        /* Tabsheet does not render its children's captions */
    }

    public void layout() {
        VTabsheet tabsheet = getWidget();

        tabsheet.updateContentNodeHeight();

        if (isUndefinedWidth()) {
            tabsheet.contentNode.getStyle().setProperty("width", "");
        } else {
            int contentWidth = tabsheet.getOffsetWidth()
                    - tabsheet.getContentAreaBorderWidth();
            if (contentWidth < 0) {
                contentWidth = 0;
            }
            tabsheet.contentNode.getStyle().setProperty("width",
                    contentWidth + "px");
        }

        tabsheet.updateOpenTabSize();
        tabsheet.iLayout();

    }

}
