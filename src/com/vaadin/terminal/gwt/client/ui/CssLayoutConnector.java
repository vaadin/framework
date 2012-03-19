/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.UIDL;

public class CssLayoutConnector extends AbstractComponentContainerConnector {

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this, EventId.LAYOUT_CLICK) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return getWidget().panel.getComponent(element);
        }

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidget().addDomHandler(handler, type);
        }
    };

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
        clickEventHandler.handleEventHandlerRegistration();

        getWidget().setMarginAndSpacingStyles(
                new VMarginInfo(uidl.getIntAttribute("margins")),
                uidl.hasAttribute("spacing"));
        getWidget().panel.updateFromUIDL(uidl, client);
    }

    @Override
    public VCssLayout getWidget() {
        return (VCssLayout) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VCssLayout.class);
    }

    public void updateCaption(ComponentConnector component) {
        getWidget().panel.updateCaption(component);
    }

}
