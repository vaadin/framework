package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.RenderSpace;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation.FloatSize;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation.Size;
import com.itmill.toolkit.terminal.gwt.client.ui.layout.CellBasedLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.layout.ChildComponentContainer;

public class IOrderedLayout extends CellBasedLayout {

    public static final String CLASSNAME = "i-orderedlayout";

    private static final String MARGIN_TOP_LEFT_CLASSNAMES = "i-orderedlayout-margin-top i-orderedlayout-margin-left";
    private static final String MARGIN_BOTTOM_RIGHT_CLASSNAMES = "i-orderedlayout-margin-bottom i-orderedlayout-margin-right";
    private static final String SPACING_CLASSNAMES = "i-orderedlayout-hspacing i-orderedlayout-vspacing";

    private String marginsMeasureStyleName = "";
    private int orientation = ORIENTATION_HORIZONTAL;

    /**
     * Size of the layout excluding any margins.
     */
    private Size activeLayoutSize = new Size(0, 0);

    // private int spaceForExpansion = 0;
    private int spaceNobodyWantedToUse = 0;

    private boolean isRendering = false;

    @Override
    public void setStyleName(String styleName) {
        super.setStyleName(styleName);

        if (marginsMeasureStyleName.equals(styleName)) {
            return;
        }

        String spacingStyleNames = styleName + " " + SPACING_CLASSNAMES;
        String marginBottomRightStyleNames = styleName + " "
                + MARGIN_BOTTOM_RIGHT_CLASSNAMES;
        String marginTopLeftStyleNames = styleName + " "
                + MARGIN_TOP_LEFT_CLASSNAMES;
        if (measureMarginsAndSpacing(styleName, marginTopLeftStyleNames,
                marginBottomRightStyleNames, spacingStyleNames)) {
            marginsMeasureStyleName = styleName;
        }
    }

    public IOrderedLayout() {
        setStyleName(CLASSNAME);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        isRendering = true;
        super.updateFromUIDL(uidl, client);

        // Only non-cached UIDL:s can introduce changes
        if (uidl.getBooleanAttribute("cached")) {
            return;
        }

        handleOrientationUpdate(uidl);

        // IStopWatch w = new IStopWatch("OrderedLayout.updateFromUIDL");

        ArrayList<Widget> uidlWidgets = new ArrayList<Widget>(uidl
                .getChildCount());
        ArrayList<ChildComponentContainer> relativeSizeComponents = new ArrayList<ChildComponentContainer>();
        ArrayList<UIDL> relativeSizeComponentUIDL = new ArrayList<UIDL>();
        ArrayList<Widget> relativeSizeWidgets = new ArrayList<Widget>();

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
            FloatSize relativeSize = Util.parseRelativeSize(childUIDL);
            childComponentContainer.setRelativeSize(relativeSize);

            if (hasRelativeSize(relativeSize, orientation)) {
                relativeSizeComponents.add(childComponentContainer);
                relativeSizeComponentUIDL.add(childUIDL);
                relativeSizeWidgets.add(widget);
            } else {
                childComponentContainer.renderChild(childUIDL, client);
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

        /* Fetch widget sizes from rendered components */
        for (ChildComponentContainer childComponentContainer : widgetToComponentContainer
                .values()) {

            /*
             * Update widget size from DOM
             */
            childComponentContainer.updateWidgetSize();
        }

        recalculateLayout();

        /* Render relative size components */
        for (int i = 0; i < relativeSizeComponents.size(); i++) {
            ChildComponentContainer childComponentContainer = relativeSizeComponents
                    .get(i);
            UIDL childUIDL = relativeSizeComponentUIDL.get(i);

            childComponentContainer.renderChild(childUIDL, client);
            // childComponentContainer.updateWidgetSize();
        }

        /* Fetch widget sizes for relative size components */
        for (ChildComponentContainer childComponentContainer : widgetToComponentContainer
                .values()) {

            /* Update widget size from DOM */
            childComponentContainer.updateWidgetSize();
        }

        // w.mark("Rendering of " + (relativeSizeComponents.size())
        // + " relative size components done");

        /* Recalculate component sizes and alignments */
        recalculateComponentSizesAndAlignments();
        // w.mark("recalculateComponentSizesAndAlignments done");

        /* Must inform child components about possible size updates */
        client.runDescendentsLayout(this);
        // w.mark("runDescendentsLayout done");

        isRendering = false;
    }

    private void recalculateLayout() {
        /* Calculate space for relative size components */
        int spaceForExpansion = calculateLayoutDimensions();

        /* Divide expansion space between component containers */
        expandComponentContainers(spaceForExpansion);

        /* Update container sizes */
        calculateContainerSize();

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

            // Add extra pixels to first container
            getFirstChildComponentContainer().expandExtra(orientation,
                    remaining);

        }

    }

    private static boolean hasRelativeSize(FloatSize relativeSize,
            int orientation) {
        if (relativeSize == null) {
            return false;
        }
        if (orientation == ORIENTATION_HORIZONTAL) {
            return relativeSize.getWidth() >= 0;
        } else {
            return relativeSize.getHeight() >= 0;
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

    private void recalculateComponentSizesAndAlignments() {
        if (widgetToComponentContainer.isEmpty()) {
            return;
        }

        updateContainerMargins();

        /*
         * Update the height of relative height components in a horizontal
         * layout or the width for relative width components in a vertical
         * layout
         */
        updateRelativeSizesInNonMainDirection();

        /* Calculate alignments */
        calculateAlignments();

    }

    private void updateRelativeSizesInNonMainDirection() {
        int updateDirection = 1 - orientation;
        for (ChildComponentContainer componentContainer : widgetToComponentContainer
                .values()) {
            if (componentContainer.isComponentRelativeSized(updateDirection)) {
                client.handleComponentRelativeSize(componentContainer
                        .getWidget());
            }
        }

    }

    private int calculateLayoutDimensions() {
        int summedWidgetWidth = 0;
        int summedWidgetHeight = 0;

        int maxWidgetWidth = 0;
        int maxWidgetHeight = 0;

        // Calculate layout dimensions from component dimensions
        for (ChildComponentContainer childComponentContainer : widgetToComponentContainer
                .values()) {

            if (childComponentContainer.isComponentRelativeSized(orientation)) {
                continue;
            }

            Size s = childComponentContainer.getWidgetSize();
            int widgetWidth = s.getWidth()
                    + childComponentContainer.getCaptionWidthAfterComponent();

            if (isDynamicWidth()) {
                /*
                 * For a dynamic width layout the max of caption/widget defines
                 * the required size
                 */
                int captionWidth = childComponentContainer.getCaptionWidth();
                if (captionWidth > widgetWidth) {
                    widgetWidth = captionWidth;
                }
            }

            int widgetHeight = s.getHeight()
                    + childComponentContainer.getCaptionHeightAboveComponent();

            // ApplicationConnection.getConsole().log(
            // "Container width: " + widgetWidth);

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

        return remainingSpace;
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
        if (isHorizontal()) {
            height = activeLayoutSize.getHeight();
            for (ChildComponentContainer childComponentContainer : widgetToComponentContainer
                    .values()) {
                if (!childComponentContainer
                        .isComponentRelativeSized(ORIENTATION_HORIZONTAL)) {
                    /*
                     * Only components with non-relative size in the main
                     * direction has a container size
                     */
                    width = childComponentContainer.getWidgetSize().getWidth()
                            + childComponentContainer
                                    .getCaptionWidthAfterComponent();
                    int captionWidth = childComponentContainer
                            .getCaptionWidth();
                    if (captionWidth > width) {
                        width = captionWidth;
                    }
                } else {
                    width = 0;
                }

                childComponentContainer.setContainerSize(width, height);
            }
        } else {
            width = activeLayoutSize.getWidth();
            for (ChildComponentContainer childComponentContainer : widgetToComponentContainer
                    .values()) {

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

        int activeLayoutWidth = 0;
        int activeLayoutHeight = 0;

        // Update layout dimensions
        if (isHorizontal()) {
            // Horizontal
            if (isDynamicWidth()) {
                activeLayoutWidth = totalComponentWidth;
                setOuterLayoutWidth(activeLayoutWidth);
            } else {
                activeLayoutWidth = getOffsetWidth()
                        - activeMargins.getHorizontal();
            }

            if (isDynamicHeight()) {
                activeLayoutHeight = maxComponentHeight;
                setOuterLayoutHeight(maxComponentHeight);
            } else {
                activeLayoutHeight = getOffsetHeight()
                        - activeMargins.getVertical();

            }

        } else {
            // Vertical
            if (isDynamicHeight()) {
                activeLayoutWidth = maxComponentWidth;
                setOuterLayoutWidth(maxComponentWidth);
            } else {
                activeLayoutWidth = getOffsetWidth()
                        - activeMargins.getHorizontal();
            }

            if (isDynamicHeight()) {
                activeLayoutHeight = totalComponentHeight;
                setOuterLayoutHeight(totalComponentHeight);
            } else {
                activeLayoutHeight = getOffsetHeight()
                        - activeMargins.getVertical();
            }
        }

        activeLayoutSize.setWidth(activeLayoutWidth);
        activeLayoutSize.setHeight(activeLayoutHeight);

        return activeLayoutSize;
    }

    private void setOuterLayoutWidth(int activeLayoutWidth) {
        super.setWidth((activeLayoutWidth + activeMargins.getHorizontal())
                + "px");

    }

    private void setOuterLayoutHeight(int activeLayoutHeight) {
        super.setHeight((activeLayoutHeight + activeMargins.getVertical())
                + "px");

    }

    private void updateContainerMargins() {
        ChildComponentContainer firstChildComponent = getFirstChildComponentContainer();

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

    private boolean recalculateLayoutAndComponentSizes() {
        Size sizeBefore = new Size(activeLayoutSize.getWidth(),
                activeLayoutSize.getHeight());

        recalculateLayout();

        recalculateComponentSizesAndAlignments();

        boolean sameSize = (sizeBefore.equals(activeLayoutSize));

        return sameSize;
    }

    public boolean requestLayout(Set<Paintable> children) {
        for (Paintable p : children) {
            /* Update widget size from DOM */
            getComponentContainer((Widget) p).updateWidgetSize();
        }

        boolean sameSize = recalculateLayoutAndComponentSizes();
        if (!sameSize) {
            /* Must inform child components about possible size updates */
            client.runDescendentsLayout(this);
        }

        /* Automatically propagated upwards if the size has changed */

        return sameSize;
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);

        if (!isRendering) {
            if (recalculateLayoutAndComponentSizes()) {
                /* Must inform child components about possible size updates */
                client.runDescendentsLayout(this);
            }
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);

        if (!isRendering) {
            if (recalculateLayoutAndComponentSizes()) {
                /* Must inform child components about possible size updates */
                client.runDescendentsLayout(this);
            }
        }
    }

    protected void updateAlignmentsAndExpandRatios(UIDL uidl,
            ArrayList<Widget> renderedWidgets) {

        /*
         * UIDL contains component alignments as a comma separated list.
         * 
         * See com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo.java for
         * possible values.
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

}
