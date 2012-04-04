/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.ui.CssLayout;

@Component(CssLayout.class)
public class CssLayoutConnector extends AbstractComponentContainerConnector {

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return getWidget().panel.getComponent(element);
        }

        @Override
        protected LayoutClickRPC getLayoutClickRPC() {
            return rpc;
        };
    };

    public interface CssLayoutServerRPC extends LayoutClickRPC, ServerRpc {

    }

    private CssLayoutServerRPC rpc = GWT.create(CssLayoutServerRPC.class);

    @Override
    protected void init() {
        super.init();
        initRPC(rpc);
    }

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
