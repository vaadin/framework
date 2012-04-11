/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.nativebutton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.EventHelper;
import com.vaadin.terminal.gwt.client.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.Icon;
import com.vaadin.terminal.gwt.client.ui.button.ButtonConnector.ButtonServerRpc;
import com.vaadin.terminal.gwt.client.ui.button.ButtonState;
import com.vaadin.ui.NativeButton;

@Component(NativeButton.class)
public class NativeButtonConnector extends AbstractComponentConnector implements
        BlurHandler, FocusHandler {

    private HandlerRegistration focusHandlerRegistration;
    private HandlerRegistration blurHandlerRegistration;

    private FocusAndBlurServerRpc focusBlurRpc = RpcProxy.create(
            FocusAndBlurServerRpc.class, this);

    @Override
    public void init() {
        super.init();

        getWidget().buttonRpcProxy = RpcProxy.create(ButtonServerRpc.class,
                this);
        getWidget().client = getConnection();
        getWidget().paintableId = getConnectorId();
    }

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().disableOnClick = getState().isDisableOnClick();
        focusHandlerRegistration = EventHelper.updateFocusHandler(this,
                focusHandlerRegistration);
        blurHandlerRegistration = EventHelper.updateBlurHandler(this,
                blurHandlerRegistration);

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
                getWidget().icon = new Icon(getConnection());
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

    public void onFocus(FocusEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        focusBlurRpc.focus();
    }

    public void onBlur(BlurEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        focusBlurRpc.blur();
    }

}
