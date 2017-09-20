/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.v7.client.ui.table;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler;
import com.vaadin.client.DirectionalManagedLayout;
import com.vaadin.client.HasChildMeasurementHintConnector;
import com.vaadin.client.HasComponentsConnector;
import com.vaadin.client.Paintable;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;
import com.vaadin.v7.client.ui.AbstractFieldConnector;
import com.vaadin.v7.client.ui.VScrollTable;
import com.vaadin.v7.client.ui.VScrollTable.ContextMenuDetails;
import com.vaadin.v7.client.ui.VScrollTable.FooterCell;
import com.vaadin.v7.client.ui.VScrollTable.HeaderCell;
import com.vaadin.v7.client.ui.VScrollTable.VScrollTableBody.VScrollTableRow;
import com.vaadin.v7.shared.ui.table.TableConstants;
import com.vaadin.v7.shared.ui.table.TableConstants.Section;
import com.vaadin.v7.shared.ui.table.TableServerRpc;
import com.vaadin.v7.shared.ui.table.TableState;

@Connect(com.vaadin.v7.ui.Table.class)
public class TableConnector extends AbstractFieldConnector
        implements HasComponentsConnector, ConnectorHierarchyChangeHandler,
        Paintable, DirectionalManagedLayout, PostLayoutListener,
        HasChildMeasurementHintConnector {

    private List<ComponentConnector> childComponents;

    public TableConnector() {
        addConnectorHierarchyChangeHandler(this);
    }

    @Override
    protected void init() {
        super.init();
        getWidget().init(getConnection());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.ui.AbstractComponentConnector#onUnregister()
     */
    @Override
    public void onUnregister() {
        super.onUnregister();
        getWidget().onUnregister();
    }

    @Override
    protected void sendContextClickEvent(MouseEventDetails details,
            EventTarget eventTarget) {

        if (!Element.is(eventTarget)) {
            return;
        }
        Element e = Element.as(eventTarget);

        Section section;
        String colKey = null;
        String rowKey = null;
        if (getWidget().tFoot.getElement().isOrHasChild(e)) {
            section = Section.FOOTER;
            FooterCell w = WidgetUtil.findWidget(e, FooterCell.class);
            colKey = w.getColKey();
        } else if (getWidget().tHead.getElement().isOrHasChild(e)) {
            section = Section.HEADER;
            HeaderCell w = WidgetUtil.findWidget(e, HeaderCell.class);
            colKey = w.getColKey();
        } else {
            section = Section.BODY;
            if (getWidget().scrollBody.getElement().isOrHasChild(e)) {
                VScrollTableRow w = getScrollTableRow(e);
                /*
                 * if w is null because we've clicked on an empty area, we will
                 * let rowKey and colKey be null too, which will then lead to
                 * the server side returning a null object.
                 */
                if (w != null) {
                    rowKey = w.getKey();
                    colKey = getWidget().tHead
                            .getHeaderCell(getElementIndex(e, w.getElement()))
                            .getColKey();
                }
            }
        }

        getRpcProxy(TableServerRpc.class).contextClick(rowKey, colKey, section,
                details);

        WidgetUtil.clearTextSelection();
    }

    protected VScrollTableRow getScrollTableRow(Element e) {
        return WidgetUtil.findWidget(e, VScrollTableRow.class);
    }

    private int getElementIndex(Element e,
            com.google.gwt.user.client.Element element) {
        int i = 0;
        Element current = element.getFirstChildElement();
        while (!current.isOrHasChild(e)) {
            current = current.getNextSiblingElement();
            ++i;
        }
        return i;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.Paintable#updateFromUIDL(com.vaadin.client.UIDL,
     * com.vaadin.client.ApplicationConnection)
     */
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().rendering = true;

        // If a row has an open context menu, it will be closed as the row is
        // detached. Retain a reference here so we can restore the menu if
        // required.
        ContextMenuDetails contextMenuBeforeUpdate = getWidget().contextMenu;

        if (uidl.hasAttribute(TableConstants.ATTRIBUTE_PAGEBUFFER_FIRST)) {
            getWidget().serverCacheFirst = uidl
                    .getIntAttribute(TableConstants.ATTRIBUTE_PAGEBUFFER_FIRST);
            getWidget().serverCacheLast = uidl
                    .getIntAttribute(TableConstants.ATTRIBUTE_PAGEBUFFER_LAST);
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

        getWidget().paintableId = uidl.getStringAttribute("id");
        getWidget().immediate = getState().immediate;

        int previousTotalRows = getWidget().totalRows;
        getWidget().updateTotalRows(uidl);
        boolean totalRowsHaveChanged = (getWidget().totalRows != previousTotalRows);

        getWidget().updateDragMode(uidl);

        // Update child measure hint
        int childMeasureHint = uidl.hasAttribute("measurehint")
                ? uidl.getIntAttribute("measurehint") : 0;
        getWidget().setChildMeasurementHint(
                ChildMeasurementHint.values()[childMeasureHint]);

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
            getWidget().postponeSanityCheckForLastRendered = true;
            // we may have pending cache row fetch, cancel it. See #2136
            getWidget().rowRequestHandler.cancel();

            getWidget().updateRowsInBody(partialRowUpdates);
            getWidget().addAndRemoveRows(partialRowAdditions);

            // sanity check (in case the value has slipped beyond the total
            // amount of rows)
            getWidget().scrollBody
                    .setLastRendered(getWidget().scrollBody.getLastRendered());
            getWidget().updateMaxIndent();
        } else {
            getWidget().postponeSanityCheckForLastRendered = false;
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
                    }
                } else {
                    getWidget().initializeRows(uidl, rowData);
                }
            }
        }

        boolean keyboardSelectionOverRowFetchInProgress = getWidget()
                .selectSelectedRows(uidl);

        // If a row had an open context menu before the update, and after the
        // update there's a row with the same key as that row, restore the
        // context menu. See #8526.
        showSavedContextMenu(contextMenuBeforeUpdate);

        if (!getWidget().isSelectable()) {
            getWidget().scrollBody.addStyleName(
                    getWidget().getStylePrimaryName() + "-body-noselection");
        } else {
            getWidget().scrollBody.removeStyleName(
                    getWidget().getStylePrimaryName() + "-body-noselection");
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
                    && !getWidget().rowRequestHandler
                            .isRequestHandlerRunning()) {
                // focused row has been orphaned, can't focus
                if (getWidget().selectedRowKeys
                        .contains(getWidget().focusedRow.getKey())) {
                    // if row cache was refreshed, focused row should be
                    // in selection and exists with same index
                    getWidget().setRowFocus(getWidget().getRenderedRowByKey(
                            getWidget().focusedRow.getKey()));
                } else if (getWidget().selectedRowKeys.size() > 0) {
                    // try to focus any row in selection
                    getWidget().setRowFocus(getWidget().getRenderedRowByKey(
                            getWidget().selectedRowKeys.iterator().next()));
                } else {
                    // try to focus any row
                    getWidget().focusRowFromBody();
                }
            }
        }

        /*
         * If the server has (re)initialized the rows, our selectionRangeStart
         * row will point to an index that the server knows nothing about,
         * causing problems if doing multi selection with shift. The field will
         * be cleared a little later when the row focus has been restored.
         * (#8584)
         */
        if (uidl.hasAttribute(TableConstants.ATTRIBUTE_KEY_MAPPER_RESET)
                && uidl.getBooleanAttribute(
                        TableConstants.ATTRIBUTE_KEY_MAPPER_RESET)
                && getWidget().selectionRangeStart != null) {
            assert !getWidget().selectionRangeStart.isAttached();
            getWidget().selectionRangeStart = getWidget().focusedRow;
        }

        getWidget().tabIndex = getState().tabIndex;
        getWidget().setProperTabIndex();

        Scheduler.get().scheduleFinally(new ScheduledCommand() {

            @Override
            public void execute() {
                getWidget().resizeSortedColumnForSortIndicator();
            }
        });

        // Remember this to detect situations where overflow hack might be
        // needed during scrolling
        getWidget().lastRenderedHeight = getWidget().scrollBody
                .getOffsetHeight();

        getWidget().rendering = false;
        getWidget().headerChangedDuringUpdate = false;

        getWidget().collapsibleMenuContent = getState().collapseMenuContent;
    }

    @Override
    public void updateEnabledState(boolean enabledState) {
        super.updateEnabledState(enabledState);
        getWidget().enabled = isEnabled();

        // IE8 is no longer supported
    }

    @Override
    public VScrollTable getWidget() {
        return (VScrollTable) super.getWidget();
    }

    @Override
    public void updateCaption(ComponentConnector component) {
        // NOP, not rendered
    }

    @Override
    public void layoutVertically() {
        getWidget().updateHeight();
    }

    @Override
    public void layoutHorizontally() {
        getWidget().updateWidth();
    }

    @Override
    public void postLayout() {
        VScrollTable table = getWidget();
        if (table.sizeNeedsInit) {
            table.sizeInit();
            Scheduler.get().scheduleFinally(new ScheduledCommand() {
                @Override
                public void execute() {
                    // IE8 is no longer supported
                    getLayoutManager().setNeedsMeasure(TableConnector.this);
                    ServerConnector parent = getParent();
                    if (parent instanceof ComponentConnector) {
                        getLayoutManager()
                                .setNeedsMeasure((ComponentConnector) parent);
                    }
                    getLayoutManager()
                            .setNeedsVerticalLayout(TableConnector.this);
                    getLayoutManager().layoutNow();
                }
            });
        }
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly() || getState().propertyReadOnly;
    }

    @Override
    public TableState getState() {
        return (TableState) super.getState();
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
                    row.showContextMenu(savedContextMenu.left,
                            savedContextMenu.top);
                }
            }
        }
    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {

        TooltipInfo info = null;

        if (element != getWidget().getElement()) {
            Object node = WidgetUtil.findWidget(element, VScrollTableRow.class);

            if (node != null) {
                VScrollTableRow row = (VScrollTableRow) node;
                info = row.getTooltip(element);
            }
        }

        if (info == null) {
            info = super.getTooltipInfo(element);
        }

        return info;
    }

    @Override
    public boolean hasTooltip() {
        /*
         * Tooltips for individual rows and cells are not processed until
         * updateFromUIDL, so we can't be sure that there are no tooltips during
         * onStateChange when this method is used.
         */
        return true;
    }

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        // TODO Move code from updateFromUIDL to this method
    }

    @Override
    protected void updateComponentSize(String newWidth, String newHeight) {
        super.updateComponentSize(newWidth, newHeight);

        if ("".equals(newWidth)) {
            getWidget().updateWidth();
        }
        if ("".equals(newHeight)) {
            getWidget().updateHeight();
        }
    }

    @Override
    public List<ComponentConnector> getChildComponents() {
        if (childComponents == null) {
            return Collections.emptyList();
        }

        return childComponents;
    }

    @Override
    public void setChildComponents(List<ComponentConnector> childComponents) {
        this.childComponents = childComponents;
    }

    @Override
    public HandlerRegistration addConnectorHierarchyChangeHandler(
            ConnectorHierarchyChangeHandler handler) {
        return ensureHandlerManager()
                .addHandler(ConnectorHierarchyChangeEvent.TYPE, handler);
    }

    @Override
    public void setChildMeasurementHint(ChildMeasurementHint hint) {
        getWidget().setChildMeasurementHint(hint);
    }

    @Override
    public ChildMeasurementHint getChildMeasurementHint() {
        return getWidget().getChildMeasurementHint();
    }

}
