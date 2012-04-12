/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.link;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ui.Icon;

public class VLink extends HTML implements ClickHandler {

    public static final String CLASSNAME = "v-link";

    protected static final int BORDER_STYLE_DEFAULT = 0;
    protected static final int BORDER_STYLE_MINIMAL = 1;
    protected static final int BORDER_STYLE_NONE = 2;

    protected String src;

    protected String target;

    protected int borderStyle = BORDER_STYLE_DEFAULT;

    protected boolean enabled;

    protected int targetWidth;

    protected int targetHeight;

    protected Element errorIndicatorElement;

    protected final Element anchor = DOM.createAnchor();

    protected final Element captionElement = DOM.createSpan();

    protected Icon icon;

    protected ApplicationConnection client;

    public VLink() {
        super();
        getElement().appendChild(anchor);
        anchor.appendChild(captionElement);
        addClickHandler(this);
        sinkEvents(VTooltip.TOOLTIP_EVENTS);
        setStyleName(CLASSNAME);
    }

    public void onClick(ClickEvent event) {
        if (enabled) {
            if (target == null) {
                target = "_self";
            }
            String features;
            switch (borderStyle) {
            case BORDER_STYLE_NONE:
                features = "menubar=no,location=no,status=no";
                break;
            case BORDER_STYLE_MINIMAL:
                features = "menubar=yes,location=no,status=no";
                break;
            default:
                features = "";
                break;
            }

            if (targetWidth > 0) {
                features += (features.length() > 0 ? "," : "") + "width="
                        + targetWidth;
            }
            if (targetHeight > 0) {
                features += (features.length() > 0 ? "," : "") + "height="
                        + targetHeight;
            }

            if (features.length() > 0) {
                // if 'special features' are set, use window.open(), unless
                // a modifier key is held (ctrl to open in new tab etc)
                Event e = DOM.eventGetCurrentEvent();
                if (!e.getCtrlKey() && !e.getAltKey() && !e.getShiftKey()
                        && !e.getMetaKey()) {
                    Window.open(src, target, features);
                    e.preventDefault();
                }
            }
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        final Element target = DOM.eventGetTarget(event);
        if (event.getTypeInt() == Event.ONLOAD) {
            Util.notifyParentOfSizeChange(this, true);
        }
        if (client != null) {
            client.handleTooltipEvent(event, this);
        }
        if (target == captionElement || target == anchor
                || (icon != null && target == icon.getElement())) {
            super.onBrowserEvent(event);
        }
        if (!enabled) {
            event.preventDefault();
        }

    }

}
