/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.ui.layout.VLayoutSlot;

public class VMeasuringOrderedLayout extends ComplexPanel {

    final boolean isVertical;

    final DivElement spacingMeasureElement;

    protected VMeasuringOrderedLayout(String className, boolean isVertical) {
        DivElement element = Document.get().createDivElement();
        setElement(element);

        spacingMeasureElement = Document.get().createDivElement();
        Style spacingStyle = spacingMeasureElement.getStyle();
        spacingStyle.setPosition(Position.ABSOLUTE);
        getElement().appendChild(spacingMeasureElement);

        setStyleName(className);
        this.isVertical = isVertical;
    }

    public void addOrMove(VLayoutSlot layoutSlot, int index) {
        Widget widget = layoutSlot.getWidget();
        Element wrapperElement = layoutSlot.getWrapperElement();

        Element containerElement = getElement();
        Node childAtIndex = containerElement.getChild(index);
        if (childAtIndex != wrapperElement) {
            // Insert at correct location not attached or at wrong location
            containerElement.insertBefore(wrapperElement, childAtIndex);
            insert(widget, wrapperElement, index, false);
        }

        widget.setLayoutData(layoutSlot);
    }

    private void togglePrefixedStyleName(String name, boolean enabled) {
        if (enabled) {
            addStyleDependentName(name);
        } else {
            removeStyleDependentName(name);
        }
    }

    void updateMarginStyleNames(VMarginInfo marginInfo) {
        togglePrefixedStyleName("margin-top", marginInfo.hasTop());
        togglePrefixedStyleName("margin-right", marginInfo.hasRight());
        togglePrefixedStyleName("margin-bottom", marginInfo.hasBottom());
        togglePrefixedStyleName("margin-left", marginInfo.hasLeft());
    }

    void updateSpacingStyleName(boolean spacingEnabled) {
        String styleName = getStylePrimaryName();
        if (spacingEnabled) {
            spacingMeasureElement.addClassName(styleName + "-spacing-on");
            spacingMeasureElement.removeClassName(styleName + "-spacing-off");
        } else {
            spacingMeasureElement.removeClassName(styleName + "-spacing-on");
            spacingMeasureElement.addClassName(styleName + "-spacing-off");
        }
    }

    public void removeSlot(VLayoutSlot slot) {
        VCaption caption = slot.getCaption();
        if (caption != null) {
            remove(caption);
        }

        remove(slot.getWidget());
        getElement().removeChild(slot.getWrapperElement());
    }

    public VLayoutSlot getSlotForChild(Widget widget) {
        return (VLayoutSlot) widget.getLayoutData();
    }

    public void setCaption(Widget child, VCaption caption) {
        VLayoutSlot slot = getSlotForChild(child);

        if (caption != null) {
            // Logical attach.
            getChildren().add(caption);
        }

        // Physical attach if not null, also removes old caption
        slot.setCaption(caption);

        if (caption != null) {
            // Adopt.
            adopt(caption);
        }
    }

    public int layoutPrimaryDirection(int spacingSize, int allocatedSize,
            int startPadding) {
        int actuallyAllocated = 0;
        double totalExpand = 0;

        int childCount = 0;
        for (Widget child : this) {
            if (child instanceof VCaption) {
                continue;
            }
            childCount++;

            VLayoutSlot slot = getSlotForChild(child);
            totalExpand += slot.getExpandRatio();

            if (!slot.isRelativeInDirection(isVertical)) {
                actuallyAllocated += slot.getUsedSizeInDirection(isVertical);
            }
        }

        actuallyAllocated += spacingSize * (childCount - 1);

        if (allocatedSize == -1) {
            allocatedSize = actuallyAllocated;
        }

        double unallocatedSpace = Math
                .max(0, allocatedSize - actuallyAllocated);

        double currentLocation = startPadding;

        for (Widget child : this) {
            if (child instanceof VCaption) {
                continue;
            }

            VLayoutSlot slot = getSlotForChild(child);

            double childExpandRatio;
            if (totalExpand == 0) {
                childExpandRatio = 1d / childCount;
            } else {
                childExpandRatio = slot.getExpandRatio() / totalExpand;
            }

            double extraPixels = unallocatedSpace * childExpandRatio;
            double endLocation = currentLocation + extraPixels;
            if (!slot.isRelativeInDirection(isVertical)) {
                endLocation += slot.getUsedSizeInDirection(isVertical);
            }

            /*
             * currentLocation and allocatedSpace are used with full precision
             * to avoid missing pixels in the end. The pixel dimensions passed
             * to the DOM are still rounded. Otherwise e.g. 10.5px start
             * position + 10.5px space might be cause the component to go 1px
             * beyond the edge as the effect of the browser's rounding may cause
             * something similar to 11px + 11px.
             * 
             * It's most efficient to use doubles all the way because native
             * javascript emulates other number types using doubles.
             */
            double roundedLocation = Math.round(currentLocation);

            /*
             * Space is calculated as the difference between rounded start and
             * end locations. Just rounding the space would cause e.g. 10.5px +
             * 10.5px = 21px -> 11px + 11px = 22px but in this way we get 11px +
             * 10px = 21px.
             */
            double roundedSpace = Math.round(endLocation) - roundedLocation;

            slot.positionInDirection(roundedLocation, roundedSpace, isVertical);

            currentLocation = endLocation + spacingSize;
        }

        return allocatedSize;
    }

    public int layoutSecondaryDirection(int allocatedSize, int startPadding) {
        int maxSize = 0;
        for (Widget child : this) {
            if (child instanceof VCaption) {
                continue;
            }

            VLayoutSlot slot = getSlotForChild(child);
            if (!slot.isRelativeInDirection(!isVertical)) {
                maxSize = Math.max(maxSize,
                        slot.getUsedSizeInDirection(!isVertical));
            }
        }

        if (allocatedSize == -1) {
            allocatedSize = maxSize;
        }

        for (Widget child : this) {
            if (child instanceof VCaption) {
                continue;
            }

            VLayoutSlot slot = getSlotForChild(child);
            slot.positionInDirection(startPadding, allocatedSize, !isVertical);
        }

        return allocatedSize;
    }
}
