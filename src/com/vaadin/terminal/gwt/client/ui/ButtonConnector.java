/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.EventHelper;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.Component.LoadStyle;
import com.vaadin.ui.Button;

@Component(value = Button.class, loadStyle = LoadStyle.EAGER)
public class ButtonConnector extends AbstractComponentConnector implements
        BlurHandler, FocusHandler {

    /**
     * RPC interface for calls from client to server.
     * 
     * @since 7.0
     */
    public interface ButtonServerRpc extends ServerRpc {
        /**
         * Button click event.
         * 
         * @param mouseEventDetails
         *            serialized mouse event details
         */
        public void click(MouseEventDetails mouseEventDetails);

        /**
         * Indicate to the server that the client has disabled the button as a
         * result of a click.
         */
        public void disableOnClick();
    }

    private ButtonServerRpc rpc = RpcProxy.create(ButtonServerRpc.class, this);
    private FocusAndBlurServerRpc focusBlurProxy = RpcProxy.create(
            FocusAndBlurServerRpc.class, this);

    private HandlerRegistration focusHandlerRegistration = null;
    private HandlerRegistration blurHandlerRegistration = null;

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void init() {
        super.init();
        getWidget().buttonRpcProxy = rpc;
        getWidget().client = getConnection();
        getWidget().paintableId = getConnectorId();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        focusHandlerRegistration = EventHelper.updateFocusHandler(this,
                focusHandlerRegistration);
        blurHandlerRegistration = EventHelper.updateBlurHandler(this,
                blurHandlerRegistration);
        // Set text
        getWidget().setText(getState().getCaption());

        getWidget().disableOnClick = getState().isDisableOnClick();

        // handle error
        if (null != getState().getErrorMessage()) {
            if (getWidget().errorIndicatorElement == null) {
                getWidget().errorIndicatorElement = DOM.createSpan();
                getWidget().errorIndicatorElement
                        .setClassName("v-errorindicator");
            }
            getWidget().wrapper.insertBefore(getWidget().errorIndicatorElement,
                    getWidget().captionElement);

        } else if (getWidget().errorIndicatorElement != null) {
            getWidget().wrapper.removeChild(getWidget().errorIndicatorElement);
            getWidget().errorIndicatorElement = null;
        }

        if (getState().getIcon() != null) {
            if (getWidget().icon == null) {
                getWidget().icon = new Icon(getConnection());
                getWidget().wrapper.insertBefore(getWidget().icon.getElement(),
                        getWidget().captionElement);
            }
            getWidget().icon.setUri(getState().getIcon().getURL());
        } else {
            if (getWidget().icon != null) {
                getWidget().wrapper.removeChild(getWidget().icon.getElement());
                getWidget().icon = null;
            }
        }

        getWidget().clickShortcut = getState().getClickShortcutKeyCode();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VButton.class);
    }

    @Override
    public VButton getWidget() {
        return (VButton) super.getWidget();
    }

    @Override
    public ButtonState getState() {
        return (ButtonState) super.getState();
    }

    public void onFocus(FocusEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        focusBlurProxy.focus();
    }

    public void onBlur(BlurEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        focusBlurProxy.blur();
    }
}
