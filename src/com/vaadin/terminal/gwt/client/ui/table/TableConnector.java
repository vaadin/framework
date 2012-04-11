/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.table;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.AbstractFieldState;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.DirectionalManagedLayout;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.PostLayoutListener;
import com.vaadin.terminal.gwt.client.ui.table.VScrollTable.ContextMenuDetails;
import com.vaadin.terminal.gwt.client.ui.table.VScrollTable.VScrollTableBody.VScrollTableRow;

@Component(com.vaadin.ui.Table.class)
public class TableConnector extends AbstractComponentContainerConnector
        implements Paintable, DirectionalManagedLayout, PostLayoutListener {

    @Override
    protected void init() {
        super.init();
        getWidget().init(getConnection());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.Paintable#updateFromUIDL(com.vaadin.terminal
     * .gwt.client.UIDL, com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().rendering = true;

        // If a row has an open context menu, it will be closed as the row is
        // detached. Retain a reference here so we can restore the menu if
        // required.
        ContextMenuDetails contextMenuBeforeUpdate = getWidget().contextMenu;

        if (uidl.hasAttribute(VScrollTable.ATTRIBUTE_PAGEBUFFER_FIRST)) {
            getWidget().serverCacheFirst = uidl
                    .getIntAttribute(VScrollTable.ATTRIBUTE_PAGEBUFFER_FIRST);
            getWidget().serverCacheLast = uidl
                    .getIntAttribute(VScrollTable.ATTRIBUTE_PAGEBUFFER_LAST);
        } else {
            getWidget().serverCacheFirst = -1;
            getWidget().serverCacheLast = -1;
        }
        /*
         * We need to do this before updateComponent since updateComponent calls
         * this.setHeight() which will calculate a new body height depending on
         * the space available.
         */
        if (uidl.hasAttribute("colfooters")) {
            getWidget().showColFooters = uidl.getBooleanAttribute("colfooters");
        }

        getWidget().tFoot.setVisible(getWidget().showColFooters);

        if (!isRealUpdate(uidl)) {
            getWidget().rendering = false;
            return;
        }

        getWidget().enabled = isEnabled();

        if (BrowserInfo.get().isIE8() && !getWidget().enabled) {
            /*
             * The disabled shim will not cover the table body if it is relative
             * in IE8. See #7324
             */
            getWidget().scrollBodyPanel.getElement().getStyle()
                    .setPosition(Position.STATIC);
        } else if (BrowserInfo.get().isIE8()) {
            getWidget().scrollBodyPanel.getElement().getStyle()
                    .setPosition(Position.RELATIVE);
        }

        getWidget().paintableId = uidl.getStringAttribute("id");
        getWidget().immediate = getState().isImmediate();

        int previousTotalRows = getWidget().totalRows;
        getWidget().updateTotalRows(uidl);
        boolean totalRowsChanged = (getWidget().totalRows != previousTotalRows);

        getWidget().updateDragMode(uidl);

        getWidget().updateSelectionProperties(uidl, getState(), isReadOnly());

        if (uidl.hasAttribute("alb")) {
            getWidget().bodyActionKeys = uidl.getStringArrayAttribute("alb");
        } else {
            // Need to clear the actions if the action handlers have been
            // removed
            getWidget().bodyActionKeys = null;
        }

        getWidget().setCacheRateFromUIDL(uidl);

        getWidget().recalcWidths = uidl.hasAttribute("recalcWidths");
        if (getWidget().recalcWidths) {
            getWidget().tHead.clear();
            getWidget().tFoot.clear();
        }

        getWidget().updatePageLength(uidl);

        getWidget().updateFirstVisibleAndScrollIfNeeded(uidl);

        getWidget().showRowHeaders = uidl.getBooleanAttribute("rowheaders");
        getWidget().showColHeaders = uidl.getBooleanAttribute("colheaders");

        getWidget().updateSortingProperties(uidl);

        boolean keyboardSelectionOverRowFetchInProgress = getWidget()
                .selectSelectedRows(uidl);

        getWidget().updateActionMap(uidl);

        getWidget().updateColumnProperties(uidl);

        UIDL ac = uidl.getChildByTagName("-ac");
        if (ac == null) {
            if (getWidget().dropHandler != null) {
                // remove dropHandler if not present anymore
                getWidget().dropHandler = null;
            }
        } else {
            if (getWidget().dropHandler == null) {
                getWidget().dropHandler = getWidget().new VScrollTableDropHandler();
            }
            getWidget().dropHandler.updateAcceptRules(ac);
        }

        UIDL partialRowAdditions = uidl.getChildByTagName("prows");
        UIDL partialRowUpdates = uidl.getChildByTagName("urows");
        if (partialRowUpdates != null || partialRowAdditions != null) {
            // we may have pending cache row fetch, cancel it. See #2136
            getWidget().rowRequestHandler.cancel();

            getWidget().updateRowsInBody(partialRowUpdates);
            getWidget().addAndRemoveRows(partialRowAdditions);
        } else {
            UIDL rowData = uidl.getChildByTagName("rows");
            if (rowData != null) {
                // we may have pending cache row fetch, cancel it. See #2136
                getWidget().rowRequestHandler.cancel();

                if (!getWidget().recalcWidths
                        && getWidget().initializedAndAttached) {
                    getWidget().updateBody(rowData,
                            uidl.getIntAttribute("firstrow"),
                            uidl.getIntAttribute("rows"));
                    if (getWidget().headerChangedDuringUpdate) {
                        getWidget().triggerLazyColumnAdjustment(true);
                    } else if (!getWidget().isScrollPositionVisible()
                            || totalRowsChanged
                            || getWidget().lastRenderedHeight != getWidget().scrollBody
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
                                Util.runWebkitOverflowAutoFix(getWidget().scrollBodyPanel
                                        .getElement());
                            }
                        });
                    }
                } else {
                    getWidget().initializeRows(uidl, rowData);
                }
            }
        }

        // If a row had an open context menu before the update, and after the
        // update there's a row with the same key as that row, restore the
        // context menu. See #8526.
        showSavedContextMenu(contextMenuBeforeUpdate);

        if (!getWidget().isSelectable()) {
            getWidget().scrollBody.addStyleName(VScrollTable.CLASSNAME
                    + "-body-noselection");
        } else {
            getWidget().scrollBody.removeStyleName(VScrollTable.CLASSNAME
                    + "-body-noselection");
        }

        getWidget().hideScrollPositionAnnotation();

        // selection is no in sync with server, avoid excessive server visits by
        // clearing to flag used during the normal operation
        if (!keyboardSelectionOverRowFetchInProgress) {
            getWidget().selectionChanged = false;
        }

        /*
         * This is called when the Home or page up button has been pressed in
         * selectable mode and the next selected row was not yet rendered in the
         * client
         */
        if (getWidget().selectFirstItemInNextRender
                || getWidget().focusFirstItemInNextRender) {
            getWidget().selectFirstRenderedRowInViewPort(
                    getWidget().focusFirstItemInNextRender);
            getWidget().selectFirstItemInNextRender = getWidget().focusFirstItemInNextRender = false;
        }

        /*
         * This is called when the page down or end button has been pressed in
         * selectable mode and the next selected row was not yet rendered in the
         * client
         */
        if (getWidget().selectLastItemInNextRender
                || getWidget().focusLastItemInNextRender) {
            getWidget().selectLastRenderedRowInViewPort(
                    getWidget().focusLastItemInNextRender);
            getWidget().selectLastItemInNextRender = getWidget().focusLastItemInNextRender = false;
        }
        getWidget().multiselectPending = false;

        if (getWidget().focusedRow != null) {
            if (!getWidget().focusedRow.isAttached()
                    && !getWidget().rowRequestHandler.isRunning()) {
                // focused row has been orphaned, can't focus
                getWidget().focusRowFromBody();
            }
        }

        getWidget().tabIndex = uidl.hasAttribute("tabindex") ? uidl
                .getIntAttribute("tabindex") : 0;
        getWidget().setProperTabIndex();

        getWidget().resizeSortedColumnForSortIndicator();

        // Remember this to detect situations where overflow hack might be
        // needed during scrolling
        getWidget().lastRenderedHeight = getWidget().scrollBody
                .getOffsetHeight();

        getWidget().rendering = false;
        getWidget().headerChangedDuringUpdate = false;

    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VScrollTable.class);
    }

    @Override
    public VScrollTable getWidget() {
        return (VScrollTable) super.getWidget();
    }

    public void updateCaption(ComponentConnector component) {
        // NOP, not rendered
    }

    public void layoutVertically() {
        getWidget().updateHeight();
    }

    public void layoutHorizontally() {
        getWidget().updateWidth();
    }

    public void postLayout() {
        getWidget().sizeInit();
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly() || getState().isPropertyReadOnly();
    }

    @Override
    public AbstractFieldState getState() {
        return (AbstractFieldState) super.getState();
    }

    /**
     * Shows a saved row context menu if the row for the context menu is still
     * visible. Does nothing if a context menu has not been saved.
     * 
     * @param savedContextMenu
     */
    public void showSavedContextMenu(ContextMenuDetails savedContextMenu) {
        if (isEnabled() && savedContextMenu != null) {
            Iterator<Widget> iterator = getWidget().scrollBody.iterator();
            while (iterator.hasNext()) {
                Widget w = iterator.next();
                VScrollTableRow row = (VScrollTableRow) w;
                if (row.getKey().equals(savedContextMenu.rowKey)) {
                    getWidget().contextMenu = savedContextMenu;
                    getConnection().getContextMenu().showAt(row,
                            savedContextMenu.left, savedContextMenu.top);
                }
            }
        }
    }

}
