package com.vaadin.terminal.gwt.client.ui.layout;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.ui.AlignmentInfo;

public abstract class VLayoutSlot {

    private final Element wrapper = Document.get().createDivElement().cast();

    private AlignmentInfo alignment;
    private VCaption caption;
    private final Widget widget;

    private double expandRatio;

    public VLayoutSlot(Widget widget) {
        this.widget = widget;

        wrapper.getStyle().setPosition(Position.ABSOLUTE);
    }

    public VCaption getCaption() {
        return caption;
    }

    public void setCaption(VCaption caption) {
        if (this.caption != null) {
            this.caption.removeFromParent();
        }
        this.caption = caption;
        if (caption != null) {
            // Physical attach.
            DOM.insertBefore(wrapper, caption.getElement(), widget.getElement());
            caption.getElement().getStyle().setPosition(Position.ABSOLUTE);
        }
    }

    public AlignmentInfo getAlignment() {
        return alignment;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setAlignment(AlignmentInfo alignment) {
        this.alignment = alignment;
    }

    public void positionHorizontally(double currentLocation,
            double allocatedSpace) {
        Style style = wrapper.getStyle();

        if (isRelativeWidth()) {
            style.setWidth(allocatedSpace, Unit.PX);
        } else {
            style.setWidth(getUsedWidth(), Unit.PX);
        }

        VCaption caption = getCaption();
        Style captionStyle = caption != null ? caption.getElement().getStyle()
                : null;

        AlignmentInfo alignment = getAlignment();
        if (!alignment.isLeft()) {
            double usedWidth = getWidgetWidth();
            if (alignment.isHorizontalCenter()) {
                currentLocation += (allocatedSpace - usedWidth) / 2d;
                if (captionStyle != null) {
                    double captionWidth = getCaptionWidth();
                    captionStyle.setLeft(usedWidth / 2 - (captionWidth / 2),
                            Unit.PX);
                    captionStyle.clearRight();
                }
            } else {
                currentLocation += (allocatedSpace - usedWidth);
                if (captionStyle != null) {
                    captionStyle.clearLeft();
                    captionStyle.setRight(0, Unit.PX);
                }
            }
        } else {
            if (captionStyle != null) {
                captionStyle.setLeft(0, Unit.PX);
                captionStyle.clearRight();
            }
        }

        style.setLeft(currentLocation, Unit.PX);
    }

    public void positionVertically(double currentLocation, double allocatedSpace) {
        Style style = wrapper.getStyle();

        VCaption caption = getCaption();
        double captionHeight = caption != null ? getCaptionHeight() : 0;
        double contentHeight = allocatedSpace - captionHeight;

        if (isRelativeHeight()) {
            style.setHeight(contentHeight, Unit.PX);
        } else {
            style.clearHeight();
        }

        if (caption != null) {
            style.setPaddingTop(getCaptionHeight(), Unit.PX);
            caption.getElement().getStyle().setTop(0, Unit.PX);
        } else {
            style.clearPaddingTop();
        }

        AlignmentInfo alignment = getAlignment();
        if (!alignment.isTop()) {
            double actualHeight = getWidgetHeight() + captionHeight;
            if (alignment.isVerticalCenter()) {
                currentLocation += (allocatedSpace - actualHeight) / 2d;
            } else {
                currentLocation += (allocatedSpace - actualHeight);
            }
        }

        style.setTop(currentLocation, Unit.PX);
    }

    public void positionInDirection(double currentLocation,
            double allocatedSpace, boolean isVertical) {
        if (isVertical) {
            positionVertically(currentLocation, allocatedSpace);
        } else {
            positionHorizontally(currentLocation, allocatedSpace);
        }
    }

    public int getWidgetSizeInDirection(boolean isVertical) {
        return isVertical ? getWidgetHeight() : getWidgetWidth();
    }

    public int getUsedWidth() {
        int captionWidth = (caption != null ? getCaptionWidth() : 0);
        return Math.max(captionWidth, getWidgetWidth());
    }

    public int getUsedHeight() {
        int captionHeight = (caption != null ? getCaptionHeight() : 0);
        return getWidgetHeight() + captionHeight;
    }

    public int getUsedSizeInDirection(boolean isVertical) {
        return isVertical ? getUsedHeight() : getUsedWidth();
    }

    protected abstract int getCaptionHeight();

    protected abstract int getCaptionWidth();

    public abstract int getWidgetHeight();

    public abstract int getWidgetWidth();

    public abstract boolean isUndefinedHeight();

    public abstract boolean isUndefinedWidth();

    public boolean isUndefinedInDirection(boolean isVertical) {
        return isVertical ? isUndefinedHeight() : isUndefinedWidth();
    }

    public abstract boolean isRelativeHeight();

    public abstract boolean isRelativeWidth();

    public boolean isRelativeInDirection(boolean isVertical) {
        return isVertical ? isRelativeHeight() : isRelativeWidth();
    }

    public Element getWrapperElement() {
        return wrapper;
    }

    public void setExpandRatio(double expandRatio) {
        this.expandRatio = expandRatio;
    }

    public double getExpandRatio() {
        return expandRatio;
    }
}