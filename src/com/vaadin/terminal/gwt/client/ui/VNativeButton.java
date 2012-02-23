/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ui.VButtonPaintable.ButtonClientToServerRpc;

public class VNativeButton extends Button implements ClickHandler,
        FocusHandler, BlurHandler {

    public static final String CLASSNAME = "v-nativebutton";

    protected String width = null;

    protected String paintableId;

    protected ApplicationConnection client;

    ButtonClientToServerRpc buttonRpcProxy;

    protected Element errorIndicatorElement;

    protected final Element captionElement = DOM.createSpan();

    protected Icon icon;

    /**
     * Helper flag to handle special-case where the button is moved from under
     * mouse while clicking it. In this case mouse leaves the button without
     * moving.
     */
    private boolean clickPending;

    protected HandlerRegistration focusHandlerRegistration;
    protected HandlerRegistration blurHandlerRegistration;

    protected boolean disableOnClick = false;

    public VNativeButton() {
        setStyleName(CLASSNAME);

        getElement().appendChild(captionElement);
        captionElement.setClassName(getStyleName() + "-caption");

        addClickHandler(this);

        sinkEvents(VTooltip.TOOLTIP_EVENTS);
        sinkEvents(Event.ONMOUSEDOWN);
        sinkEvents(Event.ONMOUSEUP);
    }

    @Override
    public void setText(String text) {
        captionElement.setInnerText(text);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);

        if (DOM.eventGetType(event) == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);

        } else if (DOM.eventGetType(event) == Event.ONMOUSEDOWN
                && event.getButton() == Event.BUTTON_LEFT) {
            clickPending = true;
        } else if (DOM.eventGetType(event) == Event.ONMOUSEMOVE) {
            clickPending = false;
        } else if (DOM.eventGetType(event) == Event.ONMOUSEOUT) {
            if (clickPending) {
                click();
            }
            clickPending = false;
        }

        if (client != null) {
            client.handleWidgetTooltipEvent(event, this);
        }
    }

    @Override
    public void setWidth(String width) {
        this.width = width;
        super.setWidth(width);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
     * .dom.client.ClickEvent)
     */
    public void onClick(ClickEvent event) {
        if (paintableId == null || client == null) {
            return;
        }

        if (BrowserInfo.get().isSafari()) {
            VNativeButton.this.setFocus(true);
        }
        if (disableOnClick) {
            setEnabled(false);
            buttonRpcProxy.disableOnClick();
        }

        // Add mouse details
        MouseEventDetails details = new MouseEventDetails(
                event.getNativeEvent(), getElement());
        buttonRpcProxy.click(details.serialize());

        clickPending = false;
    }

    public void onFocus(FocusEvent arg0) {
        client.updateVariable(paintableId, EventId.FOCUS, "", true);
    }

    public void onBlur(BlurEvent arg0) {
        client.updateVariable(paintableId, EventId.BLUR, "", true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            super.setEnabled(enabled);
            setStyleName(ApplicationConnection.DISABLED_CLASSNAME, !enabled);
        }
    }
}
