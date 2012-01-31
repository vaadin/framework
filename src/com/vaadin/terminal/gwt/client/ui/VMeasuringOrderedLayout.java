/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.MeasureManager;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ValueMap;

public class VMeasuringOrderedLayout extends ComplexPanel implements Container,
        RequiresResize {

    public static final String CLASSNAME = "v-orderedlayout";

    private static final int MARGIN_SIZE = 20;

    private final boolean isVertical;

    private ApplicationConnection client;

    private String id;

    private RenderSpace space;

    private ValueMap expandRatios;

    private ValueMap alignments;

    private Map<VPaintableWidget, VCaption> captions = new HashMap<VPaintableWidget, VCaption>();

    private boolean spacing;

    private VMarginInfo activeMarginsInfo;

    protected VMeasuringOrderedLayout(String className, boolean isVertical) {
        DivElement element = Document.get().createDivElement();
        setElement(element);
        // TODO These should actually be defined in css
        Style style = element.getStyle();
        style.setOverflow(Overflow.HIDDEN);
        style.setPosition(Position.RELATIVE);

        setStyleName(className);
        this.isVertical = isVertical;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        id = uidl.getId();
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        long start = System.currentTimeMillis();
        // long childTime = 0;

        HashSet<Widget> previousChildren = new HashSet<Widget>();
        for (Widget child : this) {
            if (!(child instanceof VCaption)) {
                previousChildren.add(child);
            }
        }
        // TODO Support reordering elements!
        for (final Iterator<Object> it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL childUIDL = (UIDL) it.next();
            final VPaintableWidget child = client.getPaintable(childUIDL);
            Widget widget = child.getWidgetForPaintable();

            if (widget.getParent() != this) {
                DivElement wrapper = Document.get().createDivElement();
                wrapper.getStyle().setPosition(Position.ABSOLUTE);
                getElement().appendChild(wrapper);
                add(widget, wrapper);
            }

            if (!childUIDL.getBooleanAttribute("cached")) {
                child.updateFromUIDL(childUIDL, client);
                client.getMeasuredSize(child).setDirty(true);
            }
            // TODO Update alignments and expand ratios

            previousChildren.remove(widget);
        }

        for (Widget widget : previousChildren) {
            Element wrapper = getWrapper(widget);
            VCaption caption = captions.remove(widget);
            if (caption != null) {
                remove(caption);
            }
            remove(widget);
            // Remove the wrapper
            getElement().removeChild(wrapper);

            client.unregisterPaintable(VPaintableMap.get(client).getPaintable(
                    widget));
        }

        int bitMask = uidl.getIntAttribute("margins");
        if (activeMarginsInfo == null
                || activeMarginsInfo.getBitMask() != bitMask) {
            activeMarginsInfo = new VMarginInfo(bitMask);
        }

        spacing = uidl.getBooleanAttribute("spacing");
        expandRatios = uidl.getMapAttribute("expandRatios");
        alignments = uidl.getMapAttribute("alignments");
        client.getMeasuredSize(this).setDirty(true);
    }

    private static Element getWrapper(Widget widget) {
        return widget.getElement().getParentElement();
    }

    private void add(Widget widget, DivElement wrapper) {
        add(widget, (com.google.gwt.user.client.Element) wrapper.cast());
    }

    public void onResize() {
        requestLayout(Collections.<Widget> emptySet());
    }

    private static boolean isUndefinedInDirection(Widget widget,
            boolean isVertical) {
        String dimension = getDimensionInDirection(widget, isVertical);
        return dimension == null || dimension.length() == 0;
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

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        throw new UnsupportedOperationException();
    }

    public boolean hasChildComponent(Widget component) {
        return component.getParent() == this;
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        if (VCaption.isNeeded(uidl)) {
            VCaption caption = captions.get(component);
            if (caption == null) {
                caption = new VCaption(component, client);

                Widget widget = (Widget) component;
                Element wrapper = getWrapper(widget);

                // Logical attach.
                getChildren().add(caption);

                // Physical attach.
                DOM.insertBefore(
                        (com.google.gwt.user.client.Element) wrapper.cast(),
                        caption.getElement(), widget.getElement());

                // Adopt.
                adopt(caption);
                captions.put(component, caption);
            }
            caption.updateCaption(uidl);
        } else {
            VCaption removedCaption = captions.remove(component);
            if (removedCaption != null) {
                remove(removedCaption);
                MeasureManager.MeasuredSize measuredSize = client
                        .getMeasuredSize(component);
                measuredSize.setCaptionHeight(0);
                measuredSize.setCaptionWidth(0);
            }
        }

    }

    private void layoutPrimaryDirection() {
        Collection<VPaintableWidget> children = MeasureManager.getChildren(
                this, client);

        // First pass - get total expand ratio and allocated size
        int totalAllocated = 0;
        double totalExpand = 0;
        for (VPaintableWidget child : children) {
            Widget widget = child.getWidgetForPaintable();

            totalExpand += getExpandRatio(child);

            int captionAllocation;
            if (isVertical) {
                captionAllocation = getCaptionHeight(child);
                getWrapper(widget).getStyle().setPaddingTop(captionAllocation,
                        Unit.PX);
            } else {
                captionAllocation = 0;
            }

            if (!isRelativeInDirection(widget, isVertical)) {
                totalAllocated += getMeasuredInDirection(child, isVertical)
                        + captionAllocation;
            }
        }
        int startMargin = getStartMarginInDirection(isVertical);
        int totalMargins = startMargin + getEndMarginInDirection(isVertical);

        totalAllocated += totalMargins
                + (getSpacingInDirection(isVertical) * (children.size() - 1));

        Style ownStyle = getElement().getStyle();
        double ownSize;
        if (isUndefinedInDirection(this, isVertical)) {
            ownSize = totalAllocated;
            ownStyle.setProperty(getMinPropertyName(isVertical),
                    totalAllocated, Unit.PX);
        } else {
            ownSize = getMeasuredInDirection(this, isVertical);
            ownStyle.clearProperty(getMinPropertyName(isVertical));
        }

        double unallocatedSpace = Math.max(0, ownSize - totalAllocated);

        double currentLocation = startMargin;
        for (VPaintableWidget child : children) {
            Widget widget = child.getWidgetForPaintable();
            Element wrapper = getWrapper(widget);
            Style wrapperStyle = wrapper.getStyle();

            double childExpandRatio;
            if (totalExpand == 0) {
                childExpandRatio = 1d / children.size();
            } else {
                childExpandRatio = getExpandRatio(child) / totalExpand;
            }

            double extraPixels = unallocatedSpace * childExpandRatio;

            boolean relative = isRelativeInDirection(widget, isVertical);

            double size = getMeasuredInDirection(child, isVertical);
            int captionHeight = getCaptionHeight(child);

            if (isVertical) {
                size += captionHeight;
            } else if (!relative) {
                size = Math.max(size, getCaptionWidth(child));
            }

            double allocatedSpace = extraPixels;
            if (!relative) {
                allocatedSpace += size;
            }

            int alignment = getAlignmentInDirection(getAlignment(child),
                    isVertical);

            if (relative) {
                double captionReservation = isVertical ? captionHeight : 0;
                wrapperStyle.setProperty(getSizeProperty(isVertical),
                        allocatedSpace - captionReservation, Unit.PX);
            } else {
                wrapperStyle.clearProperty(getSizeProperty(isVertical));
            }

            double startPosition = currentLocation;
            if (alignment == 0) {
                // Centered
                startPosition += (allocatedSpace - size) / 2;
            } else if (alignment == 1) {
                // Right or bottom
                startPosition += allocatedSpace - size;
            }

            wrapperStyle.setProperty(getStartProperty(isVertical),
                    startPosition, Unit.PX);

            currentLocation += allocatedSpace
                    + getSpacingInDirection(isVertical);
        }
    }

    private int getEndMarginInDirection(boolean isVertical) {
        if (isVertical) {
            return activeMarginsInfo.hasBottom() ? MARGIN_SIZE : 0;
        } else {
            return activeMarginsInfo.hasRight() ? MARGIN_SIZE : 0;
        }
    }

    private int getStartMarginInDirection(boolean isVertical) {
        if (isVertical) {
            return activeMarginsInfo.hasTop() ? MARGIN_SIZE : 0;
        } else {
            return activeMarginsInfo.hasLeft() ? MARGIN_SIZE : 0;
        }
    }

    private void layoutSecondaryDirection() {
        Collection<VPaintableWidget> children = MeasureManager.getChildren(
                this, client);

        int maxSize = 0;
        for (VPaintableWidget child : children) {
            Widget widget = child.getWidgetForPaintable();

            int captionAllocation;
            if (!isVertical) {
                captionAllocation = getCaptionHeight(child);
                getWrapper(widget).getStyle().setPaddingTop(captionAllocation,
                        Unit.PX);
            } else {
                captionAllocation = 0;
            }

            if (!isRelativeInDirection(widget, !isVertical)) {
                int childSize = getMeasuredInDirection(child, !isVertical)
                        + captionAllocation;
                maxSize = Math.max(maxSize, childSize);
            }
        }

        int startMargin = getStartMarginInDirection(!isVertical);
        int totalMargins = startMargin + getEndMarginInDirection(!isVertical);

        double availableSpace;
        Style ownStyle = getElement().getStyle();

        if (isUndefinedInDirection(this, !isVertical)) {
            ownStyle.setProperty(getMinPropertyName(!isVertical), maxSize
                    + totalMargins, Unit.PX);
            availableSpace = maxSize;
        } else {
            ownStyle.clearProperty(getMinPropertyName(!isVertical));
            availableSpace = getMeasuredInDirection(this, !isVertical)
                    - totalMargins;
        }

        for (VPaintableWidget child : children) {
            Widget widget = child.getWidgetForPaintable();
            Element wrapper = getWrapper(widget);
            Style wrapperStyle = wrapper.getStyle();

            boolean relative = isRelativeInDirection(widget, !isVertical);

            int captionHeight = getCaptionHeight(child);

            double allocatedSize = getMeasuredInDirection(child, !isVertical);
            if (!isVertical) {
                allocatedSize += captionHeight;
            } else if (!relative) {
                allocatedSize = Math.max(allocatedSize, getCaptionWidth(child));
            }

            int alignment = getAlignmentInDirection(getAlignment(child),
                    !isVertical);

            double startPosition = startMargin;
            if (alignment == 0) {
                startPosition += (availableSpace - allocatedSize) / 2;
                // Centered
            } else if (alignment == 1) {
                // Right or bottom
                startPosition += (availableSpace - allocatedSize);
            }

            wrapperStyle.setProperty(getStartProperty(!isVertical),
                    startPosition, Unit.PX);

            if (relative) {
                double captionReservation = !isVertical ? captionHeight : 0;
                wrapperStyle.setProperty(getSizeProperty(!isVertical),
                        availableSpace - captionReservation, Unit.PX);
            } else {
                wrapperStyle.clearProperty(getSizeProperty(!isVertical));
            }
        }
    }

    public boolean requestLayout(Set<Widget> changed) {
        layoutPrimaryDirection();
        layoutSecondaryDirection();

        // Doesn't matter right now...
        return true;
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

    private int getSpacingInDirection(boolean isVertical) {
        if (spacing) {
            return 20;
        } else {
            return 0;
        }
    }

    private int getCaptionWidth(VPaintableWidget child) {
        MeasureManager.MeasuredSize measuredSize = client
                .getMeasuredSize(child);
        return measuredSize.getCaptionWidth();
    }

    private int getCaptionHeight(VPaintableWidget child) {
        MeasureManager.MeasuredSize measuredSize = client
                .getMeasuredSize(child);
        int captionHeight = measuredSize.getCaptionHeight();

        VCaption caption = captions.get(child);
        if (caption != null) {
            caption.getElement().getStyle()
                    .setMarginTop(-captionHeight, Unit.PX);
        }
        return captionHeight;
    }

    private AlignmentInfo getAlignment(VPaintableWidget child) {
        String pid = VPaintableMap.get(client).getPid(child);
        if (alignments.containsKey(pid)) {
            return new AlignmentInfo(alignments.getInt(pid));
        } else {
            return AlignmentInfo.TOP_LEFT;
        }
    }

    private double getExpandRatio(VPaintableWidget child) {
        String pid = VPaintableMap.get(client).getPid(child);
        if (expandRatios.containsKey(pid)) {
            return expandRatios.getRawNumber(pid);
        } else {
            return 0;
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

    private int getMeasuredInDirection(VPaintableWidget paintable,
            boolean isVertical) {
        MeasureManager.MeasuredSize measuredSize = client
                .getMeasuredSize(paintable);
        if (isVertical) {
            return measuredSize.getHeight();
        } else {
            return measuredSize.getWidth();
        }
    }

    public Collection<VCaption> getChildCaptions() {
        return captions.values();
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        // Concept borrowed from CSS layout
        if (space == null) {
            space = new RenderSpace(-1, -1) {
                @Override
                public int getWidth() {
                    return getOffsetWidth();
                }

                @Override
                public int getHeight() {
                    return getOffsetHeight();
                }
            };
        }
        return space;
    }

    public Widget getWidgetForPaintable() {
        return this;
    }
}
