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
package com.vaadin.client.ui.orderedlayout;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.Profiler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.Util;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.AbstractLayoutConnector;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.LayoutClickEventHandler;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.ComponentConstants;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.shared.ui.LayoutClickRpc;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.orderedlayout.AbstractOrderedLayoutServerRpc;
import com.vaadin.shared.ui.orderedlayout.AbstractOrderedLayoutState;

/**
 * Base class for vertical and horizontal ordered layouts
 */
public abstract class AbstractOrderedLayoutConnector extends
        AbstractLayoutConnector {

    /*
     * Handlers & Listeners
     */

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(
                com.google.gwt.user.client.Element element) {
            return Util.getConnectorForElement(getConnection(), getWidget(),
                    element);
        }

        @Override
        protected LayoutClickRpc getLayoutClickRPC() {
            return getRpcProxy(AbstractOrderedLayoutServerRpc.class);
        }
    };

    private StateChangeHandler childStateChangeHandler = new StateChangeHandler() {
        @Override
        public void onStateChanged(StateChangeEvent stateChangeEvent) {
            // Child state has changed, update stuff it hasn't already been done
            updateInternalState();

            /*
             * Some changes must always be done after each child's own state
             * change handler has been run because it might have changed some
             * styles that are overridden here.
             */
            ServerConnector child = stateChangeEvent.getConnector();
            if (child instanceof ComponentConnector) {
                ComponentConnector component = (ComponentConnector) child;
                Slot slot = getWidget().getSlot(component.getWidget());

                slot.setRelativeWidth(component.isRelativeWidth());
                slot.setRelativeHeight(component.isRelativeHeight());
            }
        }
    };

    private ElementResizeListener slotCaptionResizeListener = new ElementResizeListener() {
        @Override
        public void onElementResize(ElementResizeEvent e) {

            // Get all needed element references
            Element captionElement = e.getElement();

            // Caption position determines if the widget element is the first or
            // last child inside the caption wrap
            CaptionPosition pos = getWidget().getCaptionPositionFromElement(
                    captionElement.getParentElement());

            // The default is the last child
            Element widgetElement = captionElement.getParentElement()
                    .getLastChild().cast();

            // ...but if caption position is bottom or right, the widget is the
            // first child
            if (pos == CaptionPosition.BOTTOM || pos == CaptionPosition.RIGHT) {
                widgetElement = captionElement.getParentElement()
                        .getFirstChildElement().cast();
            }

            if (captionElement == widgetElement) {
                // Caption element already detached
                Slot slot = getWidget().getSlot(widgetElement);
                if (slot != null) {
                    slot.setCaptionResizeListener(null);
                }
                return;
            }

            String widgetWidth = widgetElement.getStyle().getWidth();
            String widgetHeight = widgetElement.getStyle().getHeight();

            if (widgetHeight.endsWith("%")
                    && (pos == CaptionPosition.TOP || pos == CaptionPosition.BOTTOM)) {
                getWidget().updateCaptionOffset(captionElement);
            } else if (widgetWidth.endsWith("%")
                    && (pos == CaptionPosition.LEFT || pos == CaptionPosition.RIGHT)) {
                getWidget().updateCaptionOffset(captionElement);
            }

            updateLayoutHeight();

            if (needsExpand()) {
                getWidget().updateExpandCompensation();
            }
        }
    };

    private ElementResizeListener childComponentResizeListener = new ElementResizeListener() {
        @Override
        public void onElementResize(ElementResizeEvent e) {
            updateLayoutHeight();
            if (needsExpand()) {
                getWidget().updateExpandCompensation();
            }
        }
    };

    private ElementResizeListener spacingResizeListener = new ElementResizeListener() {
        @Override
        public void onElementResize(ElementResizeEvent e) {
            if (needsExpand()) {
                getWidget().updateExpandCompensation();
            }
        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentConnector#init()
     */
    @Override
    public void init() {
        super.init();
        getWidget().setLayoutManager(getLayoutManager());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractLayoutConnector#getState()
     */
    @Override
    public AbstractOrderedLayoutState getState() {
        return (AbstractOrderedLayoutState) super.getState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentConnector#getWidget()
     */
    @Override
    public VAbstractOrderedLayout getWidget() {
        return (VAbstractOrderedLayout) super.getWidget();
    }

    /**
     * Keep track of whether any child has relative height. Used to determine
     * whether measurements are needed to make relative child heights work
     * together with undefined container height.
     */
    private boolean hasChildrenWithRelativeHeight = false;

    /**
     * Keep track of whether any child has relative width. Used to determine
     * whether measurements are needed to make relative child widths work
     * together with undefined container width.
     */
    private boolean hasChildrenWithRelativeWidth = false;

    /**
     * Keep track of whether any child is middle aligned. Used to determine if
     * measurements are needed to make middle aligned children work.
     */
    private boolean hasChildrenWithMiddleAlignment = false;

    /**
     * Keeps track of whether slots should be expanded based on available space.
     */
    private boolean needsExpand = false;

    /**
     * The id of the previous response for which state changes have been
     * processed. If this is the same as the
     * {@link ApplicationConnection#getLastResponseId()}, it means that we can
     * skip some quite expensive calculations because we know that the state
     * hasn't changed since the last time the values were calculated.
     */
    private int processedResponseId = -1;

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.HasComponentsConnector#updateCaption(com.vaadin
     * .client.ComponentConnector)
     */
    @Override
    public void updateCaption(ComponentConnector connector) {
        /*
         * Don't directly update captions here to avoid calling e.g.
         * updateLayoutHeight() before everything is initialized.
         * updateInternalState() will ensure all captions are updated when
         * appropriate.
         */
        updateInternalState();
    }

    private void updateCaptionInternal(ComponentConnector child) {
        Slot slot = getWidget().getSlot(child.getWidget());

        String caption = child.getState().caption;
        URLReference iconUrl = child.getState().resources
                .get(ComponentConstants.ICON_RESOURCE);
        String iconUrlString = iconUrl != null ? iconUrl.getURL() : null;
        Icon icon = child.getConnection().getIcon(iconUrlString);

        List<String> styles = child.getState().styles;
        String error = child.getState().errorMessage;
        boolean showError = error != null;
        if (child.getState() instanceof AbstractFieldState) {
            AbstractFieldState abstractFieldState = (AbstractFieldState) child
                    .getState();
            showError = showError && !abstractFieldState.hideErrors;
        }
        boolean required = false;
        if (child instanceof AbstractFieldConnector) {
            required = ((AbstractFieldConnector) child).isRequired();
        }
        boolean enabled = child.isEnabled();

        if (slot.hasCaption() && null == caption) {
            slot.setCaptionResizeListener(null);
        }

        slot.setCaption(caption, icon, styles, error, showError, required,
                enabled, child.getState().captionAsHtml);

        AriaHelper.handleInputRequired(child.getWidget(), required);
        AriaHelper.handleInputInvalid(child.getWidget(), showError);
        AriaHelper.bindCaption(child.getWidget(), slot.getCaptionElement());

        if (slot.hasCaption()) {
            CaptionPosition pos = slot.getCaptionPosition();
            slot.setCaptionResizeListener(slotCaptionResizeListener);
            if (child.isRelativeHeight()
                    && (pos == CaptionPosition.TOP || pos == CaptionPosition.BOTTOM)) {
                getWidget().updateCaptionOffset(slot.getCaptionElement());
            } else if (child.isRelativeWidth()
                    && (pos == CaptionPosition.LEFT || pos == CaptionPosition.RIGHT)) {
                getWidget().updateCaptionOffset(slot.getCaptionElement());
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentContainerConnector#
     * onConnectorHierarchyChange
     * (com.vaadin.client.ConnectorHierarchyChangeEvent)
     */
    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        Profiler.enter("AOLC.onConnectorHierarchyChange");

        List<ComponentConnector> previousChildren = event.getOldChildren();
        int currentIndex = 0;
        VAbstractOrderedLayout layout = getWidget();

        // remove spacing as it is exists as separate elements that cannot be
        // removed easily after reordering the contents
        Profiler.enter("AOLC.onConnectorHierarchyChange temporarily remove spacing");
        layout.setSpacing(false);
        Profiler.leave("AOLC.onConnectorHierarchyChange temporarily remove spacing");

        for (ComponentConnector child : getChildComponents()) {
            Profiler.enter("AOLC.onConnectorHierarchyChange add children");
            Slot slot = layout.getSlot(child.getWidget());
            if (slot.getParent() != layout) {
                Profiler.enter("AOLC.onConnectorHierarchyChange add state change handler");
                child.addStateChangeHandler(childStateChangeHandler);
                Profiler.leave("AOLC.onConnectorHierarchyChange add state change handler");
            }
            Profiler.enter("AOLC.onConnectorHierarchyChange addOrMoveSlot");
            layout.addOrMoveSlot(slot, currentIndex++, false);
            Profiler.leave("AOLC.onConnectorHierarchyChange addOrMoveSlot");

            Profiler.leave("AOLC.onConnectorHierarchyChange add children");
        }

        // re-add spacing for the elements that should have it
        Profiler.enter("AOLC.onConnectorHierarchyChange setSpacing");
        // spacings were removed above
        if (getState().spacing) {
            layout.setSpacing(true);
        }
        Profiler.leave("AOLC.onConnectorHierarchyChange setSpacing");

        for (ComponentConnector child : previousChildren) {
            Profiler.enter("AOLC.onConnectorHierarchyChange remove children");
            if (child.getParent() != this) {
                Slot slot = layout.getSlot(child.getWidget());
                slot.setWidgetResizeListener(null);
                if (slot.hasCaption()) {
                    slot.setCaptionResizeListener(null);
                }
                slot.setSpacingResizeListener(null);
                child.removeStateChangeHandler(childStateChangeHandler);
                layout.removeWidget(child.getWidget());
            }
            Profiler.leave("AOLC.onConnectorHierarchyChange remove children");
        }
        Profiler.leave("AOLC.onConnectorHierarchyChange");

        updateInternalState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.client.ui.AbstractComponentConnector#onStateChanged(com.vaadin
     * .client.communication.StateChangeEvent)
     */
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        clickEventHandler.handleEventHandlerRegistration();
        getWidget().setMargin(new MarginInfo(getState().marginsBitmask));
        getWidget().setSpacing(getState().spacing);

        updateInternalState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.client.ui.AbstractComponentConnector#getTooltipInfo(com.google
     * .gwt.dom.client.Element)
     */
    @Override
    public TooltipInfo getTooltipInfo(com.google.gwt.dom.client.Element element) {
        if (element != getWidget().getElement()) {
            Slot slot = WidgetUtil.findWidget(element, Slot.class);
            if (slot != null && slot.getCaptionElement() != null
                    && slot.getParent() == getWidget()
                    && slot.getCaptionElement().isOrHasChild(element)) {
                ComponentConnector connector = Util.findConnectorFor(slot
                        .getWidget());
                if (connector != null) {
                    return connector.getTooltipInfo(element);
                }
            }
        }
        return super.getTooltipInfo(element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentConnector#hasTooltip()
     */
    @Override
    public boolean hasTooltip() {
        /*
         * Tooltips are fetched from child connectors -> there's no quick way of
         * checking whether there might a tooltip hiding somewhere
         */
        return true;
    }

    /**
     * Updates DOM properties and listeners based on the current state of this
     * layout and its children.
     */
    private void updateInternalState() {
        // Avoid updating again for the same data
        int lastResponseId = getConnection().getLastResponseId();
        if (processedResponseId == lastResponseId) {
            return;
        }
        Profiler.enter("AOLC.updateInternalState");
        // Remember that everything is updated for this response
        processedResponseId = lastResponseId;

        hasChildrenWithRelativeHeight = false;
        hasChildrenWithRelativeWidth = false;

        hasChildrenWithMiddleAlignment = false;

        needsExpand = getWidget().vertical ? !isUndefinedHeight()
                : !isUndefinedWidth();

        boolean onlyZeroExpands = true;
        if (needsExpand) {
            for (ComponentConnector child : getChildComponents()) {
                double expandRatio = getState().childData.get(child).expandRatio;
                if (expandRatio != 0) {
                    onlyZeroExpands = false;
                    break;
                }
            }
        }

        // First update bookkeeping for all children
        for (ComponentConnector child : getChildComponents()) {
            Slot slot = getWidget().getSlot(child.getWidget());

            slot.setRelativeWidth(child.isRelativeWidth());
            slot.setRelativeHeight(child.isRelativeHeight());

            if (child.delegateCaptionHandling()) {
                updateCaptionInternal(child);
            }

            // Update slot style names
            List<String> childStyles = child.getState().styles;
            if (childStyles == null) {
                getWidget().setSlotStyleNames(child.getWidget(),
                        (String[]) null);
            } else {
                getWidget().setSlotStyleNames(child.getWidget(),
                        childStyles.toArray(new String[childStyles.size()]));
            }

            AlignmentInfo alignment = new AlignmentInfo(
                    getState().childData.get(child).alignmentBitmask);
            slot.setAlignment(alignment);

            if (alignment.isVerticalCenter()) {
                hasChildrenWithMiddleAlignment = true;
            }

            double expandRatio = onlyZeroExpands ? 1 : getState().childData
                    .get(child).expandRatio;

            slot.setExpandRatio(expandRatio);

            if (child.isRelativeHeight()) {
                hasChildrenWithRelativeHeight = true;
            }
            if (child.isRelativeWidth()) {
                hasChildrenWithRelativeWidth = true;
            }
        }

        if (needsFixedHeight()) {
            // Add resize listener to ensure the widget itself is measured
            getLayoutManager().addElementResizeListener(
                    getWidget().getElement(), childComponentResizeListener);
        } else {
            getLayoutManager().removeElementResizeListener(
                    getWidget().getElement(), childComponentResizeListener);
        }

        // Then update listeners based on bookkeeping
        updateAllSlotListeners();

        // Update the layout at this point to ensure it's OK even if we get no
        // element resize events
        updateLayoutHeight();
        if (needsExpand()) {
            getWidget().updateExpandedSizes();
            // updateExpandedSizes causes fixed size components to temporarily
            // lose their size. updateExpandCompensation must be delayed until
            // the browser has a chance to measure them.
            Scheduler.get().scheduleFinally(new ScheduledCommand() {
                @Override
                public void execute() {
                    getWidget().updateExpandCompensation();
                }
            });
        } else {
            getWidget().clearExpand();
        }

        Profiler.leave("AOLC.updateInternalState");
    }

    /**
     * Does the layout need a fixed height?
     */
    private boolean needsFixedHeight() {
        boolean isVertical = getWidget().vertical;

        if (isVertical) {
            // Doesn't need height fix for vertical layouts
            return false;
        }

        else if (!isUndefinedHeight()) {
            // Fix not needed unless the height is undefined
            return false;
        }

        else if (!hasChildrenWithRelativeHeight
                && !hasChildrenWithMiddleAlignment) {
            // Already works if there are no relative heights or middle aligned
            // children
            return false;
        }

        return true;
    }

    /**
     * Does the layout need to expand?
     */
    private boolean needsExpand() {
        return needsExpand;
    }

    /**
     * Add slot listeners
     */
    private void updateAllSlotListeners() {
        for (ComponentConnector child : getChildComponents()) {
            updateSlotListeners(child);
        }
    }

    /**
     * Add/remove necessary ElementResizeListeners for one slot. This should be
     * called after each update to the slot's or it's widget.
     */
    private void updateSlotListeners(ComponentConnector child) {
        Slot slot = getWidget().getSlot(child.getWidget());

        // Clear all possible listeners first
        slot.setWidgetResizeListener(null);
        if (slot.hasCaption()) {
            slot.setCaptionResizeListener(null);
        }
        if (slot.hasSpacing()) {
            slot.setSpacingResizeListener(null);
        }

        // Add all necessary listeners
        if (needsFixedHeight()) {
            slot.setWidgetResizeListener(childComponentResizeListener);
            if (slot.hasCaption()) {
                slot.setCaptionResizeListener(slotCaptionResizeListener);
            }
        } else if ((hasChildrenWithRelativeHeight || hasChildrenWithRelativeWidth)
                && slot.hasCaption()) {
            /*
             * If the slot has caption, we need to listen for its size changes
             * in order to update the padding/margin offset for relative sized
             * components.
             * 
             * TODO might only be needed if the caption is in the same direction
             * as the relative size?
             */
            slot.setCaptionResizeListener(slotCaptionResizeListener);
        }

        if (needsExpand()) {
            // TODO widget resize only be needed for children without expand?
            slot.setWidgetResizeListener(childComponentResizeListener);
            if (slot.hasSpacing()) {
                slot.setSpacingResizeListener(spacingResizeListener);
            }
        }
    }

    /**
     * Re-calculate the layout height
     */
    private void updateLayoutHeight() {
        if (needsFixedHeight()) {
            int h = getMaxHeight();
            if (h < 0) {
                // Postpone change if there are elements that have not yet been
                // measured
                return;
            }
            h += getLayoutManager().getBorderHeight(getWidget().getElement())
                    + getLayoutManager().getPaddingHeight(
                            getWidget().getElement());
            getWidget().getElement().getStyle().setHeight(h, Unit.PX);
            getLayoutManager().setNeedsMeasure(this);
        }
    }

    /**
     * Measures the maximum height of the layout in pixels
     */
    private int getMaxHeight() {
        int highestNonRelative = -1;
        int highestRelative = -1;

        LayoutManager layoutManager = getLayoutManager();

        for (ComponentConnector child : getChildComponents()) {
            Widget childWidget = child.getWidget();
            Slot slot = getWidget().getSlot(childWidget);
            Element captionElement = slot.getCaptionElement();
            CaptionPosition captionPosition = slot.getCaptionPosition();

            int pixelHeight = layoutManager.getOuterHeight(childWidget
                    .getElement());
            if (pixelHeight == -1) {
                // Height has not yet been measured -> postpone actions that
                // depend on the max height
                return -1;
            }

            boolean hasRelativeHeight = slot.hasRelativeHeight();

            boolean captionSizeShouldBeAddedtoComponentHeight = captionPosition == CaptionPosition.TOP
                    || captionPosition == CaptionPosition.BOTTOM;
            boolean includeCaptionHeight = captionElement != null
                    && captionSizeShouldBeAddedtoComponentHeight;

            if (includeCaptionHeight) {
                int captionHeight = layoutManager
                        .getOuterHeight(captionElement)
                        - getLayoutManager().getMarginHeight(captionElement);
                if (captionHeight == -1) {
                    // Height has not yet been measured -> postpone actions that
                    // depend on the max height
                    return -1;
                }
                pixelHeight += captionHeight;
            }

            if (!hasRelativeHeight) {
                if (pixelHeight > highestNonRelative) {
                    highestNonRelative = pixelHeight;
                }
            } else {
                if (pixelHeight > highestRelative) {
                    highestRelative = pixelHeight;
                }
            }
        }
        return highestNonRelative > -1 ? highestNonRelative : highestRelative;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentConnector#onUnregister()
     */
    @Override
    public void onUnregister() {
        // Cleanup all ElementResizeListeners
        for (ComponentConnector child : getChildComponents()) {
            Slot slot = getWidget().getSlot(child.getWidget());
            if (slot.hasCaption()) {
                slot.setCaptionResizeListener(null);
            }

            if (slot.getSpacingElement() != null) {
                slot.setSpacingResizeListener(null);
            }

            slot.setWidgetResizeListener(null);
        }

        super.onUnregister();
    }
}
