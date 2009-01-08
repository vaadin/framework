/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ITooltip;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class ILink extends HTML implements Paintable, ClickListener {

    public static final String CLASSNAME = "i-link";

    private static final int BORDER_STYLE_DEFAULT = 0;
    private static final int BORDER_STYLE_MINIMAL = 1;
    private static final int BORDER_STYLE_NONE = 2;

    private String src;

    private String target;

    private int borderStyle = BORDER_STYLE_DEFAULT;

    private boolean enabled;

    private boolean readonly;

    private int targetWidth;

    private int targetHeight;

    private Element errorIndicatorElement;

    private final Element captionElement = DOM.createAnchor();

    private Icon icon;

    private ApplicationConnection client;

    public ILink() {
        super();
        DOM.appendChild(getElement(), captionElement);
        addClickListener(this);
        sinkEvents(ITooltip.TOOLTIP_EVENTS);
        setStyleName(CLASSNAME);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // Ensure correct implementation,
        // but don't let container manage caption etc.
        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        this.client = client;

        enabled = uidl.hasAttribute("disabled") ? false : true;
        readonly = uidl.hasAttribute("readonly") ? true : false;

        if (uidl.hasAttribute("name")) {
            target = uidl.getStringAttribute("name");
            captionElement.setAttribute("target", target);
        }
        if (uidl.hasAttribute("src")) {
            src = client.translateToolkitUri(uidl.getStringAttribute("src"));
            captionElement.setAttribute("href", src);
        }

        if (uidl.hasAttribute("border")) {
            if ("none".equals(uidl.getStringAttribute("border"))) {
                borderStyle = BORDER_STYLE_NONE;
            } else {
                borderStyle = BORDER_STYLE_MINIMAL;
            }
        } else {
            borderStyle = BORDER_STYLE_DEFAULT;
        }

        targetHeight = uidl.hasAttribute("targetHeight") ? uidl
                .getIntAttribute("targetHeight") : -1;
        targetWidth = uidl.hasAttribute("targetWidth") ? uidl
                .getIntAttribute("targetWidth") : -1;

        // Set link caption
        DOM.setInnerText(captionElement, uidl.getStringAttribute("caption"));

        // handle error
        if (uidl.hasAttribute("error")) {
            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createDiv();
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "i-errorindicator");
            }
            DOM.insertChild(getElement(), errorIndicatorElement, 0);
        } else if (errorIndicatorElement != null) {
            DOM.setStyleAttribute(errorIndicatorElement, "display", "none");
        }

        if (uidl.hasAttribute("icon")) {
            if (icon == null) {
                icon = new Icon(client);
                DOM.insertChild(getElement(), icon.getElement(), 0);
            }
            icon.setUri(uidl.getStringAttribute("icon"));
        }

    }

    public void onClick(Widget sender) {
        if (enabled && !readonly) {
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
        if (target == captionElement
                || (icon != null && target == icon.getElement())) {
            super.onBrowserEvent(event);
        }
        if (!enabled) {
            event.preventDefault();
        }

    }

}
