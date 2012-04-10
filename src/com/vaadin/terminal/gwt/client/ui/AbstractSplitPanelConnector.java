/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.LinkedList;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.VAbstractSplitPanel.SplitterMoveHandler;
import com.vaadin.terminal.gwt.client.ui.VAbstractSplitPanel.SplitterMoveHandler.SplitterMoveEvent;

public abstract class AbstractSplitPanelConnector extends
        AbstractComponentContainerConnector implements SimpleManagedLayout {

    public interface AbstractSplitPanelRPC extends ServerRpc {

        /**
         * Called when the position has been updated by the user.
         * 
         * @param position
         *            The new position in % if the current unit is %, in px
         *            otherwise
         */
        public void setSplitterPosition(float position);

        /**
         * Called when a click event has occurred on the splitter.
         * 
         * @param mouseDetails
         *            Details about the mouse when the event took place
         */
        public void splitterClick(MouseEventDetails mouseDetails);

    }

    public static class SplitterState {
        private float position;
        private String positionUnit;
        private boolean positionReversed = false;
        private boolean locked = false;

        public float getPosition() {
            return position;
        }

        public void setPosition(float position) {
            this.position = position;
        }

        public String getPositionUnit() {
            return positionUnit;
        }

        public void setPositionUnit(String positionUnit) {
            this.positionUnit = positionUnit;
        }

        public boolean isPositionReversed() {
            return positionReversed;
        }

        public void setPositionReversed(boolean positionReversed) {
            this.positionReversed = positionReversed;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

    }

    public static class AbstractSplitPanelState extends ComponentState {
        private Connector firstChild = null;
        private Connector secondChild = null;
        private SplitterState splitterState = new SplitterState();

        public boolean hasFirstChild() {
            return firstChild != null;
        }

        public boolean hasSecondChild() {
            return secondChild != null;
        }

        public Connector getFirstChild() {
            return firstChild;
        }

        public void setFirstChild(Connector firstChild) {
            this.firstChild = firstChild;
        }

        public Connector getSecondChild() {
            return secondChild;
        }

        public void setSecondChild(Connector secondChild) {
            this.secondChild = secondChild;
        }

        public SplitterState getSplitterState() {
            return splitterState;
        }

        public void setSplitterState(SplitterState splitterState) {
            this.splitterState = splitterState;
        }

    }

    private AbstractSplitPanelRPC rpc;

    @Override
    protected void init() {
        super.init();
        rpc = RpcProxy.create(AbstractSplitPanelRPC.class, this);
        // TODO Remove
        getWidget().client = getConnection();

        getWidget().addHandler(new SplitterMoveHandler() {

            public void splitterMoved(SplitterMoveEvent event) {
                String position = getWidget().getSplitterPosition();
                float pos = 0;
                if (position.indexOf("%") > 0) {
                    // Send % values as a fraction to avoid that the splitter
                    // "jumps" when server responds with the integer pct value
                    // (e.g. dragged 16.6% -> should not jump to 17%)
                    pos = Float.valueOf(position.substring(0,
                            position.length() - 1));
                } else {
                    pos = Integer.parseInt(position.substring(0,
                            position.length() - 2));
                }

                rpc.setSplitterPosition(pos);
            }

        }, SplitterMoveEvent.TYPE);
    }

    public void updateCaption(ComponentConnector component) {
        // TODO Implement caption handling
    }

    ClickEventHandler clickEventHandler = new ClickEventHandler(this) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            if ((Event.getEventsSunk(getWidget().splitter) & Event
                    .getTypeInt(type.getName())) != 0) {
                // If we are already sinking the event for the splitter we do
                // not want to additionally sink it for the root element
                return getWidget().addHandler(handler, type);
            } else {
                return getWidget().addDomHandler(handler, type);
            }
        }

        @Override
        protected boolean shouldFireEvent(DomEvent<?> event) {
            Element target = event.getNativeEvent().getEventTarget().cast();
            if (!getWidget().splitter.isOrHasChild(target)) {
                return false;
            }

            return super.shouldFireEvent(event);
        };

        @Override
        protected Element getRelativeToElement() {
            return getWidget().splitter;
        };

        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            rpc.splitterClick(mouseDetails);
        }

    };

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().immediate = getState().isImmediate();

        getWidget().setEnabled(isEnabled());

        clickEventHandler.handleEventHandlerRegistration();

        if (getState().hasStyles()) {
            getWidget().componentStyleNames = getState().getStyles();
        } else {
            getWidget().componentStyleNames = new LinkedList<String>();
        }

        // Splitter updates
        SplitterState splitterState = getState().getSplitterState();

        getWidget().setLocked(splitterState.isLocked());
        getWidget().setPositionReversed(splitterState.isPositionReversed());

        getWidget().setStylenames();

        getWidget().position = splitterState.getPosition()
                + splitterState.getPositionUnit();

        // This is needed at least for cases like #3458 to take
        // appearing/disappearing scrollbars into account.
        getConnection().runDescendentsLayout(getWidget());

        getLayoutManager().setNeedsUpdate(this);

    }

    public void layout() {
        VAbstractSplitPanel splitPanel = getWidget();
        splitPanel.setSplitPosition(splitPanel.position);
        splitPanel.updateSizes();
    }

    @Override
    public VAbstractSplitPanel getWidget() {
        return (VAbstractSplitPanel) super.getWidget();
    }

    @Override
    protected abstract VAbstractSplitPanel createWidget();

    @Override
    public AbstractSplitPanelState getState() {
        return (AbstractSplitPanelState) super.getState();
    }

    private ComponentConnector getFirstChild() {
        return (ComponentConnector) getState().getFirstChild();
    }

    private ComponentConnector getSecondChild() {
        return (ComponentConnector) getState().getSecondChild();
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);

        Widget newFirstChildWidget = null;
        if (getFirstChild() != null) {
            newFirstChildWidget = getFirstChild().getWidget();
        }
        getWidget().setFirstWidget(newFirstChildWidget);

        Widget newSecondChildWidget = null;
        if (getSecondChild() != null) {
            newSecondChildWidget = getSecondChild().getWidget();
        }
        getWidget().setSecondWidget(newSecondChildWidget);
    }
}
