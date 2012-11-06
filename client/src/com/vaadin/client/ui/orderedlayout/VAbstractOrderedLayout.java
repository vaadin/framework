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
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.LayoutManager;
import com.vaadin.shared.ui.MarginInfo;

/**
 * Base class for ordered layouts
 */
public abstract class VAbstractOrderedLayout extends FlowPanel {

    static final String ALIGN_CLASS_PREFIX = "v-align-";

    protected boolean spacing = false;

    protected boolean definedHeight = false;

    protected Map<Widget, Slot> widgetToSlot = new HashMap<Widget, Slot>();

    protected Element expandWrapper;

    protected LayoutManager layoutManager;

    /**
     * Add or move a slot to another index
     * 
     * @param slot
     *            The slot to move or add
     * @param index
     *            The index where the slot should be placed
     */
    protected void addOrMoveSlot(Slot slot, int index) {
        if (slot.getParent() == this) {
            int currentIndex = getWidgetIndex(slot);
            if (index == currentIndex) {
                return;
            }
        }
        insert(slot, index);
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
            slot = new Slot(this, widget);
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
    protected CaptionPosition getCaptionPositionFromElement(Element captionWrap) {
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
    protected void updateCaptionOffset(Element caption) {

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
    protected abstract void recalculateExpands();

    /**
     * Removes elements used to expand a slot
     */
    protected abstract void clearExpand();

    /**
     * Adds elements used to expand a slot
     */
    public abstract void updateExpand();

    /**
     * Perform a recalculation of the layout height
     */
    public abstract void recalculateLayoutHeight();

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
