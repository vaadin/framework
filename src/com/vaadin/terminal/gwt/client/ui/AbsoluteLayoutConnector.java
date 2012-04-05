/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.DirectionalManagedLayout;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractLayoutConnector.AbstractLayoutState;
import com.vaadin.terminal.gwt.client.ui.VAbsoluteLayout.AbsoluteWrapper;
import com.vaadin.ui.AbsoluteLayout;

@Component(AbsoluteLayout.class)
public class AbsoluteLayoutConnector extends
        AbstractComponentContainerConnector implements DirectionalManagedLayout {

    public static class AbsoluteLayoutState extends AbstractLayoutState {
        // Maps each component to a position
        private Map<String, String> connectorToCssPosition = new HashMap<String, String>();

        public String getConnectorPosition(Connector connector) {
            return connectorToCssPosition.get(connector.getConnectorId());
        }

        public Map<String, String> getConnectorToCssPosition() {
            return connectorToCssPosition;
        }

        public void setConnectorToCssPosition(
                Map<String, String> componentToCssPosition) {
            connectorToCssPosition = componentToCssPosition;
        }

    }

    public interface AbsoluteLayoutServerRPC extends LayoutClickRPC, ServerRpc {

    }

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return getWidget().getComponent(element);
        }

        @Override
        protected LayoutClickRPC getLayoutClickRPC() {
            return rpc;
        };

    };

    private AbsoluteLayoutServerRPC rpc;

    private Map<String, AbsoluteWrapper> connectorIdToComponentWrapper = new HashMap<String, AbsoluteWrapper>();

    @Override
    protected void init() {
        super.init();
        rpc = RpcProxy.create(AbsoluteLayoutServerRPC.class, this);
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
    protected Widget createWidget() {
        return GWT.create(VAbsoluteLayout.class);
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
