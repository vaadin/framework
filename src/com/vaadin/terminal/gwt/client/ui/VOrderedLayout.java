package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.terminal.gwt.client.ui.layout.CellBasedLayout;
import com.vaadin.terminal.gwt.client.ui.layout.ChildComponentContainer;

public class VOrderedLayout extends CellBasedLayout {

    public static final String CLASSNAME = "v-orderedlayout";

    private int orientation;

    // Can be removed once OrderedLayout is removed
    private boolean allowOrientationUpdate = false;

    /**
     * Size of the layout excluding any margins.
     */
    private Size activeLayoutSize = new Size(0, 0);

    private boolean isRendering = false;

    private String width = "";

    private boolean sizeHasChangedDuringRendering = false;

    public VOrderedLayout() {
        this(CLASSNAME, ORIENTATION_VERTICAL);
        allowOrientationUpdate = true;
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

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        isRendering = true;
        super.updateFromUIDL(uidl, client);

        // Only non-cached, visible UIDL:s can introduce changes
        if (uidl.getBooleanAttribute("cached")
                || uidl.getBooleanAttribute("invisible")) {
            isRendering = false;
            return;
        }

        if (allowOrientationUpdate) {
            handleOrientationUpdate(uidl);
        }

        // IStopWatch w = new IStopWatch("OrderedLayout.updateFromUIDL");

        ArrayList<Widget> uidlWidgets = new ArrayList<Widget>(uidl
                .getChildCount());
        ArrayList<ChildComponentContainer> relativeSizeComponents = new ArrayList<ChildComponentContainer>();
        ArrayList<UIDL> relativeSizeComponentUIDL = new ArrayList<UIDL>();

        int pos = 0;
        for (final Iterator<UIDL> it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL childUIDL = it.next();
            final Paintable child = client.getPaintable(childUIDL);
            Widget widget = (Widget) child;

            // Create container for component
            ChildComponentContainer childComponentContainer = getComponentContainer(widget);

            if (childComponentContainer == null) {
                // This is a new component
                childComponentContainer = createChildContainer(widget);
            }

            addOrMoveChild(childComponentContainer, pos++);

            /*
             * Components which are to be expanded in the same orientation as
             * the layout are rendered later when it is clear how much space
             * they can use
             */
            if (!Util.isCached(childUIDL)) {
                FloatSize relativeSize = Util.parseRelativeSize(childUIDL);
                childComponentContainer.setRelativeSize(relativeSize);
            }

            if (childComponentContainer.isComponentRelativeSized(orientation)) {
                relativeSizeComponents.add(childComponentContainer);
                relativeSizeComponentUIDL.add(childUIDL);
            } else {
                if (isDynamicWidth()) {
                    childComponentContainer.renderChild(childUIDL, client, 0);
                } else {
                    childComponentContainer.renderChild(childUIDL, client,
                            activeLayoutSize.getWidth());
                }
                if (sizeHasChangedDuringRendering && Util.isCached(childUIDL)) {
                    // notify cached relative sized component about size
                    // chance
                    client.handleComponentRelativeSize(childComponentContainer
                            .getWidget());
                }
            }

            uidlWidgets.add(widget);

        }

        // w.mark("Rendering of "
        // + (uidlWidgets.size() - relativeSizeComponents.size())
        // + " absolute size components done");

        /*
         * Remove any children after pos. These are the ones that previously
         * were in the layout but have now been removed
         */
        removeChildrenAfter(pos);

        // w.mark("Old children removed");

        /* Fetch alignments and expand ratio from UIDL */
        updateAlignmentsAndExpandRatios(uidl, uidlWidgets);
        // w.mark("Alignments and expand ratios updated");

        /* Fetch widget sizes from rendered components */
        updateWidgetSizes();
        // w.mark("Widget sizes updated");

        recalculateLayout();
        // w.mark("Layout size calculated (" + activeLayoutSize +
        // ") offsetSize: "
        // + getOffsetWidth() + "," + getOffsetHeight());

        /* Render relative size components */
        for (int i = 0; i < relativeSizeComponents.size(); i++) {
            ChildComponentContainer childComponentContainer = relativeSizeComponents
                    .get(i);
            UIDL childUIDL = relativeSizeComponentUIDL.get(i);

            if (isDynamicWidth()) {
                childComponentContainer.renderChild(childUIDL, client, 0);
            } else {
                childComponentContainer.renderChild(childUIDL, client,
                        activeLayoutSize.getWidth());
            }

            if (Util.isCached(childUIDL)) {
                /*
                 * We must update the size of the relative sized component if
                 * the expand ratio or something else in the layout changes
                 * which affects the size of a relative sized component
                 */
                client.handleComponentRelativeSize(childComponentContainer
                        .getWidget());
            }

            // childComponentContainer.updateWidgetSize();
        }

        // w.mark("Rendering of " + (relativeSizeComponents.size())
        // + " relative size components done");

        /* Fetch widget sizes for relative size components */
        for (ChildComponentContainer childComponentContainer : widgetToComponentContainer
                .values()) {

            /* Update widget size from DOM */
            childComponentContainer.updateWidgetSize();
        }

        // w.mark("Widget sizes updated");

        /*
         * Components with relative size in main direction may affect the layout
         * size in the other direction
         */
        if ((isHorizontal() && isDynamicHeight())
                || (isVertical() && isDynamicWidth())) {
            layoutSizeMightHaveChanged();
        }
        // w.mark("Layout dimensions updated");

        /* Update component spacing */
        updateContainerMargins();

        /*
         * Update component sizes for components with relative size in non-main
         * direction
         */
        if (updateRelativeSizesInNonMainDirection()) {
            // Sizes updated - might affect the other dimension so we need to
            // recheck the widget sizes and recalculate layout dimensions
            updateWidgetSizes();
            layoutSizeMightHaveChanged();
        }
        calculateAlignments();
        // w.mark("recalculateComponentSizesAndAlignments done");

        setRootSize();

        if (BrowserInfo.get().isIE()) {
            /*
             * This should fix the issue with padding not always taken into
             * account for the containers leading to no spacing between
             * elements.
             */
            root.getStyle().setProperty("zoom", "1");
        }

        // w.mark("runDescendentsLayout done");
        isRendering = false;
        sizeHasChangedDuringRendering = false;
    }

    private void layoutSizeMightHaveChanged() {
        Size oldSize = new Size(activeLayoutSize.getWidth(), activeLayoutSize
                .getHeight());
        calculateLayoutDimensions();

        /*
         * If layout dimension changes we must also update container sizes
         */
        if (!oldSize.equals(activeLayoutSize)) {
            calculateContainerSize();
        }
    }

    private void updateWidgetSizes() {
        for (ChildComponentContainer childComponentContainer : widgetToComponentContainer
                .values()) {

            /*
             * Update widget size from DOM
             */
            childComponentContainer.updateWidgetSize();
        }
    }

    private void recalculateLayout() {

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

            Iterator<Widget> widgetIterator = iterator();
            while (widgetIterator.hasNext() && remaining-- > 0) {
                ChildComponentContainer childComponentContainer = (ChildComponentContainer) widgetIterator
                        .next();
                childComponentContainer.expandExtra(orientation, 1);
            }
        }

    }

    private void handleOrientationUpdate(UIDL uidl) {
        int newOrientation = ORIENTATION_VERTICAL;
        if ("horizontal".equals(uidl.getStringAttribute("orientation"))) {
            newOrientation = ORIENTATION_HORIZONTAL;
        }

        if (orientation != newOrientation) {
            orientation = newOrientation;

            for (ChildComponentContainer childComponentContainer : widgetToComponentContainer
                    .values()) {
                childComponentContainer.setOrientation(orientation);
            }
        }

    }

    /**
     * Updated components with relative height in horizontal layouts and
     * components with relative width in vertical layouts. This is only needed
     * if the height (horizontal layout) or width (vertical layout) has not been
     * specified.
     */
    private boolean updateRelativeSizesInNonMainDirection() {
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

    private void calculateAlignments() {
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
        super.setWidth((activeLayoutWidth + activeMargins.getHorizontal())
                + "px");

    }

    private void setOuterLayoutHeight(int activeLayoutHeight) {
        super.setHeight((activeLayoutHeight + activeMargins.getVertical())
                + "px");

    }

    /**
     * Updates the spacing between components. Needs to be done only when
     * components are added/removed.
     */
    private void updateContainerMargins() {
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

    private boolean isHorizontal() {
        return orientation == ORIENTATION_HORIZONTAL;
    }

    private boolean isVertical() {
        return orientation == ORIENTATION_VERTICAL;
    }

    private ChildComponentContainer createChildContainer(Widget child) {

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

    private void setRootSize() {
        root.getStyle().setPropertyPx("width", activeLayoutSize.getWidth());
        root.getStyle().setPropertyPx("height", activeLayoutSize.getHeight());
    }

    public boolean requestLayout(Set<Paintable> children) {
        for (Paintable p : children) {
            /* Update widget size from DOM */
            ChildComponentContainer componentContainer = getComponentContainer((Widget) p);
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
        if (this.width.equals(width)) {
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
         * UIDL contains component alignments as a comma separated list.
         * 
         * See com.vaadin.terminal.gwt.client.ui.AlignmentInfo.java for possible
         * values.
         */
        final int[] alignments = uidl.getIntArrayAttribute("alignments");

        /*
         * UIDL contains normalized expand ratios as a comma separated list.
         */
        final int[] expandRatios = uidl.getIntArrayAttribute("expandRatios");

        for (int i = 0; i < renderedWidgets.size(); i++) {
            Widget widget = renderedWidgets.get(i);

            ChildComponentContainer container = getComponentContainer(widget);

            // Calculate alignment info
            container.setAlignment(new AlignmentInfo(alignments[i]));

            // Update expand ratio
            container.setExpandRatio(expandRatios[i]);
        }
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        ChildComponentContainer componentContainer = getComponentContainer((Widget) component);
        componentContainer.updateCaption(uidl, client);
        if (!isRendering) {
            /*
             * This was a component-only update and the possible size change
             * must be propagated to the layout
             */
            client.captionSizeUpdated(component);
        }
    }

}
