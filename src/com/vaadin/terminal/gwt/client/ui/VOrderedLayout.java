/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ValueMap;
import com.vaadin.terminal.gwt.client.ui.layout.CellBasedLayout;
import com.vaadin.terminal.gwt.client.ui.layout.ChildComponentContainer;

public class VOrderedLayout extends CellBasedLayout {

    public static final String CLASSNAME = "v-orderedlayout";

    int orientation;

    /**
     * Size of the layout excluding any margins.
     */
    Size activeLayoutSize = new Size(0, 0);

    boolean isRendering = false;

    private String width = "";

    boolean sizeHasChangedDuringRendering = false;

    private ValueMap expandRatios;

    private double expandRatioSum;

    private double defaultExpandRatio;

    private ValueMap alignments;

    public VOrderedLayout() {
        this(CLASSNAME, ORIENTATION_VERTICAL);
    }

    protected VOrderedLayout(String className, int orientation) {
        setStyleName(className);
        this.orientation = orientation;

        STYLENAME_SPACING = className + "-spacing";
        STYLENAME_MARGIN_TOP = className + "-margin-top";
        STYLENAME_MARGIN_RIGHT = className + "-margin-right";
        STYLENAME_MARGIN_BOTTOM = className + "-margin-bottom";
        STYLENAME_MARGIN_LEFT = className + "-margin-left";
    }

    void layoutSizeMightHaveChanged() {
        Size oldSize = new Size(activeLayoutSize.getWidth(),
                activeLayoutSize.getHeight());
        calculateLayoutDimensions();

        /*
         * If layout dimension changes we must also update container sizes
         */
        if (!oldSize.equals(activeLayoutSize)) {
            calculateContainerSize();
        }
    }

    void updateWidgetSizes() {
        for (ChildComponentContainer childComponentContainer : widgetToComponentContainer
                .values()) {

            /*
             * Update widget size from DOM
             */
            childComponentContainer.updateWidgetSize();
        }
    }

    void recalculateLayout() {

        /* Calculate space for relative size components */
        int spaceForExpansion = calculateLayoutDimensions();

        if (!widgetToComponentContainer.isEmpty()) {
            /* Divide expansion space between component containers */
            expandComponentContainers(spaceForExpansion);

            /* Update container sizes */
            calculateContainerSize();
        }

    }

    private void expandComponentContainers(int spaceForExpansion) {
        int remaining = spaceForExpansion;
        for (ChildComponentContainer childComponentContainer : widgetToComponentContainer
                .values()) {
            remaining -= childComponentContainer.expand(orientation,
                    spaceForExpansion);
        }

        if (remaining > 0) {

            // Some left-over pixels due to rounding errors

            // Add one pixel to each container until there are no pixels left
            // FIXME extra pixels should be divided among expanded widgets if
            // such a widgets exists

            Iterator<Widget> widgetIterator = iterator();
            while (widgetIterator.hasNext() && remaining-- > 0) {
                ChildComponentContainer childComponentContainer = (ChildComponentContainer) widgetIterator
                        .next();
                childComponentContainer.expandExtra(orientation, 1);
            }
        }

    }

    /**
     * Updated components with relative height in horizontal layouts and
     * components with relative width in vertical layouts. This is only needed
     * if the height (horizontal layout) or width (vertical layout) has not been
     * specified.
     */
    boolean updateRelativeSizesInNonMainDirection() {
        int updateDirection = 1 - orientation;
        if ((updateDirection == ORIENTATION_HORIZONTAL && !isDynamicWidth())
                || (updateDirection == ORIENTATION_VERTICAL && !isDynamicHeight())) {
            return false;
        }

        boolean updated = false;
        for (ChildComponentContainer componentContainer : widgetToComponentContainer
                .values()) {
            if (componentContainer.isComponentRelativeSized(updateDirection)) {
                client.handleComponentRelativeSize(componentContainer
                        .getWidget());
            }

            updated = true;
        }

        return updated;
    }

    private int calculateLayoutDimensions() {
        int summedWidgetWidth = 0;
        int summedWidgetHeight = 0;

        int maxWidgetWidth = 0;
        int maxWidgetHeight = 0;

        // Calculate layout dimensions from component dimensions
        for (ChildComponentContainer childComponentContainer : widgetToComponentContainer
                .values()) {

            int widgetHeight = 0;
            int widgetWidth = 0;
            if (childComponentContainer.isComponentRelativeSized(orientation)) {
                if (orientation == ORIENTATION_HORIZONTAL) {
                    widgetHeight = getWidgetHeight(childComponentContainer);
                } else {
                    widgetWidth = getWidgetWidth(childComponentContainer);
                }
            } else {
                widgetWidth = getWidgetWidth(childComponentContainer);
                widgetHeight = getWidgetHeight(childComponentContainer);
            }

            summedWidgetWidth += widgetWidth;
            summedWidgetHeight += widgetHeight;

            maxWidgetHeight = Math.max(maxWidgetHeight, widgetHeight);
            maxWidgetWidth = Math.max(maxWidgetWidth, widgetWidth);
        }

        if (isHorizontal()) {
            summedWidgetWidth += activeSpacing.hSpacing
                    * (widgetToComponentContainer.size() - 1);
        } else {
            summedWidgetHeight += activeSpacing.vSpacing
                    * (widgetToComponentContainer.size() - 1);
        }

        Size layoutSize = updateLayoutDimensions(summedWidgetWidth,
                summedWidgetHeight, maxWidgetWidth, maxWidgetHeight);

        int remainingSpace;
        if (isHorizontal()) {
            remainingSpace = layoutSize.getWidth() - summedWidgetWidth;
        } else {
            remainingSpace = layoutSize.getHeight() - summedWidgetHeight;
        }
        if (remainingSpace < 0) {
            remainingSpace = 0;
        }

        // ApplicationConnection.getConsole().log(
        // "Layout size: " + activeLayoutSize);
        return remainingSpace;
    }

    private int getWidgetHeight(ChildComponentContainer childComponentContainer) {
        Size s = childComponentContainer.getWidgetSize();
        return s.getHeight()
                + childComponentContainer.getCaptionHeightAboveComponent();
    }

    private int getWidgetWidth(ChildComponentContainer childComponentContainer) {
        Size s = childComponentContainer.getWidgetSize();
        int widgetWidth = s.getWidth()
                + childComponentContainer.getCaptionWidthAfterComponent();

        /*
         * If the component does not have a specified size in the main direction
         * the caption may determine the space used by the component
         */
        if (!childComponentContainer.widgetHasSizeSpecified(orientation)) {
            int captionWidth = childComponentContainer
                    .getCaptionRequiredWidth();

            if (captionWidth > widgetWidth) {
                widgetWidth = captionWidth;
            }
        }

        return widgetWidth;
    }

    void calculateAlignments() {
        int w = 0;
        int h = 0;

        if (isHorizontal()) {
            // HORIZONTAL
            h = activeLayoutSize.getHeight();
            if (!isDynamicWidth()) {
                w = -1;
            }

        } else {
            // VERTICAL
            w = activeLayoutSize.getWidth();
            if (!isDynamicHeight()) {
                h = -1;
            }
        }

        for (ChildComponentContainer childComponentContainer : widgetToComponentContainer
                .values()) {
            childComponentContainer.updateAlignments(w, h);
        }

    }

    private void calculateContainerSize() {

        /*
         * Container size here means the size the container gets from the
         * component. The expansion size is not include in this but taken
         * separately into account.
         */
        int height = 0, width = 0;
        Iterator<Widget> widgetIterator = iterator();
        if (isHorizontal()) {
            height = activeLayoutSize.getHeight();
            int availableWidth = activeLayoutSize.getWidth();
            boolean first = true;
            while (widgetIterator.hasNext()) {
                ChildComponentContainer childComponentContainer = (ChildComponentContainer) widgetIterator
                        .next();
                if (!childComponentContainer
                        .isComponentRelativeSized(ORIENTATION_HORIZONTAL)) {
                    /*
                     * Only components with non-relative size in the main
                     * direction has a container size
                     */
                    width = childComponentContainer.getWidgetSize().getWidth()
                            + childComponentContainer
                                    .getCaptionWidthAfterComponent();

                    /*
                     * If the component does not have a specified size in the
                     * main direction the caption may determine the space used
                     * by the component
                     */
                    if (!childComponentContainer
                            .widgetHasSizeSpecified(orientation)) {
                        int captionWidth = childComponentContainer
                                .getCaptionRequiredWidth();
                        // ApplicationConnection.getConsole().log(
                        // "Component width: " + width
                        // + ", caption width: " + captionWidth);
                        if (captionWidth > width) {
                            width = captionWidth;
                        }
                    }
                } else {
                    width = 0;
                }

                if (!isDynamicWidth()) {
                    if (availableWidth == 0) {
                        /*
                         * Let the overflowing components overflow. IE has
                         * problems with zero sizes.
                         */
                        // width = 0;
                        // height = 0;
                    } else if (width > availableWidth) {
                        width = availableWidth;

                        if (!first) {
                            width -= activeSpacing.hSpacing;
                        }
                        availableWidth = 0;
                    } else {
                        availableWidth -= width;
                        if (!first) {
                            availableWidth -= activeSpacing.hSpacing;
                        }
                    }

                    first = false;
                }

                childComponentContainer.setContainerSize(width, height);
            }
        } else {
            width = activeLayoutSize.getWidth();
            while (widgetIterator.hasNext()) {
                ChildComponentContainer childComponentContainer = (ChildComponentContainer) widgetIterator
                        .next();

                if (!childComponentContainer
                        .isComponentRelativeSized(ORIENTATION_VERTICAL)) {
                    /*
                     * Only components with non-relative size in the main
                     * direction has a container size
                     */
                    height = childComponentContainer.getWidgetSize()
                            .getHeight()
                            + childComponentContainer
                                    .getCaptionHeightAboveComponent();
                } else {
                    height = 0;
                }

                childComponentContainer.setContainerSize(width, height);
            }

        }

    }

    private Size updateLayoutDimensions(int totalComponentWidth,
            int totalComponentHeight, int maxComponentWidth,
            int maxComponentHeight) {

        /* Only need to calculate dynamic dimensions */
        if (!isDynamicHeight() && !isDynamicWidth()) {
            return activeLayoutSize;
        }

        int activeLayoutWidth = 0;
        int activeLayoutHeight = 0;

        // Update layout dimensions
        if (isHorizontal()) {
            // Horizontal
            if (isDynamicWidth()) {
                activeLayoutWidth = totalComponentWidth;
            }

            if (isDynamicHeight()) {
                activeLayoutHeight = maxComponentHeight;
            }

        } else {
            // Vertical
            if (isDynamicWidth()) {
                activeLayoutWidth = maxComponentWidth;
            }

            if (isDynamicHeight()) {
                activeLayoutHeight = totalComponentHeight;
            }
        }

        if (isDynamicWidth()) {
            setActiveLayoutWidth(activeLayoutWidth);
            setOuterLayoutWidth(activeLayoutSize.getWidth());
        }

        if (isDynamicHeight()) {
            setActiveLayoutHeight(activeLayoutHeight);
            setOuterLayoutHeight(activeLayoutSize.getHeight());
        }

        return activeLayoutSize;
    }

    private void setActiveLayoutWidth(int activeLayoutWidth) {
        if (activeLayoutWidth < 0) {
            activeLayoutWidth = 0;
        }
        activeLayoutSize.setWidth(activeLayoutWidth);
    }

    private void setActiveLayoutHeight(int activeLayoutHeight) {
        if (activeLayoutHeight < 0) {
            activeLayoutHeight = 0;
        }
        activeLayoutSize.setHeight(activeLayoutHeight);

    }

    private void setOuterLayoutWidth(int activeLayoutWidth) {
        // Don't call setWidth to avoid triggering all kinds of recalculations
        // Also don't call super.setWidth to avoid messing with the
        // dynamicWidth property
        int newPixelWidth = (activeLayoutWidth + activeMargins.getHorizontal());
        getElement().getStyle().setWidth(newPixelWidth, Unit.PX);
    }

    private void setOuterLayoutHeight(int activeLayoutHeight) {
        // Don't call setHeight to avoid triggering all kinds of recalculations
        // Also don't call super.setHeight to avoid messing with the
        // dynamicHeight property
        int newPixelHeight = (activeLayoutHeight + activeMargins.getVertical());
        getElement().getStyle().setHeight(newPixelHeight, Unit.PX);
    }

    /**
     * Updates the spacing between components. Needs to be done only when
     * components are added/removed.
     */
    void updateContainerMargins() {
        ChildComponentContainer firstChildComponent = getFirstChildComponentContainer();
        if (firstChildComponent != null) {
            firstChildComponent.setMarginLeft(0);
            firstChildComponent.setMarginTop(0);

            for (ChildComponentContainer childComponent : widgetToComponentContainer
                    .values()) {
                if (childComponent == firstChildComponent) {
                    continue;
                }

                if (isHorizontal()) {
                    childComponent.setMarginLeft(activeSpacing.hSpacing);
                } else {
                    childComponent.setMarginTop(activeSpacing.vSpacing);
                }
            }
        }
    }

    boolean isHorizontal() {
        return orientation == ORIENTATION_HORIZONTAL;
    }

    boolean isVertical() {
        return orientation == ORIENTATION_VERTICAL;
    }

    ChildComponentContainer createChildContainer(VPaintableWidget child) {

        // Create a container DIV for the child
        ChildComponentContainer childComponent = new ChildComponentContainer(
                child, orientation);

        return childComponent;

    }

    public RenderSpace getAllocatedSpace(Widget child) {
        int width = 0;
        int height = 0;
        ChildComponentContainer childComponentContainer = getComponentContainer(child);
        // WIDTH CALCULATION
        if (isVertical()) {
            width = activeLayoutSize.getWidth();
            width -= childComponentContainer.getCaptionWidthAfterComponent();
        } else if (!isDynamicWidth()) {
            // HORIZONTAL
            width = childComponentContainer.getContSize().getWidth();
            width -= childComponentContainer.getCaptionWidthAfterComponent();
        }

        // HEIGHT CALCULATION
        if (isHorizontal()) {
            height = activeLayoutSize.getHeight();
            height -= childComponentContainer.getCaptionHeightAboveComponent();
        } else if (!isDynamicHeight()) {
            // VERTICAL
            height = childComponentContainer.getContSize().getHeight();
            height -= childComponentContainer.getCaptionHeightAboveComponent();
        }

        // ApplicationConnection.getConsole().log(
        // "allocatedSpace for " + Util.getSimpleName(child) + ": "
        // + width + "," + height);
        RenderSpace space = new RenderSpace(width, height);
        return space;
    }

    private void recalculateLayoutAndComponentSizes() {
        recalculateLayout();

        if (!(isDynamicHeight() && isDynamicWidth())) {
            /* First update relative sized components */
            for (ChildComponentContainer componentContainer : widgetToComponentContainer
                    .values()) {
                client.handleComponentRelativeSize(componentContainer
                        .getWidget());

                // Update widget size from DOM
                componentContainer.updateWidgetSize();
            }
        }

        if (isDynamicHeight()) {
            /*
             * Height is not necessarily correct anymore as the height of
             * components might have changed if the width has changed.
             */

            /*
             * Get the new widget sizes from DOM and calculate new container
             * sizes
             */
            updateWidgetSizes();

            /* Update layout dimensions based on widget sizes */
            recalculateLayout();
        }

        updateRelativeSizesInNonMainDirection();
        calculateAlignments();

        setRootSize();
    }

    void setRootSize() {
        root.getStyle().setPropertyPx("width", activeLayoutSize.getWidth());
        root.getStyle().setPropertyPx("height", activeLayoutSize.getHeight());
    }

    public boolean requestLayout(Set<Widget> children) {
        for (Widget p : children) {
            /* Update widget size from DOM */
            ChildComponentContainer componentContainer = getComponentContainer(p);
            // This should no longer be needed (after #2563)
            // if (isDynamicWidth()) {
            // componentContainer.setUnlimitedContainerWidth();
            // } else {
            // componentContainer.setLimitedContainerWidth(activeLayoutSize
            // .getWidth());
            // }

            componentContainer.updateWidgetSize();

            /*
             * If this is the result of an caption icon onload event the caption
             * size may have changed
             */
            componentContainer.updateCaptionSize();
        }

        Size sizeBefore = new Size(activeLayoutSize.getWidth(),
                activeLayoutSize.getHeight());

        recalculateLayoutAndComponentSizes();
        boolean sameSize = (sizeBefore.equals(activeLayoutSize));
        if (!sameSize) {
            /* Must inform child components about possible size updates */
            client.runDescendentsLayout(this);
        }

        /* Automatically propagated upwards if the size has changed */

        return sameSize;
    }

    @Override
    public void setHeight(String height) {
        Size sizeBefore = new Size(activeLayoutSize.getWidth(),
                activeLayoutSize.getHeight());

        super.setHeight(height);

        if (height != null && !height.equals("")) {
            setActiveLayoutHeight(getOffsetHeight()
                    - activeMargins.getVertical());
        }

        if (isRendering) {
            sizeHasChangedDuringRendering = true;
        } else {
            recalculateLayoutAndComponentSizes();
            boolean sameSize = (sizeBefore.equals(activeLayoutSize));
            if (!sameSize) {
                /* Must inform child components about possible size updates */
                client.runDescendentsLayout(this);
            }
        }
    }

    @Override
    public void setWidth(String width) {
        if (this.width.equals(width) || !isVisible()) {
            return;
        }
        Size sizeBefore = new Size(activeLayoutSize.getWidth(),
                activeLayoutSize.getHeight());

        super.setWidth(width);
        this.width = width;
        if (width != null && !width.equals("")) {
            setActiveLayoutWidth(getOffsetWidth()
                    - activeMargins.getHorizontal());
        }

        if (isRendering) {
            sizeHasChangedDuringRendering = true;
        } else {
            recalculateLayoutAndComponentSizes();
            boolean sameSize = (sizeBefore.equals(activeLayoutSize));
            if (!sameSize) {
                /* Must inform child components about possible size updates */
                client.runDescendentsLayout(this);
            }
            /*
             * If the height changes as a consequence of this we must inform the
             * parent also
             */
            if (isDynamicHeight()
                    && sizeBefore.getHeight() != activeLayoutSize.getHeight()) {
                Util.notifyParentOfSizeChange(this, false);
            }

        }
    }

    protected void updateAlignmentsAndExpandRatios(UIDL uidl,
            ArrayList<Widget> renderedWidgets) {

        /*
         */
        alignments = uidl.getMapAttribute("alignments");

        /*
         * UIDL contains a map of paintable ids to expand ratios
         */

        expandRatios = uidl.getMapAttribute("expandRatios");
        expandRatioSum = -1.0;

        for (int i = 0; i < renderedWidgets.size(); i++) {
            Widget widget = renderedWidgets.get(i);
            String pid = VPaintableMap.get(client).getPid(widget);

            ChildComponentContainer container = getComponentContainer(widget);

            // Calculate alignment info
            container.setAlignment(getAlignment(pid));

            // Update expand ratio
            container.setNormalizedExpandRatio(getExpandRatio(pid));
        }
    }

    private AlignmentInfo getAlignment(String pid) {
        if (alignments.containsKey(pid)) {
            return new AlignmentInfo(alignments.getInt(pid));
        } else {
            return AlignmentInfo.TOP_LEFT;
        }
    }

    private double getExpandRatio(String pid) {
        if (expandRatioSum < 0) {
            expandRatioSum = 0;
            JsArrayString keyArray = expandRatios.getKeyArray();
            int length = keyArray.length();
            for (int i = 0; i < length; i++) {
                expandRatioSum += expandRatios.getRawNumber(keyArray.get(i));
            }
            if (expandRatioSum == 0) {
                // by default split equally among components
                defaultExpandRatio = 1.0 / widgetToComponentContainer.size();
            } else {
                defaultExpandRatio = 0;
            }
        }
        if (expandRatios.containsKey(pid)) {
            return expandRatios.getRawNumber(pid) / expandRatioSum;
        } else {
            return defaultExpandRatio;
        }
    }

    /**
     * Returns the deepest nested child component which contains "element". The
     * child component is also returned if "element" is part of its caption.
     * 
     * @param element
     *            An element that is a nested sub element of the root element in
     *            this layout
     * @return The Paintable which the element is a part of. Null if the element
     *         belongs to the layout and not to a child.
     */
    VPaintableWidget getComponent(Element element) {
        return Util.getPaintableForElement(client, this, element);
    }

    public Widget getWidgetForPaintable() {
        return this;
    }

}
