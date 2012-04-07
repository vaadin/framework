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
import com.vaadin.terminal.gwt.client.communication.URLReference;
import com.vaadin.terminal.gwt.client.ui.AbstractOrderedLayoutConnector.AbstractOrderedLayoutServerRPC;
import com.vaadin.terminal.gwt.client.ui.AbstractOrderedLayoutConnector.AbstractOrderedLayoutState;
import com.vaadin.terminal.gwt.client.ui.VBoxLayout.Slot;
import com.vaadin.terminal.gwt.client.ui.layout.ElementResizeEvent;
import com.vaadin.terminal.gwt.client.ui.layout.ElementResizeListener;

public abstract class AbstractBoxLayoutConnector extends
        AbstractLayoutConnector implements Paintable, ElementResizeListener {

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

        getWidget().recalculateUsedSpace();
        getWidget().recalculateExpands();
    }

    public void updateCaption(ComponentConnector connector) {
        Slot slot = getWidget().getSlot(connector.getWidget());
        URLReference icon = connector.getState().getIcon();
        slot.setCaption(connector.getState().getCaption(),
                icon != null ? icon.getURL() : null, connector.getState()
                        .getStyles());
        // Description is handled from somewhere else?
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
                        slot.getWidget().getElement(), this);
            }
            layout.addOrMoveSlot(slot, currentIndex++);
            child.addStateChangeHandler(childStateChange);
        }

        for (ComponentConnector child : previousChildren) {
            if (child.getParent() != this) {
                Slot removed = layout.removeSlot(child.getWidget());
                getLayoutManager().removeElementResizeListener(
                        removed.getWidget().getElement(), this);
            }
        }
        getWidget().recalculateUsedSpace();
        getWidget().recalculateLayoutHeight();
    }

    StateChangeHandler childStateChange = new StateChangeHandler() {
        public void onStateChanged(StateChangeEvent stateChangeEvent) {
            ComponentConnector child = (ComponentConnector) stateChangeEvent
                    .getConnector();
            // TODO handle captions here as well, once 'updateCaption' is
            // removed

            // We need to update the slot size if the component size is changed
            // to relative
            Slot slot = getWidget().getSlot(child.getWidget());
            slot.updateSize();

            getWidget().recalculateLayoutHeight();
        }
    };

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        getWidget().setMargin(new VMarginInfo(getState().getMarginsBitmask()));
        getWidget().setSpacing(getState().isSpacing());
        getWidget().recalculateLayoutHeight();
    }

    @Override
    public void onUnregister() {
        for (int i = 0; i < getWidget().getWidgetCount(); i++) {
            Slot slot = (Slot) getWidget().getWidget(i);
            getLayoutManager().removeElementResizeListener(
                    slot.getWidget().getElement(), this);
        }
        super.onUnregister();
    }

    public void onElementResize(ElementResizeEvent e) {
        getWidget().recalculateUsedSpace();
        getWidget().recalculateLayoutHeight();
    }
}
