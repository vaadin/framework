package com.vaadin.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ui.VAbsoluteLayout.AbsoluteWrapper;

public class VAbsoluteLayoutPaintable extends VAbstractPaintableWidgetContainer {

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this, EventId.LAYOUT_CLICK) {

        @Override
        protected VPaintableWidget getChildComponent(Element element) {
            return getWidgetForPaintable().getComponent(element);
        }

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidgetForPaintable().addDomHandler(handler, type);
        }
    };

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().rendering = true;
        getWidgetForPaintable().client = client;
        // TODO margin handling
        if (client.updateComponent(this, uidl, true)) {
            getWidgetForPaintable().rendering = false;
            return;
        }

        clickEventHandler.handleEventHandlerRegistration(client);

        HashSet<String> unrenderedPids = new HashSet<String>(
                getWidgetForPaintable().pidToComponentWrappper.keySet());

        for (Iterator<Object> childIterator = uidl.getChildIterator(); childIterator
                .hasNext();) {
            UIDL cc = (UIDL) childIterator.next();
            if (cc.getTag().equals("cc")) {
                UIDL componentUIDL = cc.getChildUIDL(0);
                unrenderedPids.remove(componentUIDL.getId());
                getWidgetForPaintable().getWrapper(client, componentUIDL)
                        .updateFromUIDL(cc);
            }
        }

        for (String pid : unrenderedPids) {
            AbsoluteWrapper absoluteWrapper = getWidgetForPaintable().pidToComponentWrappper
                    .get(pid);
            getWidgetForPaintable().pidToComponentWrappper.remove(pid);
            absoluteWrapper.destroy();
        }
        getWidgetForPaintable().rendering = false;
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        AbsoluteWrapper parent2 = (AbsoluteWrapper) (component
                .getWidgetForPaintable()).getParent();
        parent2.updateCaption(uidl);
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VAbsoluteLayout.class);
    }

    @Override
    public VAbsoluteLayout getWidgetForPaintable() {
        return (VAbsoluteLayout) super.getWidgetForPaintable();
    }
}
