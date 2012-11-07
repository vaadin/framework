/*
 * Copyright 2011 Vaadin Ltd.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.Util;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.shared.ui.MarginInfo;

/**
 * Base class for ordered layouts
 */
public class VOrderedLayout extends FlowPanel {

    private static final String ALIGN_CLASS_PREFIX = "v-align-";

    protected boolean spacing = false;

    protected boolean vertical = true;

    protected boolean definedHeight = false;

    private Map<Widget, Slot> widgetToSlot = new HashMap<Widget, Slot>();

    private Element expandWrapper;

    private LayoutManager layoutManager;

    public VOrderedLayout(boolean vertical) {
        this.vertical = vertical;
    }

    /**
     * Add or move a slot to another index
     * 
     * @param slot
     *            The slot to move or add
     * @param index
     *            The index where the slot should be placed
     */
    void addOrMoveSlot(Slot slot, int index) {
        if (slot.getParent() == this) {
            int currentIndex = getWidgetIndex(slot);
            if (index == currentIndex) {
                return;
            }
        }
        insert(slot, index);

        /*
         * We need to call setSpacing once again since if the widget has moved
         * the spacing element also needs to be moved.
         */
        slot.setSpacing(spacing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void insert(Widget child, Element container, int beforeIndex,
            boolean domInsert) {
        // Validate index; adjust if the widget is already a child of this
        // panel.
        beforeIndex = adjustIndex(child, beforeIndex);

        // Detach new child.
        child.removeFromParent();

        // Logical attach.
        getChildren().insert(child, beforeIndex);

        // Physical attach.
        container = expandWrapper != null ? expandWrapper : getElement();
        if (domInsert) {
            DOM.insertChild(container, child.getElement(),
                    spacing ? beforeIndex * 2 : beforeIndex);
        } else {
            DOM.appendChild(container, child.getElement());
        }

        // Adopt.
        adopt(child);
    }

    /**
     * Remove a slot from the layout
     * 
     * @param widget
     * @return
     */
    public void removeWidget(Widget widget) {
        Slot slot = widgetToSlot.get(widget);
        remove(slot);
        widgetToSlot.remove(widget);
    }

    /**
     * Get the containing slot for a widget. If no slot is found a new slot is
     * created and returned.
     * 
     * @param widget
     *            The widget whose slot you want to get
     * 
     * @return
     */
    public Slot getSlot(Widget widget) {
        Slot slot = widgetToSlot.get(widget);
        if (slot == null) {
            slot = new Slot(widget);
            widgetToSlot.put(widget, slot);
        }
        return slot;
    }

    /**
     * Gets a slot based on the widget element. If no slot is found then null is
     * returned.
     * 
     * @param widgetElement
     *            The element of the widget ( Same as getWidget().getElement() )
     * @return
     */
    public Slot getSlot(Element widgetElement) {
        for (Map.Entry<Widget, Slot> entry : widgetToSlot.entrySet()) {
            if (entry.getKey().getElement() == widgetElement) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Defines where the caption should be placed
     */
    public enum CaptionPosition {
        TOP, RIGHT, BOTTOM, LEFT
    }

    /**
     * Represents a slot which contains the actual widget in the layout.
     */
    public final class Slot extends SimplePanel {

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
         * 
         * @param layoutManager
         *            The layout manager used by the layout
         */
        private Slot(Widget widget) {
            setStyleName(SLOT_CLASSNAME);
            setWidget(widget);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.user.client.ui.SimplePanel#remove(com.google.gwt.user
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
         * @see
         * com.google.gwt.user.client.ui.SimplePanel#setWidget(com.google.gwt
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
            if (getWidget() != null && getLayoutManager() != null) {
                LayoutManager lm = getLayoutManager();
                if (getCaptionElement() != null
                        && captionResizeListener != null) {
                    lm.addElementResizeListener(getCaptionElement(),
                            captionResizeListener);
                }
                if (widgetResizeListener != null) {
                    lm.addElementResizeListener(getWidget().getElement(),
                            widgetResizeListener);
                }
                if (getSpacingElement() != null
                        && spacingResizeListener != null) {
                    lm.addElementResizeListener(getSpacingElement(),
                            spacingResizeListener);
                }
            }
        }

        /**
         * Detaches resize listeners from the widget, caption and spacing
         * elements
         */
        private void detachListeners() {
            if (getWidget() != null && getLayoutManager() != null) {
                LayoutManager lm = getLayoutManager();
                if (getCaptionElement() != null
                        && captionResizeListener != null) {
                    lm.removeElementResizeListener(getCaptionElement(),
                            captionResizeListener);
                }
                if (widgetResizeListener != null) {
                    lm.removeElementResizeListener(getWidget().getElement(),
                            widgetResizeListener);
                }
                if (getSpacingElement() != null
                        && spacingResizeListener != null) {
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
         * Set how the slot should be expanded relative to the other slots
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
         * @return
         */
        public double getExpandRatio() {
            return expandRatio;
        }

        /**
         * Set the spacing for the slot. The spacing determines if there should
         * be empty space around the slot when the slot.
         * 
         * @param spacing
         *            Should spacing be enabled
         */
        public void setSpacing(boolean spacing) {
            if (spacing) {
                if (spacer == null) {
                    spacer = DOM.createDiv();
                    spacer.addClassName("v-spacing");
                }

                /*
                 * We need to detach the element since the widget might have
                 * changed the location to another place and the spacer should
                 * follow.
                 */
                if (spacer.getParentElement() != null) {
                    spacer.removeFromParent();
                }

                /*
                 * The first slot does not have spacing so do not attach it in
                 * that case.
                 */
                if (getElement().getPreviousSibling() != null) {
                    getElement().getParentElement().insertBefore(spacer,
                            getElement());
                }
            } else if (spacer != null) {
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
            } else if (getLayoutManager() != null) {
                return getLayoutManager().getOuterHeight(spacer);
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
            } else if (getLayoutManager() != null) {
                return getLayoutManager().getOuterWidth(spacer);
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
            if (captionText != null || iconUrl != null || error != null
                    || required) {
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

        /**
         * Set if the slot has a relative width
         * 
         * @param relativeWidth
         *            True if slot uses relative width, false if the slot has a
         *            static width
         */
        private boolean relativeWidth = false;

        protected void setRelativeWidth(boolean relativeWidth) {
            this.relativeWidth = relativeWidth;
            updateRelativeSize(relativeWidth, "width");
        }

        /**
         * Set if the slot has a relative height
         * 
         * @param relativeHeight
         *            Trie if the slot uses a relative height, false if the slot
         *            has a static height
         */
        private boolean relativeHeight = false;

        protected void setRelativeHeight(boolean relativeHeight) {
            this.relativeHeight = relativeHeight;
            updateRelativeSize(relativeHeight, "height");
        }

        /**
         * Updates the captions size if the slot is relative
         * 
         * @param isRelativeSize
         *            Is the slot relatived sized
         * @param direction
         *            The directorion of the caption
         */
        private void updateRelativeSize(boolean isRelativeSize, String direction) {
            if (isRelativeSize && hasCaption()) {
                captionWrap.getStyle().setProperty(
                        direction,
                        getWidget().getElement().getStyle()
                                .getProperty(direction));
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
         * @see
         * com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt
         * .user.client.Event)
         */
        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (DOM.eventGetType(event) == Event.ONLOAD
                    && icon.getElement() == DOM.eventGetTarget(event)) {
                if (getLayoutManager() != null) {
                    getLayoutManager().layoutLater();
                } else {
                    updateCaptionOffset(caption);
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
                getElement().getParentElement().insertBefore(spacer,
                        getElement());
            }
        }
    }

    /**
     * The icon for each widget. Located in the caption of the slot.
     */
    public static class Icon extends UIObject {

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

    /**
     * Set the layout manager for the layout
     * 
     * @param manager
     *            The layout manager to use
     */
    public void setLayoutManager(LayoutManager manager) {
        layoutManager = manager;
    }

    /**
     * Get the layout manager used by this layout
     * 
     */
    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    /**
     * Deducts the caption position by examining the wrapping element
     * 
     * @param captionWrap
     *            The wrapping element
     * 
     * @return The caption position
     */
    CaptionPosition getCaptionPositionFromElement(Element captionWrap) {
        RegExp captionPositionRegexp = RegExp.compile("v-caption-on-(\\S+)");

        // Get caption position from the classname
        MatchResult matcher = captionPositionRegexp.exec(captionWrap
                .getClassName());
        if (matcher == null || matcher.getGroupCount() < 2) {
            return CaptionPosition.TOP;
        }
        String captionClass = matcher.getGroup(1);
        CaptionPosition captionPosition = CaptionPosition.valueOf(
                CaptionPosition.class, captionClass.toUpperCase());
        return captionPosition;
    }

    /**
     * Update the offset off the caption relative to the slot
     * 
     * @param caption
     *            The caption element
     */
    void updateCaptionOffset(Element caption) {

        Element captionWrap = caption.getParentElement().cast();

        Style captionWrapStyle = captionWrap.getStyle();
        captionWrapStyle.clearPaddingTop();
        captionWrapStyle.clearPaddingRight();
        captionWrapStyle.clearPaddingBottom();
        captionWrapStyle.clearPaddingLeft();

        Style captionStyle = caption.getStyle();
        captionStyle.clearMarginTop();
        captionStyle.clearMarginRight();
        captionStyle.clearMarginBottom();
        captionStyle.clearMarginLeft();

        // Get caption position from the classname
        CaptionPosition captionPosition = getCaptionPositionFromElement(captionWrap);

        if (captionPosition == CaptionPosition.LEFT
                || captionPosition == CaptionPosition.RIGHT) {
            int captionWidth;
            if (layoutManager != null) {
                captionWidth = layoutManager.getOuterWidth(caption)
                        - layoutManager.getMarginWidth(caption);
            } else {
                captionWidth = caption.getOffsetWidth();
            }
            if (captionWidth > 0) {
                if (captionPosition == CaptionPosition.LEFT) {
                    captionWrapStyle.setPaddingLeft(captionWidth, Unit.PX);
                    captionStyle.setMarginLeft(-captionWidth, Unit.PX);
                } else {
                    captionWrapStyle.setPaddingRight(captionWidth, Unit.PX);
                    captionStyle.setMarginRight(-captionWidth, Unit.PX);
                }
            }
        }
        if (captionPosition == CaptionPosition.TOP
                || captionPosition == CaptionPosition.BOTTOM) {
            int captionHeight;
            if (layoutManager != null) {
                captionHeight = layoutManager.getOuterHeight(caption)
                        - layoutManager.getMarginHeight(caption);
            } else {
                captionHeight = caption.getOffsetHeight();
            }
            if (captionHeight > 0) {
                if (captionPosition == CaptionPosition.TOP) {
                    captionWrapStyle.setPaddingTop(captionHeight, Unit.PX);
                    captionStyle.setMarginTop(-captionHeight, Unit.PX);
                } else {
                    captionWrapStyle.setPaddingBottom(captionHeight, Unit.PX);
                    captionStyle.setMarginBottom(-captionHeight, Unit.PX);
                }
            }
        }
    }

    /**
     * Set the margin of the layout
     * 
     * @param marginInfo
     *            The margin information
     */
    public void setMargin(MarginInfo marginInfo) {
        if (marginInfo != null) {
            setStyleName("v-margin-top", marginInfo.hasTop());
            setStyleName("v-margin-right", marginInfo.hasRight());
            setStyleName("v-margin-bottom", marginInfo.hasBottom());
            setStyleName("v-margin-left", marginInfo.hasLeft());
        }
    }

    /**
     * Turn on or off spacing in the layout
     * 
     * @param spacing
     *            True if spacing should be used, false if not
     */
    public void setSpacing(boolean spacing) {
        this.spacing = spacing;
        for (Slot slot : widgetToSlot.values()) {
            if (getWidgetIndex(slot) > 0) {
                slot.setSpacing(spacing);
            }
        }
    }

    /**
     * Triggers a recalculation of the expand width and heights
     */
    private void recalculateExpands() {
        double total = 0;
        for (Slot slot : widgetToSlot.values()) {
            if (slot.getExpandRatio() > -1) {
                total += slot.getExpandRatio();
            } else {
                if (vertical) {
                    slot.getElement().getStyle().clearHeight();
                } else {
                    slot.getElement().getStyle().clearWidth();
                }
            }
        }
        for (Slot slot : widgetToSlot.values()) {
            if (slot.getExpandRatio() > -1) {
                if (vertical) {
                    slot.setHeight((100 * (slot.getExpandRatio() / total))
                            + "%");
                    if (slot.relativeHeight) {
                        Util.notifyParentOfSizeChange(this, true);
                    }
                } else {
                    slot.setWidth((100 * (slot.getExpandRatio() / total)) + "%");
                    if (slot.relativeWidth) {
                        Util.notifyParentOfSizeChange(this, true);
                    }
                }
            }
        }
    }

    /**
     * Removes elements used to expand a slot
     */
    void clearExpand() {
        if (expandWrapper != null) {
            for (; expandWrapper.getChildCount() > 0;) {
                Element el = expandWrapper.getChild(0).cast();
                getElement().appendChild(el);
                if (vertical) {
                    el.getStyle().clearHeight();
                    el.getStyle().clearMarginTop();
                } else {
                    el.getStyle().clearWidth();
                    el.getStyle().clearMarginLeft();
                }
            }
            expandWrapper.removeFromParent();
            expandWrapper = null;
        }
    }

    /**
     * Adds elements used to expand a slot
     */
    public void updateExpand() {
        boolean isExpanding = false;
        for (Widget slot : getChildren()) {
            if (((Slot) slot).getExpandRatio() > -1) {
                isExpanding = true;
            } else {
                if (vertical) {
                    slot.getElement().getStyle().clearHeight();
                } else {
                    slot.getElement().getStyle().clearWidth();
                }
            }
            slot.getElement().getStyle().clearMarginLeft();
            slot.getElement().getStyle().clearMarginTop();
        }

        if (isExpanding) {
            if (expandWrapper == null) {
                expandWrapper = DOM.createDiv();
                expandWrapper.setClassName("v-expand");
                for (; getElement().getChildCount() > 0;) {
                    Node el = getElement().getChild(0);
                    expandWrapper.appendChild(el);
                }
                getElement().appendChild(expandWrapper);
            }

            int totalSize = 0;
            for (Widget w : getChildren()) {
                Slot slot = (Slot) w;
                if (slot.getExpandRatio() == -1) {

                    if (layoutManager != null) {
                        // TODO check caption position
                        if (vertical) {
                            int size = layoutManager.getOuterHeight(slot
                                    .getWidget().getElement())
                                    - layoutManager.getMarginHeight(slot
                                            .getWidget().getElement());
                            if (slot.hasCaption()) {
                                size += layoutManager.getOuterHeight(slot
                                        .getCaptionElement())
                                        - layoutManager.getMarginHeight(slot
                                                .getCaptionElement());
                            }
                            if (size > 0) {
                                totalSize += size;
                            }
                        } else {
                            int max = -1;
                            max = layoutManager.getOuterWidth(slot.getWidget()
                                    .getElement())
                                    - layoutManager.getMarginWidth(slot
                                            .getWidget().getElement());
                            if (slot.hasCaption()) {
                                int max2 = layoutManager.getOuterWidth(slot
                                        .getCaptionElement())
                                        - layoutManager.getMarginWidth(slot
                                                .getCaptionElement());
                                max = Math.max(max, max2);
                            }
                            if (max > 0) {
                                totalSize += max;
                            }
                        }
                    } else {
                        totalSize += vertical ? slot.getOffsetHeight() : slot
                                .getOffsetWidth();
                    }
                }
                // TODO fails in Opera, always returns 0
                int spacingSize = vertical ? slot.getVerticalSpacing() : slot
                        .getHorizontalSpacing();
                if (spacingSize > 0) {
                    totalSize += spacingSize;
                }
            }

            // When we set the margin to the first child, we don't need
            // overflow:hidden in the layout root element, since the wrapper
            // would otherwise be placed outside of the layout root element
            // and block events on elements below it.
            if (vertical) {
                expandWrapper.getStyle().setPaddingTop(totalSize, Unit.PX);
                expandWrapper.getFirstChildElement().getStyle()
                        .setMarginTop(-totalSize, Unit.PX);
            } else {
                expandWrapper.getStyle().setPaddingLeft(totalSize, Unit.PX);
                expandWrapper.getFirstChildElement().getStyle()
                        .setMarginLeft(-totalSize, Unit.PX);
            }

            recalculateExpands();
        }
    }

    /**
     * Perform a recalculation of the layout height
     */
    public void recalculateLayoutHeight() {
        // Only needed if a horizontal layout is undefined high, and contains
        // relative height children or vertical alignments
        if (vertical || definedHeight) {
            return;
        }

        boolean hasRelativeHeightChildren = false;
        boolean hasVAlign = false;

        for (Widget slot : getChildren()) {
            Widget widget = ((Slot) slot).getWidget();
            String h = widget.getElement().getStyle().getHeight();
            if (h != null && h.indexOf("%") > -1) {
                hasRelativeHeightChildren = true;
            }
            AlignmentInfo a = ((Slot) slot).getAlignment();
            if (a != null && (a.isVerticalCenter() || a.isBottom())) {
                hasVAlign = true;
            }
        }

        if (hasRelativeHeightChildren || hasVAlign) {
            int newHeight;
            if (layoutManager != null) {
                newHeight = layoutManager.getOuterHeight(getElement())
                        - layoutManager.getMarginHeight(getElement());
            } else {
                newHeight = getElement().getOffsetHeight();
            }
            VOrderedLayout.this.getElement().getStyle()
                    .setHeight(newHeight, Unit.PX);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        definedHeight = (height != null && !"".equals(height));
    }

    /**
     * Sets the slots style names. The style names will be prefixed with the
     * v-slot prefix.
     * 
     * @param stylenames
     *            The style names of the slot.
     */
    public void setSlotStyleNames(Widget widget, String... stylenames) {
        Slot slot = getSlot(widget);
        if (slot == null) {
            throw new IllegalArgumentException(
                    "A slot for the widget could not be found. Has the widget been added to the layout?");
        }
        slot.setStyleNames(stylenames);
    }

}
