/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.DirectionalManagedLayout;
import com.vaadin.terminal.gwt.client.LayoutManager;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.ValueMap;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.ui.layout.ComponentConnectorLayoutSlot;
import com.vaadin.terminal.gwt.client.ui.layout.VLayoutSlot;

public abstract class AbstractOrderedLayoutConnector extends
        AbstractLayoutConnector implements Paintable, DirectionalManagedLayout {

    public static class AbstractOrderedLayoutState extends AbstractLayoutState {
        private boolean spacing = false;

        public boolean isSpacing() {
            return spacing;
        }

        public void setSpacing(boolean spacing) {
            this.spacing = spacing;
        }

    }

    public interface AbstractOrderedLayoutServerRPC extends LayoutClickRPC,
            ServerRpc {

    }

    AbstractOrderedLayoutServerRPC rpc;

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return Util.getConnectorForElement(getConnection(), getWidget(),
                    element);
        }

        @Override
        protected LayoutClickRPC getLayoutClickRPC() {
            return rpc;
        };

    };

    @Override
    public void init() {
        rpc = RpcProxy.create(AbstractOrderedLayoutServerRPC.class, this);
        getLayoutManager().registerDependency(this,
                getWidget().spacingMeasureElement);
    }

    @Override
    public AbstractOrderedLayoutState getState() {
        return (AbstractOrderedLayoutState) super.getState();
    }

    public void updateCaption(ComponentConnector component) {
        VMeasuringOrderedLayout layout = getWidget();
        if (VCaption.isNeeded(component.getState())) {
            VLayoutSlot layoutSlot = layout.getSlotForChild(component
                    .getWidget());
            VCaption caption = layoutSlot.getCaption();
            if (caption == null) {
                caption = new VCaption(component, getConnection());

                Widget widget = component.getWidget();

                layout.setCaption(widget, caption);
            }
            caption.updateCaption();
        } else {
            layout.setCaption(component.getWidget(), null);
            getLayoutManager().setNeedsUpdate(this);
        }
    }

    @Override
    public VMeasuringOrderedLayout getWidget() {
        return (VMeasuringOrderedLayout) super.getWidget();
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }
        clickEventHandler.handleEventHandlerRegistration();

        VMeasuringOrderedLayout layout = getWidget();

        ValueMap expandRatios = uidl.getMapAttribute("expandRatios");
        ValueMap alignments = uidl.getMapAttribute("alignments");

        for (ComponentConnector child : getChildren()) {
            VLayoutSlot slot = layout.getSlotForChild(child.getWidget());
            String pid = child.getConnectorId();

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
        }

        layout.updateMarginStyleNames(new VMarginInfo(getState()
                .getMarginsBitmask()));

        layout.updateSpacingStyleName(getState().isSpacing());

        getLayoutManager().setNeedsUpdate(this);
    }

    private int getSizeForInnerSize(int size, boolean isVertical) {
        LayoutManager layoutManager = getLayoutManager();
        Element element = getWidget().getElement();
        if (isVertical) {
            return size + layoutManager.getBorderHeight(element)
                    + layoutManager.getPaddingHeight(element);
        } else {
            return size + layoutManager.getBorderWidth(element)
                    + layoutManager.getPaddingWidth(element);
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
            return getLayoutManager().getInnerHeight(getWidget().getElement());
        } else {
            return getLayoutManager().getInnerWidth(getWidget().getElement());
        }
    }

    private void layoutPrimaryDirection() {
        VMeasuringOrderedLayout layout = getWidget();
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

        Style ownStyle = getWidget().getElement().getStyle();
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
            return getLayoutManager().getOuterHeight(
                    getWidget().spacingMeasureElement);
        } else {
            return getLayoutManager().getOuterWidth(
                    getWidget().spacingMeasureElement);
        }
    }

    private void layoutSecondaryDirection() {
        VMeasuringOrderedLayout layout = getWidget();
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

        Style ownStyle = getWidget().getElement().getStyle();

        if (isUndefined) {
            ownStyle.setPropertyPx(getSizeProperty(!getWidget().isVertical),
                    getSizeForInnerSize(allocatedSize, !getWidget().isVertical));
        } else {
            ownStyle.setProperty(getSizeProperty(!getWidget().isVertical),
                    getDefinedSize(!getWidget().isVertical));
        }
    }

    private String getDefinedSize(boolean isVertical) {
        if (isVertical) {
            return getState().getHeight();
        } else {
            return getState().getWidth();
        }
    }

    private int getStartPadding(boolean isVertical) {
        if (isVertical) {
            return getLayoutManager().getPaddingTop(getWidget().getElement());
        } else {
            return getLayoutManager().getPaddingLeft(getWidget().getElement());
        }
    }

    public void layoutHorizontally() {
        if (getWidget().isVertical) {
            layoutSecondaryDirection();
        } else {
            layoutPrimaryDirection();
        }
    }

    public void layoutVertically() {
        if (getWidget().isVertical) {
            layoutPrimaryDirection();
        } else {
            layoutSecondaryDirection();
        }
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);
        List<ComponentConnector> previousChildren = event.getOldChildren();
        int currentIndex = 0;
        VMeasuringOrderedLayout layout = getWidget();

        for (ComponentConnector child : getChildren()) {
            Widget childWidget = child.getWidget();
            VLayoutSlot slot = layout.getSlotForChild(childWidget);

            if (childWidget.getParent() != layout) {
                // If the child widget was previously attached to another
                // AbstractOrderedLayout a slot might be found that belongs to
                // another AbstractOrderedLayout. In this case we discard it and
                // create a new slot.
                slot = new ComponentConnectorLayoutSlot(getWidget()
                        .getStylePrimaryName(), child, this);
            }
            layout.addOrMove(slot, currentIndex++);
        }

        for (ComponentConnector child : previousChildren) {
            if (child.getParent() != this) {
                // Remove slot if the connector is no longer a child of this
                // layout
                layout.removeSlotForWidget(child.getWidget());
            }
        }

    };

}
