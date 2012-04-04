/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.DirectionalManagedLayout;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractLayoutConnector.AbstractLayoutState;
import com.vaadin.terminal.gwt.client.ui.VGridLayout.Cell;
import com.vaadin.terminal.gwt.client.ui.layout.VLayoutSlot;
import com.vaadin.ui.GridLayout;

@Component(GridLayout.class)
public class GridLayoutConnector extends AbstractComponentContainerConnector
        implements Paintable, DirectionalManagedLayout {

    public static class GridLayoutState extends AbstractLayoutState {
        private boolean spacing = false;
        private int rows = 0;
        private int columns = 0;

        public boolean isSpacing() {
            return spacing;
        }

        public void setSpacing(boolean spacing) {
            this.spacing = spacing;
        }

        public int getRows() {
            return rows;
        }

        public void setRows(int rows) {
            this.rows = rows;
        }

        public int getColumns() {
            return columns;
        }

        public void setColumns(int cols) {
            columns = cols;
        }

    }

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return getWidget().getComponent(element);
        }

        @Override
        protected LayoutClickRPC getLayoutClickRPC() {
            return rpc;
        };

    };

    public interface GridLayoutServerRPC extends LayoutClickRPC, ServerRpc {

    }

    private GridLayoutServerRPC rpc;
    private boolean needCaptionUpdate = false;

    @Override
    public void init() {
        rpc = RpcProxy.create(GridLayoutServerRPC.class, this);
        getLayoutManager().registerDependency(this,
                getWidget().spacingMeasureElement);
    }

    @Override
    public GridLayoutState getState() {
        return (GridLayoutState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        clickEventHandler.handleEventHandlerRegistration();

    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        VGridLayout layout = getWidget();
        layout.client = client;

        if (!isRealUpdate(uidl)) {
            return;
        }

        int cols = getState().getColumns();
        int rows = getState().getRows();

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
                    final UIDL cellUidl = (UIDL) j.next();
                    if ("gc".equals(cellUidl.getTag())) {
                        int row = cellUidl.getIntAttribute("y");
                        int col = cellUidl.getIntAttribute("x");

                        Cell cell = layout.getCell(row, col, cellUidl);
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

        layout.updateMarginStyleNames(new VMarginInfo(getState()
                .getMarginsBitmask()));

        layout.updateSpacingStyleName(getState().isSpacing());

        if (needCaptionUpdate) {
            needCaptionUpdate = false;

            for (ComponentConnector child : getChildren()) {
                updateCaption(child);
            }
        }
        getLayoutManager().setNeedsUpdate(this);
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);

        VGridLayout layout = getWidget();

        // clean non rendered components
        for (ComponentConnector oldChild : event.getOldChildren()) {
            if (oldChild.getParent() == this) {
                continue;
            }

            Widget childWidget = oldChild.getWidget();
            layout.remove(childWidget);

            Cell cell = layout.widgetToCell.remove(childWidget);
            cell.slot.setCaption(null);
            cell.slot.getWrapperElement().removeFromParent();
            cell.slot = null;
        }

    }

    public void updateCaption(ComponentConnector childConnector) {
        VGridLayout layout = getWidget();
        Cell cell = layout.widgetToCell.get(childConnector.getWidget());
        if (cell == null) {
            // workaround before updateFromUidl is removed. Update the
            // captions at the end of updateFromUidl instead of immediately
            needCaptionUpdate = true;
            return;
        }
        if (VCaption.isNeeded(childConnector.getState())) {
            VLayoutSlot layoutSlot = cell.slot;
            VCaption caption = layoutSlot.getCaption();
            if (caption == null) {
                caption = new VCaption(childConnector, getConnection());

                Widget widget = childConnector.getWidget();

                layout.setCaption(widget, caption);
            }
            caption.updateCaption();
        } else {
            layout.setCaption(childConnector.getWidget(), null);
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
