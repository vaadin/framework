/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.AbstractFieldState;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.terminal.gwt.client.ui.VBoxLayout.CaptionPosition;
import com.vaadin.terminal.gwt.client.ui.VBoxLayout.Slot;
import com.vaadin.terminal.gwt.client.ui.layout.ElementResizeEvent;
import com.vaadin.terminal.gwt.client.ui.layout.ElementResizeListener;
import com.vaadin.terminal.gwt.client.ui.orderedlayout.AbstractOrderedLayoutServerRpc;
import com.vaadin.terminal.gwt.client.ui.orderedlayout.AbstractOrderedLayoutState;

public abstract class AbstractBoxLayoutConnector extends
        AbstractLayoutConnector /* implements PostLayoutListener */{

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
        rpc = RpcProxy.create(AbstractOrderedLayoutServerRpc.class, this);
        getWidget().setLayoutManager(getLayoutManager());
    }

    @Override
    public AbstractOrderedLayoutState getState() {
        return (AbstractOrderedLayoutState) super.getState();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VBoxLayout.class);
    }

    @Override
    public VBoxLayout getWidget() {
        return (VBoxLayout) super.getWidget();
    }

    /**
     * For bookkeeping. Used to determine if extra calculations are needed for
     * horizontal layout.
     */
    private HashSet<ComponentConnector> hasVerticalAlignment = new HashSet<ComponentConnector>();

    /**
     * For bookkeeping. Used to determine if extra calculations are needed for
     * horizontal layout.
     */
    private HashSet<ComponentConnector> hasRelativeHeight = new HashSet<ComponentConnector>();

    /**
     * For bookkeeping. Used to determine if extra calculations are needed for
     * horizontal layout.
     */
    private HashSet<ComponentConnector> hasExpandRatio = new HashSet<ComponentConnector>();

    /**
     * For bookkeeping. Used in extra calculations for horizontal layout.
     */
    private HashSet<Element> needsMeasure = new HashSet<Element>();

    /**
     * For bookkeeping. Used in extra calculations for horizontal layout.
     */
    private HashMap<Element, Integer> childElementHeight = new HashMap<Element, Integer>();

    /**
     * For bookkeeping. Used in extra calculations for horizontal layout.
     */
    private HashMap<Element, Integer> childCaptionElementHeight = new HashMap<Element, Integer>();

    public void updateCaption(ComponentConnector child) {
        Slot slot = getWidget().getSlot(child);

        String caption = child.getState().getCaption();
        String iconUrl = child.getState().getIcon() != null ? child.getState()
                .getIcon().getURL() : null;
        List<String> styles = child.getState().getStyles();
        String error = child.getState().getErrorMessage();
        boolean showError = error != null;
        if (child.getState() instanceof AbstractFieldState) {
            AbstractFieldState abstractFieldState = (AbstractFieldState) child
                    .getState();
            showError = showError && !abstractFieldState.isHideErrors();
        }
        boolean required = false;
        if (child instanceof AbstractFieldConnector) {
            required = ((AbstractFieldConnector) child).isRequired();
        }
        boolean enabled = child.getState().isEnabled();
        // TODO Description is handled from somewhere else?

        slot.setCaption(caption, iconUrl, styles, error, showError, required,
                enabled);

        slot.setRelativeWidth(child.isRelativeWidth());
        slot.setRelativeHeight(child.isRelativeHeight());

        if (slot.hasCaption()) {
            CaptionPosition pos = slot.getCaptionPosition();
            getLayoutManager().addElementResizeListener(
                    slot.getCaptionElement(), slotCaptionResizeListener);
            if (child.isRelativeHeight()
                    && (pos == CaptionPosition.TOP || pos == CaptionPosition.BOTTOM)) {
                getWidget().updateCaptionOffset(slot.getCaptionElement());
            } else if (child.isRelativeWidth()
                    && (pos == CaptionPosition.LEFT || pos == CaptionPosition.RIGHT)) {
                getWidget().updateCaptionOffset(slot.getCaptionElement());
            }
        } else {
            childCaptionElementHeight.remove(child.getWidget().getElement());
        }

        updateLayoutHeight();

        if (needsExpand()) {
            updateExpand();
        }
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);

        List<ComponentConnector> previousChildren = event.getOldChildren();
        int currentIndex = 0;
        VBoxLayout layout = getWidget();

        for (ComponentConnector child : getChildren()) {
            Slot slot = layout.getSlot(child);
            if (slot.getParent() != layout) {
                child.addStateChangeHandler(childStateChangeHandler);
            }
            layout.addOrMoveSlot(slot, currentIndex++);
        }

        for (ComponentConnector child : previousChildren) {
            if (child.getParent() != this) {
                Slot slot = layout.getSlot(child);
                hasVerticalAlignment.remove(child);
                hasRelativeHeight.remove(child);
                hasExpandRatio.remove(child);
                needsMeasure.remove(child.getWidget().getElement());
                childElementHeight.remove(child.getWidget().getElement());
                childCaptionElementHeight
                        .remove(child.getWidget().getElement());
                getLayoutManager().removeElementResizeListener(
                        child.getWidget().getElement(),
                        childComponentResizeListener);
                if (slot.hasCaption()) {
                    getLayoutManager()
                            .removeElementResizeListener(
                                    slot.getCaptionElement(),
                                    slotCaptionResizeListener);
                }
                if (slot.getSpacingElement() != null) {
                    getLayoutManager().removeElementResizeListener(
                            slot.getSpacingElement(), spacingResizeListener);
                }
                child.removeStateChangeHandler(childStateChangeHandler);
                layout.removeSlot(child.getWidget());
            }
        }

        // If some component is added/removed, we need to recalculate the expand
        if (needsExpand()) {
            updateExpand();
        } else {
            getWidget().clearExpand();
        }

    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        clickEventHandler.handleEventHandlerRegistration();

        getWidget().setMargin(new VMarginInfo(getState().getMarginsBitmask()));
        getWidget().setSpacing(getState().isSpacing());

        hasExpandRatio.clear();
        hasVerticalAlignment.clear();
        hasRelativeHeight.clear();
        needsMeasure.clear();

        for (ComponentConnector child : getChildren()) {
            Slot slot = getWidget().getSlot(child);

            AlignmentInfo alignment = new AlignmentInfo(getState()
                    .getChildData().get(child).getAlignmentBitmask());
            slot.setAlignment(alignment);

            double expandRatio = getState().getChildData().get(child)
                    .getExpandRatio();
            if (expandRatio == 0) {
                expandRatio = -1;
            }
            slot.setExpandRatio(expandRatio);

            // Bookkeeping to identify special cases that need extra
            // calculations
            if (alignment.isVerticalCenter() || alignment.isBottom()) {
                hasVerticalAlignment.add(child);
            }

            if (expandRatio > 0) {
                hasExpandRatio.add(child);
            }

            if (child.getState().isRelativeHeight()) {
                hasRelativeHeight.add(child);
            } else {
                needsMeasure.add(child.getWidget().getElement());
            }
        }

        updateAllSlotListeners();

        updateLayoutHeight();
    }

    StateChangeHandler childStateChangeHandler = new StateChangeHandler() {
        public void onStateChanged(StateChangeEvent stateChangeEvent) {

            ComponentConnector child = (ComponentConnector) stateChangeEvent
                    .getConnector();

            // We need to update the slot size if the component size is changed
            // to relative
            Slot slot = getWidget().getSlot(child);
            slot.setRelativeWidth(child.isRelativeWidth());
            slot.setRelativeHeight(child.isRelativeHeight());

            // For relative sized widgets, we need to set the caption offset
            // if (slot.hasCaption()) {
            // CaptionPosition pos = slot.getCaptionPosition();
            // if (child.isRelativeHeight()
            // && (pos == CaptionPosition.TOP || pos == CaptionPosition.BOTTOM))
            // {
            // getWidget().updateCaptionOffset(slot.getCaptionElement());
            // } else if (child.isRelativeWidth()
            // && (pos == CaptionPosition.LEFT || pos == CaptionPosition.RIGHT))
            // {
            // getWidget().updateCaptionOffset(slot.getCaptionElement());
            // }
            // }

            updateSlotListeners(child);
        }
    };

    private boolean needsFixedHeight() {
        if (!getWidget().vertical && isUndefinedHeight()
                && (hasRelativeHeight.size() > 0 /*
                                                  * ||
                                                  * hasVerticalAlignment.size()
                                                  * > 0
                                                  */)) {
            return true;
        }
        return false;
    }

    private boolean needsExpand() {
        boolean canApplyExpand = (getWidget().vertical && !isUndefinedHeight())
                || (!getWidget().vertical && !isUndefinedWidth());
        return hasExpandRatio.size() > 0 && canApplyExpand;
    }

    private void updateAllSlotListeners() {
        for (ComponentConnector child : getChildren()) {
            updateSlotListeners(child);
        }
        // if (needsFixedHeight()) {
        // getWidget().clearHeight();
        // setLayoutHeightListener(true);
        // getLayoutManager().setNeedsMeasure(AbstractBoxLayoutConnector.this);
        // } else {
        // setLayoutHeightListener(false);
        // }
    }

    /**
     * Add/remove necessary ElementResizeListeners for one slot. This should be
     * called after each update to the slot's or it's widget.
     */
    private void updateSlotListeners(ComponentConnector child) {
        Slot slot = getWidget().getSlot(child);

        // Clear all possible listeners first
        dontListen(slot.getWidget().getElement(), childComponentResizeListener);
        if (slot.hasCaption()) {
            dontListen(slot.getCaptionElement(), slotCaptionResizeListener);
        }
        if (slot.hasSpacing()) {
            dontListen(slot.getSpacingElement(), spacingResizeListener);
        }

        // Add all necessary listeners
        if (needsFixedHeight()) {
            listen(slot.getWidget().getElement(), childComponentResizeListener);
            if (slot.hasCaption()) {
                listen(slot.getCaptionElement(), slotCaptionResizeListener);
            }
        } else if ((child.isRelativeHeight() || child.isRelativeWidth())
                && slot.hasCaption()) {
            // If the slot has caption, we need to listen for it's size changes
            // in order to update the padding/margin offset for relative sized
            // components
            listen(slot.getCaptionElement(), slotCaptionResizeListener);
        }

        if (needsExpand()) {
            listen(slot.getWidget().getElement(), childComponentResizeListener);
            if (slot.hasSpacing()) {
                listen(slot.getSpacingElement(), spacingResizeListener);
            }
        }

        if (child.isRelativeHeight()) {
            hasRelativeHeight.add(child);
            needsMeasure.remove(child.getWidget().getElement());
        } else {
            hasRelativeHeight.remove(child);
            needsMeasure.add(child.getWidget().getElement());
        }

    }

    // public void postLayout() {
    // if (needsFixedHeight()) {
    // // Re-measure all elements that are available
    // for (Element el : needsMeasure) {
    // childElementHeight.put(el, getLayoutManager()
    // .getOuterHeight(el));
    //
    // Element captionElement = el.getParentElement()
    // .getFirstChildElement().cast();
    // if (captionElement.getClassName().contains("v-caption")) {
    // childCaptionElementHeight.put(el, getLayoutManager()
    // .getOuterHeight(captionElement));
    // }
    // }
    // // System.out.println("  ###  Child sizes: "
    // // + childElementHeight.values().toString());
    // // System.out.println("  ###  Caption sizes: "
    // // + childCaptionElementHeight.values().toString());
    //
    // int height = getMaxHeight()
    // + getLayoutManager().getBorderHeight(
    // getWidget().getElement())
    // + getLayoutManager().getPaddingHeight(
    // getWidget().getElement());
    // getWidget().getElement().getStyle().setHeight(height, Unit.PX);
    // }
    // }

    // private ElementResizeListener layoutResizeListener = new
    // ElementResizeListener() {
    // public void onElementResize(ElementResizeEvent e) {
    // updateLayoutHeight();
    // if (needsExpand() && (isUndefinedHeight() || isUndefinedWidth())) {
    // updateExpand();
    // }
    // }
    // };

    private ElementResizeListener slotCaptionResizeListener = new ElementResizeListener() {
        public void onElementResize(ElementResizeEvent e) {

            // Get all needed element references
            Element captionElement = (Element) e.getElement().cast();

            // Caption position determines if the widget element is the first or
            // last child inside the caption wrap
            CaptionPosition pos = getWidget().getCaptionPositionFromElement(
                    (Element) captionElement.getParentElement().cast());

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
                dontListen(captionElement, slotCaptionResizeListener);
                childCaptionElementHeight.remove(widgetElement);
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

            int h = getLayoutManager().getOuterHeight(captionElement)
                    - getLayoutManager().getMarginHeight(captionElement);
            childCaptionElementHeight.put(widgetElement, h);

            // if (needsFixedHeight()) {
            // getWidget().clearHeight();
            // getLayoutManager().setNeedsMeasure(
            // AbstractBoxLayoutConnector.this);
            // }

            updateLayoutHeight();

            if (needsExpand()) {
                updateExpand();
            }
        }
    };

    private ElementResizeListener childComponentResizeListener = new ElementResizeListener() {
        public void onElementResize(ElementResizeEvent e) {
            int h = getLayoutManager().getOuterHeight(e.getElement());
            childElementHeight.put((Element) e.getElement().cast(), h);
            updateLayoutHeight();

            if (needsExpand()) {
                updateExpand();
            }
        }
    };

    private ElementResizeListener spacingResizeListener = new ElementResizeListener() {
        public void onElementResize(ElementResizeEvent e) {
            if (needsExpand()) {
                updateExpand();
            }
        }
    };

    private void updateLayoutHeight() {
        if (needsFixedHeight() && childElementHeight.size() > 0) {
            int h = getMaxHeight();
            h += getLayoutManager().getBorderHeight(getWidget().getElement())
                    + getLayoutManager().getPaddingHeight(
                            getWidget().getElement());
            getWidget().getElement().getStyle().setHeight(h, Unit.PX);
            getLayoutManager().setNeedsMeasure(this);
        }
    }

    private void updateExpand() {
        // System.out.println("All sizes: "
        // + childElementHeight.values().toString() + " - Caption sizes: "
        // + childCaptionElementHeight.values().toString());
        getWidget().updateExpand();
    }

    private int getMaxHeight() {
        // TODO should use layout manager instead of inner lists of element
        // sizes
        int highestNonRelative = -1;
        int highestRelative = -1;
        // System.out.println("Child sizes: "
        // + childElementHeight.values().toString());
        for (Element el : childElementHeight.keySet()) {
            // TODO would be more efficient to measure the slot element if both
            // caption and child widget elements need to be measured. Keeping
            // track of what to measure is the most difficult part of this
            // layout.
            CaptionPosition pos = getWidget().getCaptionPositionFromElement(
                    (Element) el.getParentElement().cast());
            if (needsMeasure.contains(el)) {
                int h = childElementHeight.get(el);
                String sHeight = el.getStyle().getHeight();
                // Only add the caption size to the height of the slot if
                // coption position is top or bottom
                if (childCaptionElementHeight.containsKey(el)
                        && (sHeight == null || !sHeight.endsWith("%"))
                        && (pos == CaptionPosition.TOP || pos == CaptionPosition.BOTTOM)) {
                    h += childCaptionElementHeight.get(el);
                }
                if (h > highestNonRelative) {
                    highestNonRelative = h;
                }
            } else {
                int h = childElementHeight.get(el);
                if (childCaptionElementHeight.containsKey(el)
                        && (pos == CaptionPosition.TOP || pos == CaptionPosition.BOTTOM)) {
                    h += childCaptionElementHeight.get(el);
                }
                if (h > highestRelative) {
                    highestRelative = h;
                }
            }
        }
        return highestNonRelative > -1 ? highestNonRelative : highestRelative;
    }

    @Override
    public void onUnregister() {
        // Cleanup all ElementResizeListeners

        // dontListen(getWidget().getElement(), layoutResizeListener);

        for (ComponentConnector child : getChildren()) {
            Slot slot = getWidget().getSlot(child);
            if (slot.hasCaption()) {
                dontListen(slot.getCaptionElement(), slotCaptionResizeListener);
            }

            if (slot.getSpacingElement() != null) {
                dontListen(slot.getSpacingElement(), spacingResizeListener);
            }

            dontListen(slot.getWidget().getElement(),
                    childComponentResizeListener);
        }

        super.onUnregister();
    }

    // private void setLayoutHeightListener(boolean add) {
    // if (add) {
    // listen(getWidget().getElement(), layoutResizeListener);
    // } else {
    // dontListen(getWidget().getElement(), layoutResizeListener);
    // if (!needsExpand()) {
    // System.out.println("Clearing element sizes");
    // childElementHeight.clear();
    // childCaptionElementHeight.clear();
    // }
    // }
    // }

    /*
     * Convenience methods
     */

    private void listen(Element el, ElementResizeListener listener) {
        getLayoutManager().addElementResizeListener(el, listener);
    }

    private void dontListen(Element el, ElementResizeListener listener) {
        getLayoutManager().removeElementResizeListener(el, listener);
    }

}
