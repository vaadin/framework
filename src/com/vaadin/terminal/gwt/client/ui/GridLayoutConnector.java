/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.DirectionalManagedLayout;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.ui.VGridLayout.Cell;
import com.vaadin.terminal.gwt.client.ui.layout.VLayoutSlot;

public class GridLayoutConnector extends AbstractComponentContainerConnector
        implements DirectionalManagedLayout {
    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this, EventId.LAYOUT_CLICK) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return getWidget().getComponent(element);
        }

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidget().addDomHandler(handler, type);
        }
    };

    @Override
    public void init() {
        getLayoutManager().registerDependency(this,
                getWidget().spacingMeasureElement);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        VGridLayout layout = getWidget();
        layout.client = client;

        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
        clickEventHandler.handleEventHandlerRegistration(client);

        int cols = uidl.getIntAttribute("w");
        int rows = uidl.getIntAttribute("h");

        layout.columnWidths = new int[cols];
        layout.rowHeights = new int[rows];

        if (layout.cells == null) {
            layout.cells = new Cell[cols][rows];
        } else if (layout.cells.length != cols
                || layout.cells[0].length != rows) {
            Cell[][] newCells = new Cell[cols][rows];
            for (int i = 0; i < layout.cells.length; i++) {
                for (int j = 0; j < layout.cells[i].length; j++) {
                    if (i < cols && j < rows) {
                        newCells[i][j] = layout.cells[i][j];
                    }
                }
            }
            layout.cells = newCells;
        }

        final int[] alignments = uidl.getIntArrayAttribute("alignments");
        int alignmentIndex = 0;

        HashSet<Widget> nonRenderedWidgets = new HashSet<Widget>(
                layout.widgetToCell.keySet());

        for (final Iterator<?> i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL r = (UIDL) i.next();
            if ("gr".equals(r.getTag())) {
                for (final Iterator<?> j = r.getChildIterator(); j.hasNext();) {
                    final UIDL c = (UIDL) j.next();
                    if ("gc".equals(c.getTag())) {
                        Cell cell = layout.getCell(c);
                        if (cell.hasContent()) {
                            cell.setAlignment(new AlignmentInfo(
                                    alignments[alignmentIndex++]));
                            nonRenderedWidgets.remove(cell.slot.getWidget());
                        }
                    }
                }
            }
        }

        layout.colExpandRatioArray = uidl.getIntArrayAttribute("colExpand");
        layout.rowExpandRatioArray = uidl.getIntArrayAttribute("rowExpand");

        // clean non rendered components
        for (Widget w : nonRenderedWidgets) {
            Cell cell = layout.widgetToCell.remove(w);
            cell.slot.setCaption(null);

            if (w.getParent() == layout) {
                w.removeFromParent();
                ConnectorMap paintableMap = ConnectorMap.get(client);
                paintableMap.unregisterConnector(paintableMap.getConnector(w));
            }
            cell.slot.getWrapperElement().removeFromParent();
            cell.slot = null;
        }

        int bitMask = uidl.getIntAttribute("margins");
        layout.updateMarginStyleNames(new VMarginInfo(bitMask));

        layout.updateSpacingStyleName(uidl.getBooleanAttribute("spacing"));

        getLayoutManager().setNeedsUpdate(this);
    }

    public void updateCaption(ComponentConnector paintable, UIDL uidl) {
        VGridLayout layout = getWidget();
        if (VCaption.isNeeded(uidl, paintable.getState())) {
            Cell cell = layout.widgetToCell.get(paintable.getWidget());
            VLayoutSlot layoutSlot = cell.slot;
            VCaption caption = layoutSlot.getCaption();
            if (caption == null) {
                caption = new VCaption(paintable, getConnection());

                Widget widget = paintable.getWidget();

                layout.setCaption(widget, caption);
            }
            caption.updateCaption(uidl);
        } else {
            layout.setCaption(paintable.getWidget(), null);
        }
    }

    @Override
    public VGridLayout getWidget() {
        return (VGridLayout) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VGridLayout.class);
    }

    public void layoutVertically() {
        getWidget().updateHeight();
    }

    public void layoutHorizontally() {
        getWidget().updateWidth();
    }
}
