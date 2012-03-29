/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangedEvent;
import com.vaadin.terminal.gwt.client.UIDL;

public abstract class AbstractSplitPanelConnector extends
        AbstractComponentContainerConnector implements SimpleManagedLayout {

    public static class SplitterState {
        private int position;
        private String positionUnit;
        private boolean positionReversed;
        private boolean locked;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
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

    public static final String SPLITTER_CLICK_EVENT_IDENTIFIER = "sp_click";

    @Override
    protected void init() {
        super.init();
        // TODO Remove
        getWidget().client = getConnection();
        getWidget().id = getConnectorId();
    }

    public void updateCaption(ComponentConnector component) {
        // TODO Implement caption handling
    }

    ClickEventHandler clickEventHandler = new ClickEventHandler(this,
            SPLITTER_CLICK_EVENT_IDENTIFIER) {

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
        public void onContextMenu(
                com.google.gwt.event.dom.client.ContextMenuEvent event) {
            Element target = event.getNativeEvent().getEventTarget().cast();
            if (getWidget().splitter.isOrHasChild(target)) {
                super.onContextMenu(event);
            }
        };

        @Override
        protected void fireClick(NativeEvent event) {
            Element target = event.getEventTarget().cast();
            if (getWidget().splitter.isOrHasChild(target)) {
                super.fireClick(event);
            }
        }

        @Override
        protected Element getRelativeToElement() {
            return null;
        }

    };

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().immediate = getState().isImmediate();

        // TODO Should be handled by framework
        getWidget().setEnabled(isEnabled());

        clickEventHandler.handleEventHandlerRegistration();

        // TODO Move to stateChangeListener
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
        client.runDescendentsLayout(getWidget());

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

    @Override
    protected AbstractSplitPanelState createState() {
        return GWT.create(AbstractSplitPanelState.class);
    }

    private ComponentConnector getFirstChild() {
        return (ComponentConnector) getState().getFirstChild();
    }

    private ComponentConnector getSecondChild() {
        return (ComponentConnector) getState().getSecondChild();
    }

    @Override
    public void connectorHierarchyChanged(ConnectorHierarchyChangedEvent event) {
        super.connectorHierarchyChanged(event);

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
