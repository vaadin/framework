package com.vaadin.terminal.gwt.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
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
import com.vaadin.terminal.gwt.client.ui.AbstractOrderedLayoutConnector.AbstractOrderedLayoutServerRPC;
import com.vaadin.terminal.gwt.client.ui.AbstractOrderedLayoutConnector.AbstractOrderedLayoutState;
import com.vaadin.terminal.gwt.client.ui.VBoxLayout.Slot;
import com.vaadin.terminal.gwt.client.ui.layout.ElementResizeEvent;
import com.vaadin.terminal.gwt.client.ui.layout.ElementResizeListener;

public abstract class AbstractBoxLayoutConnector extends
        AbstractLayoutConnector implements Paintable, PreLayoutListener {

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
            } else {
                alignment = AlignmentInfo.TOP_LEFT;
            }
            slot.setAlignment(alignment);

            double expandRatio;
            if (expandRatios.containsKey(pid)
                    && expandRatios.getRawNumber(pid) > 0) {
                expandRatio = expandRatios.getRawNumber(pid);
            } else {
                expandRatio = -1;
            }
            slot.setExpandRatio(expandRatio);

        }

        layout.setMargin(new VMarginInfo(getState().getMarginsBitmask()));
        layout.setSpacing(getState().isSpacing());
        // TODO add/remove spacing size listener

        // TODO
        getWidget().recalculateExpands();
        getWidget().recalculateUsedSpace();
    }

    public void updateCaption(ComponentConnector connector) {
        Slot slot = getWidget().getSlot(connector.getWidget());

        String caption = connector.getState().getCaption();
        String iconUrl = connector.getState().getIcon() != null ? connector
                .getState().getIcon().getURL() : null;
        List<String> styles = connector.getState().getStyles();
        String error = connector.getState().getErrorMessage();
        // TODO Description is handled from somewhere else?

        slot.setCaption(caption, iconUrl, styles, error);

        slot.setRelativeWidth(connector.isRelativeWidth());
        slot.setRelativeHeight(connector.isRelativeHeight());

        // Should also check captionposition: && captionPosition==TOP ||
        // captionPosition==BOTTOM
        if (connector.isRelativeHeight() && slot.hasCaption()) {
            getLayoutManager().addElementResizeListener(
                    slot.getCaptionElement(), slotCaptionResizeListener);
        } else {
            getLayoutManager().removeElementResizeListener(
                    slot.getCaptionElement(), slotCaptionResizeListener);
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
                getLayoutManager().addElementResizeListener(
                        slot.getWidget().getElement(),
                        childComponentResizeListener);
                child.addStateChangeHandler(childStateChangeHandler);
            }
            layout.addOrMoveSlot(slot, currentIndex++);
        }

        for (ComponentConnector child : previousChildren) {
            if (child.getParent() != this) {
                Slot removed = layout.removeSlot(child.getWidget());
                getLayoutManager().removeElementResizeListener(
                        removed.getWidget().getElement(),
                        childComponentResizeListener);
                // child.removeStateChangeHandler(this);
            }
        }

        updateLayoutHeight();

        getWidget().recalculateUsedSpace();
    }

    private boolean layoutHeightListenerAdded = false;

    private void updateLayoutHeight() {
        if (!getWidget().vertical && isUndefinedHeight()) {
            if (!layoutHeightListenerAdded) {
                getLayoutManager().addElementResizeListener(
                        getWidget().getElement(), layoutHeightResizeListener);
                layoutHeightListenerAdded = true;
            }
        } else if (getWidget().vertical || !isUndefinedHeight()) {
            getLayoutManager().removeElementResizeListener(
                    getWidget().getElement(), layoutHeightResizeListener);
            layoutHeightListenerAdded = false;
        }
        getWidget().recalculateLayoutHeight();
    }

    StateChangeHandler childStateChangeHandler = new StateChangeHandler() {
        public void onStateChanged(StateChangeEvent stateChangeEvent) {
            ComponentConnector child = (ComponentConnector) stateChangeEvent
                    .getConnector();
            // TODO handle captions here as well, once 'updateCaption' is
            // removed

            // We need to update the slot size if the component size is changed
            // to relative
            Slot slot = getWidget().getSlot(child.getWidget());

            slot.setRelativeWidth(child.isRelativeWidth());
            slot.setRelativeHeight(child.isRelativeHeight());

            if (child.isRelativeHeight() && slot.hasCaption()) {
                getLayoutManager().addElementResizeListener(
                        slot.getCaptionElement(), slotCaptionResizeListener);
            } else {
                getLayoutManager().removeElementResizeListener(
                        slot.getCaptionElement(), slotCaptionResizeListener);
            }

            // TODO should copy component styles to the slot element as well,
            // with a prefix

            if (!getWidget().vertical && isUndefinedHeight()) {
                getWidget().getElement().getStyle().clearHeight();
                getLayoutManager().setNeedsMeasure(
                        AbstractBoxLayoutConnector.this);
                getLayoutManager().layoutNow();
            }

            updateLayoutHeight();
        }
    };

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        getWidget().setMargin(new VMarginInfo(getState().getMarginsBitmask()));
        getWidget().setSpacing(getState().isSpacing());
        updateLayoutHeight();
    }

    @Override
    public void onUnregister() {
        getLayoutManager().removeElementResizeListener(
                getWidget().getElement(), layoutHeightResizeListener);

        for (int i = 0; i < getWidget().getWidgetCount(); i++) {
            Slot slot = (Slot) getWidget().getWidget(i);

            getLayoutManager().removeElementResizeListener(
                    slot.getCaptionElement(), slotCaptionResizeListener);

            getLayoutManager()
                    .removeElementResizeListener(slot.getWidget().getElement(),
                            childComponentResizeListener);
        }

        super.onUnregister();
    }

    public void preLayout() {
        if (!getWidget().vertical && isUndefinedHeight()) {
            getWidget().getElement().getStyle().clearHeight();
        }
    }

    ElementResizeListener layoutHeightResizeListener = new ElementResizeListener() {
        public void onElementResize(ElementResizeEvent e) {
            getWidget().recalculateLayoutHeight();
        }
    };

    ElementResizeListener slotCaptionResizeListener = new ElementResizeListener() {
        public void onElementResize(ElementResizeEvent e) {
            getWidget().updateSize((Element) e.getElement().cast());
        }
    };

    private ElementResizeListener childComponentResizeListener = new ElementResizeListener() {
        public void onElementResize(ElementResizeEvent e) {
            updateLayoutHeight();
            getWidget().recalculateUsedSpace();
            getWidget().recalculateLayoutHeight();
        }
    };
}
