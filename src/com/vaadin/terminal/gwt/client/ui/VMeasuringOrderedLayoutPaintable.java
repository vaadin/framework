package com.vaadin.terminal.gwt.client.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.CalculatingLayout;
import com.vaadin.terminal.gwt.client.MeasureManager;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public abstract class VMeasuringOrderedLayoutPaintable extends
        VAbstractPaintableWidgetContainer implements CalculatingLayout {

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        if (VCaption.isNeeded(uidl)) {
            VCaption caption = getWidgetForPaintable().captions.get(component);
            if (caption == null) {
                caption = new VCaption(component,
                        getWidgetForPaintable().client);

                Widget widget = component.getWidgetForPaintable();

                getWidgetForPaintable().addCaption(caption, widget);
                getWidgetForPaintable().captions.put(component, caption);

                MeasureManager.MeasuredSize measuredSize = getWidgetForPaintable().client
                        .getMeasuredSize(component);

                measuredSize.registerDependency(caption.getElement());
            }
            caption.updateCaption(uidl);
        } else {
            VCaption removedCaption = getWidgetForPaintable().captions
                    .remove(component);
            if (removedCaption != null) {
                getWidgetForPaintable().remove(removedCaption);
                MeasureManager.MeasuredSize measuredSize = getWidgetForPaintable().client
                        .getMeasuredSize(component);
                measuredSize.deRegisterDependency(removedCaption.getElement());
            }
        }
    }

    @Override
    public VMeasuringOrderedLayout getWidgetForPaintable() {
        return (VMeasuringOrderedLayout) super.getWidgetForPaintable();
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().client = client;
        getWidgetForPaintable().id = uidl.getId();
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        long start = System.currentTimeMillis();
        // long childTime = 0;

        HashSet<Widget> previousChildren = new HashSet<Widget>();
        for (Widget child : getWidgetForPaintable()) {
            if (!(child instanceof VCaption)) {
                previousChildren.add(child);
            }
        }
        // TODO Support reordering elements!
        for (final Iterator<Object> it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL childUIDL = (UIDL) it.next();
            final VPaintableWidget child = client.getPaintable(childUIDL);
            Widget widget = child.getWidgetForPaintable();

            if (widget.getParent() != getWidgetForPaintable()) {
                getWidgetForPaintable().addChildWidget(widget);
            }

            if (!childUIDL.getBooleanAttribute("cached")) {
                child.updateFromUIDL(childUIDL, client);
                client.getMeasuredSize(child).setDirty(true);
            }
            // TODO Update alignments and expand ratios

            previousChildren.remove(widget);
        }

        for (Widget widget : previousChildren) {
            Element wrapper = getWidgetForPaintable().getWrapper(widget);
            VCaption caption = getWidgetForPaintable().captions.remove(widget);
            if (caption != null) {
                getWidgetForPaintable().remove(caption);
            }
            getWidgetForPaintable().remove(widget);
            // Remove the wrapper
            getWidgetForPaintable().getElement().removeChild(wrapper);

            client.unregisterPaintable(VPaintableMap.get(client).getPaintable(
                    widget));
        }

        int bitMask = uidl.getIntAttribute("margins");
        if (getWidgetForPaintable().activeMarginsInfo == null
                || getWidgetForPaintable().activeMarginsInfo.getBitMask() != bitMask) {
            getWidgetForPaintable().activeMarginsInfo = new VMarginInfo(bitMask);
        }

        getWidgetForPaintable().spacing = uidl.getBooleanAttribute("spacing");
        getWidgetForPaintable().expandRatios = uidl
                .getMapAttribute("expandRatios");
        getWidgetForPaintable().alignments = uidl.getMapAttribute("alignments");
        client.getMeasuredSize(this).setDirty(true);
    }

    private int getCaptionWidth(VPaintableWidget child) {
        VCaption caption = getWidgetForPaintable().captions.get(child);
        if (caption == null) {
            return 0;
        } else {
            MeasureManager.MeasuredSize measuredSize = getWidgetForPaintable().client
                    .getMeasuredSize(child);
            return measuredSize.getDependencyWidth(caption.getElement());
        }
    }

    private int getCaptionHeight(VPaintableWidget child) {

        VCaption caption = getWidgetForPaintable().captions.get(child);
        if (caption != null) {
            MeasureManager.MeasuredSize measuredSize = getWidgetForPaintable().client
                    .getMeasuredSize(child);
            int captionHeight = measuredSize.getDependencyHeight(caption
                    .getElement());

            caption.getElement().getStyle()
                    .setMarginTop(-captionHeight, Unit.PX);
            return captionHeight;
        } else {
            return 0;
        }
    }

    private static boolean isRelativeInDirection(Widget widget,
            boolean isVertical) {
        String dimension = getDimensionInDirection(widget, isVertical);
        return dimension != null && dimension.endsWith("%");
    }

    private static String getDimensionInDirection(Widget widget,
            boolean vertical) {
        com.google.gwt.user.client.Element element = widget.getElement();
        Style style = element.getStyle();
        if (vertical) {
            return style.getHeight();
        } else {
            return style.getWidth();
        }
    }

    private static String getSizeProperty(boolean isVertical) {
        return isVertical ? "height" : "width";
    }

    private static String getStartProperty(boolean isVertical) {
        return isVertical ? "top" : "left";
    }

    private static String getMinPropertyName(boolean isVertical) {
        return isVertical ? "minHeight" : "minWidth";
    }

    private static boolean isUndefinedInDirection(Widget widget,
            boolean isVertical) {
        String dimension = getDimensionInDirection(widget, isVertical);
        return dimension == null || dimension.length() == 0;
    }

    private int getMeasuredInDirection(VPaintableWidget paintable,
            boolean isVertical) {
        MeasureManager.MeasuredSize measuredSize = getWidgetForPaintable().client
                .getMeasuredSize(paintable);
        if (isVertical) {
            return measuredSize.getHeight();
        } else {
            return measuredSize.getWidth();
        }
    }

    private static int getAlignmentInDirection(AlignmentInfo alignment,
            boolean isVertical) {
        if (alignment == null) {
            return -1;
        }
        if (isVertical) {
            if (alignment.isTop()) {
                return -1;
            } else if (alignment.isBottom()) {
                return 1;
            } else {
                return 0;
            }
        } else {
            if (alignment.isLeft()) {
                return -1;
            } else if (alignment.isRight()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private void layoutPrimaryDirection() {
        Collection<VPaintableWidget> children = getChildren();

        // First pass - get total expand ratio and allocated size
        int totalAllocated = 0;
        double totalExpand = 0;
        for (VPaintableWidget child : children) {
            Widget widget = child.getWidgetForPaintable();

            totalExpand += getWidgetForPaintable().getExpandRatio(child);

            int captionAllocation;
            if (getWidgetForPaintable().isVertical) {
                captionAllocation = getCaptionHeight(child);
                getWidgetForPaintable().getWrapper(widget).getStyle()
                        .setPaddingTop(captionAllocation, Unit.PX);
            } else {
                captionAllocation = 0;
            }

            if (!isRelativeInDirection(widget,
                    getWidgetForPaintable().isVertical)) {
                totalAllocated += getMeasuredInDirection(child,
                        getWidgetForPaintable().isVertical) + captionAllocation;
            }
        }
        int startMargin = getWidgetForPaintable().getStartMarginInDirection(
                getWidgetForPaintable().isVertical);
        int totalMargins = startMargin
                + getWidgetForPaintable().getEndMarginInDirection(
                        getWidgetForPaintable().isVertical);

        totalAllocated += totalMargins
                + (getWidgetForPaintable().getSpacingInDirection(
                        getWidgetForPaintable().isVertical) * (children.size() - 1));

        Style ownStyle = getWidgetForPaintable().getElement().getStyle();
        double ownSize;
        if (isUndefinedInDirection(getWidgetForPaintable(),
                getWidgetForPaintable().isVertical)) {
            ownSize = totalAllocated;
            ownStyle.setProperty(
                    getMinPropertyName(getWidgetForPaintable().isVertical),
                    totalAllocated, Unit.PX);
        } else {
            ownSize = getMeasuredInDirection(this,
                    getWidgetForPaintable().isVertical);
            ownStyle.clearProperty(getMinPropertyName(getWidgetForPaintable().isVertical));
        }

        double unallocatedSpace = Math.max(0, ownSize - totalAllocated);

        double currentLocation = startMargin;
        for (VPaintableWidget child : children) {
            Widget widget = child.getWidgetForPaintable();
            Element wrapper = getWidgetForPaintable().getWrapper(widget);
            Style wrapperStyle = wrapper.getStyle();

            double childExpandRatio;
            if (totalExpand == 0) {
                childExpandRatio = 1d / children.size();
            } else {
                childExpandRatio = getWidgetForPaintable()
                        .getExpandRatio(child) / totalExpand;
            }

            double extraPixels = unallocatedSpace * childExpandRatio;

            boolean relative = isRelativeInDirection(widget,
                    getWidgetForPaintable().isVertical);

            double size = getMeasuredInDirection(child,
                    getWidgetForPaintable().isVertical);
            int captionHeight = getCaptionHeight(child);

            if (getWidgetForPaintable().isVertical) {
                size += captionHeight;
            } else if (!relative) {
                size = Math.max(size, getCaptionWidth(child));
            }

            double allocatedSpace = extraPixels;
            if (!relative) {
                allocatedSpace += size;
            }

            int alignment = getAlignmentInDirection(getWidgetForPaintable()
                    .getAlignment(child), getWidgetForPaintable().isVertical);

            if (relative) {
                double captionReservation = getWidgetForPaintable().isVertical ? captionHeight
                        : 0;
                wrapperStyle.setProperty(
                        getSizeProperty(getWidgetForPaintable().isVertical),
                        allocatedSpace - captionReservation, Unit.PX);
            } else {
                wrapperStyle
                        .clearProperty(getSizeProperty(getWidgetForPaintable().isVertical));
            }

            double startPosition = currentLocation;
            if (alignment == 0) {
                // Centered
                startPosition += (allocatedSpace - size) / 2;
            } else if (alignment == 1) {
                // Right or bottom
                startPosition += allocatedSpace - size;
            }

            wrapperStyle.setProperty(
                    getStartProperty(getWidgetForPaintable().isVertical),
                    startPosition, Unit.PX);

            currentLocation += allocatedSpace
                    + getWidgetForPaintable().getSpacingInDirection(
                            getWidgetForPaintable().isVertical);
        }
    }

    private void layoutSecondaryDirection() {
        Collection<VPaintableWidget> children = getChildren();

        int maxSize = 0;
        for (VPaintableWidget child : children) {
            Widget widget = child.getWidgetForPaintable();

            int captionAllocation;
            if (!getWidgetForPaintable().isVertical) {
                captionAllocation = getCaptionHeight(child);
                getWidgetForPaintable().getWrapper(widget).getStyle()
                        .setPaddingTop(captionAllocation, Unit.PX);
            } else {
                captionAllocation = 0;
            }

            if (!isRelativeInDirection(widget,
                    !getWidgetForPaintable().isVertical)) {
                int childSize = getMeasuredInDirection(child,
                        !getWidgetForPaintable().isVertical)
                        + captionAllocation;
                maxSize = Math.max(maxSize, childSize);
            }
        }

        int startMargin = getWidgetForPaintable().getStartMarginInDirection(
                !getWidgetForPaintable().isVertical);
        int totalMargins = startMargin
                + getWidgetForPaintable().getEndMarginInDirection(
                        !getWidgetForPaintable().isVertical);

        double availableSpace;
        Style ownStyle = getWidgetForPaintable().getElement().getStyle();

        if (isUndefinedInDirection(getWidgetForPaintable(),
                !getWidgetForPaintable().isVertical)) {
            ownStyle.setProperty(
                    getMinPropertyName(!getWidgetForPaintable().isVertical),
                    maxSize + totalMargins, Unit.PX);
            availableSpace = maxSize;
        } else {
            ownStyle.clearProperty(getMinPropertyName(!getWidgetForPaintable().isVertical));
            availableSpace = getMeasuredInDirection(this,
                    !getWidgetForPaintable().isVertical) - totalMargins;
        }

        for (VPaintableWidget child : children) {
            Widget widget = child.getWidgetForPaintable();
            Element wrapper = getWidgetForPaintable().getWrapper(widget);
            Style wrapperStyle = wrapper.getStyle();

            boolean relative = isRelativeInDirection(widget,
                    !getWidgetForPaintable().isVertical);

            int captionHeight = getCaptionHeight(child);

            double allocatedSize = getMeasuredInDirection(child,
                    !getWidgetForPaintable().isVertical);
            if (!getWidgetForPaintable().isVertical) {
                allocatedSize += captionHeight;
            } else if (!relative) {
                allocatedSize = Math.max(allocatedSize, getCaptionWidth(child));
            }

            int alignment = getAlignmentInDirection(getWidgetForPaintable()
                    .getAlignment(child), !getWidgetForPaintable().isVertical);

            double startPosition = startMargin;
            if (alignment == 0) {
                startPosition += (availableSpace - allocatedSize) / 2;
                // Centered
            } else if (alignment == 1) {
                // Right or bottom
                startPosition += (availableSpace - allocatedSize);
            }

            wrapperStyle.setProperty(
                    getStartProperty(!getWidgetForPaintable().isVertical),
                    startPosition, Unit.PX);

            if (relative) {
                double captionReservation = !getWidgetForPaintable().isVertical ? captionHeight
                        : 0;
                wrapperStyle.setProperty(
                        getSizeProperty(!getWidgetForPaintable().isVertical),
                        availableSpace - captionReservation, Unit.PX);
            } else {
                wrapperStyle
                        .clearProperty(getSizeProperty(!getWidgetForPaintable().isVertical));
            }
        }
    }

    public void updateHorizontalSizes() {
        if (getWidgetForPaintable().isVertical) {
            layoutSecondaryDirection();
        } else {
            layoutPrimaryDirection();
        }
    }

    public void updateVerticalSizes() {
        if (getWidgetForPaintable().isVertical) {
            layoutPrimaryDirection();
        } else {
            layoutSecondaryDirection();
        }
    }
}
