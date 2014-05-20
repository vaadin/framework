/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client.ui.treetable;

import com.google.gwt.dom.client.Element;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.ui.FocusableScrollPanel;
import com.vaadin.client.ui.VScrollTable.VScrollTableBody.VScrollTableRow;
import com.vaadin.client.ui.VTreeTable;
import com.vaadin.client.ui.VTreeTable.PendingNavigationEvent;
import com.vaadin.client.ui.VTreeTable.VTreeTableScrollBody.VTreeTableRow;
import com.vaadin.client.ui.table.TableConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.treetable.TreeTableConstants;
import com.vaadin.shared.ui.treetable.TreeTableState;
import com.vaadin.ui.TreeTable;

@Connect(TreeTable.class)
public class TreeTableConnector extends TableConnector {

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
                .hasAttribute(TreeTableConstants.ATTRIBUTE_HIERARCHY_COLUMN_INDEX) ? uidl
                .getIntAttribute(TreeTableConstants.ATTRIBUTE_HIERARCHY_COLUMN_INDEX)
                : 0;
        int oldTotalRows = getWidget().getTotalRows();

        super.updateFromUIDL(uidl, client);
        // super.updateFromUIDL set rendering to false, even though we continue
        // rendering here. Set it back to true.
        getWidget().rendering = true;

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
        getWidget().rendering = false;
    }

    @Override
    public VTreeTable getWidget() {
        return (VTreeTable) super.getWidget();
    }

    @Override
    public TreeTableState getState() {
        return (TreeTableState) super.getState();
    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {

        TooltipInfo info = null;

        if (element != getWidget().getElement()) {
            Object node = Util.findWidget(element, VTreeTableRow.class);

            if (node != null) {
                VTreeTableRow row = (VTreeTableRow) node;
                info = row.getTooltip(element);
            }
        }

        if (info == null) {
            info = super.getTooltipInfo(element);
        }

        return info;
    }
}
