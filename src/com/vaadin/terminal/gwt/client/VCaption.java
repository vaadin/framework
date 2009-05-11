/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.ui.Icon;

public class VCaption extends HTML {

    public static final String CLASSNAME = "i-caption";

    private final Paintable owner;

    private Element errorIndicatorElement;

    private Element requiredFieldIndicator;

    private Icon icon;

    private Element captionText;

    private Element clearElement;

    private final ApplicationConnection client;

    private boolean placedAfterComponent = false;
    private boolean iconOnloadHandled = false;

    private int maxWidth = -1;

    private static String ATTRIBUTE_ICON = "icon";
    private static String ATTRIBUTE_CAPTION = "caption";
    private static String ATTRIBUTE_DESCRIPTION = "description";
    private static String ATTRIBUTE_REQUIRED = "required";
    private static String ATTRIBUTE_ERROR = "error";
    private static String ATTRIBUTE_HIDEERRORS = "hideErrors";

    /**
     * 
     * @param component
     *            optional owner of caption. If not set, getOwner will return
     *            null
     * @param client
     */
    public VCaption(Paintable component, ApplicationConnection client) {
        super();
        this.client = client;
        owner = component;
        setStyleName(CLASSNAME);
        sinkEvents(VTooltip.TOOLTIP_EVENTS);

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

        boolean wasPlacedAfterComponent = placedAfterComponent;

        placedAfterComponent = true;

        String style = CLASSNAME;
        if (uidl.hasAttribute("style")) {
            final String[] styles = uidl.getStringAttribute("style").split(" ");
            for (int i = 0; i < styles.length; i++) {
                style += " " + CLASSNAME + "-" + styles[i];
            }
        }

        if (uidl.hasAttribute("disabled")) {
            style += " " + "i-disabled";
        }

        setStyleName(style);

        if (uidl.hasAttribute(ATTRIBUTE_ICON)) {
            if (icon == null) {
                icon = new Icon(client);
                icon.setWidth("0px");
                icon.setHeight("0px");

                DOM.insertChild(getElement(), icon.getElement(),
                        getInsertPosition(ATTRIBUTE_ICON));
            }
            placedAfterComponent = false;

            iconOnloadHandled = false;
            icon.setUri(uidl.getStringAttribute(ATTRIBUTE_ICON));

        } else if (icon != null) {
            // Remove existing
            DOM.removeChild(getElement(), icon.getElement());
            icon = null;
        }

        if (uidl.hasAttribute(ATTRIBUTE_CAPTION)) {
            if (captionText == null) {
                captionText = DOM.createDiv();
                captionText.setClassName("i-captiontext");

                DOM.insertChild(getElement(), captionText,
                        getInsertPosition(ATTRIBUTE_CAPTION));
            }

            // Update caption text
            String c = uidl.getStringAttribute(ATTRIBUTE_CAPTION);
            if (c == null) {
                c = "";
            } else {
                placedAfterComponent = false;
            }
            DOM.setInnerText(captionText, c);
        } else if (captionText != null) {
            // Remove existing
            DOM.removeChild(getElement(), captionText);
            captionText = null;
        }

        if (uidl.hasAttribute(ATTRIBUTE_DESCRIPTION)) {
            if (captionText != null) {
                addStyleDependentName("hasdescription");
            } else {
                removeStyleDependentName("hasdescription");
            }
        }

        if (uidl.getBooleanAttribute(ATTRIBUTE_REQUIRED)) {
            if (requiredFieldIndicator == null) {
                requiredFieldIndicator = DOM.createDiv();
                requiredFieldIndicator
                        .setClassName("i-required-field-indicator");
                DOM.setInnerText(requiredFieldIndicator, "*");

                DOM.insertChild(getElement(), requiredFieldIndicator,
                        getInsertPosition(ATTRIBUTE_REQUIRED));
            }
        } else if (requiredFieldIndicator != null) {
            // Remove existing
            DOM.removeChild(getElement(), requiredFieldIndicator);
            requiredFieldIndicator = null;
        }

        if (uidl.hasAttribute(ATTRIBUTE_ERROR)
                && !uidl.getBooleanAttribute(ATTRIBUTE_HIDEERRORS)) {
            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createDiv();
                DOM.setInnerHTML(errorIndicatorElement, "&nbsp;");
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "i-errorindicator");

                DOM.insertChild(getElement(), errorIndicatorElement,
                        getInsertPosition(ATTRIBUTE_ERROR));
            }
        } else if (errorIndicatorElement != null) {
            // Remove existing
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

        return (wasPlacedAfterComponent != placedAfterComponent);
    }

    private int getInsertPosition(String element) {
        int pos = 0;
        if (element.equals(ATTRIBUTE_ICON)) {
            return pos;
        }
        if (icon != null) {
            pos++;
        }

        if (element.equals(ATTRIBUTE_CAPTION)) {
            return pos;
        }

        if (captionText != null) {
            pos++;
        }

        if (element.equals(ATTRIBUTE_REQUIRED)) {
            return pos;
        }
        if (requiredFieldIndicator != null) {
            pos++;
        }

        // if (element.equals(ATTRIBUTE_ERROR)) {
        // }
        return pos;

    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        final Element target = DOM.eventGetTarget(event);
        if (client != null && owner != null && target != getElement()) {
            client.handleTooltipEvent(event, owner);
        }

        if (DOM.eventGetType(event) == Event.ONLOAD
                && icon.getElement() == target && !iconOnloadHandled) {
            icon.setWidth("");
            icon.setHeight("");

            /*
             * IE6 pngFix causes two onload events to be fired and we want to
             * react only to the first one
             */
            iconOnloadHandled = true;

            // if max width defined, recalculate
            if (maxWidth != -1) {
                setMaxWidth(maxWidth);
            } else {
                String width = getElement().getStyle().getProperty("width");
                if (width != null && !width.equals("")) {
                    setWidth(getRequiredWidth() + "px");
                }
            }

            /*
             * The size of the icon might affect the size of the component so we
             * must report the size change to the parent TODO consider moving
             * the responsibility of reacting to ONLOAD from VCaption to layouts
             */
            if (owner != null) {
                Util.notifyParentOfSizeChange(owner, true);
            } else {
                ApplicationConnection
                        .getConsole()
                        .log(
                                "Warning: Icon load event was not propagated because VCaption owner is unknown.");
            }
        }
    }

    public static boolean isNeeded(UIDL uidl) {
        if (uidl.getStringAttribute(ATTRIBUTE_CAPTION) != null) {
            return true;
        }
        if (uidl.hasAttribute(ATTRIBUTE_ERROR)) {
            return true;
        }
        if (uidl.hasAttribute(ATTRIBUTE_ICON)) {
            return true;
        }
        if (uidl.hasAttribute(ATTRIBUTE_REQUIRED)) {
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

    public int getRenderedWidth() {
        int width = 0;

        if (icon != null) {
            width += Util.getRequiredWidth(icon.getElement());
        }

        if (captionText != null) {
            width += Util.getRequiredWidth(captionText);
        }
        if (requiredFieldIndicator != null) {
            width += Util.getRequiredWidth(requiredFieldIndicator);
        }
        if (errorIndicatorElement != null) {
            width += Util.getRequiredWidth(errorIndicatorElement);
        }

        return width;

    }

    public int getRequiredWidth() {
        int width = 0;

        if (icon != null) {
            width += Util.getRequiredWidth(icon.getElement());
        }
        if (captionText != null) {
            int textWidth = captionText.getScrollWidth();
            if (BrowserInfo.get().isFF3()) {
                /*
                 * In Firefox3 the caption might require more space than the
                 * scrollWidth returns as scrollWidth is rounded down.
                 */
                int requiredWidth = Util.getRequiredWidth(captionText);
                if (requiredWidth > textWidth) {
                    textWidth = requiredWidth;
                }

            }
            width += textWidth;
        }
        if (requiredFieldIndicator != null) {
            width += Util.getRequiredWidth(requiredFieldIndicator);
        }
        if (errorIndicatorElement != null) {
            width += Util.getRequiredWidth(errorIndicatorElement);
        }

        return width;

    }

    public int getHeight() {
        int height = 0;
        int h;

        if (icon != null) {
            h = icon.getOffsetHeight();
            if (h > height) {
                height = h;
            }
        }

        if (captionText != null) {
            h = captionText.getOffsetHeight();
            if (h > height) {
                height = h;
            }
        }
        if (requiredFieldIndicator != null) {
            h = requiredFieldIndicator.getOffsetHeight();
            if (h > height) {
                height = h;
            }
        }
        if (errorIndicatorElement != null) {
            h = errorIndicatorElement.getOffsetHeight();
            if (h > height) {
                height = h;
            }
        }

        return height;
    }

    public void setAlignment(String alignment) {
        DOM.setStyleAttribute(getElement(), "textAlign", alignment);
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        DOM.setStyleAttribute(getElement(), "width", maxWidth + "px");

        if (icon != null) {
            DOM.setStyleAttribute(icon.getElement(), "width", "");
        }

        if (captionText != null) {
            DOM.setStyleAttribute(captionText, "width", "");
        }

        int requiredWidth = getRequiredWidth();
        /*
         * ApplicationConnection.getConsole().log( "Caption maxWidth: " +
         * maxWidth + ", requiredWidth: " + requiredWidth);
         */
        if (requiredWidth > maxWidth) {
            // Needs to truncate and clip
            int availableWidth = maxWidth;

            // DOM.setStyleAttribute(getElement(), "width", maxWidth + "px");
            if (requiredFieldIndicator != null) {
                availableWidth -= Util.getRequiredWidth(requiredFieldIndicator);
            }

            if (errorIndicatorElement != null) {
                availableWidth -= Util.getRequiredWidth(errorIndicatorElement);
            }

            if (availableWidth < 0) {
                availableWidth = 0;
            }

            if (icon != null) {
                int iconRequiredWidth = Util
                        .getRequiredWidth(icon.getElement());
                if (availableWidth > iconRequiredWidth) {
                    availableWidth -= iconRequiredWidth;
                } else {
                    DOM.setStyleAttribute(icon.getElement(), "width",
                            availableWidth + "px");
                    availableWidth = 0;
                }
            }
            if (captionText != null) {
                int captionWidth = Util.getRequiredWidth(captionText);
                if (availableWidth > captionWidth) {
                    availableWidth -= captionWidth;

                } else {
                    DOM.setStyleAttribute(captionText, "width", availableWidth
                            + "px");
                    availableWidth = 0;
                }

            }

        }
    }

    protected Element getTextElement() {
        return captionText;
    }

}
