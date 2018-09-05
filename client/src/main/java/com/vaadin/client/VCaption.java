/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client;

import java.util.logging.Logger;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.vaadin.client.WidgetUtil.ErrorUtil;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.HasErrorIndicator;
import com.vaadin.client.ui.HasErrorIndicatorElement;
import com.vaadin.client.ui.HasRequiredIndicator;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.ImageIcon;
import com.vaadin.client.ui.aria.AriaHelper;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.ComponentConstants;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.ErrorLevel;

public class VCaption extends HTML implements HasErrorIndicatorElement {

    public static final String CLASSNAME = "v-caption";

    private final ComponentConnector owner;

    private Element errorIndicatorElement;

    private Element requiredFieldIndicator;

    private Icon icon;

    private String iconAltText = "";

    private Element captionText;

    private final ApplicationConnection client;

    private boolean placedAfterComponent = false;

    private int maxWidth = -1;

    private enum InsertPosition {
        ICON, CAPTION, REQUIRED, ERROR
    }

    private TooltipInfo tooltipInfo = null;

    private boolean captionAsHtml = false;

    /**
     * Creates a caption that is not linked to a {@link ComponentConnector}.
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
     * Creates a caption for a {@link ComponentConnector}.
     *
     * @param component
     *            owner of caption, not null
     * @param client
     *            ApplicationConnection
     */
    public VCaption(ComponentConnector component,
            ApplicationConnection client) {
        super();
        this.client = client;
        owner = component;

        if (client != null && owner != null) {
            setOwnerPid(getElement(), owner.getConnectorId());
        }

        setStyleName(CLASSNAME);
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        if (null != owner) {
            AriaHelper.bindCaption(owner.getWidget(), getElement());
        }

    }

    @Override
    protected void onDetach() {
        super.onDetach();

        if (null != owner) {
            AriaHelper.bindCaption(owner.getWidget(), null);
            AriaHelper.handleInputInvalid(owner.getWidget(), false);
            AriaHelper.handleInputRequired(owner.getWidget(), false);
        }
    }

    /**
     * Updates the caption from UIDL.
     *
     * This method may only be called when the caption has an owner - otherwise,
     * use {@link #updateCaptionWithoutOwner(UIDL, String, boolean, boolean)}.
     *
     * @return true if the position where the caption should be placed has
     *         changed
     */
    public boolean updateCaption() {
        boolean wasPlacedAfterComponent = placedAfterComponent;

        // Caption is placed after component unless there is some part which
        // moves it above.
        placedAfterComponent = true;

        String style = CLASSNAME;
        if (ComponentStateUtil.hasStyles(owner.getState())) {
            for (String customStyle : owner.getState().styles) {
                style += " " + CLASSNAME + "-" + customStyle;
            }
        }
        if (!owner.isEnabled()) {
            style += " " + StyleConstants.DISABLED;
        }
        setStyleName(style);

        boolean hasIcon = owner.getState().resources
                .containsKey(ComponentConstants.ICON_RESOURCE);
        boolean showRequired = false;
        boolean showError = false;
        if (owner instanceof HasRequiredIndicator) {
            showRequired = ((HasRequiredIndicator) owner)
                    .isRequiredIndicatorVisible();
        }
        if (owner instanceof HasErrorIndicator) {
            showError = ((HasErrorIndicator) owner).isErrorIndicatorVisible();
        }

        if (icon != null) {
            getElement().removeChild(icon.getElement());
            icon = null;
        }
        if (hasIcon) {
            String uri = owner.getState().resources
                    .get(ComponentConstants.ICON_RESOURCE).getURL();

            icon = client.getIcon(uri);

            if (icon instanceof ImageIcon) {
                // onload will set appropriate size later
                icon.setWidth("0");
                icon.setHeight("0");
            }

            DOM.insertChild(getElement(), icon.getElement(),
                    getInsertPosition(InsertPosition.ICON));

            // Icon forces the caption to be above the component
            placedAfterComponent = false;
        }

        if (owner.getState().caption != null) {
            // A caption text should be shown if the attribute is set
            // If the caption is null the ATTRIBUTE_CAPTION should not be set to
            // avoid ending up here.

            if (captionText == null) {
                captionText = DOM.createDiv();
                captionText.setClassName("v-captiontext");

                DOM.insertChild(getElement(), captionText,
                        getInsertPosition(InsertPosition.CAPTION));
            }

            // Update caption text
            String c = owner.getState().caption;
            // A text forces the caption to be above the component.
            placedAfterComponent = false;
            if (c == null || c.trim().isEmpty()) {
                // Not sure if c even can be null. Should not.

                // This is required to ensure that the caption uses space in all
                // browsers when it is set to the empty string. If there is an
                // icon, error indicator or required indicator they will ensure
                // that space is reserved.
                if (!hasIcon && !showRequired && !showError) {
                    captionText.setInnerHTML("&nbsp;");
                }
            } else {
                setCaptionText(captionText, owner.getState());
            }

        } else if (captionText != null) {
            // Remove existing
            DOM.removeChild(getElement(), captionText);
            captionText = null;
        }

        if (ComponentStateUtil.hasDescription(owner.getState())
                && captionText != null) {
            addStyleDependentName("hasdescription");
        } else {
            removeStyleDependentName("hasdescription");
        }

        AriaHelper.handleInputRequired(owner.getWidget(), showRequired);

        if (showRequired) {
            if (requiredFieldIndicator == null) {
                requiredFieldIndicator = DOM.createDiv();
                requiredFieldIndicator
                        .setClassName("v-required-field-indicator");
                DOM.setInnerText(requiredFieldIndicator, "*");

                DOM.insertChild(getElement(), requiredFieldIndicator,
                        getInsertPosition(InsertPosition.REQUIRED));

                // Hide the required indicator from assistive device
                Roles.getTextboxRole()
                        .setAriaHiddenState(requiredFieldIndicator, true);
            }
        } else if (requiredFieldIndicator != null) {
            // Remove existing
            DOM.removeChild(getElement(), requiredFieldIndicator);
            requiredFieldIndicator = null;
        }

        AriaHelper.handleInputInvalid(owner.getWidget(), showError);

        if (showError) {
            setErrorIndicatorElementVisible(true);

            // Hide error indicator from assistive devices
            Roles.getTextboxRole().setAriaHiddenState(errorIndicatorElement,
                    true);

            ErrorUtil.setErrorLevelStyle(errorIndicatorElement,
                    StyleConstants.STYLE_NAME_ERROR_INDICATOR,
                    owner.getState().errorLevel);
        } else {
            setErrorIndicatorElementVisible(false);
        }

        return (wasPlacedAfterComponent != placedAfterComponent);
    }

    private int getInsertPosition(InsertPosition element) {
        int pos = 0;
        if (InsertPosition.ICON.equals(element)) {
            return pos;
        }
        if (icon != null) {
            pos++;
        }

        if (InsertPosition.CAPTION.equals(element)) {
            return pos;
        }

        if (captionText != null) {
            pos++;
        }

        if (InsertPosition.REQUIRED.equals(element)) {
            return pos;
        }
        if (requiredFieldIndicator != null) {
            pos++;
        }

        // if (InsertPosition.ERROR.equals(element)) {
        // }
        return pos;

    }

    @Deprecated
    public boolean updateCaptionWithoutOwner(String caption, boolean disabled,
            boolean hasDescription, boolean hasError, String iconURL) {
        return updateCaptionWithoutOwner(caption, disabled, hasDescription,
                hasError, iconURL, "");
    }

    @Deprecated
    public boolean updateCaptionWithoutOwner(String caption, boolean disabled,
            boolean hasDescription, boolean hasError, String iconURL,
            String iconAltText) {
        return updateCaptionWithoutOwner(caption, disabled, hasDescription,
                hasError, null, iconURL, iconAltText);
    }

    @Deprecated
    public boolean updateCaptionWithoutOwner(String caption, boolean disabled,
            boolean hasDescription, boolean hasError, ErrorLevel errorLevel,
            String iconURL, String iconAltText) {
        boolean wasPlacedAfterComponent = placedAfterComponent;

        // Caption is placed after component unless there is some part which
        // moves it above.
        placedAfterComponent = true;

        String style = VCaption.CLASSNAME;
        if (disabled) {
            style += " " + StyleConstants.DISABLED;
        }
        setStyleName(style);
        if (hasDescription) {
            if (captionText != null) {
                addStyleDependentName("hasdescription");
            } else {
                removeStyleDependentName("hasdescription");
            }
        }
        boolean hasIcon = iconURL != null;

        if (icon != null) {
            getElement().removeChild(icon.getElement());
            icon = null;
        }
        if (hasIcon) {
            icon = client.getIcon(iconURL);
            if (icon instanceof ImageIcon) {
                // onload sets appropriate size later
                icon.setWidth("0");
                icon.setHeight("0");
            }
            icon.setAlternateText(iconAltText);
            DOM.insertChild(getElement(), icon.getElement(),
                    getInsertPosition(InsertPosition.ICON));

            // Icon forces the caption to be above the component
            placedAfterComponent = false;

        }

        if (caption != null) {
            // A caption text should be shown if the attribute is set
            // If the caption is null the ATTRIBUTE_CAPTION should not be set to
            // avoid ending up here.

            if (captionText == null) {
                captionText = DOM.createDiv();
                captionText.setClassName("v-captiontext");

                DOM.insertChild(getElement(), captionText,
                        getInsertPosition(InsertPosition.CAPTION));
            }

            // Update caption text
            // A text forces the caption to be above the component.
            placedAfterComponent = false;
            if (caption.trim().isEmpty()) {
                // This is required to ensure that the caption uses space in all
                // browsers when it is set to the empty string. If there is an
                // icon, error indicator or required indicator they will ensure
                // that space is reserved.
                if (!hasIcon && !hasError) {
                    captionText.setInnerHTML("&nbsp;");
                }
            } else {
                if (captionAsHtml) {
                    captionText.setInnerHTML(caption);
                } else {
                    captionText.setInnerText(caption);
                }
            }

        } else if (captionText != null) {
            // Remove existing
            DOM.removeChild(getElement(), captionText);
            captionText = null;
        }

        if (hasError) {
            setErrorIndicatorElementVisible(true);
            ErrorUtil.setErrorLevelStyle(errorIndicatorElement,
                    StyleConstants.STYLE_NAME_ERROR_INDICATOR, errorLevel);
        } else {
            setErrorIndicatorElementVisible(false);
        }

        return (wasPlacedAfterComponent != placedAfterComponent);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        final Element target = DOM.eventGetTarget(event);

        if (DOM.eventGetType(event) == Event.ONLOAD
                && icon.getElement() == target) {
            icon.setWidth("");
            icon.setHeight("");

            // if max width defined, recalculate
            if (maxWidth != -1) {
                setMaxWidth(maxWidth);
            } else {
                String width = getElement().getStyle().getProperty("width");
                if (width != null && !width.isEmpty()) {
                    setWidth(getRequiredWidth() + "px");
                }
            }

            /*
             * The size of the icon might affect the size of the component so we
             * must report the size change to the parent TODO consider moving
             * the responsibility of reacting to ONLOAD from VCaption to layouts
             */
            if (owner != null) {
                Util.notifyParentOfSizeChange(owner.getWidget(), true);
            } else {
                getLogger().warning(
                        "Warning: Icon load event was not propagated because VCaption owner is unknown.");
            }
        }
    }

    public static boolean isNeeded(ComponentConnector connector) {
        AbstractComponentState state = connector.getState();
        if (state.caption != null) {
            return true;
        }
        if (state.resources.containsKey(ComponentConstants.ICON_RESOURCE)) {
            return true;
        }
        if (connector instanceof HasErrorIndicator
                && ((HasErrorIndicator) connector).isErrorIndicatorVisible()) {
            return true;
        }

        if (connector instanceof HasRequiredIndicator
                && ((HasRequiredIndicator) connector)
                        .isRequiredIndicatorVisible()) {
            return true;
        }

        return false;

    }

    /**
     * Checks whether anything in a given state change might cause the caption
     * to change.
     *
     * @param event
     *            the state change event to check
     * @return <code>true</code> if the caption might have changed; otherwise
     *         <code>false</code>
     */
    public static boolean mightChange(StateChangeEvent event) {
        if (event.hasPropertyChanged("caption")) {
            return true;
        }
        if (event.hasPropertyChanged("resources")) {
            return true;
        }
        if (event.hasPropertyChanged("errorMessage")) {
            return true;
        }

        return false;
    }

    /**
     * Returns Paintable for which this Caption belongs to.
     *
     * @return owner Widget
     */
    public ComponentConnector getOwner() {
        return owner;
    }

    public boolean shouldBePlacedAfterComponent() {
        return placedAfterComponent;
    }

    public int getRenderedWidth() {
        int width = 0;

        if (icon != null) {
            width += WidgetUtil.getRequiredWidth(icon.getElement());
        }

        if (captionText != null) {
            width += WidgetUtil.getRequiredWidth(captionText);
        }
        if (requiredFieldIndicator != null) {
            width += WidgetUtil.getRequiredWidth(requiredFieldIndicator);
        }
        if (errorIndicatorElement != null) {
            width += WidgetUtil.getRequiredWidth(errorIndicatorElement);
        }

        return width;

    }

    public int getRequiredWidth() {
        int width = 0;

        if (icon != null) {
            width += WidgetUtil.getRequiredWidth(icon.getElement());
        }
        if (captionText != null) {
            int textWidth = captionText.getScrollWidth();
            if (BrowserInfo.get().isFirefox() || BrowserInfo.get().isChrome()) {
                /*
                 * The caption might require more space than the scrollWidth
                 * returns as scrollWidth is rounded down.
                 */
                int requiredWidth = WidgetUtil.getRequiredWidth(captionText);
                if (requiredWidth > textWidth) {
                    textWidth = requiredWidth;
                }

            }
            width += textWidth;
        }
        if (requiredFieldIndicator != null) {
            width += WidgetUtil.getRequiredWidth(requiredFieldIndicator);
        }
        if (errorIndicatorElement != null) {
            width += WidgetUtil.getRequiredWidth(errorIndicatorElement);
        }

        return width;

    }

    public int getHeight() {
        int height = 0;
        int h;

        if (icon != null) {
            h = WidgetUtil.getRequiredHeight(icon.getElement());
            if (h > height) {
                height = h;
            }
        }

        if (captionText != null) {
            h = WidgetUtil.getRequiredHeight(captionText);
            if (h > height) {
                height = h;
            }
        }
        if (requiredFieldIndicator != null) {
            h = WidgetUtil.getRequiredHeight(requiredFieldIndicator);
            if (h > height) {
                height = h;
            }
        }
        if (errorIndicatorElement != null) {
            h = WidgetUtil.getRequiredHeight(errorIndicatorElement);
            if (h > height) {
                height = h;
            }
        }

        return height;
    }

    public void setAlignment(String alignment) {
        getElement().getStyle().setProperty("textAlign", alignment);
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        getElement().getStyle().setWidth(maxWidth, Unit.PX);

        if (icon != null) {
            icon.getElement().getStyle().clearWidth();
        }

        if (captionText != null) {
            captionText.getStyle().clearWidth();
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
                availableWidth -= WidgetUtil
                        .getRequiredWidth(requiredFieldIndicator);
            }

            if (errorIndicatorElement != null) {
                availableWidth -= WidgetUtil
                        .getRequiredWidth(errorIndicatorElement);
            }

            if (availableWidth < 0) {
                availableWidth = 0;
            }

            if (icon != null) {
                int iconRequiredWidth = WidgetUtil
                        .getRequiredWidth(icon.getElement());
                if (availableWidth > iconRequiredWidth) {
                    availableWidth -= iconRequiredWidth;
                } else {
                    icon.getElement().getStyle().setWidth(availableWidth,
                            Unit.PX);
                    availableWidth = 0;
                }
            }
            if (captionText != null) {
                int captionWidth = WidgetUtil.getRequiredWidth(captionText);
                if (availableWidth > captionWidth) {
                    availableWidth -= captionWidth;

                } else {
                    captionText.getStyle().setWidth(availableWidth, Unit.PX);
                    availableWidth = 0;
                }

            }

        }
    }

    /**
     * Sets the tooltip that should be shown for the caption.
     *
     * @param tooltipInfo
     *            The tooltip that should be shown or null if no tooltip should
     *            be shown
     */
    public void setTooltipInfo(TooltipInfo tooltipInfo) {
        this.tooltipInfo = tooltipInfo;
    }

    /**
     * Returns the tooltip that should be shown for the caption.
     *
     * @return The tooltip to show or null if no tooltip should be shown
     */
    public TooltipInfo getTooltipInfo() {
        return tooltipInfo;
    }

    protected com.google.gwt.user.client.Element getTextElement() {
        return DOM.asOld(captionText);
    }

    public static String getCaptionOwnerPid(Element e) {
        return getOwnerPid(e);
    }

    private static native void setOwnerPid(Element el, String pid)
    /*-{
        el.vOwnerPid = pid;
    }-*/;

    public static native String getOwnerPid(Element el)
    /*-{
        return el.vOwnerPid;
    }-*/;

    /**
     * Sets whether the caption is rendered as HTML.
     * <p>
     * Default is false
     *
     * @param captionAsHtml
     *            true if the captions are rendered as HTML, false if rendered
     *            as plain text
     */
    public void setCaptionAsHtml(boolean captionAsHtml) {
        this.captionAsHtml = captionAsHtml;
    }

    /**
     * Checks whether captions are rendered as HTML.
     * <p>
     * Default is false
     *
     * @return true if the captions are rendered as HTML, false if rendered as
     *         plain text
     */
    public boolean isCaptionAsHtml() {
        return captionAsHtml;
    }

    /**
     * Sets the text of the given caption element to the caption found in the
     * state.
     * <p>
     * Uses {@link AbstractComponentState#captionAsHtml} to determine whether to
     * set the caption as html or plain text
     *
     * @since 7.4
     * @param captionElement
     *            the target element
     * @param state
     *            the state from which to read the caption text and mode
     */
    public static void setCaptionText(Element captionElement,
            AbstractComponentState state) {
        if (state.captionAsHtml) {
            captionElement.setInnerHTML(state.caption);
        } else {
            captionElement.setInnerText(state.caption);
        }

    }

    /**
     * Sets the text of the given widget to the caption found in the state.
     * <p>
     * Uses {@link AbstractComponentState#captionAsHtml} to determine whether to
     * set the caption as html or plain text
     *
     * @since 7.4
     * @param widget
     *            the target widget
     * @param state
     *            the state from which to read the caption text and mode
     */
    public static void setCaptionText(HasHTML widget,
            AbstractComponentState state) {
        if (state.captionAsHtml) {
            widget.setHTML(state.caption);
        } else {
            widget.setText(state.caption);
        }

    }

    private static Logger getLogger() {
        return Logger.getLogger(VCaption.class.getName());
    }

    @Override
    public Element getErrorIndicatorElement() {
        return errorIndicatorElement;
    }

    @Override
    public void setErrorIndicatorElementVisible(boolean visible) {
        if (visible) {
            if (errorIndicatorElement == null) {
                errorIndicatorElement = ErrorUtil.createErrorIndicatorElement();
                DOM.insertChild(getElement(), errorIndicatorElement,
                        getInsertPosition(InsertPosition.ERROR));
            }
        } else if (errorIndicatorElement != null) {
            getElement().removeChild(errorIndicatorElement);
            errorIndicatorElement = null;
        }
    }
}
