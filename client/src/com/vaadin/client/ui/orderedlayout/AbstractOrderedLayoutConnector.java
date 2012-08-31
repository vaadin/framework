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
package com.vaadin.client.ui.orderedlayout;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.DirectionalManagedLayout;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.Util;
import com.vaadin.client.VCaption;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractLayoutConnector;
import com.vaadin.client.ui.LayoutClickEventHandler;
import com.vaadin.client.ui.layout.ComponentConnectorLayoutSlot;
import com.vaadin.client.ui.layout.VLayoutSlot;
import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.shared.ui.LayoutClickRpc;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.orderedlayout.AbstractOrderedLayoutServerRpc;
import com.vaadin.shared.ui.orderedlayout.AbstractOrderedLayoutState;

public abstract class AbstractOrderedLayoutConnector extends
        AbstractLayoutConnector implements DirectionalManagedLayout {

    AbstractOrderedLayoutServerRpc rpc;

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return Util.getConnectorForElement(getConnection(), getWidget(),
                    element);
        }

        @Override
        protected LayoutClickRpc getLayoutClickRPC() {
            return rpc;
        };

    };

    @Override
    public void init() {
        super.init();
        rpc = RpcProxy.create(AbstractOrderedLayoutServerRpc.class, this);
        getLayoutManager().registerDependency(this,
                getWidget().spacingMeasureElement);
    }

    @Override
    public void onUnregister() {
        LayoutManager lm = getLayoutManager();

        VMeasuringOrderedLayout layout = getWidget();
        lm.unregisterDependency(this, layout.spacingMeasureElement);

        // Unregister child caption listeners
        for (ComponentConnector child : getChildComponents()) {
            VLayoutSlot slot = layout.getSlotForChild(child.getWidget());
            slot.setCaption(null);
        }
    }

    @Override
    public AbstractOrderedLayoutState getState() {
        return (AbstractOrderedLayoutState) super.getState();
    }

    @Override
    public void updateCaption(ComponentConnector component) {
        VMeasuringOrderedLayout layout = getWidget();
        if (VCaption.isNeeded(component.getState())) {
            VLayoutSlot layoutSlot = layout.getSlotForChild(component
                    .getWidget());
            VCaption caption = layoutSlot.getCaption();
            if (caption == null) {
                caption = new VCaption(component, getConnection());

                Widget widget = component.getWidget();

                layout.setCaption(widget, caption);
            }
            caption.updateCaption();
        } else {
            layout.setCaption(component.getWidget(), null);
            getLayoutManager().setNeedsLayout(this);
        }
    }

    @Override
    public VMeasuringOrderedLayout getWidget() {
        return (VMeasuringOrderedLayout) super.getWidget();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        clickEventHandler.handleEventHandlerRegistration();

        VMeasuringOrderedLayout layout = getWidget();

        for (ComponentConnector child : getChildComponents()) {
            VLayoutSlot slot = layout.getSlotForChild(child.getWidget());

            AlignmentInfo alignment = new AlignmentInfo(
                    getState().childData.get(child).alignmentBitmask);
            slot.setAlignment(alignment);

            double expandRatio = getState().childData.get(child).expandRatio;
            slot.setExpandRatio(expandRatio);
        }

        layout.updateMarginStyleNames(new MarginInfo(getState().marginsBitmask));
        layout.updateSpacingStyleName(getState().spacing);

        getLayoutManager().setNeedsLayout(this);
    }

    private int getSizeForInnerSize(int size, boolean isVertical) {
        LayoutManager layoutManager = getLayoutManager();
        Element element = getWidget().getElement();
        if (isVertical) {
            return size + layoutManager.getBorderHeight(element)
                    + layoutManager.getPaddingHeight(element);
        } else {
            return size + layoutManager.getBorderWidth(element)
                    + layoutManager.getPaddingWidth(element);
        }
    }

    private static String getSizeProperty(boolean isVertical) {
        return isVertical ? "height" : "width";
    }

    private boolean isUndefinedInDirection(boolean isVertical) {
        if (isVertical) {
            return isUndefinedHeight();
        } else {
            return isUndefinedWidth();
        }
    }

    private int getInnerSizeInDirection(boolean isVertical) {
        if (isVertical) {
            return getLayoutManager().getInnerHeight(getWidget().getElement());
        } else {
            return getLayoutManager().getInnerWidth(getWidget().getElement());
        }
    }

    private void layoutPrimaryDirection() {
        VMeasuringOrderedLayout layout = getWidget();
        boolean isVertical = layout.isVertical;
        boolean isUndefined = isUndefinedInDirection(isVertical);

        int startPadding = getStartPadding(isVertical);
        int endPadding = getEndPadding(isVertical);
        int spacingSize = getSpacingInDirection(isVertical);
        int allocatedSize;

        if (isUndefined) {
            allocatedSize = -1;
        } else {
            allocatedSize = getInnerSizeInDirection(isVertical);
        }

        allocatedSize = layout.layoutPrimaryDirection(spacingSize,
                allocatedSize, startPadding, endPadding);

        Style ownStyle = getWidget().getElement().getStyle();
        if (isUndefined) {
            int outerSize = getSizeForInnerSize(allocatedSize, isVertical);
            ownStyle.setPropertyPx(getSizeProperty(isVertical), outerSize);
            reportUndefinedSize(outerSize, isVertical);
        } else {
            ownStyle.setProperty(getSizeProperty(isVertical),
                    getDefinedSize(isVertical));
        }
    }

    private void reportUndefinedSize(int outerSize, boolean isVertical) {
        if (isVertical) {
            getLayoutManager().reportOuterHeight(this, outerSize);
        } else {
            getLayoutManager().reportOuterWidth(this, outerSize);
        }
    }

    private int getSpacingInDirection(boolean isVertical) {
        if (isVertical) {
            return getLayoutManager().getOuterHeight(
                    getWidget().spacingMeasureElement);
        } else {
            return getLayoutManager().getOuterWidth(
                    getWidget().spacingMeasureElement);
        }
    }

    private void layoutSecondaryDirection() {
        VMeasuringOrderedLayout layout = getWidget();
        boolean isVertical = layout.isVertical;
        boolean isUndefined = isUndefinedInDirection(!isVertical);

        int startPadding = getStartPadding(!isVertical);
        int endPadding = getEndPadding(!isVertical);

        int allocatedSize;
        if (isUndefined) {
            allocatedSize = -1;
        } else {
            allocatedSize = getInnerSizeInDirection(!isVertical);
        }

        allocatedSize = layout.layoutSecondaryDirection(allocatedSize,
                startPadding, endPadding);

        Style ownStyle = getWidget().getElement().getStyle();

        if (isUndefined) {
            int outerSize = getSizeForInnerSize(allocatedSize,
                    !getWidget().isVertical);
            ownStyle.setPropertyPx(getSizeProperty(!getWidget().isVertical),
                    outerSize);
            reportUndefinedSize(outerSize, !isVertical);
        } else {
            ownStyle.setProperty(getSizeProperty(!getWidget().isVertical),
                    getDefinedSize(!getWidget().isVertical));
        }
    }

    private String getDefinedSize(boolean isVertical) {
        if (isVertical) {
            return getState().height == null ? "" : getState().height;
        } else {
            return getState().width == null ? "" : getState().width;
        }
    }

    private int getStartPadding(boolean isVertical) {
        if (isVertical) {
            return getLayoutManager().getPaddingTop(getWidget().getElement());
        } else {
            return getLayoutManager().getPaddingLeft(getWidget().getElement());
        }
    }

    private int getEndPadding(boolean isVertical) {
        if (isVertical) {
            return getLayoutManager()
                    .getPaddingBottom(getWidget().getElement());
        } else {
            return getLayoutManager().getPaddingRight(getWidget().getElement());
        }
    }

    @Override
    public void layoutHorizontally() {
        if (getWidget().isVertical) {
            layoutSecondaryDirection();
        } else {
            layoutPrimaryDirection();
        }
    }

    @Override
    public void layoutVertically() {
        if (getWidget().isVertical) {
            layoutPrimaryDirection();
        } else {
            layoutSecondaryDirection();
        }
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);
        List<ComponentConnector> previousChildren = event.getOldChildren();
        int currentIndex = 0;
        VMeasuringOrderedLayout layout = getWidget();

        for (ComponentConnector child : getChildComponents()) {
            Widget childWidget = child.getWidget();
            VLayoutSlot slot = layout.getSlotForChild(childWidget);

            if (childWidget.getParent() != layout) {
                // If the child widget was previously attached to another
                // AbstractOrderedLayout a slot might be found that belongs to
                // another AbstractOrderedLayout. In this case we discard it and
                // create a new slot.
                slot = new ComponentConnectorLayoutSlot(getWidget()
                        .getStylePrimaryName(), child, this);
            }
            layout.addOrMove(slot, currentIndex++);
            if (child.isRelativeWidth()) {
                slot.getWrapperElement().getStyle().setWidth(100, Unit.PCT);
            }
        }

        for (ComponentConnector child : previousChildren) {
            if (child.getParent() != this) {
                // Remove slot if the connector is no longer a child of this
                // layout
                layout.removeSlotForWidget(child.getWidget());
            }
        }

    };

}
