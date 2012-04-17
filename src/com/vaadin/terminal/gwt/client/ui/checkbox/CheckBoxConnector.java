/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.checkbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.EventHelper;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.MouseEventDetailsBuilder;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractFieldConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.Icon;
import com.vaadin.ui.CheckBox;

@Connect(CheckBox.class)
public class CheckBoxConnector extends AbstractFieldConnector implements
        FocusHandler, BlurHandler, ClickHandler {

    private HandlerRegistration focusHandlerRegistration;
    private HandlerRegistration blurHandlerRegistration;

    private CheckBoxServerRpc rpc = RpcProxy.create(CheckBoxServerRpc.class,
            this);
    private FocusAndBlurServerRpc focusBlurRpc = RpcProxy.create(
            FocusAndBlurServerRpc.class, this);

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        getWidget().addClickHandler(this);
        getWidget().client = getConnection();
        getWidget().id = getConnectorId();

    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        focusHandlerRegistration = EventHelper.updateFocusHandler(this,
                focusHandlerRegistration);
        blurHandlerRegistration = EventHelper.updateBlurHandler(this,
                blurHandlerRegistration);

        if (null != getState().getErrorMessage()) {
            if (getWidget().errorIndicatorElement == null) {
                getWidget().errorIndicatorElement = DOM.createSpan();
                getWidget().errorIndicatorElement.setInnerHTML("&nbsp;");
                DOM.setElementProperty(getWidget().errorIndicatorElement,
                        "className", "v-errorindicator");
                DOM.appendChild(getWidget().getElement(),
                        getWidget().errorIndicatorElement);
                DOM.sinkEvents(getWidget().errorIndicatorElement,
                        VTooltip.TOOLTIP_EVENTS | Event.ONCLICK);
            } else {
                DOM.setStyleAttribute(getWidget().errorIndicatorElement,
                        "display", "");
            }
        } else if (getWidget().errorIndicatorElement != null) {
            DOM.setStyleAttribute(getWidget().errorIndicatorElement, "display",
                    "none");
        }

        if (isReadOnly()) {
            getWidget().setEnabled(false);
        }

        if (getState().getIcon() != null) {
            if (getWidget().icon == null) {
                getWidget().icon = new Icon(getConnection());
                DOM.insertChild(getWidget().getElement(),
                        getWidget().icon.getElement(), 1);
                getWidget().icon.sinkEvents(VTooltip.TOOLTIP_EVENTS);
                getWidget().icon.sinkEvents(Event.ONCLICK);
            }
            getWidget().icon.setUri(getState().getIcon().getURL());
        } else if (getWidget().icon != null) {
            // detach icon
            DOM.removeChild(getWidget().getElement(),
                    getWidget().icon.getElement());
            getWidget().icon = null;
        }

        // Set text
        getWidget().setText(getState().getCaption());
        getWidget().setValue(getState().isChecked());
        getWidget().immediate = getState().isImmediate();
    }

    @Override
    public CheckBoxState getState() {
        return (CheckBoxState) super.getState();
    }

    @Override
    public VCheckBox getWidget() {
        return (VCheckBox) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VCheckBox.class);
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

    public void onClick(ClickEvent event) {
        if (!isEnabled()) {
            return;
        }

        // Add mouse details
        MouseEventDetails details = MouseEventDetailsBuilder
                .buildMouseEventDetails(event.getNativeEvent(), getWidget()
                        .getElement());
        rpc.setChecked(getWidget().getValue(), details);

    }
}
