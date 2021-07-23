/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.client.ui.layout;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.VCaption;
import com.vaadin.shared.ui.AlignmentInfo;

/**
 * An abstract slot class for ManagedLayout cells.
 *
 * @author Vaadin Ltd
 */
public abstract class VLayoutSlot {

    private final Element wrapper = Document.get().createDivElement();

    private AlignmentInfo alignment;
    private VCaption caption;
    private final Widget widget;

    private double expandRatio;

    /**
     * Constructs a slot instance for a ManagedLayout cell.
     *
     * @param baseClassName
     *            the base class name of the layout
     * @param widget
     *            the widget that should be set to this slot, should not be
     *            {@code null}
     */
    public VLayoutSlot(String baseClassName, Widget widget) {
        assert widget != null : "The slot must contain a widget!";
        this.widget = widget;

        wrapper.setClassName(baseClassName + "-slot");
    }

    /**
     * Returns the caption element for this slot.
     *
     * @return the caption element, can be {@code null}
     */
    public VCaption getCaption() {
        return caption;
    }

    /**
     * Sets the caption element for this slot.
     *
     * @param caption
     *            the caption element, can be {@code null}
     */
    public void setCaption(VCaption caption) {
        if (this.caption != null) {
            this.caption.removeFromParent();
        }
        this.caption = caption;
        if (caption != null) {
            // Physical attach.
            DOM.insertBefore(wrapper, caption.getElement(),
                    widget.getElement());
            Style style = caption.getElement().getStyle();
            style.setPosition(Position.ABSOLUTE);
            style.setTop(0, Unit.PX);
        }
    }

    /**
     * Returns the alignment data for this slot.
     *
     * @return the alignment data, can be {@code null}
     */
    public AlignmentInfo getAlignment() {
        return alignment;
    }

    /**
     * Returns the widget that this slot contains.
     *
     * @return the child widget, cannot be {@code null}
     */
    public Widget getWidget() {
        return widget;
    }

    /**
     * Sets the alignment data for this slot.
     *
     * @param alignment
     *            the alignment data, can be {@code null}
     */
    public void setAlignment(AlignmentInfo alignment) {
        this.alignment = alignment;
        // if alignment is something other than topLeft then we need to align
        // the component inside this slot
        if (alignment != null && (!alignment.isLeft() || !alignment.isTop())) {
            widget.getElement().getStyle().setPosition(Position.ABSOLUTE);
        }
    }

    /**
     * Position the slot horizontally and set the width and the right margin.
     *
     * @param currentLocation
     *            the left position for this slot
     * @param allocatedSpace
     *            how much horizontal space is available for this slot
     * @param marginRight
     *            the right margin this slot should have (removed if negative)
     */
    public void positionHorizontally(double currentLocation,
            double allocatedSpace, double marginRight) {
        Style style = wrapper.getStyle();

        double availableWidth = allocatedSpace;

        VCaption caption = getCaption();
        Style captionStyle = caption != null ? caption.getElement().getStyle()
                : null;
        int captionWidth = getCaptionWidth();

        boolean captionAboveComponent;
        if (caption == null) {
            captionAboveComponent = false;
        } else {
            captionAboveComponent = !caption.shouldBePlacedAfterComponent();
            if (!captionAboveComponent) {
                availableWidth -= captionWidth;
                if (availableWidth < 0) {
                    availableWidth = 0;
                }

                style.setPaddingRight(captionWidth, Unit.PX);
                widget.getElement().getStyle().setPosition(Position.RELATIVE);
            } else {
                captionStyle.setLeft(0, Unit.PX);
            }
        }

        if (marginRight > 0) {
            style.setMarginRight(marginRight, Unit.PX);
        } else {
            style.clearMarginRight();
        }

        style.setPropertyPx("width", (int) availableWidth);

        double allocatedContentWidth = 0;
        if (isRelativeWidth()) {
            String percentWidth = getWidget().getElement().getStyle()
                    .getWidth();
            double percentage = parsePercent(percentWidth);
            allocatedContentWidth = availableWidth * (percentage / 100);
            reportActualRelativeWidth(
                    Math.round((float) allocatedContentWidth));
        }

        double usedWidth; // widget width in px
        if (isRelativeWidth()) {
            usedWidth = allocatedContentWidth;
        } else {
            usedWidth = getWidgetWidth();
        }

        style.setLeft(Math.round(currentLocation), Unit.PX);
        AlignmentInfo alignment = getAlignment();
        if (!alignment.isLeft()) {
            double padding = availableWidth - usedWidth;
            if (alignment.isHorizontalCenter()) {
                padding = padding / 2;
            }

            long roundedPadding = Math.round(padding);
            if (captionStyle != null) {
                captionStyle.setLeft(captionAboveComponent ? roundedPadding
                        : roundedPadding + usedWidth, Unit.PX);
            }
            widget.getElement().getStyle().setLeft(roundedPadding, Unit.PX);
        } else {
            if (captionStyle != null) {
                captionStyle.setLeft(captionAboveComponent ? 0 : usedWidth,
                        Unit.PX);
            }

            // Reset left when changing back to align left
            widget.getElement().getStyle().clearLeft();
        }
    }

    private double parsePercent(String size) {
        return Double.parseDouble(size.replaceAll("%", ""));
    }

    /**
     * Position the slot vertically and set the height and the bottom margin.
     *
     * @param currentLocation
     *            the top position for this slot
     * @param allocatedSpace
     *            how much vertical space is available for this slot
     * @param marginBottom
     *            the bottom margin this slot should have (removed if negative)
     */
    public void positionVertically(double currentLocation,
            double allocatedSpace, double marginBottom) {
        Style style = wrapper.getStyle();

        double contentHeight = allocatedSpace;

        int captionHeight;
        VCaption caption = getCaption();
        Style captionStyle = caption == null ? null
                : caption.getElement().getStyle();
        if (caption == null || caption.shouldBePlacedAfterComponent()) {
            style.clearPaddingTop();
            captionHeight = 0;
        } else {
            captionHeight = getCaptionHeight();
            contentHeight -= captionHeight;
            if (contentHeight < 0) {
                contentHeight = 0;
            }
            style.setPaddingTop(captionHeight, Unit.PX);
        }

        if (marginBottom > 0) {
            style.setMarginBottom(marginBottom, Unit.PX);
        } else {
            style.clearMarginBottom();
        }

        style.setHeight(contentHeight, Unit.PX);

        double allocatedContentHeight = 0;
        if (isRelativeHeight()) {
            String height = getWidget().getElement().getStyle().getHeight();
            double percentage = parsePercent(height);
            allocatedContentHeight = contentHeight * (percentage / 100);
            reportActualRelativeHeight(
                    Math.round((float) allocatedContentHeight));
        }

        style.setTop(currentLocation, Unit.PX);
        double padding = 0;
        AlignmentInfo alignment = getAlignment();
        if (!alignment.isTop()) {
            double usedHeight;
            if (isRelativeHeight()) {
                usedHeight = captionHeight + allocatedContentHeight;
            } else {
                usedHeight = getUsedHeight();
            }
            if (alignment.isVerticalCenter()) {
                padding = (allocatedSpace - usedHeight) / 2d;
            } else {
                padding = (allocatedSpace - usedHeight);
            }
            padding += captionHeight;

            widget.getElement().getStyle().setTop(padding, Unit.PX);
            if (captionStyle != null) {
                captionStyle.setTop(padding - captionHeight, Unit.PX);
            }
        } else {
            // Reset top when changing back to align top
            widget.getElement().getStyle().clearTop();
            if (captionStyle != null) {
                captionStyle.setTop(0, Unit.PX);
            }
        }
    }

    /**
     * Override this method to report the expected outer height to the
     * LayoutManager. By default does nothing.
     *
     * @param allocatedHeight
     *            the height to set (including margins, borders and paddings) in
     *            pixels
     */
    protected void reportActualRelativeHeight(int allocatedHeight) {
        // Default implementation does nothing
    }

    /**
     * Override this method to report the expected outer width to the
     * LayoutManager. By default does nothing.
     *
     * @param allocatedWidth
     *            the width to set (including margins, borders and paddings) in
     *            pixels
     */
    protected void reportActualRelativeWidth(int allocatedWidth) {
        // Default implementation does nothing
    }

    /**
     * Position the slot vertically and set the height and the bottom margin, or
     * horizontally and set the width and the right margin, depending on the
     * indicated direction.
     *
     * @param currentLocation
     *            the top position or the left position for this slot depending
     *            on the indicated direction
     * @param allocatedSpace
     *            how much space is available for this slot in the indicated
     *            direction
     * @param endingMargin
     *            the bottom margin or the right margin this slot should have
     *            depending on the indicated direction (removed if negative)
     * @param isVertical
     *            {@code true} if the positioning should be done vertically,
     *            {@code false} if horizontally
     */
    public void positionInDirection(double currentLocation,
            double allocatedSpace, double endingMargin, boolean isVertical) {
        if (isVertical) {
            positionVertically(currentLocation, allocatedSpace, endingMargin);
        } else {
            positionHorizontally(currentLocation, allocatedSpace, endingMargin);
        }
    }

    /**
     * Returns the widget's height if the indicated direction is vertical, and
     * width if horizontal.
     *
     * @param isVertical
     *            {@code true} if the requested dimension is height,
     *            {@code false} if width
     * @return the widget height or width depending on the indicated direction
     */
    public int getWidgetSizeInDirection(boolean isVertical) {
        return isVertical ? getWidgetHeight() : getWidgetWidth();
    }

    /**
     * Returns how much horizontal space the widget and its caption use.
     *
     * @return the width of the contents in pixels
     */
    public int getUsedWidth() {
        int widgetWidth = getWidgetWidth();
        if (caption == null) {
            return widgetWidth;
        } else if (caption.shouldBePlacedAfterComponent()) {
            return widgetWidth + getCaptionWidth();
        } else {
            return Math.max(widgetWidth, getCaptionWidth());
        }
    }

    /**
     * Returns how much vertical space the widget and its caption use.
     *
     * @return the height of the contents in pixels
     */
    public int getUsedHeight() {
        int widgetHeight = getWidgetHeight();
        if (caption == null) {
            return widgetHeight;
        } else if (caption.shouldBePlacedAfterComponent()) {
            return Math.max(widgetHeight, getCaptionHeight());
        } else {
            return widgetHeight + getCaptionHeight();
        }
    }

    /**
     * Returns how much vertical or horizontal space the widget and its caption
     * use depending on the indicated direction.
     *
     * @param isVertical
     *            {@code true} if the requested dimension is height,
     *            {@code false} if width
     * @return the height or the width of the contents in pixels
     */
    public int getUsedSizeInDirection(boolean isVertical) {
        return isVertical ? getUsedHeight() : getUsedWidth();
    }

    /**
     * Returns the height of the caption, or zero if there is no caption.
     *
     * @return the height of the caption, or zero if not found
     */
    protected abstract int getCaptionHeight();

    /**
     * Returns the width of the caption, or zero if there is no caption.
     *
     * @return the width of the caption, or zero if not found
     */
    protected abstract int getCaptionWidth();

    /**
     * Returns the height of the widget, or zero if there is no caption.
     *
     * @return the height of the widget, or zero if not found
     */
    public abstract int getWidgetHeight();

    /**
     * Returns the width of the widget, or zero if there is no caption.
     *
     * @return the width of the widget, or zero if not found
     */
    public abstract int getWidgetWidth();

    /**
     * Returns whether the height of the widget has been set as undefined.
     *
     * @return {@code true} if the widget height is undefined, {@code false}
     *         otherwise
     */
    public abstract boolean isUndefinedHeight();

    /**
     * Returns whether the width of the widget has been set as undefined.
     *
     * @return {@code true} if the widget width is undefined, {@code false}
     *         otherwise
     */
    public abstract boolean isUndefinedWidth();

    /**
     * Returns whether the height or the width of the widget has been set as
     * undefined depending on the indicated direction.
     *
     * @param isVertical
     *            {@code true} if the requested dimension check is about height,
     *            {@code false} if about width
     * @return {@code true} if the widget height or the widget width is
     *         undefined depending on the indicated direction, {@code false}
     *         otherwise
     */
    public boolean isUndefinedInDirection(boolean isVertical) {
        return isVertical ? isUndefinedHeight() : isUndefinedWidth();
    }

    /**
     * Returns whether the height of the widget has been set as relative.
     *
     * @return {@code true} if the widget height is relative, {@code false}
     *         otherwise
     */
    public abstract boolean isRelativeHeight();

    /**
     * Returns whether the width of the widget has been set as relative.
     *
     * @return {@code true} if the widget width is relative, {@code false}
     *         otherwise
     */
    public abstract boolean isRelativeWidth();

    /**
     * Returns whether the height or the width of the widget has been set as
     * relative depending on the indicated direction.
     *
     * @param isVertical
     *            {@code true} if the requested dimension check is about height,
     *            {@code false} if about width
     * @return {@code true} if the widget height or the widget width is relative
     *         depending on the indicated direction, {@code false} otherwise
     */
    public boolean isRelativeInDirection(boolean isVertical) {
        return isVertical ? isRelativeHeight() : isRelativeWidth();
    }

    /**
     * Returns the wrapper element for the contents of this slot.
     *
     * @return the wrapper element
     */
    @SuppressWarnings("deprecation")
    public com.google.gwt.user.client.Element getWrapperElement() {
        return DOM.asOld(wrapper);
    }

    /**
     * Set how the slot should be expanded relative to the other slots.
     *
     * @param expandRatio
     *            The ratio of the space the slot should occupy
     *
     * @deprecated this value isn't used for anything by default
     */
    @Deprecated
    public void setExpandRatio(double expandRatio) {
        this.expandRatio = expandRatio;
    }

    /**
     * Get the expand ratio for the slot. The expand ratio describes how the
     * slot should be resized compared to other slots in the layout.
     *
     * @return the expand ratio of the slot
     *
     * @see #setExpandRatio(double)
     *
     * @deprecated this value isn't used for anything by default
     */
    @Deprecated
    public double getExpandRatio() {
        return expandRatio;
    }
}
