/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.ui.Icon;
import com.vaadin.terminal.gwt.client.ui.VAbstractPaintableWidget;

public class VCaption extends HTML {

    public static final String CLASSNAME = "v-caption";

    private final VPaintableWidget owner;

    private Element errorIndicatorElement;

    private Element requiredFieldIndicator;

    private Icon icon;

    private Element captionText;

    private Element clearElement;

    private final ApplicationConnection client;

    private boolean placedAfterComponent = false;

    private int maxWidth = -1;

    private static final String CLASSNAME_CLEAR = CLASSNAME + "-clearelem";

    /**
     * Creates a caption that is not linked to a {@link VPaintableWidget}.
     * 
     * When using this constructor, {@link #getOwner()} returns null.
     * 
     * @param client
     *            ApplicationConnection
     * @deprecated all captions should be associated with a paintable widget and
     *             be updated from shared state, not UIDL
     */
    @Deprecated
    public VCaption(ApplicationConnection client) {
        super();
        this.client = client;
        owner = null;

        setStyleName(CLASSNAME);
        sinkEvents(VTooltip.TOOLTIP_EVENTS);

    }

    /**
     * Creates a caption for a {@link VPaintableWidget}.
     * 
     * @param component
     *            owner of caption, not null
     * @param client
     *            ApplicationConnection
     */
    public VCaption(VPaintableWidget component, ApplicationConnection client) {
        super();
        this.client = client;
        owner = component;

        if (client != null && owner != null) {
            setOwnerPid(getElement(), VPaintableMap.get(client).getPid(owner));
        }

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

        // Caption is placed after component unless there is some part which
        // moves it above.
        placedAfterComponent = true;

        // TODO otherwise, the user should also call updateCaptionWithoutOwner()
        if (null != owner) {
            String style = CLASSNAME;
            if (owner.getState().hasStyles()) {
                final String[] styles = owner.getState().getStyle().split(" ");
                for (int i = 0; i < styles.length; i++) {
                    style += " " + CLASSNAME + "-" + styles[i];
                }
            }
            if (owner.getState().isDisabled()) {
                style += " " + ApplicationConnection.DISABLED_CLASSNAME;
            }
            setStyleName(style);
        }

        boolean hasIcon = uidl
                .hasAttribute(VAbstractPaintableWidget.ATTRIBUTE_ICON);
        boolean hasText = uidl
                .hasAttribute(VAbstractPaintableWidget.ATTRIBUTE_CAPTION);
        boolean showRequired = uidl
                .getBooleanAttribute(VAbstractPaintableWidget.ATTRIBUTE_REQUIRED);
        boolean showError = uidl
                .hasAttribute(VAbstractPaintableWidget.ATTRIBUTE_ERROR)
                && !uidl.getBooleanAttribute(VAbstractPaintableWidget.ATTRIBUTE_HIDEERRORS);

        if (hasIcon) {
            if (icon == null) {
                icon = new Icon(client);
                icon.setWidth("0");
                icon.setHeight("0");

                DOM.insertChild(
                        getElement(),
                        icon.getElement(),
                        getInsertPosition(VAbstractPaintableWidget.ATTRIBUTE_ICON));
            }
            // Icon forces the caption to be above the component
            placedAfterComponent = false;

            icon.setUri(uidl
                    .getStringAttribute(VAbstractPaintableWidget.ATTRIBUTE_ICON));

        } else if (icon != null) {
            // Remove existing
            DOM.removeChild(getElement(), icon.getElement());
            icon = null;
        }

        if (hasText) {
            // A caption text should be shown if the attribute is set
            // If the caption is null the ATTRIBUTE_CAPTION should not be set to
            // avoid ending up here.

            if (captionText == null) {
                captionText = DOM.createDiv();
                captionText.setClassName("v-captiontext");

                DOM.insertChild(
                        getElement(),
                        captionText,
                        getInsertPosition(VAbstractPaintableWidget.ATTRIBUTE_CAPTION));
            }

            // Update caption text
            String c = uidl
                    .getStringAttribute(VAbstractPaintableWidget.ATTRIBUTE_CAPTION);
            // A text forces the caption to be above the component.
            placedAfterComponent = false;
            if (c == null || c.trim().equals("")) {
                // Not sure if c even can be null. Should not.

                // This is required to ensure that the caption uses space in all
                // browsers when it is set to the empty string. If there is an
                // icon, error indicator or required indicator they will ensure
                // that space is reserved.
                if (!hasIcon && !showRequired && !showError) {
                    captionText.setInnerHTML("&nbsp;");
                }
            } else {
                DOM.setInnerText(captionText, c);
            }

        } else if (captionText != null) {
            // Remove existing
            DOM.removeChild(getElement(), captionText);
            captionText = null;
        }

        if (null != owner) {
            if (owner.getState().hasDescription() && captionText != null) {
                addStyleDependentName("hasdescription");
            } else {
                removeStyleDependentName("hasdescription");
            }
        }

        if (showRequired) {
            if (requiredFieldIndicator == null) {
                requiredFieldIndicator = DOM.createDiv();
                requiredFieldIndicator
                        .setClassName("v-required-field-indicator");
                DOM.setInnerText(requiredFieldIndicator, "*");

                DOM.insertChild(
                        getElement(),
                        requiredFieldIndicator,
                        getInsertPosition(VAbstractPaintableWidget.ATTRIBUTE_REQUIRED));
            }
        } else if (requiredFieldIndicator != null) {
            // Remove existing
            DOM.removeChild(getElement(), requiredFieldIndicator);
            requiredFieldIndicator = null;
        }

        if (showError) {
            if (errorIndicatorElement == null) {
                errorIndicatorElement = DOM.createDiv();
                DOM.setInnerHTML(errorIndicatorElement, "&nbsp;");
                DOM.setElementProperty(errorIndicatorElement, "className",
                        "v-errorindicator");

                DOM.insertChild(
                        getElement(),
                        errorIndicatorElement,
                        getInsertPosition(VAbstractPaintableWidget.ATTRIBUTE_ERROR));
            }
        } else if (errorIndicatorElement != null) {
            // Remove existing
            getElement().removeChild(errorIndicatorElement);
            errorIndicatorElement = null;
        }

        if (clearElement == null) {
            clearElement = DOM.createDiv();
            clearElement.setClassName(CLASSNAME_CLEAR);
            getElement().appendChild(clearElement);
        }

        return (wasPlacedAfterComponent != placedAfterComponent);
    }

    private int getInsertPosition(String element) {
        int pos = 0;
        if (element.equals(VAbstractPaintableWidget.ATTRIBUTE_ICON)) {
            return pos;
        }
        if (icon != null) {
            pos++;
        }

        if (element.equals(VAbstractPaintableWidget.ATTRIBUTE_CAPTION)) {
            return pos;
        }

        if (captionText != null) {
            pos++;
        }

        if (element.equals(VAbstractPaintableWidget.ATTRIBUTE_REQUIRED)) {
            return pos;
        }
        if (requiredFieldIndicator != null) {
            pos++;
        }

        // if (element.equals(ATTRIBUTE_ERROR)) {
        // }
        return pos;

    }

    @Deprecated
    public void updateCaptionWithoutOwner(boolean disabled,
            boolean hasDescription) {
        // TODO temporary method, needed because some tabsheet and accordion
        // internal captions do not have an owner or shared state.
        // Remaining such cases do not use the "style" attribute - see
        // Tabsheet.paintContent().
        String style = VCaption.CLASSNAME;
        if (disabled) {
            style += " " + ApplicationConnection.DISABLED_CLASSNAME;
        }
        setStyleName(style);
        if (hasDescription) {
            if (captionText != null) {
                addStyleDependentName("hasdescription");
            } else {
                removeStyleDependentName("hasdescription");
            }
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        final Element target = DOM.eventGetTarget(event);
        if (client != null && owner != null && target != getElement()) {
            client.handleTooltipEvent(event, owner);
        }

        if (DOM.eventGetType(event) == Event.ONLOAD
                && icon.getElement() == target) {
            icon.setWidth("");
            icon.setHeight("");

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
                Util.notifyParentOfSizeChange(owner.getWidgetForPaintable(),
                        true);
            } else {
                VConsole.log("Warning: Icon load event was not propagated because VCaption owner is unknown.");
            }
        }
    }

    public static boolean isNeeded(UIDL uidl) {
        if (uidl.getStringAttribute(VAbstractPaintableWidget.ATTRIBUTE_CAPTION) != null) {
            return true;
        }
        if (uidl.hasAttribute(VAbstractPaintableWidget.ATTRIBUTE_ERROR)) {
            return true;
        }
        if (uidl.hasAttribute(VAbstractPaintableWidget.ATTRIBUTE_ICON)) {
            return true;
        }
        if (uidl.hasAttribute(VAbstractPaintableWidget.ATTRIBUTE_REQUIRED)) {
            return true;
        }

        return false;
    }

    /**
     * Returns Paintable for which this Caption belongs to.
     * 
     * @return owner Widget
     */
    public VPaintableWidget getOwner() {
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
            if (BrowserInfo.get().isFirefox()) {
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
            h = Util.getRequiredHeight(icon.getElement());
            if (h > height) {
                height = h;
            }
        }

        if (captionText != null) {
            h = Util.getRequiredHeight(captionText);
            if (h > height) {
                height = h;
            }
        }
        if (requiredFieldIndicator != null) {
            h = Util.getRequiredHeight(requiredFieldIndicator);
            if (h > height) {
                height = h;
            }
        }
        if (errorIndicatorElement != null) {
            h = Util.getRequiredHeight(errorIndicatorElement);
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

    public static String getCaptionOwnerPid(Element e) {
        return getOwnerPid(e);
    }

    private native static void setOwnerPid(Element el, String pid)
    /*-{
        el.vOwnerPid = pid;
    }-*/;

    public native static String getOwnerPid(Element el)
    /*-{
        return el.vOwnerPid;
    }-*/;

}
