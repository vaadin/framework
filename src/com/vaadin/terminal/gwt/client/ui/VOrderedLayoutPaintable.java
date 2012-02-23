/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ui.layout.CellBasedLayoutPaintable;
import com.vaadin.terminal.gwt.client.ui.layout.ChildComponentContainer;

public abstract class VOrderedLayoutPaintable extends CellBasedLayoutPaintable {

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this, EventId.LAYOUT_CLICK) {

        @Override
        protected VPaintableWidget getChildComponent(Element element) {
            return getWidgetForPaintable().getComponent(element);
        }

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidgetForPaintable().addDomHandler(handler, type);
        }
    };

    public void updateCaption(VPaintableWidget paintable, UIDL uidl) {
        Widget widget = paintable.getWidgetForPaintable();
        ChildComponentContainer componentContainer = getWidgetForPaintable()
                .getComponentContainer(widget);
        componentContainer.updateCaption(uidl, getConnection());
        if (!getWidgetForPaintable().isRendering) {
            /*
             * This was a component-only update and the possible size change
             * must be propagated to the layout
             */
            getConnection().captionSizeUpdated(widget);
        }
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().isRendering = true;
        super.updateFromUIDL(uidl, client);

        // Only non-cached, visible UIDL:s can introduce changes
        if (!isRealUpdate(uidl) || uidl.getBooleanAttribute("invisible")) {
            getWidgetForPaintable().isRendering = false;
            return;
        }

        clickEventHandler.handleEventHandlerRegistration(client);

        // IStopWatch w = new IStopWatch("OrderedLayout.updateFromUIDL");

        ArrayList<Widget> uidlWidgets = new ArrayList<Widget>(
                uidl.getChildCount());
        ArrayList<ChildComponentContainer> relativeSizeComponents = new ArrayList<ChildComponentContainer>();
        ArrayList<UIDL> relativeSizeComponentUIDL = new ArrayList<UIDL>();

        int pos = 0;
        for (final Iterator<Object> it = uidl.getChildIterator(); it.hasNext();) {
            final UIDL childUIDL = (UIDL) it.next();
            final VPaintableWidget childPaintable = client
                    .getPaintable(childUIDL);
            Widget widget = childPaintable.getWidgetForPaintable();

            // Create container for component
            ChildComponentContainer childComponentContainer = getWidgetForPaintable()
                    .getComponentContainer(widget);

            if (childComponentContainer == null) {
                // This is a new component
                childComponentContainer = getWidgetForPaintable()
                        .createChildContainer(childPaintable);
            } else {
                /*
                 * The widget may be null if the same paintable has been
                 * rendered in a different component container while this has
                 * been invisible. Ensure the childComponentContainer has the
                 * widget attached. See e.g. #5372
                 */
                childComponentContainer.setPaintable(childPaintable);
            }

            getWidgetForPaintable().addOrMoveChild(childComponentContainer,
                    pos++);

            /*
             * Components which are to be expanded in the same orientation as
             * the layout are rendered later when it is clear how much space
             * they can use
             */
            if (null != childPaintable.getState()) {
                FloatSize relativeSize = Util.parseRelativeSize(childPaintable
                        .getState());
                childComponentContainer.setRelativeSize(relativeSize);
            }

            if (childComponentContainer
                    .isComponentRelativeSized(getWidgetForPaintable().orientation)) {
                relativeSizeComponents.add(childComponentContainer);
                relativeSizeComponentUIDL.add(childUIDL);
            } else {
                if (getWidgetForPaintable().isDynamicWidth()) {
                    childComponentContainer.renderChild(childUIDL, client, -1);
                } else {
                    childComponentContainer
                            .renderChild(childUIDL, client,
                                    getWidgetForPaintable().activeLayoutSize
                                            .getWidth());
                }
                if (getWidgetForPaintable().sizeHasChangedDuringRendering
                        && !isRealUpdate(childUIDL)) {
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
        getWidgetForPaintable().removeChildrenAfter(pos);

        // w.mark("Old children removed");

        /* Fetch alignments and expand ratio from UIDL */
        getWidgetForPaintable().updateAlignmentsAndExpandRatios(uidl,
                uidlWidgets);
        // w.mark("Alignments and expand ratios updated");

        /* Fetch widget sizes from rendered components */
        getWidgetForPaintable().updateWidgetSizes();
        // w.mark("Widget sizes updated");

        getWidgetForPaintable().recalculateLayout();
        // w.mark("Layout size calculated (" + activeLayoutSize +
        // ") offsetSize: "
        // + getOffsetWidth() + "," + getOffsetHeight());

        /* Render relative size components */
        for (int i = 0; i < relativeSizeComponents.size(); i++) {
            ChildComponentContainer childComponentContainer = relativeSizeComponents
                    .get(i);
            UIDL childUIDL = relativeSizeComponentUIDL.get(i);

            if (getWidgetForPaintable().isDynamicWidth()) {
                childComponentContainer.renderChild(childUIDL, client, -1);
            } else {
                childComponentContainer.renderChild(childUIDL, client,
                        getWidgetForPaintable().activeLayoutSize.getWidth());
            }

            if (!isRealUpdate(childUIDL)) {
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
        for (ChildComponentContainer childComponentContainer : getWidgetForPaintable()
                .getComponentContainers()) {

            /* Update widget size from DOM */
            childComponentContainer.updateWidgetSize();
        }

        // w.mark("Widget sizes updated");

        /*
         * Components with relative size in main direction may affect the layout
         * size in the other direction
         */
        if ((getWidgetForPaintable().isHorizontal() && getWidgetForPaintable()
                .isDynamicHeight())
                || (getWidgetForPaintable().isVertical() && getWidgetForPaintable()
                        .isDynamicWidth())) {
            getWidgetForPaintable().layoutSizeMightHaveChanged();
        }
        // w.mark("Layout dimensions updated");

        /* Update component spacing */
        getWidgetForPaintable().updateContainerMargins();

        /*
         * Update component sizes for components with relative size in non-main
         * direction
         */
        if (getWidgetForPaintable().updateRelativeSizesInNonMainDirection()) {
            // Sizes updated - might affect the other dimension so we need to
            // recheck the widget sizes and recalculate layout dimensions
            getWidgetForPaintable().updateWidgetSizes();
            getWidgetForPaintable().layoutSizeMightHaveChanged();
        }
        getWidgetForPaintable().calculateAlignments();
        // w.mark("recalculateComponentSizesAndAlignments done");

        getWidgetForPaintable().setRootSize();

        if (BrowserInfo.get().isIE()) {
            /*
             * This should fix the issue with padding not always taken into
             * account for the containers leading to no spacing between
             * elements.
             */
            getWidgetForPaintable().root.getStyle().setProperty("zoom", "1");
        }

        // w.mark("runDescendentsLayout done");
        getWidgetForPaintable().isRendering = false;
        getWidgetForPaintable().sizeHasChangedDuringRendering = false;
    }

    @Override
    protected abstract VOrderedLayout createWidget();

    @Override
    public VOrderedLayout getWidgetForPaintable() {
        return (VOrderedLayout) super.getWidgetForPaintable();
    }

}
