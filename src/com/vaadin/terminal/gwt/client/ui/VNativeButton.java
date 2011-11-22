/*
@ITMillApache2LicenseForJavaFiles@
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
import com.vaadin.terminal.gwt.client.EventHelper;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;

public class VNativeButton extends Button implements Paintable, ClickHandler,
        FocusHandler, BlurHandler {

    public static final String CLASSNAME = "v-nativebutton";

    protected String width = null;

    protected String id;

    protected ApplicationConnection client;

    protected Element errorIndicatorElement;

    protected final Element captionElement = DOM.createSpan();

    protected Icon icon;

    /**
     * Helper flag to handle special-case where the button is moved from under
     * mouse while clicking it. In this case mouse leaves the button without
     * moving.
     */
    private boolean clickPending;

    private HandlerRegistration focusHandlerRegistration;
    private HandlerRegistration blurHandlerRegistration;

    private boolean disableOnClick = false;

    public VNativeButton() {
        setStyleName(CLASSNAME);

        getElement().appendChild(captionElement);
        captionElement.setClassName(getStyleName() + "-caption");

        addClickHandler(this);

        sinkEvents(VTooltip.TOOLTIP_EVENTS);
        sinkEvents(Event.ONMOUSEDOWN);
        sinkEvents(Event.ONMOUSEUP);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Ensure correct implementation,
        // but don't let container manage caption etc.
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        disableOnClick = uidl.hasAttribute(VButton.ATTR_DISABLE_ON_CLICK);

        focusHandlerRegistration = EventHelper.updateFocusHandler(this, client,
                focusHandlerRegistration);
        blurHandlerRegistration = EventHelper.updateBlurHandler(this, client,
                blurHandlerRegistration);

        // Save details
        this.client = client;
        id = uidl.getId();

        // Set text
        setText(uidl.getStringAttribute("caption"));

        // handle error
        if (uidl.hasAttribute("error")) {
            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createSpan();
                errorIndicatorElement.setClassName("v-errorindicator");
            }
            getElement().insertBefore(errorIndicatorElement, captionElement);

        } else if (errorIndicatorElement != null) {
            getElement().removeChild(errorIndicatorElement);
            errorIndicatorElement = null;
        }

        if (uidl.hasAttribute("icon")) {
            if (icon == null) {
                icon = new Icon(client);
                getElement().insertBefore(icon.getElement(), captionElement);
            }
            icon.setUri(uidl.getStringAttribute("icon"));
        } else {
            if (icon != null) {
                getElement().removeChild(icon.getElement());
                icon = null;
            }
        }

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
            client.handleTooltipEvent(event, this);
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
        if (id == null || client == null) {
            return;
        }

        if (BrowserInfo.get().isSafari()) {
            VNativeButton.this.setFocus(true);
        }
        if (disableOnClick) {
            setEnabled(false);
            client.updateVariable(id, "disabledOnClick", true, false);
        }

        // Add mouse details
        MouseEventDetails details = new MouseEventDetails(
                event.getNativeEvent(), getElement());
        client.updateVariable(id, "mousedetails", details.serialize(), false);

        client.updateVariable(id, "state", true, true);
        clickPending = false;
    }

    public void onFocus(FocusEvent arg0) {
        client.updateVariable(id, EventId.FOCUS, "", true);
    }

    public void onBlur(BlurEvent arg0) {
        client.updateVariable(id, EventId.BLUR, "", true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            super.setEnabled(enabled);
            setStyleName(ApplicationConnection.DISABLED_CLASSNAME, !enabled);
        }
    }

}
