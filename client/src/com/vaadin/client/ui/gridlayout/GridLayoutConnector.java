/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.client.ui.gridlayout;

import java.util.Iterator;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.DirectionalManagedLayout;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.VCaption;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.client.ui.LayoutClickEventHandler;
import com.vaadin.client.ui.gridlayout.VGridLayout.Cell;
import com.vaadin.client.ui.layout.VLayoutSlot;
import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.LayoutClickRpc;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.gridlayout.GridLayoutServerRpc;
import com.vaadin.shared.ui.gridlayout.GridLayoutState;
import com.vaadin.ui.GridLayout;

@Connect(GridLayout.class)
public class GridLayoutConnector extends AbstractComponentContainerConnector
        implements Paintable, DirectionalManagedLayout {

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return getWidget().getComponent(element);
        }

        @Override
        protected LayoutClickRpc getLayoutClickRPC() {
            return rpc;
        };

    };

    private GridLayoutServerRpc rpc;
    private boolean needCaptionUpdate = false;

    @Override
    public void init() {
        super.init();
        rpc = getRpcProxy(GridLayoutServerRpc.class);
        getLayoutManager().registerDependency(this,
                getWidget().spacingMeasureElement);
    }

    @Override
    public void onUnregister() {
        VGridLayout layout = getWidget();
        getLayoutManager().unregisterDependency(this,
                layout.spacingMeasureElement);

        // Unregister caption size dependencies
        for (ComponentConnector child : getChildComponents()) {
            Cell cell = layout.widgetToCell.get(child.getWidget());
            cell.slot.setCaption(null);
        }
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

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        VGridLayout layout = getWidget();
        layout.client = client;

        if (!isRealUpdate(uidl)) {
            return;
        }

        int cols = getState().columns;
        int rows = getState().rows;

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

        layout.updateMarginStyleNames(new MarginInfo(getState().marginsBitmask));

        layout.updateSpacingStyleName(getState().spacing);

        if (needCaptionUpdate) {
            needCaptionUpdate = false;

            for (ComponentConnector child : getChildComponents()) {
                updateCaption(child);
            }
        }
        getLayoutManager().setNeedsLayout(this);
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
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

    @Override
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
    public void layoutVertically() {
        getWidget().updateHeight();
    }

    @Override
    public void layoutHorizontally() {
        getWidget().updateWidth();
    }
}
