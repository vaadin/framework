/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.CalculatingLayout;
import com.vaadin.terminal.gwt.client.MeasuredSize;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ValueMap;
import com.vaadin.terminal.gwt.client.ui.layout.VLayoutSlot;
import com.vaadin.terminal.gwt.client.ui.layout.VPaintableLayoutSlot;

public abstract class VMeasuringOrderedLayoutPaintable extends
        VAbstractPaintableWidgetContainer implements CalculatingLayout {

    public VMeasuringOrderedLayoutPaintable() {
        getMeasuredSize().registerDependency(
                getWidgetForPaintable().spacingMeasureElement);
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        VMeasuringOrderedLayout layout = getWidgetForPaintable();
        if (VCaption.isNeeded(uidl)) {
            VLayoutSlot layoutSlot = layout.getSlotForChild(component
                    .getWidgetForPaintable());
            VCaption caption = layoutSlot.getCaption();
            if (caption == null) {
                caption = new VCaption(component, getConnection());

                Widget widget = component.getWidgetForPaintable();

                layout.setCaption(widget, caption);
            }
            caption.updateCaption(uidl);
        } else {
            layout.setCaption(component.getWidgetForPaintable(), null);
        }
    }

    @Override
    public VMeasuringOrderedLayout getWidgetForPaintable() {
        return (VMeasuringOrderedLayout) super.getWidgetForPaintable();
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        HashSet<VPaintableWidget> previousChildren = new HashSet<VPaintableWidget>(
                getChildren());

        VMeasuringOrderedLayout layout = getWidgetForPaintable();

        ValueMap expandRatios = uidl.getMapAttribute("expandRatios");
        ValueMap alignments = uidl.getMapAttribute("alignments");

        int currentIndex = 0;
        // TODO Support reordering elements!
        for (final Iterator<Object> it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL childUIDL = (UIDL) it.next();
            final VPaintableWidget child = client.getPaintable(childUIDL);
            Widget widget = child.getWidgetForPaintable();

            VLayoutSlot slot = layout.getSlotForChild(widget);

            if (widget.getParent() != layout) {
                slot = new VPaintableLayoutSlot(getWidgetForPaintable()
                        .getStylePrimaryName(), child);
            }
            layout.addOrMove(slot, currentIndex++);

            String pid = child.getId();

            AlignmentInfo alignment;
            if (alignments.containsKey(pid)) {
                alignment = new AlignmentInfo(alignments.getInt(pid));
            } else {
                alignment = AlignmentInfo.TOP_LEFT;
            }
            slot.setAlignment(alignment);

            double expandRatio;
            if (expandRatios.containsKey(pid)) {
                expandRatio = expandRatios.getRawNumber(pid);
            } else {
                expandRatio = 0;
            }
            slot.setExpandRatio(expandRatio);

            if (!childUIDL.getBooleanAttribute("cached")) {
                child.updateFromUIDL(childUIDL, client);
            }

            previousChildren.remove(child);
        }

        for (VPaintableWidget child : previousChildren) {
            Widget widget = child.getWidgetForPaintable();

            // Don't remove and unregister if it has been moved to a different
            // parent. Slot element will be left behind, but that is taken care
            // of later
            if (widget.getParent() == getWidgetForPaintable()) {
                layout.removeSlot(layout.getSlotForChild(widget));

                VPaintableMap vPaintableMap = VPaintableMap.get(client);
                vPaintableMap.unregisterPaintable(child);
            }
        }

        // Remove empty layout slots left behind after children have moved to
        // other paintables
        while (true) {
            int childCount = layout.getElement().getChildCount();
            if (childCount <= 1) {
                // Stop if no more slots (spacing element is always present)
                break;
            }

            Node lastSlot = layout.getElement().getChild(childCount - 2);
            if (lastSlot.getChildCount() == 0) {
                // Remove if empty
                lastSlot.removeFromParent();
            } else {
                // Stop searching when last slot is not empty
                break;
            }
        }

        int bitMask = uidl.getIntAttribute("margins");
        layout.updateMarginStyleNames(new VMarginInfo(bitMask));

        layout.updateSpacingStyleName(uidl.getBooleanAttribute("spacing"));

        getMeasuredSize().setHeightNeedsUpdate();
        getMeasuredSize().setWidthNeedsUpdate();
    }

    private int getSizeForInnerSize(int size, boolean isVertical) {
        MeasuredSize measuredSize = getMeasuredSize();
        if (isVertical) {
            return size + measuredSize.getBorderHeight()
                    + measuredSize.getPaddingHeight();
        } else {
            return size + measuredSize.getBorderWidth()
                    + measuredSize.getPaddingWidth();
        }
    }

    private static String getSizeProperty(boolean isVertical) {
        return isVertical ? "height" : "width";
    }

    private boolean isUndefinedInDirection(boolean isVertical) {
        if (isVertical) {
            return isUndefinedHeight();
        } else {
            return isUndefinedWidth();
        }
    }

    private int getInnerSizeInDirection(boolean isVertical) {
        if (isVertical) {
            return getMeasuredSize().getInnerHeight();
        } else {
            return getMeasuredSize().getInnerWidth();
        }
    }

    private void layoutPrimaryDirection() {
        VMeasuringOrderedLayout layout = getWidgetForPaintable();
        boolean isVertical = layout.isVertical;
        boolean isUndefined = isUndefinedInDirection(isVertical);

        int startPadding = getStartPadding(isVertical);
        int spacingSize = getSpacingInDirection(isVertical);
        int allocatedSize;

        if (isUndefined) {
            allocatedSize = -1;
        } else {
            allocatedSize = getInnerSizeInDirection(isVertical);
        }

        allocatedSize = layout.layoutPrimaryDirection(spacingSize,
                allocatedSize, startPadding);

        Style ownStyle = getWidgetForPaintable().getElement().getStyle();
        if (isUndefined) {
            ownStyle.setPropertyPx(getSizeProperty(isVertical),
                    getSizeForInnerSize(allocatedSize, isVertical));
        } else {
            ownStyle.setProperty(getSizeProperty(isVertical),
                    getDefinedSize(isVertical));
        }
    }

    private int getSpacingInDirection(boolean isVertical) {
        if (isVertical) {
            return getMeasuredSize().getDependencyOuterHeight(
                    getWidgetForPaintable().spacingMeasureElement);
        } else {
            return getMeasuredSize().getDependencyOuterWidth(
                    getWidgetForPaintable().spacingMeasureElement);
        }
    }

    private void layoutSecondaryDirection() {
        VMeasuringOrderedLayout layout = getWidgetForPaintable();
        boolean isVertical = layout.isVertical;
        boolean isUndefined = isUndefinedInDirection(!isVertical);

        int startPadding = getStartPadding(!isVertical);

        int allocatedSize;
        if (isUndefined) {
            allocatedSize = -1;
        } else {
            allocatedSize = getInnerSizeInDirection(!isVertical);
        }

        allocatedSize = layout.layoutSecondaryDirection(allocatedSize,
                startPadding);

        Style ownStyle = getWidgetForPaintable().getElement().getStyle();

        if (isUndefined) {
            ownStyle.setPropertyPx(
                    getSizeProperty(!getWidgetForPaintable().isVertical),
                    getSizeForInnerSize(allocatedSize,
                            !getWidgetForPaintable().isVertical));
        } else {
            ownStyle.setProperty(
                    getSizeProperty(!getWidgetForPaintable().isVertical),
                    getDefinedSize(!getWidgetForPaintable().isVertical));
        }
    }

    private String getDefinedSize(boolean isVertical) {
        if (isVertical) {
            return getDeclaredHeight();
        } else {
            return getDeclaredWidth();
        }
    }

    private int getStartPadding(boolean isVertical) {
        if (isVertical) {
            return getMeasuredSize().getPaddingTop();
        } else {
            return getMeasuredSize().getPaddingLeft();
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
