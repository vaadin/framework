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
            double allocatedSpace, double paddingRight) {
        Style style = wrapper.getStyle();

        double availableWidth = allocatedSpace;

        VCaption caption = getCaption();
        Style captionStyle = caption != null ? caption.getElement().getStyle()
                : null;
        int captionWidth = getCaptionWidth();

        boolean captionAboveCompnent;
        if (caption == null) {
            captionAboveCompnent = false;
        } else {
            captionAboveCompnent = !caption.shouldBePlacedAfterComponent();
            if (!captionAboveCompnent) {
                availableWidth -= captionWidth;
                captionStyle.clearLeft();
                captionStyle.setRight(paddingRight, Unit.PX);
                paddingRight += captionWidth;
            } else {
                captionStyle.setLeft(0, Unit.PX);
                captionStyle.clearRight();
            }
        }

        if (paddingRight > 0) {
            style.setPaddingRight(paddingRight, Unit.PX);
        } else {
            style.clearPaddingRight();
        }

        if (isRelativeWidth()) {
            style.setPropertyPx("width", (int) availableWidth);
        } else {
            style.clearProperty("width");
        }

        double allocatedContentWidth = 0;
        if (isRelativeWidth()) {
            String percentWidth = getWidget().getElement().getStyle()
                    .getWidth();
            double percentage = parsePercent(percentWidth);
            allocatedContentWidth = availableWidth * (percentage / 100);
            reportActualRelativeWidth(Math.round((float) allocatedContentWidth));
        }

        AlignmentInfo alignment = getAlignment();
        if (!alignment.isLeft()) {
            double usedWidth;
            if (isRelativeWidth()) {
                usedWidth = allocatedContentWidth;
            } else {
                usedWidth = getWidgetWidth();
            }
            if (alignment.isHorizontalCenter()) {
                currentLocation += (allocatedSpace - usedWidth) / 2d;
                if (captionAboveCompnent) {
                    captionStyle.setLeft(
                            Math.round(usedWidth - captionWidth) / 2, Unit.PX);
                }
            } else {
                currentLocation += (allocatedSpace - usedWidth);
                if (captionAboveCompnent) {
                    captionStyle.setLeft(Math.round(usedWidth - captionWidth),
                            Unit.PX);
                }
            }
        } else {
            if (captionAboveCompnent) {
                captionStyle.setLeft(0, Unit.PX);
            }
        }

        style.setLeft(Math.round(currentLocation), Unit.PX);
    }

    private double parsePercent(String size) {
        return Double.parseDouble(size.replaceAll("%", ""));
    }

    public void positionVertically(double currentLocation,
            double allocatedSpace, double paddingBottom) {
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
            if (contentHeight < 0) {
                contentHeight = 0;
            }
            style.setPaddingTop(captionHeight, Unit.PX);
        }

        if (paddingBottom > 0) {
            style.setPaddingBottom(paddingBottom, Unit.PX);
        } else {
            style.clearPaddingBottom();
        }

        if (isRelativeHeight()) {
            style.setHeight(contentHeight, Unit.PX);
        } else {
            style.clearHeight();
        }

        double allocatedContentHeight = 0;
        if (isRelativeHeight()) {
            String height = getWidget().getElement().getStyle().getHeight();
            double percentage = parsePercent(height);
            allocatedContentHeight = contentHeight * (percentage / 100);
            reportActualRelativeHeight(Math
                    .round((float) allocatedContentHeight));
        }

        AlignmentInfo alignment = getAlignment();
        if (!alignment.isTop()) {
            double usedHeight;
            if (isRelativeHeight()) {
                usedHeight = captionHeight + allocatedContentHeight;
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

    protected void reportActualRelativeHeight(int allocatedHeight) {
        // Default implementation does nothing
    }

    protected void reportActualRelativeWidth(int allocatedWidth) {
        // Default implementation does nothing
    }

    public void positionInDirection(double currentLocation,
            double allocatedSpace, double endingPadding, boolean isVertical) {
        if (isVertical) {
            positionVertically(currentLocation, allocatedSpace, endingPadding);
        } else {
            positionHorizontally(currentLocation, allocatedSpace, endingPadding);
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