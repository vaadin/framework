/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.UIDL;

public abstract class AbstractSplitPanelConnector extends
        AbstractComponentContainerConnector implements SimpleManagedLayout {

    public static final String SPLITTER_CLICK_EVENT_IDENTIFIER = "sp_click";

    public void updateCaption(ComponentConnector component, UIDL uidl) {
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
        getWidget().client = client;
        getWidget().id = uidl.getId();

        getWidget().immediate = getState().isImmediate();

        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
        getWidget().setEnabled(!getState().isDisabled());

        clickEventHandler.handleEventHandlerRegistration(client);
        if (getState().hasStyles()) {
            getWidget().componentStyleNames = getState().getStyle().split(" ");
        } else {
            getWidget().componentStyleNames = new String[0];
        }

        getWidget().setLocked(uidl.getBooleanAttribute("locked"));

        getWidget().setPositionReversed(uidl.getBooleanAttribute("reversed"));

        getWidget().setStylenames();

        getWidget().position = uidl.getStringAttribute("position");

        final ComponentConnector newFirstChildPaintable = client
                .getPaintable(uidl.getChildUIDL(0));
        final ComponentConnector newSecondChildPaintable = client
                .getPaintable(uidl.getChildUIDL(1));
        Widget newFirstChild = newFirstChildPaintable.getWidget();
        Widget newSecondChild = newSecondChildPaintable.getWidget();

        if (getWidget().firstChild != newFirstChild) {
            if (getWidget().firstChild != null) {
                client.unregisterPaintable(ConnectorMap.get(client)
                        .getConnector(getWidget().firstChild));
            }
            getWidget().setFirstWidget(newFirstChild);
        }
        if (getWidget().secondChild != newSecondChild) {
            if (getWidget().secondChild != null) {
                client.unregisterPaintable(ConnectorMap.get(client)
                        .getConnector(getWidget().secondChild));
            }
            getWidget().setSecondWidget(newSecondChild);
        }
        newFirstChildPaintable.updateFromUIDL(uidl.getChildUIDL(0), client);
        newSecondChildPaintable.updateFromUIDL(uidl.getChildUIDL(1), client);

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

}
