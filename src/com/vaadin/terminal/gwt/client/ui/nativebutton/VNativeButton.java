/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.nativebutton;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.button.ButtonServerRpc;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.MouseEventDetailsBuilder;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.Icon;

public class VNativeButton extends Button implements ClickHandler {

    public static final String CLASSNAME = "v-nativebutton";

    protected String width = null;

    protected String paintableId;

    protected ApplicationConnection client;

    ButtonServerRpc buttonRpcProxy;

    protected Element errorIndicatorElement;

    protected final Element captionElement = DOM.createSpan();

    protected Icon icon;

    /**
     * Helper flag to handle special-case where the button is moved from under
     * mouse while clicking it. In this case mouse leaves the button without
     * moving.
     */
    private boolean clickPending;

    protected boolean disableOnClick = false;

    public VNativeButton() {
        setStyleName(CLASSNAME);

        getElement().appendChild(captionElement);
        captionElement.setClassName(getStyleName() + "-caption");

        addClickHandler(this);

        sinkEvents(Event.ONMOUSEDOWN);
        sinkEvents(Event.ONMOUSEUP);
    }

    @Override
    public void setText(String text) {
        captionElement.setInnerText(text);
    }

    @Override
    public void setHTML(String html) {
        captionElement.setInnerHTML(html);
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
    @Override
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
        MouseEventDetails details = MouseEventDetailsBuilder
                .buildMouseEventDetails(event.getNativeEvent(), getElement());
        buttonRpcProxy.click(details);

        clickPending = false;
    }

}
