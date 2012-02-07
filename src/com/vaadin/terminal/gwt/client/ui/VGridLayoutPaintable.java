/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ui.VGridLayout.Cell;
import com.vaadin.terminal.gwt.client.ui.layout.ChildComponentContainer;

public class VGridLayoutPaintable extends VAbstractPaintableWidgetContainer {
    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this, EventId.LAYOUT_CLICK) {

        @Override
        protected VPaintableWidget getChildComponent(Element element) {
            return getWidgetForPaintable().getComponent(element);
        }

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidgetForPaintable().addDomHandler(handler, type);
        }
    };

    @SuppressWarnings("unchecked")
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().rendering = true;
        getWidgetForPaintable().client = client;

        if (client.updateComponent(this, uidl, true)) {
            getWidgetForPaintable().rendering = false;
            return;
        }
        clickEventHandler.handleEventHandlerRegistration(client);

        getWidgetForPaintable().canvas.setWidth("0px");

        getWidgetForPaintable().handleMargins(uidl);
        getWidgetForPaintable().detectSpacing(uidl);

        int cols = uidl.getIntAttribute("w");
        int rows = uidl.getIntAttribute("h");

        getWidgetForPaintable().columnWidths = new int[cols];
        getWidgetForPaintable().rowHeights = new int[rows];

        if (getWidgetForPaintable().cells == null) {
            getWidgetForPaintable().cells = new Cell[cols][rows];
        } else if (getWidgetForPaintable().cells.length != cols
                || getWidgetForPaintable().cells[0].length != rows) {
            Cell[][] newCells = new Cell[cols][rows];
            for (int i = 0; i < getWidgetForPaintable().cells.length; i++) {
                for (int j = 0; j < getWidgetForPaintable().cells[i].length; j++) {
                    if (i < cols && j < rows) {
                        newCells[i][j] = getWidgetForPaintable().cells[i][j];
                    }
                }
            }
            getWidgetForPaintable().cells = newCells;
        }

        getWidgetForPaintable().nonRenderedWidgets = (HashMap<Widget, ChildComponentContainer>) getWidgetForPaintable().widgetToComponentContainer
                .clone();

        final int[] alignments = uidl.getIntArrayAttribute("alignments");
        int alignmentIndex = 0;

        LinkedList<Cell> pendingCells = new LinkedList<Cell>();

        LinkedList<Cell> relativeHeighted = new LinkedList<Cell>();

        for (final Iterator<?> i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL r = (UIDL) i.next();
            if ("gr".equals(r.getTag())) {
                for (final Iterator<?> j = r.getChildIterator(); j.hasNext();) {
                    final UIDL c = (UIDL) j.next();
                    if ("gc".equals(c.getTag())) {
                        Cell cell = getWidgetForPaintable().getCell(c);
                        if (cell.hasContent()) {
                            boolean rendered = cell.renderIfNoRelativeWidth();
                            cell.alignment = alignments[alignmentIndex++];
                            if (!rendered) {
                                pendingCells.add(cell);
                            }

                            if (cell.colspan > 1) {
                                getWidgetForPaintable().storeColSpannedCell(
                                        cell);
                            } else if (rendered) {
                                // strore non-colspanned widths to columnWidth
                                // array
                                if (getWidgetForPaintable().columnWidths[cell.col] < cell
                                        .getWidth()) {
                                    getWidgetForPaintable().columnWidths[cell.col] = cell
                                            .getWidth();
                                }
                            }
                            if (cell.hasRelativeHeight()) {
                                relativeHeighted.add(cell);
                            }
                        }
                    }
                }
            }
        }

        getWidgetForPaintable().colExpandRatioArray = uidl
                .getIntArrayAttribute("colExpand");
        getWidgetForPaintable().rowExpandRatioArray = uidl
                .getIntArrayAttribute("rowExpand");
        getWidgetForPaintable().distributeColSpanWidths();

        getWidgetForPaintable().minColumnWidths = VGridLayout
                .cloneArray(getWidgetForPaintable().columnWidths);
        getWidgetForPaintable().expandColumns();

        getWidgetForPaintable().renderRemainingComponentsWithNoRelativeHeight(
                pendingCells);

        getWidgetForPaintable().detectRowHeights();

        getWidgetForPaintable().expandRows();

        getWidgetForPaintable().renderRemainingComponents(pendingCells);

        for (Cell cell : relativeHeighted) {
            // rendering done above so cell.cc should not be null
            Widget widget2 = cell.cc.getWidget();
            client.handleComponentRelativeSize(widget2);
            cell.cc.updateWidgetSize();
        }

        getWidgetForPaintable().layoutCells();

        // clean non rendered components
        for (Widget w : getWidgetForPaintable().nonRenderedWidgets.keySet()) {
            ChildComponentContainer childComponentContainer = getWidgetForPaintable().widgetToComponentContainer
                    .get(w);
            getWidgetForPaintable().widgetToCell.remove(w);
            getWidgetForPaintable().widgetToComponentContainer.remove(w);
            childComponentContainer.removeFromParent();
            VPaintableMap paintableMap = VPaintableMap.get(client);
            paintableMap.unregisterPaintable(paintableMap.getPaintable(w));
        }
        getWidgetForPaintable().nonRenderedWidgets = null;

        getWidgetForPaintable().rendering = false;
        getWidgetForPaintable().sizeChangedDuringRendering = false;

    }

    public void updateCaption(VPaintableWidget paintable, UIDL uidl) {
        Widget widget = paintable.getWidgetForPaintable();
        ChildComponentContainer cc = getWidgetForPaintable().widgetToComponentContainer
                .get(widget);
        if (cc != null) {
            cc.updateCaption(uidl, getConnection());
        }
        if (!getWidgetForPaintable().rendering) {
            // ensure rel size details are updated
            getWidgetForPaintable().widgetToCell.get(widget)
                    .updateRelSizeStatus(uidl);
            /*
             * This was a component-only update and the possible size change
             * must be propagated to the layout
             */
            getConnection().captionSizeUpdated(widget);
        }
    }

    @Override
    public VGridLayout getWidgetForPaintable() {
        return (VGridLayout) super.getWidgetForPaintable();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VGridLayout.class);
    }

}
