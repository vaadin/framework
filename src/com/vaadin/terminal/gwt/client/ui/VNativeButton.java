/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;

public class VNativeButton extends Button implements Paintable {

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

    public VNativeButton() {
        setStyleName(CLASSNAME);

        getElement().appendChild(captionElement);
        captionElement.setClassName(getStyleName() + "-caption");

        addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (id == null || client == null) {
                    return;
                }
                if (BrowserInfo.get().isSafari()) {
                    VNativeButton.this.setFocus(true);
                }
                client.updateVariable(id, "state", true, true);
                clickPending = false;
            }
        });
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

            // Fix for IE6, IE7
            if (BrowserInfo.get().isIE()) {
                errorIndicatorElement.setInnerText(" ");
            }

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

        if (BrowserInfo.get().isIE7()) {
            /*
             * Workaround for IE7 size calculation issues. Deferred because of
             * issues with a button with an icon using the reindeer theme
             */
            if (width.equals("")) {
                DeferredCommand.addCommand(new Command() {

                    public void execute() {
                        setWidth("");
                        setWidth(getOffsetWidth() + "px");
                    }
                });
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
        /* Workaround for IE7 button size part 1 (#2014) */
        if (BrowserInfo.get().isIE7() && this.width != null) {
            if (this.width.equals(width)) {
                return;
            }

            if (width == null) {
                width = "";
            }
        }

        this.width = width;
        super.setWidth(width);

        /* Workaround for IE7 button size part 2 (#2014) */
        if (BrowserInfo.get().isIE7()) {
            super.setWidth(width);
        }
    }

}
