/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.absolutelayout;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.DirectionalManagedLayout;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.LayoutClickEventHandler;
import com.vaadin.terminal.gwt.client.ui.LayoutClickRpc;
import com.vaadin.terminal.gwt.client.ui.absolutelayout.VAbsoluteLayout.AbsoluteWrapper;
import com.vaadin.ui.AbsoluteLayout;

@Connect(AbsoluteLayout.class)
public class AbsoluteLayoutConnector extends
        AbstractComponentContainerConnector implements DirectionalManagedLayout {

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return getConnectorForElement(element);
        }

        @Override
        protected LayoutClickRpc getLayoutClickRPC() {
            return rpc;
        };

    };

    private AbsoluteLayoutServerRpc rpc;

    private Map<String, AbsoluteWrapper> connectorIdToComponentWrapper = new HashMap<String, AbsoluteWrapper>();

    @Override
    protected void init() {
        super.init();
        rpc = RpcProxy.create(AbsoluteLayoutServerRpc.class, this);
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
    protected ComponentConnector getConnectorForElement(Element element) {
        return Util.getConnectorForElement(getConnection(), getWidget(),
                element);
    }

    public void updateCaption(ComponentConnector component) {
        VAbsoluteLayout absoluteLayoutWidget = getWidget();
        AbsoluteWrapper componentWrapper = getWrapper(component);

        boolean captionIsNeeded = VCaption.isNeeded(component.getState());

        VCaption caption = componentWrapper.getCaption();

        if (captionIsNeeded) {
            if (caption == null) {
                caption = new VCaption(component, getConnection());
                absoluteLayoutWidget.add(caption);
                componentWrapper.setCaption(caption);
            }
            caption.updateCaption();
            componentWrapper.updateCaptionPosition();
        } else {
            if (caption != null) {
                caption.removeFromParent();
            }
        }

    }

    @Override
    public VAbsoluteLayout getWidget() {
        return (VAbsoluteLayout) super.getWidget();
    }

    @Override
    public AbsoluteLayoutState getState() {
        return (AbsoluteLayoutState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        clickEventHandler.handleEventHandlerRegistration();

        // TODO Margin handling

        for (ComponentConnector child : getChildren()) {
            getWrapper(child).setPosition(
                    getState().getConnectorPosition(child));
        }
    };

    private AbsoluteWrapper getWrapper(ComponentConnector child) {
        String childId = child.getConnectorId();
        AbsoluteWrapper wrapper = connectorIdToComponentWrapper.get(childId);
        if (wrapper != null) {
            return wrapper;
        }

        wrapper = new AbsoluteWrapper(child.getWidget());
        connectorIdToComponentWrapper.put(childId, wrapper);
        getWidget().add(wrapper);
        return wrapper;

    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);

        for (ComponentConnector child : getChildren()) {
            getWrapper(child);
        }

        for (ComponentConnector oldChild : event.getOldChildren()) {
            if (oldChild.getParent() != this) {
                String connectorId = oldChild.getConnectorId();
                AbsoluteWrapper absoluteWrapper = connectorIdToComponentWrapper
                        .remove(connectorId);
                absoluteWrapper.destroy();
            }
        }
    }

    public void layoutVertically() {
        VAbsoluteLayout layout = getWidget();
        for (ComponentConnector paintable : getChildren()) {
            Widget widget = paintable.getWidget();
            AbsoluteWrapper wrapper = (AbsoluteWrapper) widget.getParent();
            Style wrapperStyle = wrapper.getElement().getStyle();

            if (paintable.isRelativeHeight()) {
                int h;
                if (wrapper.top != null && wrapper.bottom != null) {
                    h = wrapper.getOffsetHeight();
                } else if (wrapper.bottom != null) {
                    // top not defined, available space 0... bottom of
                    // wrapper
                    h = wrapper.getElement().getOffsetTop()
                            + wrapper.getOffsetHeight();
                } else {
                    // top defined or both undefined, available space ==
                    // canvas - top
                    h = layout.canvas.getOffsetHeight()
                            - wrapper.getElement().getOffsetTop();
                }
                wrapperStyle.setHeight(h, Unit.PX);
                getLayoutManager().reportHeightAssignedToRelative(paintable, h);
            } else {
                wrapperStyle.clearHeight();
            }

            wrapper.updateCaptionPosition();
        }
    }

    public void layoutHorizontally() {
        VAbsoluteLayout layout = getWidget();
        for (ComponentConnector paintable : getChildren()) {
            AbsoluteWrapper wrapper = getWrapper(paintable);
            Style wrapperStyle = wrapper.getElement().getStyle();

            if (paintable.isRelativeWidth()) {
                int w;
                if (wrapper.left != null && wrapper.right != null) {
                    w = wrapper.getOffsetWidth();
                } else if (wrapper.right != null) {
                    // left == null
                    // available width == right edge == offsetleft + width
                    w = wrapper.getOffsetWidth()
                            + wrapper.getElement().getOffsetLeft();
                } else {
                    // left != null && right == null || left == null &&
                    // right == null
                    // available width == canvas width - offset left
                    w = layout.canvas.getOffsetWidth()
                            - wrapper.getElement().getOffsetLeft();
                }
                wrapperStyle.setWidth(w, Unit.PX);
                getLayoutManager().reportWidthAssignedToRelative(paintable, w);
            } else {
                wrapperStyle.clearWidth();
            }

            wrapper.updateCaptionPosition();
        }
    }
}
