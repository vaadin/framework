/* 
@VaadinApache2LicenseForJavaFiles@
 */
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

    public VLayoutSlot(String baseClassName, Widget widget) {
        this.widget = widget;

        wrapper.setClassName(baseClassName + "-slot");
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
            Style style = caption.getElement().getStyle();
            style.setPosition(Position.ABSOLUTE);
            style.setTop(0, Unit.PX);
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

        double availableWidth = allocatedSpace;

        VCaption caption = getCaption();
        Style captionStyle = caption != null ? caption.getElement().getStyle()
                : null;
        int captionWidth = getCaptionWidth();

        boolean captionAboveCompnent;
        if (caption == null) {
            captionAboveCompnent = false;
            style.clearPaddingRight();
        } else {
            captionAboveCompnent = !caption.shouldBePlacedAfterComponent();
            if (!captionAboveCompnent) {
                style.setPaddingRight(captionWidth, Unit.PX);
                availableWidth -= captionWidth;
                captionStyle.clearLeft();
                captionStyle.setRight(0, Unit.PX);
            } else {
                style.clearPaddingRight();
                captionStyle.setLeft(0, Unit.PX);
                captionStyle.clearRight();
            }
        }

        if (isRelativeWidth()) {
            style.setPropertyPx("width", (int) availableWidth);
        } else {
            style.clearProperty("width");
        }

        AlignmentInfo alignment = getAlignment();
        if (!alignment.isLeft()) {
            double usedWidth;
            if (isRelativeWidth()) {
                String percentWidth = getWidget().getElement().getStyle()
                        .getWidth();
                double percentage = parsePercent(percentWidth);
                usedWidth = availableWidth * (percentage / 100);
            } else {
                usedWidth = getWidgetWidth();
            }
            if (alignment.isHorizontalCenter()) {
                currentLocation += (allocatedSpace - usedWidth) / 2d;
                if (captionAboveCompnent) {
                    captionStyle.setLeft((usedWidth - captionWidth) / 2,
                            Unit.PX);
                }
            } else {
                currentLocation += (allocatedSpace - usedWidth);
                if (captionAboveCompnent) {
                    captionStyle.setLeft(usedWidth - captionWidth, Unit.PX);
                }
            }
        } else {
            if (captionAboveCompnent) {
                captionStyle.setLeft(0, Unit.PX);
            }
        }

        style.setLeft(currentLocation, Unit.PX);
    }

    private double parsePercent(String size) {
        return Double.parseDouble(size.replaceAll("%", ""));
    }

    public void positionVertically(double currentLocation, double allocatedSpace) {
        Style style = wrapper.getStyle();

        double contentHeight = allocatedSpace;

        int captionHeight;
        VCaption caption = getCaption();
        if (caption == null || caption.shouldBePlacedAfterComponent()) {
            style.clearPaddingTop();
            captionHeight = 0;
        } else {
            captionHeight = getCaptionHeight();
            contentHeight -= captionHeight;
            style.setPaddingTop(captionHeight, Unit.PX);
        }

        if (isRelativeHeight()) {
            style.setHeight(contentHeight, Unit.PX);
        } else {
            style.clearHeight();
        }

        AlignmentInfo alignment = getAlignment();
        if (!alignment.isTop()) {
            double usedHeight;
            if (isRelativeHeight()) {
                String height = getWidget().getElement().getStyle().getHeight();
                double percentage = parsePercent(height);
                usedHeight = captionHeight + contentHeight * (percentage / 100);
            } else {
                usedHeight = getUsedHeight();
            }
            if (alignment.isVerticalCenter()) {
                currentLocation += (allocatedSpace - usedHeight) / 2d;
            } else {
                currentLocation += (allocatedSpace - usedHeight);
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
        int widgetWidth = getWidgetWidth();
        if (caption == null) {
            return widgetWidth;
        } else if (caption.shouldBePlacedAfterComponent()) {
            return widgetWidth + getCaptionWidth();
        } else {
            return Math.max(widgetWidth, getCaptionWidth());
        }
    }

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