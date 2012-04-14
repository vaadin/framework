package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ValueMap;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.terminal.gwt.client.ui.VBoxLayout.Slot;
import com.vaadin.terminal.gwt.client.ui.layout.ElementResizeEvent;
import com.vaadin.terminal.gwt.client.ui.layout.ElementResizeListener;
import com.vaadin.terminal.gwt.client.ui.orderedlayout.AbstractOrderedLayoutServerRPC;
import com.vaadin.terminal.gwt.client.ui.orderedlayout.AbstractOrderedLayoutState;

public abstract class AbstractBoxLayoutConnector extends
        AbstractLayoutConnector implements Paintable, PreLayoutListener,
        PostLayoutListener {

    AbstractOrderedLayoutServerRPC rpc;

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return Util.getConnectorForElement(getConnection(), getWidget(),
                    element);
        }

        @Override
        protected LayoutClickRPC getLayoutClickRPC() {
            return rpc;
        };

    };

    @Override
    public void init() {
        rpc = RpcProxy.create(AbstractOrderedLayoutServerRPC.class, this);
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

    // For debugging
    private int resizeCount = 0;

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }

        clickEventHandler.handleEventHandlerRegistration();

        VBoxLayout layout = getWidget();

        ValueMap expandRatios = uidl.getMapAttribute("expandRatios");
        ValueMap alignments = uidl.getMapAttribute("alignments");

        for (ComponentConnector child : getChildren()) {
            Slot slot = layout.getSlot(child.getWidget());
            String pid = child.getConnectorId();

            AlignmentInfo alignment;
            if (alignments.containsKey(pid)) {
                alignment = new AlignmentInfo(alignments.getInt(pid));
                if (alignment.isVerticalCenter() || alignment.isBottom()) {
                    hasVerticalAlignment.add(child);
                } else {
                    hasVerticalAlignment.remove(child);
                }
            } else {
                alignment = AlignmentInfo.TOP_LEFT;
                hasVerticalAlignment.remove(child);
            }
            slot.setAlignment(alignment);

            double expandRatio;
            if (expandRatios.containsKey(pid)
                    && expandRatios.getRawNumber(pid) > 0) {
                expandRatio = expandRatios.getRawNumber(pid);
                hasExpandRatio.add(child);
            } else {
                expandRatio = -1;
                hasExpandRatio.remove(child);
                getLayoutManager().addElementResizeListener(
                        child.getWidget().getElement(),
                        childComponentResizeListener);
                if (slot.hasCaption()) {
                    getLayoutManager()
                            .addElementResizeListener(slot.getCaptionElement(),
                                    slotCaptionResizeListener);
                }
            }
            slot.setExpandRatio(expandRatio);

        }

        if (needsExpand()) {
            updateExpand();
        } else {
            getWidget().clearExpand();
        }

    }

    public void updateCaption(ComponentConnector child) {
        Slot slot = getWidget().getSlot(child.getWidget());

        String caption = child.getState().getCaption();
        String iconUrl = child.getState().getIcon() != null ? child.getState()
                .getIcon().getURL() : null;
        List<String> styles = child.getState().getStyles();
        String error = child.getState().getErrorMessage();
        // TODO Description is handled from somewhere else?

        slot.setCaption(caption, iconUrl, styles, error);

        slot.setRelativeWidth(child.isRelativeWidth());
        slot.setRelativeHeight(child.isRelativeHeight());

        // TODO Should also check captionposition: && captionPosition==TOP ||
        // captionPosition==BOTTOM
        if (slot.hasCaption()) {
            getLayoutManager().addElementResizeListener(
                    slot.getCaptionElement(), slotCaptionResizeListener);
        } else {
            getLayoutManager().removeElementResizeListener(
                    slot.getCaptionElement(), slotCaptionResizeListener);
        }

        if (!slot.hasCaption()) {
            childCaptionElementHeight.remove(child.getWidget().getElement());
        }
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);

        List<ComponentConnector> previousChildren = event.getOldChildren();
        int currentIndex = 0;
        VBoxLayout layout = getWidget();

        for (ComponentConnector child : getChildren()) {
            Widget childWidget = child.getWidget();
            Slot slot = layout.getSlot(childWidget);
            if (slot.getParent() != layout) {
                child.addStateChangeHandler(childStateChangeHandler);
            }
            layout.addOrMoveSlot(slot, currentIndex++);
        }

        for (ComponentConnector child : previousChildren) {
            if (child.getParent() != this) {
                Slot slot = layout.getSlot(child.getWidget());
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
                layout.removeSlot(child.getWidget());
            }
        }

        if (needsExpand()) {
            getWidget().updateExpand();
        } else {
            getWidget().clearExpand();
        }

    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().setMargin(new VMarginInfo(getState().getMarginsBitmask()));
        getWidget().setSpacing(getState().isSpacing());

        if (needsFixedHeight()) {
            setLayoutHeightListener(true);
        } else {
            setLayoutHeightListener(false);
        }

    }

    StateChangeHandler childStateChangeHandler = new StateChangeHandler() {
        public void onStateChanged(StateChangeEvent stateChangeEvent) {
            ComponentConnector child = (ComponentConnector) stateChangeEvent
                    .getConnector();

            // We need to update the slot size if the component size is changed
            // to relative
            Slot slot = getWidget().getSlot(child.getWidget());
            slot.setRelativeWidth(child.isRelativeWidth());
            slot.setRelativeHeight(child.isRelativeHeight());
            if (slot.hasCaption()) {
                getWidget().updateCaptionOffset(slot.getCaptionElement());
            }

            // If the slot has caption, we need to listen for it's size changes
            // in order to update the padding/margin offset for relative sized
            // components
            if ((child.isRelativeHeight() || needsFixedHeight())
                    && slot.hasCaption()) {
                getLayoutManager().addElementResizeListener(
                        slot.getCaptionElement(), slotCaptionResizeListener);
            } else if (!needsExpand()) {
                getLayoutManager().removeElementResizeListener(
                        slot.getCaptionElement(), slotCaptionResizeListener);
            }

            if (child.isRelativeHeight()) {
                hasRelativeHeight.add(child);
                needsMeasure.remove(child.getWidget().getElement());
                // childElementHeight.remove(child.getWidget().getElement());
            } else {
                hasRelativeHeight.remove(child);
                needsMeasure.add(child.getWidget().getElement());
            }

            if (needsFixedHeight()) {
                getLayoutManager().addElementResizeListener(
                        child.getWidget().getElement(),
                        childComponentResizeListener);
            } else if (!needsExpand()) {
                getLayoutManager().removeElementResizeListener(
                        child.getWidget().getElement(),
                        childComponentResizeListener);
            }

            if (needsFixedHeight()) {
                setLayoutHeightListener(true);
            } else {
                setLayoutHeightListener(false);
            }

        }
    };

    private boolean needsFixedHeight() {
        if (!getWidget().vertical
                && isUndefinedHeight()
                && (hasRelativeHeight.size() > 0 || hasVerticalAlignment.size() > 0)) {
            return true;
        }
        return false;
    }

    private boolean needsExpand() {
        return hasExpandRatio.size() > 0;
    }

    public void preLayout() {
        resizeCount = 0;
        if (needsFixedHeight()) {
            getWidget().clearHeight();
            getLayoutManager().setNeedsMeasure(this);
        }
    }

    public void postLayout() {
        if (needsFixedHeight()) {
            // Re-measure all elements that are available
            for (Element el : needsMeasure) {
                childElementHeight.put(el, getLayoutManager()
                        .getOuterHeight(el));

                // Element captionElement = el.getParentElement()
                // .getFirstChildElement().cast();
                // if (captionElement.getClassName().contains("v-caption")) {
                // childCaptionElementHeight.put(el, getLayoutManager()
                // .getOuterHeight(captionElement));
                // }
            }

            String h = getWidget().getElement().getStyle().getHeight();
            if (h == null || h.equals("")) {
                int height = getLayoutManager().getOuterHeight(
                        getWidget().getElement())
                        - getLayoutManager().getMarginHeight(
                                getWidget().getElement());
                // int height = getMaxHeight();
                getWidget().getElement().getStyle().setHeight(height, Unit.PX);
            }
        }
        // System.err.println("Element resize listeners fired for " +
        // resizeCount
        // + " times");
    }

    private ElementResizeListener layoutResizeListener = new ElementResizeListener() {
        public void onElementResize(ElementResizeEvent e) {
            resizeCount++;
            updateLayoutHeight();
            if (needsExpand()) {
                updateExpand();
            }
        }
    };

    private ElementResizeListener slotCaptionResizeListener = new ElementResizeListener() {
        public void onElementResize(ElementResizeEvent e) {
            resizeCount++;

            Element captionElement = (Element) e.getElement().cast();

            // TODO only apply to widgets with relative size
            getWidget().updateCaptionOffset(captionElement);

            // TODO take caption position into account
            Element widgetElement = captionElement.getParentElement()
                    .getLastChild().cast();

            if (captionElement == widgetElement) {
                // Caption element already detached
                return;
            }

            String widgetHeight = widgetElement.getStyle().getHeight();
            // if (widgetHeight == null || !widgetHeight.endsWith("%")) {
            int h = getLayoutManager().getOuterHeight(captionElement)
                    - getLayoutManager().getMarginHeight(captionElement);
            // System.out.println("Adding caption height: " + h);
            childCaptionElementHeight.put(widgetElement, h);
            // }

            updateLayoutHeight();

            if (needsExpand()) {
                updateExpand();
            }
        }
    };

    private ElementResizeListener childComponentResizeListener = new ElementResizeListener() {
        public void onElementResize(ElementResizeEvent e) {
            resizeCount++;
            int h = getLayoutManager().getOuterHeight(e.getElement());
            childElementHeight.put((Element) e.getElement().cast(), h);
            updateLayoutHeight();

            if (needsExpand()) {
                updateExpand();
            }
        }
    };

    private void updateLayoutHeight() {
        if (needsFixedHeight() && childElementHeight.size() > 0) {
            int h = getMaxHeight();
            System.out.println("Max height: " + h);
            h += getLayoutManager().getBorderHeight(getWidget().getElement())
                    + getLayoutManager().getPaddingHeight(
                            getWidget().getElement());
            getWidget().getElement().getStyle().setHeight(h, Unit.PX);
            getLayoutManager().setNeedsMeasure(this);
        }
    }

    private void updateExpand() {
        System.out.println("All sizes: "
                + childElementHeight.values().toString() + " - Caption sizes: "
                + childCaptionElementHeight.values().toString());
        getWidget().updateExpand();
    }

    private int getMaxHeight() {
        int highestNonRelative = -1;
        int highestRelative = -1;
        for (Element el : childElementHeight.keySet()) {
            if (needsMeasure.contains(el)) {
                int h = childElementHeight.get(el);
                String sHeight = el.getStyle().getHeight();
                if (childCaptionElementHeight.containsKey(el)
                        && (sHeight == null || !sHeight.endsWith("%"))) {
                    h += childCaptionElementHeight.get(el);
                }
                if (h > highestNonRelative) {
                    highestNonRelative = h;
                }
            } else {
                int h = childElementHeight.get(el);
                if (childCaptionElementHeight.containsKey(el)) {
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

        getLayoutManager().removeElementResizeListener(
                getWidget().getElement(), layoutResizeListener);

        for (int i = 0; i < getWidget().getWidgetCount(); i++) {
            // TODO unsafe
            Slot slot = (Slot) getWidget().getWidget(i);

            if (slot.hasCaption()) {
                getLayoutManager().removeElementResizeListener(
                        slot.getCaptionElement(), slotCaptionResizeListener);
            }

            getLayoutManager()
                    .removeElementResizeListener(slot.getWidget().getElement(),
                            childComponentResizeListener);

        }

        super.onUnregister();
    }

    private void setLayoutHeightListener(boolean add) {
        if (add) {
            getLayoutManager().addElementResizeListener(
                    getWidget().getElement(), layoutResizeListener);
        } else {
            getLayoutManager().removeElementResizeListener(
                    getWidget().getElement(), layoutResizeListener);
            if (!needsExpand()) {
                childElementHeight.clear();
                childCaptionElementHeight.clear();
            }
        }
    }

}
