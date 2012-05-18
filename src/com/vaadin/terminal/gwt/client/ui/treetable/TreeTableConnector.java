/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.treetable;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.FocusableScrollPanel;
import com.vaadin.terminal.gwt.client.ui.table.TableConnector;
import com.vaadin.terminal.gwt.client.ui.table.VScrollTable.VScrollTableBody.VScrollTableRow;
import com.vaadin.terminal.gwt.client.ui.treetable.VTreeTable.PendingNavigationEvent;
import com.vaadin.ui.TreeTable;

@Connect(TreeTable.class)
public class TreeTableConnector extends TableConnector {
    public static final String ATTRIBUTE_HIERARCHY_COLUMN_INDEX = "hci";

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        FocusableScrollPanel widget = null;
        int scrollPosition = 0;
        if (getWidget().collapseRequest) {
            widget = (FocusableScrollPanel) getWidget().getWidget(1);
            scrollPosition = widget.getScrollPosition();
        }
        getWidget().animationsEnabled = uidl.getBooleanAttribute("animate");
        getWidget().colIndexOfHierarchy = uidl
                .hasAttribute(ATTRIBUTE_HIERARCHY_COLUMN_INDEX) ? uidl
                .getIntAttribute(ATTRIBUTE_HIERARCHY_COLUMN_INDEX) : 0;
        int oldTotalRows = getWidget().getTotalRows();
        super.updateFromUIDL(uidl, client);
        if (getWidget().collapseRequest) {
            if (getWidget().collapsedRowKey != null
                    && getWidget().scrollBody != null) {
                VScrollTableRow row = getWidget().getRenderedRowByKey(
                        getWidget().collapsedRowKey);
                if (row != null) {
                    getWidget().setRowFocus(row);
                    getWidget().focus();
                }
            }

            int scrollPosition2 = widget.getScrollPosition();
            if (scrollPosition != scrollPosition2) {
                widget.setScrollPosition(scrollPosition);
            }

            // check which rows are needed from the server and initiate a
            // deferred fetch
            getWidget().onScroll(null);
        }
        // Recalculate table size if collapse request, or if page length is zero
        // (not sent by server) and row count changes (#7908).
        if (getWidget().collapseRequest
                || (!uidl.hasAttribute("pagelength") && getWidget()
                        .getTotalRows() != oldTotalRows)) {
            /*
             * Ensure that possibly removed/added scrollbars are considered.
             * Triggers row calculations, removes cached rows etc. Basically
             * cleans up state. Be careful if touching this, you will break
             * pageLength=0 if you remove this.
             */
            getWidget().triggerLazyColumnAdjustment(true);

            getWidget().collapseRequest = false;
        }
        if (uidl.hasAttribute("focusedRow")) {
            String key = uidl.getStringAttribute("focusedRow");
            getWidget().setRowFocus(getWidget().getRenderedRowByKey(key));
            getWidget().focusParentResponsePending = false;
        } else if (uidl.hasAttribute("clearFocusPending")) {
            // Special case to detect a response to a focusParent request that
            // does not return any focusedRow because the selected node has no
            // parent
            getWidget().focusParentResponsePending = false;
        }

        while (!getWidget().collapseRequest
                && !getWidget().focusParentResponsePending
                && !getWidget().pendingNavigationEvents.isEmpty()) {
            // Keep replaying any queued events as long as we don't have any
            // potential content changes pending
            PendingNavigationEvent event = getWidget().pendingNavigationEvents
                    .removeFirst();
            getWidget()
                    .handleNavigation(event.keycode, event.ctrl, event.shift);
        }
    }

    @Override
    public VTreeTable getWidget() {
        return (VTreeTable) super.getWidget();
    }
}
