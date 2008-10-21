/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.itmill.toolkit.terminal.gwt.client.ui.Icon;

//TODO Move styles to CSS
public class ICaption extends HTML {

    public static final String CLASSNAME = "i-caption";

    private final Paintable owner;

    private Element errorIndicatorElement;

    private Element requiredFieldIndicator;

    private Icon icon;

    private Element captionText;

    private Element clearElement;

    private final ApplicationConnection client;

    private boolean placedAfterComponent = false;

    private int maxWidth = -1;

    /**
     * 
     * @param component
     *            optional owner of caption. If not set, getOwner will return
     *            null
     * @param client
     */
    public ICaption(Paintable component, ApplicationConnection client) {
        super();
        this.client = client;
        owner = component;
        setStyleName(CLASSNAME);
        sinkEvents(ITooltip.TOOLTIP_EVENTS);

        DOM.setStyleAttribute(getElement(), "whiteSpace", "nowrap");

    }

    /**
     * Updates the caption from UIDL.
     * 
     * @param uidl
     * @return true if the position where the caption should be placed has
     *         changed
     */
    public boolean updateCaption(UIDL uidl) {
        setVisible(!uidl.getBooleanAttribute("invisible"));

        setStyleName(getElement(), "i-disabled", uidl.hasAttribute("disabled"));

        boolean isEmpty = true;

        boolean wasPlacedAfterComponent = placedAfterComponent;

        placedAfterComponent = true;

        if (uidl.hasAttribute("icon")) {
            if (icon == null) {
                icon = new Icon(client);
                DOM.sinkEvents(icon.getElement(), Event.ONLOAD);

                Util.setFloat(icon.getElement(), "left");
                placedAfterComponent = false;
                DOM.insertChild(getElement(), icon.getElement(), 0);
            }
            icon.setUri(uidl.getStringAttribute("icon"));
            isEmpty = false;
        } else {
            if (icon != null) {
                DOM.removeChild(getElement(), icon.getElement());
                icon = null;
            }
        }

        if (uidl.hasAttribute("caption")) {
            if (captionText == null) {
                captionText = DOM.createDiv();
                Util.setFloat(captionText, "left");
                DOM.setStyleAttribute(captionText, "overflow", "hidden");
                // DOM.setStyleAttribute(captionText, "textOverflow",
                // "ellipsis");
                DOM
                        .insertChild(getElement(), captionText,
                                icon == null ? 0 : 1);
            }
            String c = uidl.getStringAttribute("caption");
            if (c == null) {
                c = "";
            } else {
                isEmpty = false;
                placedAfterComponent = false;
            }
            DOM.setInnerText(captionText, c);
        } else {
            // TODO should element also be removed
        }

        if (uidl.hasAttribute("description")) {
            if (captionText != null) {
                addStyleDependentName("hasdescription");
            } else {
                removeStyleDependentName("hasdescription");
            }
        }

        if (uidl.getBooleanAttribute("required")) {
            isEmpty = false;
            if (requiredFieldIndicator == null) {
                requiredFieldIndicator = DOM.createDiv();
                Util.setFloat(requiredFieldIndicator, "left");
                DOM.setInnerText(requiredFieldIndicator, "*");
                DOM.setElementProperty(requiredFieldIndicator, "className",
                        "i-required-field-indicator");

                // TODO Insert before if errorIndicatorElement exists
                DOM.appendChild(getElement(), requiredFieldIndicator);
            }
        } else {
            if (requiredFieldIndicator != null) {
                DOM.removeChild(getElement(), requiredFieldIndicator);
                requiredFieldIndicator = null;
            }
        }

        if (uidl.hasAttribute("error")) {
            isEmpty = false;
            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createDiv();
                DOM.setInnerHTML(errorIndicatorElement, "&nbsp;");
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "i-errorindicator");
                DOM.appendChild(getElement(), errorIndicatorElement);
            }
        } else if (errorIndicatorElement != null) {
            DOM.removeChild(getElement(), errorIndicatorElement);
            errorIndicatorElement = null;
        }

        if (clearElement == null) {
            clearElement = DOM.createDiv();
            DOM.setStyleAttribute(clearElement, "clear", "both");
            DOM.setStyleAttribute(clearElement, "width", "0px");
            DOM.setStyleAttribute(clearElement, "height", "0px");
            DOM.setStyleAttribute(clearElement, "overflow", "hidden");
            DOM.appendChild(getElement(), clearElement);
        }
        // Workaround for IE weirdness, sometimes returns bad height in some
        // circumstances when Caption is empty. See #1444
        // IE7 bugs more often. I wonder what happens when IE8 arrives...
        if (Util.isIE()) {
            if (isEmpty) {
                setHeight("0px");
                DOM.setStyleAttribute(getElement(), "overflow", "hidden");
            } else {
                setHeight("");
                DOM.setStyleAttribute(getElement(), "overflow", "");
            }

        }

        return (wasPlacedAfterComponent != placedAfterComponent);
    }

    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        final Element target = DOM.eventGetTarget(event);
        if (client != null && !DOM.compare(target, getElement())) {
            client.handleTooltipEvent(event, owner);
        }

        if (DOM.eventGetType(event) == Event.ONLOAD) {
            setMaxWidth(maxWidth);

            // TODO: What if the caption's height changes drastically. Should we
            // send the size updated message?
            // Set<Widget> w = new HashSet<Widget>();
            // w.add(this);
            // Util.componentSizeUpdated(w);
        }

    }

    public static boolean isNeeded(UIDL uidl) {
        if (uidl.getStringAttribute("caption") != null) {
            return true;
        }
        if (uidl.hasAttribute("error")) {
            return true;
        }
        if (uidl.hasAttribute("icon")) {
            return true;
        }
        if (uidl.hasAttribute("required")) {
            return true;
        }

        return false;
    }

    /**
     * Returns Paintable for which this Caption belongs to.
     * 
     * @return owner Widget
     */
    public Paintable getOwner() {
        return owner;
    }

    public boolean shouldBePlacedAfterComponent() {
        return placedAfterComponent;
    }

    public int getWidth() {
        int width = 0;

        if (icon != null) {
            width += icon.getOffsetWidth();
        }

        if (maxWidth >= 0) {
            if (captionText != null) {
                width += captionText.getOffsetWidth();
            }
            if (requiredFieldIndicator != null) {
                width += requiredFieldIndicator.getOffsetWidth();
            }
            if (errorIndicatorElement != null) {
                width += errorIndicatorElement.getOffsetWidth();
            }

        } else {
            if (captionText != null) {
                width += captionText.getScrollWidth();
            }
            if (requiredFieldIndicator != null) {
                width += requiredFieldIndicator.getScrollWidth();
            }
            if (errorIndicatorElement != null) {
                width += errorIndicatorElement.getScrollWidth();
            }

        }

        return width;

    }

    public int getHeight() {
        return clearElement.getOffsetTop() - getElement().getOffsetTop();
    }

    public void setAlignment(String alignment) {
        DOM.setStyleAttribute(getElement(), "textAlign", alignment);
    }

    public void setMaxWidth(int maxWidth) {

        this.maxWidth = maxWidth;
        DOM.setStyleAttribute(getElement(), "width", "");

        if (icon != null) {
            DOM.setStyleAttribute(icon.getElement(), "width", "");
        }

        if (captionText != null) {
            DOM.setStyleAttribute(captionText, "width", "");
        }

        if (maxWidth < 0) {
            return;
        }

        int currentWidth = getWidth();
        if (currentWidth > maxWidth) {
            // Needs to truncate and clip
            int availableWidth = maxWidth;

            // ApplicationConnection.getConsole().log(
            // "Caption maxWidth: " + maxWidth);

            DOM.setStyleAttribute(getElement(), "width", maxWidth + "px");
            if (requiredFieldIndicator != null) {
                // ApplicationConnection.getConsole().log(
                // "requiredFieldIndicator width: "
                // + requiredFieldIndicator.getOffsetWidth());
                availableWidth -= requiredFieldIndicator.getOffsetWidth();
            }

            if (errorIndicatorElement != null) {
                // ApplicationConnection.getConsole().log(
                // "errorIndicatorElement width: "
                // + errorIndicatorElement.getOffsetWidth());
                availableWidth -= errorIndicatorElement.getOffsetWidth();
            }

            if (icon != null) {
                if (availableWidth > icon.getOffsetWidth()) {
                    // ApplicationConnection.getConsole().log(
                    // "icon width: " + icon.getOffsetWidth());
                    availableWidth -= icon.getOffsetWidth();
                } else {
                    // ApplicationConnection.getConsole().log(
                    // "icon forced width: " + availableWidth);
                    DOM.setStyleAttribute(icon.getElement(), "width",
                            availableWidth + "px");
                    availableWidth = 0;
                }
            }
            if (captionText != null) {
                if (availableWidth > captionText.getOffsetWidth()) {
                    // ApplicationConnection.getConsole().log(
                    // "captionText width: "
                    // + captionText.getOffsetWidth());
                    availableWidth -= captionText.getOffsetWidth();

                } else {
                    // ApplicationConnection.getConsole().log(
                    // "captionText forced width: " + availableWidth);
                    DOM.setStyleAttribute(captionText, "width", availableWidth
                            + "px");
                    availableWidth = 0;
                }

            }

        }
    }

}
