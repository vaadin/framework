/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.EventHelper;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.ui.ButtonConnector.ButtonServerRpc;
import com.vaadin.ui.NativeButton;

@Component(NativeButton.class)
public class NativeButtonConnector extends AbstractComponentConnector {

    @Override
    public void init() {
        super.init();

        getWidget().buttonRpcProxy = RpcProxy.create(ButtonServerRpc.class,
                this);
    }

    @Override
    protected boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Ensure correct implementation,
        // but don't let container manage caption etc.
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().disableOnClick = getState().isDisableOnClick();
        getWidget().focusHandlerRegistration = EventHelper.updateFocusHandler(
                this, client, getWidget().focusHandlerRegistration);
        getWidget().blurHandlerRegistration = EventHelper.updateBlurHandler(
                this, client, getWidget().blurHandlerRegistration);

        // Save details
        getWidget().client = client;
        getWidget().paintableId = uidl.getId();

        // Set text
        getWidget().setText(getState().getCaption());

        // handle error
        if (null != getState().getErrorMessage()) {
            if (getWidget().errorIndicatorElement == null) {
                getWidget().errorIndicatorElement = DOM.createSpan();
                getWidget().errorIndicatorElement
                        .setClassName("v-errorindicator");
            }
            getWidget().getElement().insertBefore(
                    getWidget().errorIndicatorElement,
                    getWidget().captionElement);

        } else if (getWidget().errorIndicatorElement != null) {
            getWidget().getElement().removeChild(
                    getWidget().errorIndicatorElement);
            getWidget().errorIndicatorElement = null;
        }

        if (getState().getIcon() != null) {
            if (getWidget().icon == null) {
                getWidget().icon = new Icon(client);
                getWidget().getElement().insertBefore(
                        getWidget().icon.getElement(),
                        getWidget().captionElement);
            }
            getWidget().icon.setUri(getState().getIcon().getURL());
        } else {
            if (getWidget().icon != null) {
                getWidget().getElement().removeChild(
                        getWidget().icon.getElement());
                getWidget().icon = null;
            }
        }

    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VNativeButton.class);
    }

    @Override
    public VNativeButton getWidget() {
        return (VNativeButton) super.getWidget();
    }

    @Override
    public ButtonState getState() {
        return (ButtonState) super.getState();
    }

    @Override
    protected ComponentState createState() {
        return GWT.create(ButtonState.class);
    }
}
