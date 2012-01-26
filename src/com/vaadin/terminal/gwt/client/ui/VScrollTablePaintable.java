package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

public class VScrollTablePaintable extends VAbstractPaintableWidget {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.Paintable#updateFromUIDL(com.vaadin.terminal
     * .gwt.client.UIDL, com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().rendering = true;

        if (uidl.hasAttribute(VScrollTable.ATTRIBUTE_PAGEBUFFER_FIRST)) {
            getWidgetForPaintable().serverCacheFirst = uidl
                    .getIntAttribute(VScrollTable.ATTRIBUTE_PAGEBUFFER_FIRST);
            getWidgetForPaintable().serverCacheLast = uidl
                    .getIntAttribute(VScrollTable.ATTRIBUTE_PAGEBUFFER_LAST);
        } else {
            getWidgetForPaintable().serverCacheFirst = -1;
            getWidgetForPaintable().serverCacheLast = -1;
        }
        /*
         * We need to do this before updateComponent since updateComponent calls
         * this.setHeight() which will calculate a new body height depending on
         * the space available.
         */
        if (uidl.hasAttribute("colfooters")) {
            getWidgetForPaintable().showColFooters = uidl
                    .getBooleanAttribute("colfooters");
        }

        getWidgetForPaintable().tFoot
                .setVisible(getWidgetForPaintable().showColFooters);

        if (client.updateComponent(this, uidl, true)) {
            getWidgetForPaintable().rendering = false;
            return;
        }

        getWidgetForPaintable().enabled = !uidl.hasAttribute("disabled");

        if (BrowserInfo.get().isIE8() && !getWidgetForPaintable().enabled) {
            /*
             * The disabled shim will not cover the table body if it is relative
             * in IE8. See #7324
             */
            getWidgetForPaintable().scrollBodyPanel.getElement().getStyle()
                    .setPosition(Position.STATIC);
        } else if (BrowserInfo.get().isIE8()) {
            getWidgetForPaintable().scrollBodyPanel.getElement().getStyle()
                    .setPosition(Position.RELATIVE);
        }

        getWidgetForPaintable().client = client;
        getWidgetForPaintable().paintableId = uidl.getStringAttribute("id");
        getWidgetForPaintable().immediate = uidl
                .getBooleanAttribute("immediate");

        int previousTotalRows = getWidgetForPaintable().totalRows;
        getWidgetForPaintable().updateTotalRows(uidl);
        boolean totalRowsChanged = (getWidgetForPaintable().totalRows != previousTotalRows);

        getWidgetForPaintable().updateDragMode(uidl);

        getWidgetForPaintable().updateSelectionProperties(uidl);

        if (uidl.hasAttribute("alb")) {
            getWidgetForPaintable().bodyActionKeys = uidl
                    .getStringArrayAttribute("alb");
        } else {
            // Need to clear the actions if the action handlers have been
            // removed
            getWidgetForPaintable().bodyActionKeys = null;
        }

        getWidgetForPaintable().setCacheRateFromUIDL(uidl);

        getWidgetForPaintable().recalcWidths = uidl
                .hasAttribute("recalcWidths");
        if (getWidgetForPaintable().recalcWidths) {
            getWidgetForPaintable().tHead.clear();
            getWidgetForPaintable().tFoot.clear();
        }

        getWidgetForPaintable().updatePageLength(uidl);

        getWidgetForPaintable().updateFirstVisibleAndScrollIfNeeded(uidl);

        getWidgetForPaintable().showRowHeaders = uidl
                .getBooleanAttribute("rowheaders");
        getWidgetForPaintable().showColHeaders = uidl
                .getBooleanAttribute("colheaders");

        getWidgetForPaintable().updateSortingProperties(uidl);

        boolean keyboardSelectionOverRowFetchInProgress = getWidgetForPaintable()
                .selectSelectedRows(uidl);

        getWidgetForPaintable().updateActionMap(uidl);

        getWidgetForPaintable().updateColumnProperties(uidl);

        UIDL ac = uidl.getChildByTagName("-ac");
        if (ac == null) {
            if (getWidgetForPaintable().dropHandler != null) {
                // remove dropHandler if not present anymore
                getWidgetForPaintable().dropHandler = null;
            }
        } else {
            if (getWidgetForPaintable().dropHandler == null) {
                getWidgetForPaintable().dropHandler = getWidgetForPaintable().new VScrollTableDropHandler();
            }
            getWidgetForPaintable().dropHandler.updateAcceptRules(ac);
        }

        UIDL partialRowAdditions = uidl.getChildByTagName("prows");
        UIDL partialRowUpdates = uidl.getChildByTagName("urows");
        if (partialRowUpdates != null || partialRowAdditions != null) {
            // we may have pending cache row fetch, cancel it. See #2136
            getWidgetForPaintable().rowRequestHandler.cancel();

            getWidgetForPaintable().updateRowsInBody(partialRowUpdates);
            getWidgetForPaintable().addAndRemoveRows(partialRowAdditions);
        } else {
            UIDL rowData = uidl.getChildByTagName("rows");
            if (rowData != null) {
                // we may have pending cache row fetch, cancel it. See #2136
                getWidgetForPaintable().rowRequestHandler.cancel();

                if (!getWidgetForPaintable().recalcWidths
                        && getWidgetForPaintable().initializedAndAttached) {
                    getWidgetForPaintable().updateBody(rowData,
                            uidl.getIntAttribute("firstrow"),
                            uidl.getIntAttribute("rows"));
                    if (getWidgetForPaintable().headerChangedDuringUpdate) {
                        getWidgetForPaintable().triggerLazyColumnAdjustment(
                                true);
                    } else if (!getWidgetForPaintable()
                            .isScrollPositionVisible()
                            || totalRowsChanged
                            || getWidgetForPaintable().lastRenderedHeight != getWidgetForPaintable().scrollBody
                                    .getOffsetHeight()) {
                        // webkits may still bug with their disturbing scrollbar
                        // bug, see #3457
                        // Run overflow fix for the scrollable area
                        // #6698 - If there's a scroll going on, don't abort it
                        // by changing overflows as the length of the contents
                        // *shouldn't* have changed (unless the number of rows
                        // or the height of the widget has also changed)
                        Scheduler.get().scheduleDeferred(new Command() {
                            public void execute() {
                                Util.runWebkitOverflowAutoFix(getWidgetForPaintable().scrollBodyPanel
                                        .getElement());
                            }
                        });
                    }
                } else {
                    getWidgetForPaintable().initializeRows(uidl, rowData);
                }
            }
        }

        if (!getWidgetForPaintable().isSelectable()) {
            getWidgetForPaintable().scrollBody
                    .addStyleName(VScrollTable.CLASSNAME + "-body-noselection");
        } else {
            getWidgetForPaintable().scrollBody
                    .removeStyleName(VScrollTable.CLASSNAME
                            + "-body-noselection");
        }

        getWidgetForPaintable().hideScrollPositionAnnotation();
        getWidgetForPaintable().purgeUnregistryBag();

        // selection is no in sync with server, avoid excessive server visits by
        // clearing to flag used during the normal operation
        if (!keyboardSelectionOverRowFetchInProgress) {
            getWidgetForPaintable().selectionChanged = false;
        }

        /*
         * This is called when the Home or page up button has been pressed in
         * selectable mode and the next selected row was not yet rendered in the
         * client
         */
        if (getWidgetForPaintable().selectFirstItemInNextRender
                || getWidgetForPaintable().focusFirstItemInNextRender) {
            getWidgetForPaintable().selectFirstRenderedRowInViewPort(
                    getWidgetForPaintable().focusFirstItemInNextRender);
            getWidgetForPaintable().selectFirstItemInNextRender = getWidgetForPaintable().focusFirstItemInNextRender = false;
        }

        /*
         * This is called when the page down or end button has been pressed in
         * selectable mode and the next selected row was not yet rendered in the
         * client
         */
        if (getWidgetForPaintable().selectLastItemInNextRender
                || getWidgetForPaintable().focusLastItemInNextRender) {
            getWidgetForPaintable().selectLastRenderedRowInViewPort(
                    getWidgetForPaintable().focusLastItemInNextRender);
            getWidgetForPaintable().selectLastItemInNextRender = getWidgetForPaintable().focusLastItemInNextRender = false;
        }
        getWidgetForPaintable().multiselectPending = false;

        if (getWidgetForPaintable().focusedRow != null) {
            if (!getWidgetForPaintable().focusedRow.isAttached()
                    && !getWidgetForPaintable().rowRequestHandler.isRunning()) {
                // focused row has been orphaned, can't focus
                getWidgetForPaintable().focusRowFromBody();
            }
        }

        getWidgetForPaintable().tabIndex = uidl.hasAttribute("tabindex") ? uidl
                .getIntAttribute("tabindex") : 0;
        getWidgetForPaintable().setProperTabIndex();

        getWidgetForPaintable().resizeSortedColumnForSortIndicator();

        // Remember this to detect situations where overflow hack might be
        // needed during scrolling
        getWidgetForPaintable().lastRenderedHeight = getWidgetForPaintable().scrollBody
                .getOffsetHeight();

        getWidgetForPaintable().rendering = false;
        getWidgetForPaintable().headerChangedDuringUpdate = false;

    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VScrollTable.class);
    }

    @Override
    public VScrollTable getWidgetForPaintable() {
        return (VScrollTable) super.getWidgetForPaintable();
    }
}
