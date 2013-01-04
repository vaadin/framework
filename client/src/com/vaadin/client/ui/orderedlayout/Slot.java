/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.client.ui.orderedlayout;

import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.shared.ui.AlignmentInfo;

/**
 * Represents a slot which contains the actual widget in the layout.
 */
public final class Slot extends SimplePanel {
    /**
     * The icon for each widget. Located in the caption of the slot.
     */
    private static class Icon extends UIObject {

        public static final String CLASSNAME = "v-icon";

        private String myUrl;

        /**
         * Constructor
         */
        public Icon() {
            setElement(DOM.createImg());
            DOM.setElementProperty(getElement(), "alt", "");
            setStyleName(CLASSNAME);
        }

        /**
         * Set the URL where the icon is located
         * 
         * @param url
         *            A fully qualified URL
         */
        public void setUri(String url) {
            if (!url.equals(myUrl)) {
                /*
                 * Start sinking onload events, widgets responsibility to react.
                 * We must do this BEFORE we set src as IE fires the event
                 * immediately if the image is found in cache (#2592).
                 */
                sinkEvents(Event.ONLOAD);

                DOM.setElementProperty(getElement(), "src", url);
                myUrl = url;
            }
        }
    }

    private static final String ALIGN_CLASS_PREFIX = "v-align-";

    private final VAbstractOrderedLayout layout;

    public static final String SLOT_CLASSNAME = "v-slot";

    private Element spacer;
    private Element captionWrap;
    private Element caption;
    private Element captionText;
    private Icon icon;
    private Element errorIcon;
    private Element requiredIcon;

    private ElementResizeListener captionResizeListener;

    private ElementResizeListener widgetResizeListener;

    private ElementResizeListener spacingResizeListener;

    // Caption is placed after component unless there is some part which
    // moves it above.
    private CaptionPosition captionPosition = CaptionPosition.RIGHT;

    private AlignmentInfo alignment;

    private double expandRatio = -1;

    /**
     * Constructor
     * 
     * @param widget
     *            The widget to put in the slot
     * @param layout
     *            TODO
     * 
     * @param layoutManager
     *            The layout manager used by the layout
     */
    public Slot(VAbstractOrderedLayout layout, Widget widget) {
        this.layout = layout;
        setStyleName(SLOT_CLASSNAME);
        setWidget(widget);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.SimplePanel#remove(com.google.gwt.user
     * .client.ui.Widget)
     */
    @Override
    public boolean remove(Widget w) {
        detachListeners();
        return super.remove(w);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.SimplePanel#setWidget(com.google.gwt
     * .user.client.ui.Widget)
     */
    @Override
    public void setWidget(Widget w) {
        detachListeners();
        super.setWidget(w);
        attachListeners();
    }

    /**
     * Attached resize listeners to the widget, caption and spacing elements
     */
    private void attachListeners() {
        if (getWidget() != null && layout.getLayoutManager() != null) {
            LayoutManager lm = layout.getLayoutManager();
            if (getCaptionElement() != null && captionResizeListener != null) {
                lm.addElementResizeListener(getCaptionElement(),
                        captionResizeListener);
            }
            if (widgetResizeListener != null) {
                lm.addElementResizeListener(getWidget().getElement(),
                        widgetResizeListener);
            }
            if (getSpacingElement() != null && spacingResizeListener != null) {
                lm.addElementResizeListener(getSpacingElement(),
                        spacingResizeListener);
            }
        }
    }

    /**
     * Detaches resize listeners from the widget, caption and spacing elements
     */
    private void detachListeners() {
        if (getWidget() != null && layout.getLayoutManager() != null) {
            LayoutManager lm = layout.getLayoutManager();
            if (getCaptionElement() != null && captionResizeListener != null) {
                lm.removeElementResizeListener(getCaptionElement(),
                        captionResizeListener);
            }
            if (widgetResizeListener != null) {
                lm.removeElementResizeListener(getWidget().getElement(),
                        widgetResizeListener);
            }
            if (getSpacingElement() != null && spacingResizeListener != null) {
                lm.removeElementResizeListener(getSpacingElement(),
                        spacingResizeListener);
            }
        }
    }

    public ElementResizeListener getCaptionResizeListener() {
        return captionResizeListener;
    }

    public void setCaptionResizeListener(
            ElementResizeListener captionResizeListener) {
        detachListeners();
        this.captionResizeListener = captionResizeListener;
        attachListeners();
    }

    public ElementResizeListener getWidgetResizeListener() {
        return widgetResizeListener;
    }

    public void setWidgetResizeListener(
            ElementResizeListener widgetResizeListener) {
        detachListeners();
        this.widgetResizeListener = widgetResizeListener;
        attachListeners();
    }

    public ElementResizeListener getSpacingResizeListener() {
        return spacingResizeListener;
    }

    public void setSpacingResizeListener(
            ElementResizeListener spacingResizeListener) {
        detachListeners();
        this.spacingResizeListener = spacingResizeListener;
        attachListeners();
    }

    /**
     * Returns the alignment for the slot
     * 
     */
    public AlignmentInfo getAlignment() {
        return alignment;
    }

    /**
     * Sets the style names for the slot containing the widget
     * 
     * @param stylenames
     *            The style names for the slot
     */
    protected void setStyleNames(String... stylenames) {
        setStyleName(SLOT_CLASSNAME);
        if (stylenames != null) {
            for (String stylename : stylenames) {
                addStyleDependentName(stylename);
            }
        }

        // Ensure alignment style names are correct
        setAlignment(alignment);
    }

    /**
     * Sets how the widget is aligned inside the slot
     * 
     * @param alignment
     *            The alignment inside the slot
     */
    public void setAlignment(AlignmentInfo alignment) {
        this.alignment = alignment;

        if (alignment != null && alignment.isHorizontalCenter()) {
            addStyleName(ALIGN_CLASS_PREFIX + "center");
            removeStyleName(ALIGN_CLASS_PREFIX + "right");
        } else if (alignment != null && alignment.isRight()) {
            addStyleName(ALIGN_CLASS_PREFIX + "right");
            removeStyleName(ALIGN_CLASS_PREFIX + "center");
        } else {
            removeStyleName(ALIGN_CLASS_PREFIX + "right");
            removeStyleName(ALIGN_CLASS_PREFIX + "center");
        }

        if (alignment != null && alignment.isVerticalCenter()) {
            addStyleName(ALIGN_CLASS_PREFIX + "middle");
            removeStyleName(ALIGN_CLASS_PREFIX + "bottom");
        } else if (alignment != null && alignment.isBottom()) {
            addStyleName(ALIGN_CLASS_PREFIX + "bottom");
            removeStyleName(ALIGN_CLASS_PREFIX + "middle");
        } else {
            removeStyleName(ALIGN_CLASS_PREFIX + "middle");
            removeStyleName(ALIGN_CLASS_PREFIX + "bottom");
        }
    }

    /**
     * Set how the slot should be expanded relative to the other slots. 0 means
     * that the slot should not participate in the division of space based on
     * the expand ratios but instead be allocated space based on its natural
     * size. Other values causes the slot to get a share of the otherwise
     * unallocated space in proportion to the slot's expand ratio value.
     * 
     * @param expandRatio
     *            The ratio of the space the slot should occupy
     * 
     */
    public void setExpandRatio(double expandRatio) {
        this.expandRatio = expandRatio;
    }

    /**
     * Get the expand ratio for the slot. The expand ratio describes how the
     * slot should be resized compared to other slots in the layout
     * 
     * @return the expand ratio of the slot
     * 
     * @see #setExpandRatio(double)
     */
    public double getExpandRatio() {
        return expandRatio;
    }

    /**
     * Set the spacing for the slot. The spacing determines if there should be
     * empty space around the slot when the slot.
     * 
     * @param spacing
     *            Should spacing be enabled
     */
    public void setSpacing(boolean spacing) {
        if (spacing && spacer == null) {
            spacer = DOM.createDiv();
            spacer.addClassName("v-spacing");

            /*
             * This has to be done here for the initial render. In other cases
             * where the spacer already exists onAttach will handle it.
             */
            getElement().getParentElement().insertBefore(spacer, getElement());
        } else if (!spacing && spacer != null) {
            spacer.removeFromParent();
            spacer = null;
        }
    }

    /**
     * Get the element which is added to make the spacing
     * 
     * @return
     */
    public Element getSpacingElement() {
        return spacer;
    }

    /**
     * Does the slot have spacing
     */
    public boolean hasSpacing() {
        return getSpacingElement() != null;
    }

    /**
     * Get the vertical amount in pixels of the spacing
     */
    protected int getVerticalSpacing() {
        if (spacer == null) {
            return 0;
        } else if (layout.getLayoutManager() != null) {
            return layout.getLayoutManager().getOuterHeight(spacer);
        }
        return spacer.getOffsetHeight();
    }

    /**
     * Get the horizontal amount of pixels of the spacing
     * 
     * @return
     */
    protected int getHorizontalSpacing() {
        if (spacer == null) {
            return 0;
        } else if (layout.getLayoutManager() != null) {
            return layout.getLayoutManager().getOuterWidth(spacer);
        }
        return spacer.getOffsetWidth();
    }

    /**
     * Set the position of the caption relative to the slot
     * 
     * @param captionPosition
     *            The position of the caption
     */
    public void setCaptionPosition(CaptionPosition captionPosition) {
        if (caption == null) {
            return;
        }
        captionWrap.removeClassName("v-caption-on-"
                + this.captionPosition.name().toLowerCase());

        this.captionPosition = captionPosition;
        if (captionPosition == CaptionPosition.BOTTOM
                || captionPosition == CaptionPosition.RIGHT) {
            captionWrap.appendChild(caption);
        } else {
            captionWrap.insertFirst(caption);
        }

        captionWrap.addClassName("v-caption-on-"
                + captionPosition.name().toLowerCase());
    }

    /**
     * Get the position of the caption relative to the slot
     */
    public CaptionPosition getCaptionPosition() {
        return captionPosition;
    }

    /**
     * Set the caption of the slot
     * 
     * @param captionText
     *            The text of the caption
     * @param iconUrl
     *            The icon URL
     * @param styles
     *            The style names
     * @param error
     *            The error message
     * @param showError
     *            Should the error message be shown
     * @param required
     *            Is the (field) required
     * @param enabled
     *            Is the component enabled
     */
    public void setCaption(String captionText, String iconUrl,
            List<String> styles, String error, boolean showError,
            boolean required, boolean enabled) {

        // TODO place for optimization: check if any of these have changed
        // since last time, and only run those changes

        // Caption wrappers
        if (captionText != null || iconUrl != null || error != null || required) {
            if (caption == null) {
                caption = DOM.createDiv();
                captionWrap = DOM.createDiv();
                captionWrap.addClassName(StyleConstants.UI_WIDGET);
                captionWrap.addClassName("v-has-caption");
                getElement().appendChild(captionWrap);
                captionWrap.appendChild(getWidget().getElement());
            }
        } else if (caption != null) {
            getElement().appendChild(getWidget().getElement());
            captionWrap.removeFromParent();
            caption = null;
            captionWrap = null;
        }

        // Caption text
        if (captionText != null) {
            if (this.captionText == null) {
                this.captionText = DOM.createSpan();
                this.captionText.addClassName("v-captiontext");
                caption.appendChild(this.captionText);
            }
            if (captionText.trim().equals("")) {
                this.captionText.setInnerHTML("&nbsp;");
            } else {
                this.captionText.setInnerText(captionText);
            }
        } else if (this.captionText != null) {
            this.captionText.removeFromParent();
            this.captionText = null;
        }

        // Icon
        if (iconUrl != null) {
            if (icon == null) {
                icon = new Icon();
                caption.insertFirst(icon.getElement());
            }
            icon.setUri(iconUrl);
        } else if (icon != null) {
            icon.getElement().removeFromParent();
            icon = null;
        }

        // Required
        if (required) {
            if (requiredIcon == null) {
                requiredIcon = DOM.createSpan();
                // TODO decide something better (e.g. use CSS to insert the
                // character)
                requiredIcon.setInnerHTML("*");
                requiredIcon.setClassName("v-required-field-indicator");
            }
            caption.appendChild(requiredIcon);
        } else if (requiredIcon != null) {
            requiredIcon.removeFromParent();
            requiredIcon = null;
        }

        // Error
        if (error != null && showError) {
            if (errorIcon == null) {
                errorIcon = DOM.createSpan();
                errorIcon.setClassName("v-errorindicator");
            }
            caption.appendChild(errorIcon);
        } else if (errorIcon != null) {
            errorIcon.removeFromParent();
            errorIcon = null;
        }

        if (caption != null) {
            // Styles
            caption.setClassName("v-caption");

            if (styles != null) {
                for (String style : styles) {
                    caption.addClassName("v-caption-" + style);
                }
            }

            if (enabled) {
                caption.removeClassName("v-disabled");
            } else {
                caption.addClassName("v-disabled");
            }

            // Caption position
            if (captionText != null || iconUrl != null) {
                setCaptionPosition(CaptionPosition.TOP);
            } else {
                setCaptionPosition(CaptionPosition.RIGHT);
            }
        }
    }

    /**
     * Does the slot have a caption
     */
    public boolean hasCaption() {
        return caption != null;
    }

    /**
     * Get the slots caption element
     */
    public Element getCaptionElement() {
        return caption;
    }

    private boolean relativeWidth = false;

    /**
     * Set if the slot has a relative width
     * 
     * @param relativeWidth
     *            True if slot uses relative width, false if the slot has a
     *            static width
     */
    public void setRelativeWidth(boolean relativeWidth) {
        this.relativeWidth = relativeWidth;
        updateRelativeSize(relativeWidth, "width");
    }

    public boolean hasRelativeWidth() {
        return relativeWidth;
    }

    private boolean relativeHeight = false;

    /**
     * Set if the slot has a relative height
     * 
     * @param relativeHeight
     *            True if the slot uses a relative height, false if the slot has
     *            a static height
     */
    public void setRelativeHeight(boolean relativeHeight) {
        this.relativeHeight = relativeHeight;
        updateRelativeSize(relativeHeight, "height");
    }

    public boolean hasRelativeHeight() {
        return relativeHeight;
    }

    /**
     * Updates the captions size if the slot is relative
     * 
     * @param isRelativeSize
     *            Is the slot relatively sized
     * @param direction
     *            The direction of the caption
     */
    private void updateRelativeSize(boolean isRelativeSize, String direction) {
        if (isRelativeSize && hasCaption()) {
            captionWrap.getStyle().setProperty(direction,
                    getWidget().getElement().getStyle().getProperty(direction));
            captionWrap.addClassName("v-has-" + direction);
        } else if (hasCaption()) {
            if (direction.equals("height")) {
                captionWrap.getStyle().clearHeight();
            } else {
                captionWrap.getStyle().clearWidth();
            }
            captionWrap.removeClassName("v-has-" + direction);
            captionWrap.getStyle().clearPaddingTop();
            captionWrap.getStyle().clearPaddingRight();
            captionWrap.getStyle().clearPaddingBottom();
            captionWrap.getStyle().clearPaddingLeft();
            caption.getStyle().clearMarginTop();
            caption.getStyle().clearMarginRight();
            caption.getStyle().clearMarginBottom();
            caption.getStyle().clearMarginLeft();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt
     * .user.client.Event)
     */
    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (DOM.eventGetType(event) == Event.ONLOAD
                && icon.getElement() == DOM.eventGetTarget(event)) {
            if (layout.getLayoutManager() != null) {
                layout.getLayoutManager().layoutLater();
            } else {
                layout.updateCaptionOffset(caption);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.SimplePanel#getContainerElement()
     */
    @Override
    protected Element getContainerElement() {
        if (captionWrap == null) {
            return getElement();
        } else {
            return captionWrap;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Widget#onDetach()
     */
    @Override
    protected void onDetach() {
        if (spacer != null) {
            spacer.removeFromParent();
        }
        super.onDetach();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Widget#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        if (spacer != null) {
            getElement().getParentElement().insertBefore(spacer, getElement());
        }
    }

    public boolean isRelativeInDirection(boolean vertical) {
        if (vertical) {
            return hasRelativeHeight();
        } else {
            return hasRelativeWidth();
        }
    }
}