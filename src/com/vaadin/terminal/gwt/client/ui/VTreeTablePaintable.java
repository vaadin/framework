/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VScrollTable.VScrollTableBody.VScrollTableRow;
import com.vaadin.terminal.gwt.client.ui.VTreeTable.PendingNavigationEvent;

public class VTreeTablePaintable extends VScrollTablePaintable {
    public static final String ATTRIBUTE_HIERARCHY_COLUMN_INDEX = "hci";

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        FocusableScrollPanel widget = null;
        int scrollPosition = 0;
        if (getWidgetForPaintable().collapseRequest) {
            widget = (FocusableScrollPanel) getWidgetForPaintable()
                    .getWidget(1);
            scrollPosition = widget.getScrollPosition();
        }
        getWidgetForPaintable().animationsEnabled = uidl
                .getBooleanAttribute("animate");
        getWidgetForPaintable().colIndexOfHierarchy = uidl
                .hasAttribute(ATTRIBUTE_HIERARCHY_COLUMN_INDEX) ? uidl
                .getIntAttribute(ATTRIBUTE_HIERARCHY_COLUMN_INDEX) : 0;
        int oldTotalRows = getWidgetForPaintable().getTotalRows();
        super.updateFromUIDL(uidl, client);
        if (getWidgetForPaintable().collapseRequest) {
            if (getWidgetForPaintable().collapsedRowKey != null
                    && getWidgetForPaintable().scrollBody != null) {
                VScrollTableRow row = getWidgetForPaintable()
                        .getRenderedRowByKey(
                                getWidgetForPaintable().collapsedRowKey);
                if (row != null) {
                    getWidgetForPaintable().setRowFocus(row);
                    getWidgetForPaintable().focus();
                }
            }

            int scrollPosition2 = widget.getScrollPosition();
            if (scrollPosition != scrollPosition2) {
                widget.setScrollPosition(scrollPosition);
            }

            // check which rows are needed from the server and initiate a
            // deferred fetch
            getWidgetForPaintable().onScroll(null);
        }
        // Recalculate table size if collapse request, or if page length is zero
        // (not sent by server) and row count changes (#7908).
        if (getWidgetForPaintable().collapseRequest
                || (!uidl.hasAttribute("pagelength") && getWidgetForPaintable()
                        .getTotalRows() != oldTotalRows)) {
            /*
             * Ensure that possibly removed/added scrollbars are considered.
             * Triggers row calculations, removes cached rows etc. Basically
             * cleans up state. Be careful if touching this, you will break
             * pageLength=0 if you remove this.
             */
            getWidgetForPaintable().triggerLazyColumnAdjustment(true);

            getWidgetForPaintable().collapseRequest = false;
        }
        if (uidl.hasAttribute("focusedRow")) {
            String key = uidl.getStringAttribute("focusedRow");
            getWidgetForPaintable().setRowFocus(
                    getWidgetForPaintable().getRenderedRowByKey(key));
            getWidgetForPaintable().focusParentResponsePending = false;
        } else if (uidl.hasAttribute("clearFocusPending")) {
            // Special case to detect a response to a focusParent request that
            // does not return any focusedRow because the selected node has no
            // parent
            getWidgetForPaintable().focusParentResponsePending = false;
        }

        while (!getWidgetForPaintable().collapseRequest
                && !getWidgetForPaintable().focusParentResponsePending
                && !getWidgetForPaintable().pendingNavigationEvents.isEmpty()) {
            // Keep replaying any queued events as long as we don't have any
            // potential content changes pending
            PendingNavigationEvent event = getWidgetForPaintable().pendingNavigationEvents
                    .removeFirst();
            getWidgetForPaintable().handleNavigation(event.keycode, event.ctrl,
                    event.shift);
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VTreeTable.class);
    }

    @Override
    public VTreeTable getWidgetForPaintable() {
        return (VTreeTable) super.getWidgetForPaintable();
    }
}
