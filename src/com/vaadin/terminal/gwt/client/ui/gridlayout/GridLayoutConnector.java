/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.gridlayout;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.DirectionalManagedLayout;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ui.AlignmentInfo;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.LayoutClickEventHandler;
import com.vaadin.terminal.gwt.client.ui.LayoutClickRPC;
import com.vaadin.terminal.gwt.client.ui.VMarginInfo;
import com.vaadin.terminal.gwt.client.ui.gridlayout.VGridLayout.Cell;
import com.vaadin.terminal.gwt.client.ui.layout.VLayoutSlot;
import com.vaadin.ui.GridLayout;

@Component(GridLayout.class)
public class GridLayoutConnector extends AbstractComponentContainerConnector
        implements Paintable, DirectionalManagedLayout {

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

        layout.setSize(rows, cols);

        final int[] alignments = uidl.getIntArrayAttribute("alignments");
        int alignmentIndex = 0;

        for (final Iterator<?> i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL r = (UIDL) i.next();
            if ("gr".equals(r.getTag())) {
                for (final Iterator<?> j = r.getChildIterator(); j.hasNext();) {
                    final UIDL cellUidl = (UIDL) j.next();
                    if ("gc".equals(cellUidl.getTag())) {
                        int row = cellUidl.getIntAttribute("y");
                        int col = cellUidl.getIntAttribute("x");

                        Widget previousWidget = null;

                        Cell cell = layout.getCell(row, col);
                        if (cell != null && cell.slot != null) {
                            // This is an update. Track if the widget changes
                            // and update the caption if that happens. This
                            // workaround can be removed once the DOM update is
                            // done in onContainerHierarchyChange
                            previousWidget = cell.slot.getWidget();
                        }

                        cell = layout.createCell(row, col);

                        cell.updateFromUidl(cellUidl);

                        if (cell.hasContent()) {
                            cell.setAlignment(new AlignmentInfo(
                                    alignments[alignmentIndex++]));
                            if (cell.slot.getWidget() != previousWidget) {
                                // Widget changed or widget moved from another
                                // slot. Update its caption as the widget might
                                // have called updateCaption when the widget was
                                // still in its old slot. This workaround can be
                                // removed once the DOM update
                                // is done in onContainerHierarchyChange
                                updateCaption(ConnectorMap.get(getConnection())
                                        .getConnector(cell.slot.getWidget()));
                            }
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
        if (!childConnector.delegateCaptionHandling()) {
            // Check not required by interface but by workarounds in this class
            // when updateCaption is explicitly called for all children.
            return;
        }

        VGridLayout layout = getWidget();
        Cell cell = layout.widgetToCell.get(childConnector.getWidget());
        if (cell == null) {
            // workaround before updateFromUidl is removed. We currently update
            // the captions at the end of updateFromUidl instead of immediately
            // because the DOM has not been set up at this point (as it is done
            // in updateFromUidl)
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
