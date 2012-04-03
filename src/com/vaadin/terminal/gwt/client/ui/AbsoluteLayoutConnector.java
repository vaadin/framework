/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.DirectionalManagedLayout;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.ui.VAbsoluteLayout.AbsoluteWrapper;

public class AbsoluteLayoutConnector extends
        AbstractComponentContainerConnector implements DirectionalManagedLayout {

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

    private AbsoluteLayoutServerRPC rpc = GWT
            .create(AbsoluteLayoutServerRPC.class);

    @Override
    protected void init() {
        super.init();
        initRPC(rpc);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().client = client;
        // TODO margin handling
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        clickEventHandler.handleEventHandlerRegistration();

        HashSet<String> unrenderedPids = new HashSet<String>(
                getWidget().pidToComponentWrappper.keySet());

        for (Iterator<Object> childIterator = uidl.getChildIterator(); childIterator
                .hasNext();) {
            UIDL cc = (UIDL) childIterator.next();
            if (cc.getTag().equals("cc")) {
                UIDL componentUIDL = cc.getChildUIDL(0);
                unrenderedPids.remove(componentUIDL.getId());
                getWidget().getWrapper(client, componentUIDL)
                        .updateFromUIDL(cc);
            }
        }

        for (String pid : unrenderedPids) {
            AbsoluteWrapper absoluteWrapper = getWidget().pidToComponentWrappper
                    .get(pid);
            getWidget().pidToComponentWrappper.remove(pid);
            absoluteWrapper.destroy();
        }
    }

    public void updateCaption(ComponentConnector component) {
        AbsoluteWrapper parent2 = (AbsoluteWrapper) (component.getWidget())
                .getParent();
        parent2.updateCaption();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VAbsoluteLayout.class);
    }

    @Override
    public VAbsoluteLayout getWidget() {
        return (VAbsoluteLayout) super.getWidget();
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
                float percentSize = parsePercent(paintable.getState()
                        .getHeight());
                int effectiveHeight = Math.round(h * (percentSize / 100));
                getLayoutManager()
                        .reportOuterHeight(paintable, effectiveHeight);
            } else {
                wrapperStyle.clearHeight();
            }

            wrapper.updateCaptionPosition();
        }
    }

    private static float parsePercent(String size) {
        return Float.parseFloat(size.substring(0, size.length() - 1));
    }

    public void layoutHorizontally() {
        VAbsoluteLayout layout = getWidget();
        for (ComponentConnector paintable : getChildren()) {
            Widget widget = paintable.getWidget();
            AbsoluteWrapper wrapper = (AbsoluteWrapper) widget.getParent();
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
                float percentSize = parsePercent(paintable.getState()
                        .getWidth());
                int effectiveWidth = Math.round(w * (percentSize / 100));
                getLayoutManager().reportOuterWidth(paintable, effectiveWidth);
            } else {
                wrapperStyle.clearWidth();
            }

            wrapper.updateCaptionPosition();
        }
    }
}
