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
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public abstract class VAbstractSplitPanelPaintable extends
        VAbstractPaintableWidgetContainer {

    public static final String SPLITTER_CLICK_EVENT_IDENTIFIER = "sp_click";

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        // TODO Implement caption handling
    }

    ClickEventHandler clickEventHandler = new ClickEventHandler(this,
            SPLITTER_CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            if ((Event.getEventsSunk(getWidgetForPaintable().splitter) & Event
                    .getTypeInt(type.getName())) != 0) {
                // If we are already sinking the event for the splitter we do
                // not want to additionally sink it for the root element
                return getWidgetForPaintable().addHandler(handler, type);
            } else {
                return getWidgetForPaintable().addDomHandler(handler, type);
            }
        }

        @Override
        public void onContextMenu(
                com.google.gwt.event.dom.client.ContextMenuEvent event) {
            Element target = event.getNativeEvent().getEventTarget().cast();
            if (getWidgetForPaintable().splitter.isOrHasChild(target)) {
                super.onContextMenu(event);
            }
        };

        @Override
        protected void fireClick(NativeEvent event) {
            Element target = event.getEventTarget().cast();
            if (getWidgetForPaintable().splitter.isOrHasChild(target)) {
                super.fireClick(event);
            }
        }

        @Override
        protected Element getRelativeToElement() {
            return null;
        }

    };

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().client = client;
        getWidgetForPaintable().id = uidl.getId();
        getWidgetForPaintable().rendering = true;

        getWidgetForPaintable().immediate = uidl.hasAttribute("immediate");

        if (client.updateComponent(this, uidl, true)) {
            getWidgetForPaintable().rendering = false;
            return;
        }
        getWidgetForPaintable().setEnabled(
                !uidl.getBooleanAttribute("disabled"));

        clickEventHandler.handleEventHandlerRegistration(client);
        if (uidl.hasAttribute("style")) {
            getWidgetForPaintable().componentStyleNames = uidl
                    .getStringAttribute("style").split(" ");
        } else {
            getWidgetForPaintable().componentStyleNames = new String[0];
        }

        getWidgetForPaintable().setLocked(uidl.getBooleanAttribute("locked"));

        getWidgetForPaintable().setPositionReversed(
                uidl.getBooleanAttribute("reversed"));

        getWidgetForPaintable().setStylenames();

        getWidgetForPaintable().position = uidl.getStringAttribute("position");
        getWidgetForPaintable().setSplitPosition(
                getWidgetForPaintable().position);

        final VPaintableWidget newFirstChildPaintable = client
                .getPaintable(uidl.getChildUIDL(0));
        final VPaintableWidget newSecondChildPaintable = client
                .getPaintable(uidl.getChildUIDL(1));
        Widget newFirstChild = newFirstChildPaintable.getWidgetForPaintable();
        Widget newSecondChild = newSecondChildPaintable.getWidgetForPaintable();

        if (getWidgetForPaintable().firstChild != newFirstChild) {
            if (getWidgetForPaintable().firstChild != null) {
                client.unregisterPaintable(VPaintableMap.get(client)
                        .getPaintable(getWidgetForPaintable().firstChild));
            }
            getWidgetForPaintable().setFirstWidget(newFirstChild);
        }
        if (getWidgetForPaintable().secondChild != newSecondChild) {
            if (getWidgetForPaintable().secondChild != null) {
                client.unregisterPaintable(VPaintableMap.get(client)
                        .getPaintable(getWidgetForPaintable().secondChild));
            }
            getWidgetForPaintable().setSecondWidget(newSecondChild);
        }
        newFirstChildPaintable.updateFromUIDL(uidl.getChildUIDL(0), client);
        newSecondChildPaintable.updateFromUIDL(uidl.getChildUIDL(1), client);

        getWidgetForPaintable().renderInformation
                .updateSize(getWidgetForPaintable().getElement());

        // This is needed at least for cases like #3458 to take
        // appearing/disappearing scrollbars into account.
        client.runDescendentsLayout(getWidgetForPaintable());

        getWidgetForPaintable().rendering = false;

    }

    @Override
    public VAbstractSplitPanel getWidgetForPaintable() {
        return (VAbstractSplitPanel) super.getWidgetForPaintable();
    }

    @Override
    protected abstract VAbstractSplitPanel createWidget();

}
